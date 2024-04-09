package com.ndhunju.ultraprivatemessenger.data

import com.ndhunju.ultraprivatemessenger.ui.threads.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MessageRepository {

    fun getMessages(): Flow<List<Message>>

    fun getMessagesSince(date: Long): Flow<List<Message>>

    fun addMessage(message: Message)

    // Internal implementation would emit on this flow when ever the last message changes
    fun getLastMessageForEachThread(): Flow<List<Message>>
}

class MessageRepositoryImpl: MessageRepository {

    private val messages = mutableListOf<Message>().apply { addAll(sampleMessages) }

    override fun getMessages(): Flow<List<Message>> = flow {
        emit(messages)
    }

    override fun getMessagesSince(date: Long): Flow<List<Message>> = flow {
        emit(messages.filter { message -> message.date > date})
    }

    override fun addMessage(message: Message) {
        messages.add(message)
    }

    var change = 0

    override fun getLastMessageForEachThread(): Flow<List<Message>> = flow {
        val threadToMessagesMap = mutableMapOf<String, Message>()
        messages.forEach { message ->
            val previousLastMessage = threadToMessagesMap[message.threadId]
            if (previousLastMessage == null) {
                threadToMessagesMap[message.threadId] = message //.copy(body = "changed $change", threadId = "threadId$change")
            } else if (previousLastMessage.date < message.date ) {
                threadToMessagesMap[message.threadId] = message //.copy(body = "changed $change", threadId = "threadId$change")
            }
            change++
        }
        emit(threadToMessagesMap.map {  it.value })
    }

}