package com.ndhunju.ultraprivatemessenger.data

import com.ndhunju.ultraprivatemessenger.ui.threads.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun getMessagesSince(date: Long): Flow<List<Message>>

    fun addMessage(message: Message)

    // Internal implementation would emit on this flow when ever the last message changes
    fun getLastMessageForEachThread(): Flow<List<Message>>
}