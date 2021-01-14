package com.queryholic.housekeepingbook.service

import com.queryholic.housekeepingbook.config.WebClientConfig
import javafx.geometry.Rectangle2D
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class OcrServiceTest {
    @Test
    fun `inferText() - 정상 결과 기대`() {
        // given
        val webClient = WebClientConfig(3000, 10000).ocrWebClient(
                "https://5b26183adbc44dd9b8820afbaa170fd6.apigw.ntruss.com/custom/v1/6087/c22071ddbd7a07c4cda3c34d8c41f749f1a28ee2d2a162d008f3a6bfe00f8ef4/general", "d2lNTENYSndjVHdVQ3hleFNkaFBsd2hIQkxGUkFzeUs="
        )

        val ocrService = OcrService(ocrWebClient = webClient)

        val imageUrl = "https://user-images.githubusercontent.com/10183131/104323086-19cfc900-5529-11eb-89f9-5bd610207002.png"

        // when
        val result = runBlocking { ocrService.inferText(imageUrl, "") }

        // then
        Assertions.assertIterableEquals(
                listOf(
                        "프라임홈마트",
                        "894-32-00070서상원031-317-9863",
                        "경기도시흥시시흥대로404",
                        "판매일:21-01-1022:38, 일요일계산대:001",
                        "NO.상품명단가수량금액",
                        "001감자",
                        "2000072,5002,500#1",
                        "002솔잎란15구",
                        "88092743012482,8002,8001",
                        "003풀무원소찌개두부",
                        "88011141190751,5001,500#1",
                        "004야채",
                        "220000262,2002,200#1",
                        "(#)면세물품:6,200",
                        "과세물품:2,546",
                        "부가세(VAT):254",
                        "합계:9,000",
                        "신용카드지불:9,000",
                        "카드승인(IC)",
                        "하나비씨카드일시불/9,000",
                        "9420-25**-****-****(60453966)",
                        "거래NO:0110203853계산원:관리자(001)",
                        "250110203853"
                ), result
        );

    }

    @Test
    fun `test`() {
        val imageWidth = 3000.0
        val lineHeight = 3449.0

        val line = Rectangle2D(0.0, lineHeight, imageWidth, lineHeight)
        val rect = Rectangle2D(46.0, 3358.0, 285.0, 3532.0)

        assert(line.intersects(rect))
    }

}