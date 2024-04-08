package com.ndhunju.ultraprivatemessenger

import kotlinx.datetime.LocalDateTime
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun LocalDateTime.format(format: String): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.dateFormat = format
    return dateFormatter.stringFromDate(
        toNSDate(NSCalendar.currentCalendar)
            ?: throw IllegalStateException("Could not convert kotlin date to NSDate $this")
    )
}