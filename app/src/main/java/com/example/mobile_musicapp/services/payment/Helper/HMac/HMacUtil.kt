package com.example.mobile_musicapp.services.payment.Helper.HMac

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMacUtil {
    const val HMACMD5 = "HmacMD5"
    const val HMACSHA1 = "HmacSHA1"
    const val HMACSHA256 = "HmacSHA256"
    const val HMACSHA512 = "HmacSHA512"
    val UTF8CHARSET: Charset = StandardCharsets.UTF_8

    val HMACS = LinkedList(listOf("UnSupport", "HmacSHA256", "HmacMD5", "HmacSHA384", "HMacSHA1", "HmacSHA512"))

    private fun HMacEncode(algorithm: String, key: String, data: String): ByteArray? {
        val macGenerator: Mac? = try {
            Mac.getInstance(algorithm).apply {
                val signingKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
                init(signingKey)
            }
        } catch (ex: Exception) {
            null
        }

        val dataByte: ByteArray? = try {
            data.toByteArray(StandardCharsets.UTF_8)
        } catch (e: UnsupportedEncodingException) {
            null
        }

        return macGenerator?.doFinal(dataByte)
    }

    /**
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented base64-encoded string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     * @param key A secret cryptographic key
     * @param data The message to be authenticated
     * @return Base64-encoded HMAC String
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun HMacBase64Encode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
        return Base64.getEncoder().encodeToString(hmacEncodeBytes)
    }

    /**
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented hex string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     * @param key A secret cryptographic key
     * @param data The message to be authenticated
     * @return Hex HMAC String
     */
    fun HMacHexStringEncode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes)
    }
}