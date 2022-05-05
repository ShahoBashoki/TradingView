package com.shaho.tradingview.di

import android.util.Base64
import com.shaho.tradingview.BuildConfig
import com.shaho.tradingview.util.enum.AlgorithmType
import okhttp3.Request
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class SecretFields @Inject constructor() {
    val baseUrl: String
        get() {
            return if (BuildConfig.SANDBOX)
                "https://openapi-sandbox.kucoin.com"
            else
                "https://openapi-v2.kucoin.com"
        }
    val apiKey: String
        get() {
            return if (BuildConfig.SANDBOX)
                "624c6c3529c69200011e2908"
            else
                "624847e3d73d7100017acc8d"
        }
    private val apiSecret: String
        get() {
            return if (BuildConfig.SANDBOX)
                "c55b6585-3474-48f2-bad5-d7d617611480"
            else
                "4950db57-1a41-4c9a-9745-6a455ea45336"
        }
    private val myApiPassphrase: String
        get() {
            return if (BuildConfig.SANDBOX)
                "Sh.b2860381643"
            else
                "Sh.b2860381643"
        }
    val apiKeyVersion = "2"

    fun getApiSign(timestamp: String, request: Request): String {
        val endpoint: String = request.url.encodedPath
        val requestUriParams: String = request.url.query ?: ""
        val requestBody = getRequestBody(request)

        val stringBuilder = StringBuilder()
        stringBuilder.append(timestamp)
        stringBuilder.append(request.method)
        stringBuilder.append(endpoint)

        stringBuilder.append(if (requestUriParams.isBlank()) "" else "?$requestUriParams")
        requestBody?.let { itRequestBody ->
            stringBuilder.append(if (itRequestBody.isBlank()) "" else "" + itRequestBody)
        } ?: kotlin.run {
            stringBuilder.append("")
        }
        val originToSign = stringBuilder.toString()

        return getBase64(key = apiSecret, value = originToSign)
    }

    fun getApiPassphrase(): String {
        return getBase64(key = apiSecret, value = myApiPassphrase)
    }

    private fun getRequestBody(request: Request): String? {
        if (request.body == null) {
            return null
        }
        val buffer = Buffer()
        try {
            request.body?.writeTo(buffer)
        } catch (e: IOException) {
            throw RuntimeException("I/O error fetching request body", e)
        }

        var charset = Charset.forName("UTF-8")
        val contentType = request.body?.contentType()
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"))
        }
        return buffer.readString(charset)
    }

    private fun getBase64(key: String, value: String): String {
        val keySpec = SecretKeySpec(key.toByteArray(), AlgorithmType.H_MAC_SHA256.value)
        val mac = Mac.getInstance(AlgorithmType.H_MAC_SHA256.value)
        mac.init(keySpec)
        return String(Base64.encode(mac.doFinal(value.toByteArray()), 2))
    }
}
