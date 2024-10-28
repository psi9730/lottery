package com.example.lottery.domain.user.controller.dto

import com.example.lottery.domain.user.entity.User

data class UserResponse (
    val id: String,
    val user_name: String,
    val email: String,
    val phoneNumber: String,
) {
    companion object {
        fun of(user: User): UserResponse {
            return UserResponse(user.id, user.user_name, user.email, user.phoneNumber)
        }
    }
}