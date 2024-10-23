package com.example.lottery.domain.user.entity

import com.example.lottery.util.function.generateId
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User (
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
        fun of(user_name: String, email: String, phoneNumber: String): User {
            return User(
                id = generateId("UID"),
                user_name = user_name,
                email = email,
                phoneNumber = phoneNumber
            )
        }
    }
}