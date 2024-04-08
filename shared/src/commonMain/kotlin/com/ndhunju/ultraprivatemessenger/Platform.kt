package com.ndhunju.ultraprivatemessenger

import kotlinx.datetime.LocalDateTime

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// https://github.com/Kotlin/kotlinx-datetime/issues/211
expect fun LocalDateTime.format(format: String): String