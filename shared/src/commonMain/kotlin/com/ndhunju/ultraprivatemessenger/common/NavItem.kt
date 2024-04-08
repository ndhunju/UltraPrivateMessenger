package com.ndhunju.ultraprivatemessenger.common

/**
* Date class that represents items in side navigation drawer
*/
enum class NavItem(
    val selected: Boolean = false
) {
    Account(false),
    Contacts(false)
}