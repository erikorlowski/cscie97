# Housemate Entitlement Service Design Document

__Date:__ 11/1/2025
__Author:__ Erik Orlowski
__Reviewers:__ Audreen Soh, Kyriaki Avgerinou

## Introduction
This document defines the design and requirements for the Housemate Entitlement Service. This service is used to control access to the Housemate Model Service.

## Overview
The Housemate system automates devices within the home to respond to user inputs and other appliances. Because of the level of access granted to elements of a user's home, it is imperative that only authorized user have access to objects in the Housemate system. The responsibility for this access control falls primarily on the Housemate Entitlement Service.

The Housemate Entitlement Service operates by defining Roles, which are compositions of Permissions and other Roles. The Housemate Entitlement Service also defines Resources, which map to objects in the Housemate Model Service. Resources require a user to have a specific Role or Resource Role to interact with them through the Housemate Model Service. Finally, the Housemate Entitlement Service defines users that can be assigned Roles and Resource Roles allowing them to interact with objects in the Housemate Model Service.

## Requirements
### Access Checks
The most fundamental feature of the Housemate Entitlement Service is to check if a user making a request to the Housemate Model Service has the appropriate access. This is implemented by the Housemate Model Service sending a request to the Housemate Entitlement Service in the form of

```check_access <auth_token>, <permission>, <resource>```

When a check_access request is received, the Housemate Entitlement Service first attempts to find the provided auth_token.

| __Requirement: Access Check With Invalid auth_token Is Rejected__ |
|--|
| When a check_access request is received and the provided auth_token is not found, the Housemate Entitlement Service shall throw an InvalidAccessTokenException. |

After the auth_token is found, the next step is to determine whether the auth_token has the required Permission provided by the Housemate Model Service.

| __Requirement: Permission Check Success for Admin User__ |
|--|
| When a check_access request is received and the auth_token is resolved to an Admin User, then the Housemate Entitlement Service shall return a successful response if the user has the Permission contained in the request. |

| __Requirement: Permission Check Failure for Admin User__ |
|--|
| When a check_access request is received and the auth_token is resolved to an Admin User, then the Housemate Entitelement Service shall throw an AccessDeniedException if the conditions in __Requirement: Permission Check Success for Admin User__ are not met. |

| __Requirement: Permission Check Success for Non-Admin User__ |
|--|
| When a check_access request is received and the auth_token is resolved to an Non-Admin User, then the Housemate Entitlement Service shall return a successful response if [the user has a Resource Role that specifies the provided Resource OR the Resource provided in the request is contained directly or indirectly in the resource specified by the Resource Role] AND [a Resource Role matching the provided Resource contained the Permission given in the request]. |

| __Requirement: Permission Check Failure for Non-Admin User__ |
|--|
| When a check_access request is received and the auth_token is resolved to an Non-Admin User, then the Housemate Entitlement Service shall throw an AccessDeniedException if the conditions in __Requirement: Permission Check Success for Non-Admin User__ are not met. |

### Access Management
Several constructs are used to represent the access that Users have in the Housemate system. These include Permissions, Roles and Resource Roles.

#### Permissions
Permissions are the most fundamental Entitlement. A Permission is requested by the Housemate Model Service to perform actions. 

A Permission consists of an ID, a name and a description.

Permissions are created with the following command:

```define_permission, <permission_id>, <permission_name>, <permission_description>```

| __Requirement: Define Permission Success__ |
|--|
| When a define_permission command is entered, if the command is correctly formatted, a Permission will be created with the requested ID, name and description. |

#### Roles
A Role is a composition of Permissions and sub-Roles. Roles are used to create logical groupings of Permissions that are easier to interact with than individual Permissions.

A Role consists of an ID, a name and a description.

A new Role is created with the following command:

```define_role, <role_id>, <role_name>, <role_description>```

| __Requirement: Define Role Success__ |
|--|
| When a define_role command is entered, if the command is correctly formatted, a Role will be created with the requested ID, name and description. |

Entitlements (Roles or Permissions) can be added to a Role with the following command:

```add_entitlement_to_role, <role_id>, <entitlement_id>```

| __Requirement: Add Entitlement to Role - Entitlement Not Found__ |
|--|
| When the add_entitlement_to_role command is run and the role_id or entitlement_id cannot be found, the command shall be rejected and the user will be informed of the error. |

| __Requirement: A Entitlement to Role Success__ |
|--|
| When an add_entitlement command is run, if the command is formatted correctly, the role_id is found and the entitlement_id is found, the Entitlement with entitlement_id shall be added to the Role with role_id.

#### Resource Roles
Resource Roles are used to indicate that a User can perform actions associated with a specific Role on a Resource or objects contained in that Resource.

A Resource Role consists of a name, a Role and a Resource.

Resource Roles are created with the following command:

```create_resource_role <resource_role_name>, <role_id>, <resource_id>```

| __Requirement: Resource Role Item Not Found__ |
|--|
| When a create_resource_role command is run, if a Role with the provided role_id cannot be found in the Housemate Entitlement Service or a Resource with the provided resource_id cannot be found in the Housemate Model Service, the command shall be rejected and the user will be informed of the error. |

| __Requirement: Resource Role Creation Successful__ |
|--|
| When a create_resource_role command is run, if a Role and Resource are found with the specified parameters, a new Resource Role shall be created with the provided resource_role_name, role_id and resource_id. |

| __Requirement: Resource Role Creation on Existing Resource Role__ |
|--|
| When a create_resource_role command is run and the resource_role_name already exists, the command shall be executed successfully with the Resource Role being associated with the new role_id and resource_id. |

### User Management
Users interact with the Housemate System through commands and through interacting the Housemate devices. The Housemate Entitlement Service has Admin users with access to configure the Housemate Entitlement Service and interact with any home and Non-Admin users who only have access to interact with Homes they are associated with.

Users are created by running the following command. At the time of creation, it is unknown whether the User should be an Admin or Non-Admin User.

```create_user <user_id>, <user_name>```

| __Requirement: User Creation__ |
|--|
| When the create_user command is run and it is properly formatted, a new User shall be created with the specified ID and name. |

Users also have credentials. A User's credentials are configured through the following command:

```add_user_credential <user_id>, <voice_print|password>, <value>```

| __Requirement: Add User Credential - User Not Found__ |
|--|
| If the add_user_credential command is run and the user_id provided does not match a User in the Housemate Entitlement Service, the command shall be rejected and a message shall be displayed. |

| __Requirement: Add User Credential - Unknown Credential Type__ |
|--|
| If the add_user_credential command is run and the second argument is neither "voice_print" nor "password", the command shall be rejected and a message shall be displayed. |

| __Requirement: Add User Credential - Admin User__ |
|--|
| If the add_user_credential command is run and the second argument is "password", the User's password shall be saved and the User shall be registered as an Admin. |

| __Requirement: Add User Credential - Admin User__ |
|--|
| If the add_user_credential command is run and the second argument is "voice_print", the User's voiceprint shall be saved and the User shall be registered as a Non-Admin. |

In order for Users to perform actions, they must be associated with Roles and Resource Roles. Users are given a Role with the following command:

```add_role_to_user <user_id>, <role>```

| __Requirement: Add Role To Invalid User__ |
|--|
| If the add_role_to_user command is executed and the user_id cannot be found, the command shall be rejected and an error message shall be displayed. |

| __Requirement: Add Invalid Role to User__ |
|--|
| If the add_role_to_user command is executed and the role cannot be found, the command shall be rejected and an error message shall be displayed. |

| __Requirement: Add Role to User Success__ |
|--|
| If the add_role_to_user command is run with a valid user_id and role, the User shall be given the specified Role. |

Users are given Resource Roles through the following command:

```add_resource_role_to_user <user_id>, <resource_role>```

| __Requirement: Add Resource Role To Invalid User__ |
|--|
| If the add_resource_role_to_user command is executed and the user_id cannot be found, the command shall be rejected and an error message shall be displayed. |

| __Requirement: Add Invalid Resource Role to User__ |
|--|
| If the add_resource_role_to_user command is executed and the resource_role cannot be found, the command shall be rejected and an error message shall be displayed. |

| __Requirement: Add Resource Role to User Success__ |
|--|
| If the add_resource_role_to_user command is run with a valid user_id and resource_role, the User shall be given the specified Resource Role. |

### Authentication
When Users are authenticated, an AccessToken is created for the User.

Admin Users are authenticated through the following command:

```login user <user_id>, password <password>```

| __Requirement: Admin User Successful Authentication__ |
|--|
| When the login command is executed with proper Admin formatting, if the user_id and password match a User, then the User shall be authenticated. |

Non-Admin Users are authenticated through the following command:

```login voiceprint <voiceprint>```

| __Requirement: Non-Admin User Successful Authentication__ |
|--|
| When the login command is executed with proper Non-Admin formatting, if the voiceprint matches a User, then the User shall be authenticated. |

| __Requirement: Invalid Login Rejection__ |
|--|
| If the login command is executed and [it is not formatted properly for an Admin or Non-Admin login] OR [the credential information provided does not match a User] then an AuthenticationException shall be thrown and the authentication attempt shall be rejected. |

| __Request: AccessToken After Login__ |
|--|
| If the login command is called successfully by the Housemate Model Service or the Housemate Controller Service, the newly created AccessToken shall be returned. |

#### Access Token Handling
When a User is authenticated, an AccessToken is created. This AccessToken is attached to the User and stays active until it is logged out or until it is timed out.

| __Requirement: Access Token Creation__ |
|--|
| When a User is successfully authenticated, a new AccessToken shall be created and attached to that User. |

An AccessToken is logged out through the following command:

```logout <auth_token>```

| __Requirement: Access Token Logout__ |
|--|
| When the logout command is executed successfully, any subsequent attempts to use the provided access key shall result in an InvalidAccessTokenException being thrown. |

| __Requirement: Access Token Logout Not Found__ |
|--|
| When the logout command is executed, if the auth_token provided is not found, an InvalidAccessTokenException shall be thrown. |

When AccessTokens are created, they are given an expiration time. This expiration time is renewed each time the AccessToken is used. If an attempt is made to use the AccessToken after its expiration time has expires, an exception is thrown.

| __Requirement: Access Token Timeout__ |
|--|
| When an attempt is made to use an AccessToken more than 1 hour after when the AccessToken was created or that last time it was used (whichever is later), an InvalidAccessTokenException shall be thrown. |

#### Authentication Required for Administrator Commands
In order to use certain commands, an Admin User must be logged in.

| __Requirement: Admin User for Configuration Commands__ |
|--|
| With the exception of the login, logout and check_access commands, all commands described in this document shall throw an AccessDeniedException if an Admin User is not currently logged in with a valid AccessToken. |

### Invalid Commands
A number of error cases for commands are laid out in this document. In addition, a catch-all requirement is added to deal with any otherwise improper commands.

| __Requirement: Improper Housemate Entitlement Service Commands__ |
|--|
| If a command is processed by the Housemate Entitlement Service and this command is not properly formatted according to the commands outlined in this document, the command shall be rejected and an appropriate error message shall be shown. |

### External Interactions
In order to function properly in the Housemate System as a whole, the Housemate Entitelement Service interacts with the Housemate Model Service and the Housemate Controller Service.

#### check_access Interactions with Housemate Model Service
When the Housemate Model Service receives a request to view or change any Housemate Model Objects, it sends a check_access request to the Housemate Entitlement Service. The permission_id is be view_<device_type> for read only requests and control_<device_type> for requests that modify the device.

#### Occupants Interaction with Housemate Model Service
When a new Occupant is created in the Housemate Model Service, the Housemate Model Service sends a create_user request to the Housemate Entitlement Service, followed by an add_user_credential request for a default voiceprint associated with the newly created User.

When an Occupant is associated with a House, a create_resource_role request is made with the resource_role_name of <House Name>_(Adult|Child|Pet)_Resource_Role, a role of (Adult|Child|Pet)_Role and a resource of <House Name>. This is followed by an add_resource_role_to_user request to add the newly created Resource Role to the User.

#### Voice Command Device Interaction with Housemate Controller Service
When the 'voiceprint' status on an Ava device is modified, the Housemate Controller Service sends a login request to the Housemate Entitlement Service with the voiceprint associated with the status value.

When a voice command is received, the AccessToken associated with the current voiceprint status value of the Ava device is used in a check_access request to the Housemate Entitlement Service.

#### Housemate Controller Service Initial Login
At startup, the Housemate Controller Service uses Housemate Entitlement Service commands to create an Admin User and authenticate that User. The AccessToken associated with this User is used for all requests made by the Housemate Controller Service to the Housemate Model Service.