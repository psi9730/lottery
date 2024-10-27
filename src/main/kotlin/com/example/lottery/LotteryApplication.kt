package com.example.lottery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class LotteryApplication

fun main(args: Array<String>) {
	runApplication<LotteryApplication>(*args)
}
