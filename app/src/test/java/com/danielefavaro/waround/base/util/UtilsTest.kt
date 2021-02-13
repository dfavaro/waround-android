package com.danielefavaro.waround.base.util

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import okio.Buffer

object UtilsTest {
    fun getResponseFromFile(statusCode: Int, fileName: String) = MockResponse().apply {
        setResponseCode(statusCode)
        val resourceAsStream = this.javaClass.getResourceAsStream("/${fileName}")
        resourceAsStream?.let { setBody(Buffer().readFrom(it)) }
    }

    fun getResponse(statusCode: Int, body: String) = MockResponse().apply {
        setResponseCode(statusCode)
        setBody(body)
    }

    fun getTimeoutRequest(): MockResponse =
        MockResponse().apply { setSocketPolicy(SocketPolicy.NO_RESPONSE) }

    fun getConnectionRefusedRequest(): MockResponse =
        MockResponse().apply { setSocketPolicy(SocketPolicy.DISCONNECT_AT_START) }

    fun getJsonFromFile(fileName: String): String {
        val fileInputStream = javaClass.classLoader?.getResourceAsStream(fileName)
        return fileInputStream?.bufferedReader()?.readText() ?: ""
    }
}