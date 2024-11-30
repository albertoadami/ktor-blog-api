package it.adami.services.blog.exceptions

import it.adami.services.blog.model.UserStatus

class UserInInvalidStateException(userId: Long, userStatus: UserStatus) : IllegalStateException("The user $userId has invalid state $userStatus for the required operation")