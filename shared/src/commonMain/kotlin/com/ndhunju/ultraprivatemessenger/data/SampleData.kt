package com.ndhunju.ultraprivatemessenger.data

import com.ndhunju.ultraprivatemessenger.common.*
import com.ndhunju.ultraprivatemessenger.ui.threads.Message
import kotlinx.datetime.Clock

/** See https://stackoverflow.com/questions/346372/whats-the-difference-between-faking-mocking-and-stubbing
 * Fake might be not a right prefix here. Reconsider it later. May be can use dummyMessages **/
val sampleMessages = listOf(
    Message(
        "0",
        "ThreadId1",
        "Bikesh",
        "See you soon!",
        Clock.System.now().toEpochMilliseconds(),
        "1",
        Result.Failure()
    ),
    Message(
        "1",
        "ThreadId22",
        "Nabil Bank",
        "This is a sample long messages that should overflow to the next line at the minimum and be start aligned",
        Clock.System.now().toEpochMilliseconds(),
        "2",
        Result.Success()
    ),
    Message(
        "1",
        "ThreadId2",
        "Nabil Bank",
        "Your new balance is 50,000",
        Clock.System.now().toEpochMilliseconds(),
        "2",
        Result.Success()
    ),
    Message(
        "1",
        "ThreadId23",
        "Nabil Bank",
        "2,300.00 added to your account.",
        Clock.System.now().toEpochMilliseconds(),
        "2",
        Result.Success()
    ),
    Message(
        "2",
        "ThreadId3",
        "Bikesh",
        "This is a sample long messages that should overflow to the next line at the minimum",
        Clock.System.now().toEpochMilliseconds(),
        "1",
        Result.Success()
    ),
    Message(
        "2",
        "ThreadId32",
        "Bikesh",
        "Hi there!",
        Clock.System.now().toEpochMilliseconds(),
        "1",
        Result.Success()
    )
)
