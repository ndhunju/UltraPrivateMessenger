package com.ndhunju.ultraprivatemessenger.service

import com.ndhunju.ultraprivatemessenger.ui.threads.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Provides fields to observe app wide state,
 * and APIs to update those state
 */
interface AppStateBroadcastService {

    /**
     * True if current user is signed in
     */
    val isUserSignedIn: Flow<Boolean>

    /**
     * True if device has internet
     */
    val isDeviceOnline: Flow<Boolean>

    /**
     * Stores new processed [Message]s.
     * UI can make use of this to update their state.
     */
    val newSyncedMessages: Flow<List<Message>>

    /**
     * Stores the time stamp of when the new messages where received
     */
    val newMessagesReceivedTime: Flow<Long>

    /**
     * Updates [isUserSignedIn] if different and notifies observer
     */
    fun updateIsUserSignedIn(newValue: Boolean)

    /**
     * Updates [isDeviceOnline] if different and notifies observer
     */
    fun updateIsDeviceOnline(newValue: Boolean)

    /**
     * Updates [newSyncedMessages] and notifies observer
     */
    fun updateNewSyncedMessages(newValue: List<Message>)

    /**
     * Updates [newMessagesReceivedTime] and notifies observer
     */
    fun updateNewMessagesReceivedTime(newTime: Long)
}


/**
 * Simple implementation of [AppStateBroadcastService]
 */
class AppStateBroadcastServiceImpl(
    //networkConnectionChecker: NetworkConnectionChecker,
    //currentUser: CurrentUser
): AppStateBroadcastService {

    private val _isUserSignedIn = MutableStateFlow(false) //MutableStateFlow(currentUser.isUserSignedIn())
    override val isUserSignedIn = _isUserSignedIn.asStateFlow()

    private val _isOnline = MutableStateFlow(true) //networkConnectionChecker
    override val isDeviceOnline = _isOnline.asStateFlow()

    private val _newSyncedMessages = MutableStateFlow<List<Message>>(emptyList())
    override val newSyncedMessages = _newSyncedMessages.asStateFlow()

    private val _newMessagesReceivedTime = MutableStateFlow<Long>(0)
    override val newMessagesReceivedTime = _newMessagesReceivedTime.asStateFlow()

    override fun updateIsUserSignedIn(newValue: Boolean) {
        if (newValue == _isUserSignedIn.value) return
        // Log who made the update request
        //Log.d(TAG, "updateIsUserSignedIn: ${Throwable().stackTrace.first()}")
        _isUserSignedIn.value = newValue
    }

    override fun updateIsDeviceOnline(newValue: Boolean) {
        if (newValue == _isOnline.value) return
        // Log who made the update request
        //Log.d(TAG, "updateIsDeviceOnline: ${Throwable().stackTrace.first()}")
        _isOnline.value = newValue
    }

    override fun updateNewSyncedMessages(newValue: List<Message>) {
        // Log who made the update request
        //Log.d(TAG, "updateNewSyncedMessages: ${Throwable().stackTrace.first()}")
        _newSyncedMessages.value = newValue
    }

    override fun updateNewMessagesReceivedTime(newTime: Long) {
        // Log who made the update request
        //Log.d(TAG, "updateNewMessagesReceivedTime: ${Throwable().stackTrace.first()}")
        _newMessagesReceivedTime.value = newTime
    }

    companion object {
        val TAG: String = AppStateBroadcastServiceImpl::class.simpleName ?: ""
    }

}