package com.example.lottery.domain.user.service

import com.example.lottery.domain.user.dto.CreateUserDto
import com.example.lottery.domain.user.entity.User
import com.example.lottery.domain.user.repository.UserRepository
import com.example.lottery.util.error.UserAlreadyExistsException
import com.example.lottery.util.error.UserNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun saveUser(user: CreateUserDto): User {
        try {
            return userRepository.save(User.fromDto(user))
        } catch (ex: DataIntegrityViolationException) {
            throw UserAlreadyExistsException("User with this phoneNumber already exists: ${user.phoneNumber}")
        }
    }

    fun findUserOrThrow(uid: String): User {
        return userRepository.findByIdOrNull(uid) ?: throw UserNotFoundException()
    }
}