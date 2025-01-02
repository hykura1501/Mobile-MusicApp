package com.example.mobile_musicapp.services.payment.Api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpProvider {
    @JvmStatic
    fun sendPost(URL: String, formBody: RequestBody): JSONObject? {
        var data = JSONObject()
        try {
            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                )
                .build()

            val client = OkHttpClient.Builder()
                .connectionSpecs(listOf(spec))
                .callTimeout(5000, TimeUnit.MILLISECONDS)
                .build()

            val request = Request.Builder()
                .url(URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("BAD_REQUEST", response.body?.string().orEmpty())
                data = JSONObject()
            } else {
                data = JSONObject(response.body?.string().orEmpty())
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return data
    }
    suspend fun sendPostAsync(URL: String, formBody: RequestBody): JSONObject? {
        return withContext(Dispatchers.IO) {
            sendPost(URL, formBody)
        }
    }

}