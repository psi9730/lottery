package com.example.lottery.domain.user.controller.dto

data class CreateUserRequest (
    val user_name: String,
    val email: String,
    val phoneNumber: String,
)