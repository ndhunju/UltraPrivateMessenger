package com.ndhunju.ultraprivatemessenger

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()


actual fun LocalDateTime.format(format: String): String {
    return DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDateTime())
}