package com.graphql.resolver

import com.graphql.entity.user.User
import com.graphql.input.AddUserInput
import com.graphql.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserResolver(
    private val userService: UserService
) {

    @QueryMapping
    fun getUser(
        @Argument userId: String
    ): User {
        return userService.getUser(userId)
    }

    @MutationMapping
    fun addUser(
        @Argument addUserInput: AddUserInput
    ): User {
        return userService.addUser(addUserInput)
    }

}