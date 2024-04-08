package com.ndhunju.ultraprivatemessenger.android.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ndhunju.ultraprivatemessenger.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavAppBar(
    title: State<String>? = mutableStateOf(""),
    showUpIcon: State<Boolean>? = mutableStateOf(false),
    showSearchTextField: State<Boolean>? = mutableStateOf(false),
    onClickSearchIcon: (() -> Unit)? = null,
    onSearchTextChanged: ((String) -> Unit)? = null,
    onClickMenuOrUpIcon: (() -> Unit)? = null,
) {
    Column {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top),
            navigationIcon = {
                if (showUpIcon?.value == true) {
                    IconButton(onClick = onClickMenuOrUpIcon ?: {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.image_description_go_back)
                        )
                    }
                } else {
                    IconButton(onClick = onClickMenuOrUpIcon ?: {}) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(
                                androidx.compose.ui.R.string.navigation_menu
                            )
                        )
                    }
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showSearchTextField?.value == true) {
                        SearchTextField(onSearchTextChanged = onSearchTextChanged)
                    } else {
                        Text(text = title?.value ?: "")
                    }
                }
            },
            actions = {
                IconButton(onClick = onClickSearchIcon ?: {}) {
                    if (showSearchTextField?.value != true) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = stringResource(id = R.string.image_description_search)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(id = R.string.image_description_go_back)
                        )
                    }
                }
            }
        )
        CriticalMessageBar()
    }
}