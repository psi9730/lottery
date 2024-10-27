package com.example.lottery.domain.lottery.dto

data class LotteryRoundApiResponse(
    val totSellamnt: Long?,
    val returnValue: String,
    val drwNoDate: String?,
    val firstWinamnt: Long?,
    val drwtNo1: Int?,
    val drwtNo2: Int?,
    val drwtNo3: Int?,
    val drwtNo4: Int?,
    val drwtNo5: Int?,
    val drwtNo6: Int?,
    val bnusNo: Int?,
    val firstPrzwnerCo: Int?,
    val firstAccumamnt: Long?,
    val drwNo: Int?
) {
    fun toLotteryRoundResponse(): LotteryRoundResponse {
        return when (returnValue) {
            "success" -> {
                if (totSellamnt == null || drwNoDate == null || firstWinamnt == null ||
                    drwtNo1 == null || drwtNo2 == null || drwtNo3 == null ||
                    drwtNo4 == null || drwtNo5 == null || drwtNo6 == null ||
                    bnusNo == null || firstPrzwnerCo == null || firstAccumamnt == null ||
                    drwNo == null) {

                    throw IllegalArgumentException("Required fields are missing for success response.")
                }

                LotteryRoundResponse.Success(
                    totSellamnt = totSellamnt,
                    returnValue = returnValue,
                    drwNoDate = drwNoDate,
                    firstWinamnt = firstWinamnt,
                    drwtNo1 = drwtNo1,
                    drwtNo2 = drwtNo2,
                    drwtNo3 = drwtNo3,
                    drwtNo4 = drwtNo4,
                    drwtNo5 = drwtNo5,
                    drwtNo6 = drwtNo6,
                    bnusNo = bnusNo,
                    firstPrzwnerCo = firstPrzwnerCo,
                    firstAccumamnt = firstAccumamnt,
                    drwNo = drwNo
                )
            }
            "fail" -> LotteryRoundResponse.Failure(returnValue = returnValue)
            else -> throw IllegalArgumentException("Unknown return value: $returnValue")
        }
    }
}