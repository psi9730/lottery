package com.example.lottery.domain.user.entity

import com.example.lottery.domain.user.dto.CreateUserDto
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false, unique = true)
    val phoneNumber: String,
) {
    companion object {
        fun fromDto(dto: CreateUserDto): User {
            return User(
                name = dto.name,
                email = dto.email,
                phoneNumber = dto.phoneNumber
            )
        }
    }
}