package com.queryholic.housekeepingbook.handler

import com.nhaarman.mockitokotlin2.mock
import com.queryholic.housekeepingbook.service.OcrService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class OcrHandlerUnitTest {
    private val ocrService = mock<OcrService> {
    }

    @Test
    fun getItems() {
        // given
        val ocrHandler = OcrHandler(ocrService)
        val rawText: List<String> = listOf(
                "프라임홈마트",
                "894-32-00070|서상원|031-317-9863",
                "경기도|시흥시|시흥대로404",
                "판매일:|21-01-10|22:|38, 일요일|계산대:|001",
                "NO.|상품명|단가|수량|금액",
                "001|감자",
                "200007|2,500|2,500|#|1",
                "002|솔잎란15구",
                "8809274301248|2,800|2,800|1",
                "003|풀무원소찌개두부",
                "8801114119075|1,500|1,500|#|1",
                "004|야채",
                "22000026|2,200|2,200|#|1",
                "(#)면세물품|:|6,200",
                "과세물품|:|2,546",
                "부가세|(VAT)|:|254",
                "합|계:|9,000",
                "신용카드지불|:|9,000",
                "카드승인|(IC)",
                "하나비씨카드|일시불|/|9,000",
                "9420-25**-****-****|(60453966)",
                "거래NO:|0110203853|계산원:|관리자|(001)",
                "250110203853"
        )
        val itemHeaderLineNumber: Int = rawText.indexOfFirst { it.contains("|단가|") }

        // when
        val actualResult: List<String> = ocrHandler.getItems(rawText, itemHeaderLineNumber)

        // then
        Assertions.assertIterableEquals(
                listOf(
                        "NO.|상품명|단가|수량|금액",
                        "001|감자|2500|2500|1",
                        "002|솔잎란15구|2800|2800|1",
                        "003|풀무원소찌개두부|1500|1500|1",
                        "004|야채|2200|2200|1",
                ), actualResult
        )
    }
}