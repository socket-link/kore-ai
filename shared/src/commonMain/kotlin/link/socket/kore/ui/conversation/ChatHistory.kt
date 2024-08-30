package link.socket.kore.ui.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.core.Role
import link.socket.kore.model.chat.Chat

/**
 * A composable function that displays the chat history in a lazy column.
 *
 * @param modifier Modifier to be applied to the LazyColumn.
 * @param listState State of the lazy list to control scrolling.
 * @param messages List of chat messages to be displayed.
 * @param isLoading Boolean flag to indicate if the chat is loading.
 * @param displaySnackbar Function to display a snackbar with a given message.
 */
@Composable
fun ChatHistory(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    messages: List<Chat>,
    isLoading: Boolean,
    displaySnackbar: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 16.dp,
            end = 8.dp,
            bottom = 72.dp,
        ),
    ) {
        itemsIndexed(messages) { index, message ->
            ChatMessage(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                message = message,
                displaySnackbar = displaySnackbar,
                showRegenerate = (index == messages.size - 1) && message.role == Role.Assistant,
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}