package com.ndhunju.ultraprivatemessenger.android.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ndhunju.ultraprivatemessenger.android.R
import com.ndhunju.ultraprivatemessenger.android.ui.threads.NavItemAndroid
import com.ndhunju.ultraprivatemessenger.common.NavItem

val defaultNavigationItems = listOf(
    NavItemAndroid.AccountNavItem,
    NavItemAndroid.ContactNavItem
)

@Composable
fun NavigationDrawerItems(
    navigationItems: List<NavItemAndroid> = defaultNavigationItems,
    onClickNavItem: (NavItem) -> Unit,
    onClickLauncherIcon: () -> Unit,
) {
    ModalDrawerSheet {
        // Show big app icon
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(112.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { onClickLauncherIcon() }
        )

        HorizontalDivider()

        // Show each items in navigationItems
        navigationItems.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Row {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = item.drawableRes),
                                contentDescription = stringResource(item.contentDescriptionStrRes)
                            )
                        }
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = stringResource(id = item.labelStrRes)
                        )
                    }
                },
                selected = item.navItem.selected,
                onClick = { onClickNavItem(item.navItem) }
            )
            HorizontalDivider()
        }
    }
}