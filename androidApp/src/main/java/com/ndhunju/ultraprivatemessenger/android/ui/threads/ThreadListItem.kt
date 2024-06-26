package com.ndhunju.ultraprivatemessenger.android.ui.threads

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.toColorInt
import com.ndhunju.ultraprivatemessenger.android.R
import com.ndhunju.ultraprivatemessenger.android.ui.common.SyncStatusIcon
import com.ndhunju.ultraprivatemessenger.android.ui.theme.LocalDimens
import com.ndhunju.ultraprivatemessenger.common.Result
import com.ndhunju.ultraprivatemessenger.data.sampleMessages
import com.ndhunju.ultraprivatemessenger.service.UserColorProvider
import com.ndhunju.ultraprivatemessenger.ui.threads.Message
import com.ndhunju.ultraprivatemessenger.ui.threads.getFormattedTime

@Preview(showBackground = true)
@Composable
fun MessageListItemPreview() {
    ThreadListItem(
        message = sampleMessages.first().copy(syncStatus = Result.Success()),
        onClick = {}
    )
}

@Composable
fun ThreadListItem(
    modifier: Modifier = Modifier,
    message: Message,
    onClick: ((Message) -> Unit)? = null
) {
    ConstraintLayout(modifier = modifier
        .clickable (
            // Click labels describe what happens when the user interacts with the composable.
            onClickLabel = stringResource(R.string.click_label_thread),
            onClick = { onClick?.invoke(message) }
        )
    ) {
        val (divider, profilePic, from, body, date, status) = createRefs()
        val itemVerticalPadding = LocalDimens.current.itemPaddingVertical
        val contentHorizontalPadding = LocalDimens.current.contentPaddingHorizontal

        //LogCompositions(tag = "MessageListItem", msg = "MessageListItem scope")

        HorizontalDivider(Modifier.constrainAs(divider) {
            top.linkTo(parent.top)
            width = Dimension.fillToConstraints
        })

        Image(
            modifier = Modifier.constrainAs(profilePic) {
                top.linkTo(parent.top, itemVerticalPadding)
                bottom.linkTo(parent.bottom, itemVerticalPadding)
                start.linkTo(parent.start, contentHorizontalPadding.div(2))
                width = Dimension.value(50.dp)
                height = Dimension.value(50.dp)
            },
            imageVector = Icons.Default.AccountCircle,
            contentDescription = stringResource(id = R.string.image_description_user),
            colorFilter = ColorFilter.tint(
                Color(UserColorProvider.provide(message.from.hashCode()).toColorInt())
            ),
        )

        Text(text = message.from, Modifier.constrainAs(from) {
            top.linkTo(parent.top, itemVerticalPadding)
            start.linkTo(profilePic.end, contentHorizontalPadding.div(2))
            width = Dimension.fillToConstraints
        },
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )


        Text(
            text = message.getFormattedTime(),
            Modifier.constrainAs(date) {
                linkTo(
                    start = from.end,
                    end = parent.end,
                    startMargin = 4.dp,
                    endMargin = contentHorizontalPadding,
                    bias = 1f,
                )
                top.linkTo(parent.top, itemVerticalPadding)
            },
            style = MaterialTheme.typography.labelMedium
        )

        SyncStatusIcon(
            modifier = Modifier
                .constrainAs(status) {
                    end.linkTo(parent.end, contentHorizontalPadding)
                    top.linkTo(date.bottom, 4.dp)
                    bottom.linkTo(parent.bottom, itemVerticalPadding)
                },
            syncStatus = message.syncStatus
        )

        Text(
            text = message.body, // + "\n" + message.toString(), // for debugging
            Modifier
                .constrainAs(body) {
                    top.linkTo(from.bottom)
                    linkTo(
                        start = profilePic.end,
                        end = status.start,
                        startMargin = contentHorizontalPadding.div(2),
                        endMargin = 8.dp,
                        bias = 0f,
                    )
                    bottom.linkTo(parent.bottom, itemVerticalPadding)
                    width = Dimension.fillToConstraints
                },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}