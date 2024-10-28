package com.example.lottery.domain.user.controller

import com.example.lottery.domain.user.controller.dto.CreateUserRequest
import com.example.lottery.domain.user.controller.dto.UserResponse
import com.example.lottery.domain.user.dto.CreateUserDto
import com.example.lottery.domain.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun createUser(@RequestBody dto: CreateUserRequest): UserResponse {
        return UserResponse.of(userService.saveUser(CreateUserDto(dto.user_name, dto.email, dto.phoneNumber)))
    }
}