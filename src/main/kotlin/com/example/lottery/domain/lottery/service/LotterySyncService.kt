package com.example.lottery.domain.lottery.service

import com.example.lottery.domain.lottery.dto.LotteryRoundResponse
import com.example.lottery.domain.lottery.dto.LotteryRoundApiResponse
import com.example.lottery.domain.lottery.entity.LotteryRound
import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.ChannelOption
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.Duration


@Service
class LotterySyncService (
    private val lotteryRoundService: LotteryRoundService,
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

    @Transactional
    fun syncLotteryNumbers() {
        val thisWeekLotteryRound = lotteryRoundService.findThisWeekLotteryRound()

        thisWeekLotteryRound?.run {
            if (this.isLotteryRoundSynced) {
                return
            }
        }
        val drawNo = thisWeekLotteryRound?.round ?: lotteryRoundService.findLatestBeforeDate()?.nextRound ?: 1143

        val apiUrl = "/common.do?method=getLottoNumber&drwNo=$drawNo"

        val response = webClient.get()
            .uri(apiUrl)
            .retrieve()
            .toEntity(String::class.java)
            .map { responseEntity ->
                val jsonString = responseEntity.body ?: ""
                objectMapper.readValue(jsonString, LotteryRoundApiResponse::class.java).toLotteryRoundResponse()
            }
            .onErrorResume {
                Mono.empty()
            }.block()

        if (response is LotteryRoundResponse.Success) {
            val lotteryRound: LotteryRound = when (thisWeekLotteryRound) {
                null -> {
                    response.toLotteryRound(drawNo)
                }
                else -> {
                    val updateDto = response.toLotteryRoundUpdateDto()
                    return thisWeekLotteryRound.sync(
                        numbers = updateDto.numbers,
                        bonusNumber = updateDto.bonusNumber,
                        drwNoDate = updateDto.drwNoDate,
                        firstWinCount = updateDto.firstWinCount,
                        firstWinAmount = updateDto.firstWinAmount,
                        totalSellAmount = updateDto.totalSellAmount,
                    )
                }
            }
            lotteryRoundService.saveLotteryRound(lotteryRound)
        }
    }


}