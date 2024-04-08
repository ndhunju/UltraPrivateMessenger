package com.ndhunju.ultraprivatemessenger.android.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.ndhunju.ultraprivatemessenger.common.NavItem
import kotlinx.coroutines.launch

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    onClickNavItem: ((NavItem) -> Unit)? = null,
    onClickLauncherIcon: (() -> Unit)? = null,
    content: (@Composable () -> Unit)
) {
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerItems(
                onClickNavItem = {
                    onClickNavItem?.invoke(it)
                    coroutineScope.launch { drawerState.close() }
                },
                onClickLauncherIcon = { onClickLauncherIcon?.invoke() }
            )

        }
    ) {
        content()
    }
}