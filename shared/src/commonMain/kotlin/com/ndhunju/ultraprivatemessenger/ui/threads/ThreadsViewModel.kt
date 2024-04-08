package com.ndhunju.ultraprivatemessenger.ui.threads

import com.ndhunju.ultraprivatemessenger.common.NavItem
import com.ndhunju.ultraprivatemessenger.common.Result
import com.ndhunju.ultraprivatemessenger.data.MessageRepository
import com.ndhunju.ultraprivatemessenger.service.AppStateBroadcastService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Encapsulates business logic for showing message threads to the user
 */
class ThreadsViewModel(
    private val viewModelScope: CoroutineScope,
    private val appStateBroadcastService: AppStateBroadcastService,
    private val messageRepository: MessageRepository,
) {

    private val _title = MutableStateFlow("")
    val title: Flow<String> = _title.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefresh  = _isRefreshing.asStateFlow()

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    val showUpIcon: Flow<Boolean> = MutableStateFlow(false)

    private val _lastMessageForEachThread = MutableStateFlow<MutableList<Message>>(mutableListOf())
    val lastMessageForEachThread = _lastMessageForEachThread.asStateFlow()

    // Note: Compose doesn't track inner fields for changes unless we use mutableStateOf
    private val _showErrorMessageForPermissionDenied = MutableStateFlow(false)
    var showErrorMessageForPermissionDenied = _showErrorMessageForPermissionDenied.asStateFlow()

    // var showSearchTextField: Boolean by mutableStateOf(false)
    private val _showSearchTextField = MutableStateFlow(false)
    var showSearchTextField = _showSearchTextField.asStateFlow()

    private val _showSplashScreen = MutableStateFlow(true)
    val showSplashScreen = _showSplashScreen.asStateFlow()

    //region UI Events
    val onRefreshByUser: () -> Unit = {
        viewModelScope.launch {
            updateLastMessagesWithCorrectSyncStatus()
            _isRefreshing.value = false
            _showProgress.value = false
        }
    }

    val onClickSearchIcon = {
        _showSearchTextField.value = _showSearchTextField.value.not()
    }

    val onSearchTextChanged: (String) -> Unit = {}
    val onClickAccountIcon = { doOpenAccountScreen?.invoke() }
    val onClickThreadMessage: (Message) -> Unit = { doOpenMessageFromScreen?.invoke(it) }
    val onClickGrantPermission: () -> Unit = { doRequestPermission?.invoke() }

    val onClickNavItem: (NavItem) -> Unit = { navItem ->
        when (navItem) {
            NavItem.Account -> onClickAccountIcon()
            NavItem.Contacts -> doOpenContacts?.invoke()
        }
    }

    //endregion

    //region Action Callbacks

    /**
     * Invoked when respect Screen needs to be opened
     */
    var doOpenMessageFromScreen: ((Message) -> Unit)? = null
    var doOpenContacts: (() -> Unit)? = null
    var doOpenAccountScreen: (() -> Unit)? = null
    var doOpenDebugScreen: (() -> Unit)? = null
    var doRequestPermission: (() -> Unit)? = null

    //endregion

    private var latestNewMessageTimeStamp: Long = Clock.System.now().toEpochMilliseconds()

    private val newMessageObserver: (Long) -> Unit =  { newMessageTimeStamp ->
        viewModelScope.launch(Dispatchers.IO) {
            val newMessages = messageRepository.getMessagesSince(latestNewMessageTimeStamp)
            latestNewMessageTimeStamp = newMessageTimeStamp
            newMessages.collect { messages ->
                messages.forEach { message ->
                    onNewMessageReceived(message)
                }
            }
        }
    }

    private val newSyncedMessageObserver = FlowCollector<List<Message>> { newSyncedMessages ->
        newSyncedMessages.forEach { message ->
            onNewSyncedMessage(message)
        }
    }

    init {
        viewModelScope.launch {
            // NOTE: When you use collect fun, the control stops there until flow emits a value
            // So you need to wrap each collect call inside launch or use onEach extension function
            viewModelScope.launch {
                appStateBroadcastService.newSyncedMessages.collect(newSyncedMessageObserver)
            }
            appStateBroadcastService.newMessagesReceivedTime.onEach(newMessageObserver)
            updateLastMessagesWithCorrectSyncStatus()
        }
    }

    val onNewMessageReceived: (Message) -> Unit = { newMessage ->
        // Update the UI to the the latest SMS
        viewModelScope.launch {
            // Update the UI
            val oldLastMessageIndex = findIndexOfMessage(newMessage)
            if (oldLastMessageIndex > -1) {
                // Update last message shown with the new message
                updateMessageAt(oldLastMessageIndex, body = newMessage.body, date = newMessage.date)
                // Show this message at the top
                moveMessageToTopFrom(oldLastMessageIndex)
            } else {
                // Update the entire list since matching thread wasn't found
                updateLastMessagesWithCorrectSyncStatus()
            }
        }
    }

    private fun moveMessageToTopFrom(currentIndex: Int) {
        if (currentIndex != 0) {
            val updatedThread = _lastMessageForEachThread.value.removeAt(currentIndex)
            _lastMessageForEachThread.value.add(0, updatedThread)
        }
    }

    val onNewSyncedMessage: (Message) -> Unit = { newSyncedMessage ->
        viewModelScope.launch {
            // Update the UI
            val oldLastMessageIndex = findIndexOfMessage(newSyncedMessage)
            if (oldLastMessageIndex > -1) {
                // Update the icon based on update call status
                updateMessageAt(oldLastMessageIndex, syncStatus = newSyncedMessage.syncStatus)
            } else {
                // Update the entire list since matching thread wasn't found
                updateLastMessagesWithCorrectSyncStatus()
            }
        }
    }

    /**
     * Finds the index of [message] in [_lastMessageForEachThread]
     */
    private fun findIndexOfMessage(message: Message): Int {
        // Find the thread in which the message is sent to
        _lastMessageForEachThread.value.forEachIndexed { index, lastMessage ->
            if (lastMessage.threadId == message.threadId) {
                return index
            }
        }
        return -1
    }

    /**
     * Updates the [Message] at [index] with passed non null values
     */
    private fun updateMessageAt(
        index: Int,
        body: String? = null,
        date: Long? = null,
        syncStatus: Result<Nothing>? = null
    ) {
        val exitingCopy = _lastMessageForEachThread.value[index]
        _lastMessageForEachThread.value[index].copy(
            body = body ?: exitingCopy.body,
            date = date ?: exitingCopy.date,
            syncStatus = syncStatus ?: exitingCopy.syncStatus
        ).let {
            _lastMessageForEachThread.value[index] = it
        }
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    /**
     * Updates the [lastMessageForEachThread] with correct value for [Message.syncStatus]
     */
    private suspend fun updateLastMessagesWithCorrectSyncStatus() {
        // Update syncStatus info of Last Message with info available in database
         messageRepository.getLastMessageForEachThread().collect { lastMessages ->
             updateLastMessages(lastMessages)
             // Hide splash screen
             _showSplashScreen.value = false
         }
    }

    private fun updateLastMessages(messages: List<Message>?) {
        // In order for collectors to be notified, me have to create a new mutable list
        // This is not efficient but will do for now. In future, hopefully we can use
        // mutableStateListOf() or SnapshotStateList when it because part of KMP
        _lastMessageForEachThread.value = mutableListOf<Message>().apply {
            addAll(messages ?: emptyList())
        }
    }

}