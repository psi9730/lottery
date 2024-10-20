package com.example.lottery.domain.user.entity

import com.example.lottery.domain.user.dto.CreateUserDto
import com.example.lottery.util.function.generateId
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User private constructor(
    @Id
    val id: String,

    @Column(nullable = false, length = 50)
    val user_name: String,

    @Column(nullable = false, length = 50)
    val email: String,

    @Column(nullable = false, unique = true, length = 20)
    val phoneNumber: String,
) {
    companion object {
        fun fromDto(dto: CreateUserDto): User {
            return User(
                id = generateId("UID"),
                user_name = dto.user_name,
                email = dto.email,
                phoneNumber = dto.phoneNumber
            )
        }
    }
}