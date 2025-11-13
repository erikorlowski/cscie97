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

### Entitlement Management
Entitlements represent the access that Users have in the Housemate system.