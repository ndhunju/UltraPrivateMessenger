package com.ndhunju.ultraprivatemessenger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform