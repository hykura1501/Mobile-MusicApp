package com.example.mobile_musicapp.services.payment.Api

import com.example.mobile_musicapp.services.payment.Constant.AppInfo
import com.example.mobile_musicapp.services.payment.Helper.Helpers
import okhttp3.FormBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Date

class CreateOrder {

    private inner class CreateOrderData(amount: String) {
        val appId: String = AppInfo.APP_ID.toString()
        val appUser: String = "Android_Demo"
        val appTime: String = Date().time.toString()
        val amount: String = amount
        val appTransId: String = Helpers.getAppTransId()
        val embedData: String = "{}"
        val items: String = "[]"
        val bankCode: String = "zalopayapp"
        val description: String = "Merchant pay for order #${Helpers.getAppTransId()}"
        val mac: String

        init {
            val inputHMac = "$appId|$appTransId|$appUser|$amount|$appTime|$embedData|$items"
            mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)
        }
    }

    @Throws(Exception::class)
    suspend fun createOrder(amount: String): JSONObject {
        val input = CreateOrderData(amount)

        val formBody: RequestBody = FormBody.Builder()
            .add("appid", input.appId)
            .add("appuser", input.appUser)
            .add("apptime", input.appTime)
            .add("amount", input.amount)
            .add("apptransid", input.appTransId)
            .add("embeddata", input.embedData)
            .add("item", input.items)
            .add("bankcode", input.bankCode)
            .add("description", input.description)
            .add("mac", input.mac)
            .build()

        return HttpProvider.sendPostAsync(AppInfo.URL_CREATE_ORDER, formBody) ?: JSONObject()
    }
}