package com.example.lottery.domain.user.service

import com.example.lottery.domain.user.dto.CreateUserDto
import com.example.lottery.domain.user.entity.User
import com.example.lottery.domain.user.repository.UserRepository
import com.example.lottery.domain.user.util.UserAlreadyExistsException
import org.springframework.dao.DataIntegrityViolationException
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
}