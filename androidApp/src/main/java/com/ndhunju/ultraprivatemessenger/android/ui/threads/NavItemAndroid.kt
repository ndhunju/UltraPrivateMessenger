package com.ndhunju.ultraprivatemessenger.android.ui.threads

import com.ndhunju.ultraprivatemessenger.android.R
import com.ndhunju.ultraprivatemessenger.common.NavItem

/**
 * Date class that represents items in side navigation drawer
 */
sealed class NavItemAndroid(
    val drawableRes: Int,
    val contentDescriptionStrRes: Int,
    val labelStrRes: Int,
    val navItem: NavItem,
) {
    data object AccountNavItem: NavItemAndroid(
        R.drawable.baseline_account_circle_24,
        R.string.nav_item_account,
        R.string.image_description_account,
        NavItem.Account
    )

    data object ContactNavItem: NavItemAndroid(
        R.drawable.baseline_contacts_24,
        R.string.nav_item_contacts,
        R.string.image_description_contact,
        NavItem.Contacts
    )

}