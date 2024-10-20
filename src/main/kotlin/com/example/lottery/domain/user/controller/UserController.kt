package com.example.lottery.domain.user.controller

import com.example.lottery.domain.user.dto.CreateUserDto
import com.example.lottery.domain.user.entity.User
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
    fun createUser(@RequestBody dto: CreateUserDto): User {
        return userService.saveUser(dto)
    }
}