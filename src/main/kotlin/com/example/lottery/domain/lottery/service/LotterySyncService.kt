package com.example.lottery.domain.lottery.service

import com.example.lottery.domain.lottery.dto.LotteryResultDto
import com.example.lottery.domain.lottery.dto.LotteryResultResponse
import com.example.lottery.domain.lottery.repository.LotteryResultRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.time.Instant


@Service
class LotterySyncService (
    private val lotteryResultRepository: LotteryResultRepository,
    private val objectMapper: ObjectMapper
){
    private val baseUrl = "https://www.dhlottery.co.kr"

    private val httpClient = HttpClient.create()
        .responseTimeout(Duration.ofSeconds(3))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)

    private val webClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .baseUrl(baseUrl)
        .build()

    fun syncLotteryNumbers() {
        val lastSavedResult = lotteryResultRepository.findTopByOrderByRoundDesc()

        lastSavedResult?.run {
            if (this.isTodayLotteryResultAlreadyExist(Instant.now())) {
                return
            }
        }

        val nextDrawNo = (lastSavedResult?.round ?: 1142) + 1
        val apiUrl = "/common.do?method=getLottoNumber&drwNo=$nextDrawNo"

        val response = webClient.get()
            .uri(apiUrl)
            .retrieve()
            .toEntity(String::class.java)
            .map { responseEntity ->
                val jsonString = responseEntity.body ?: ""
                val resultResponse = objectMapper.readValue(jsonString, LotteryResultResponse::class.java)
                resultResponse.toLotteryResultDto()
            }
            .onErrorResume {
                Mono.empty()
            }.block()

        if (response is LotteryResultDto.Success) {
            lotteryResultRepository.save(response.toLotteryResult(nextDrawNo))
        }
    }


}