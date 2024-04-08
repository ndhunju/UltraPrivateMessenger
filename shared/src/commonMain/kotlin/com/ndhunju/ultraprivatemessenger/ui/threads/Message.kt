package com.ndhunju.ultraprivatemessenger.ui.threads

import com.ndhunju.ultraprivatemessenger.common.Result
import com.ndhunju.ultraprivatemessenger.format
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


data class Message(
    val id: String,
    val threadId: String,
    val from: String,
    val body: String,
    val date: Long,
    val type: String,
    /**
     * Null means this instance of the Message/Sms was sent before our app was installed.
     * So we never pushed is to the cloud database. In terms of UI, we should hide the sync icon.
     * **/
    var syncStatus: Result<Nothing>? = null,
    val extra: String? = null
)

fun Message.getFormattedTime(): String {
    val localDateTime = Instant
        .fromEpochMilliseconds(date)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    //val date = localDateTime.date
    //val day = date.dayOfMonth
    //val month = date.monthNumber
    //val year = date.year

    return localDateTime.format("MMM d")
}