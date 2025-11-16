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

#### Access Token Handling
When a User is authenticated, an AccessToken is created. This AccessToken is attached to the User and stays active until it is logged out or until it is timed out.

| __Requirement: Access Token Creation__ |
|--|
| When a User is successfully authenticated, a new AccessToken shall be created and attached to that User. |

An AccessToken is logged out through the following command:

```logout <auth_token>```

| __Requirement: Access Token Logout__ |
|--|
| When the logout command is executed