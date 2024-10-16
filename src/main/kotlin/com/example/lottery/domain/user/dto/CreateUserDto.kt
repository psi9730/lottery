package com.example.lottery.domain.user.dto

data class CreateUserDto (
    val name: String,
    val email: String,
    val phoneNumber: String,
)