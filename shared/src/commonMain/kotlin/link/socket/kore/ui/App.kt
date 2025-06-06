package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.model.app.Application
import link.socket.kore.model.conversation.Conversation
import link.socket.kore.model.conversation.ConversationId
import link.socket.kore.ui.conversation.ConversationScreen
import link.socket.kore.ui.home.HomeScreen
import link.socket.kore.ui.selection.AgentSelectionScreen
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography

/**
 * Enum class representing the different screens in the application.
 */
enum class Screen {
    HOME,
    SELECTION,
    CONVERSATION,
}

/**
 * Extension function to create a [KoreAgent] from an [AgentDefinition].
 *
 * @param agentDefinition The definition of the agent to be created.
 * @return A new instance of [KoreAgent].
 */
fun Application.createAgent(agentDefinition: AgentDefinition): KoreAgent =
    KoreAgent(
        CoroutineScope(Dispatchers.IO),
        agentDefinition,
        conversationRepository,
    )

/**
 * Composable function representing the main application UI.
 *
 * @param modifier The [Modifier] to be applied to the root composable.
 */
@Composable
fun App(modifier: Modifier = Modifier) {
    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        val scope = rememberCoroutineScope()
        val application = remember(scope) { Application(scope) }

        var selectedScreen by remember { mutableStateOf(Screen.HOME) }
        var selectedConversationId by remember { mutableStateOf<ConversationId?>(null) }

        val conversationListState = rememberLazyListState()

        val allConversations: State<Map<ConversationId, Conversation>> =
            application
                .conversationRepository
                .observeValues()
                .collectAsState()

        val selectedConversationValue: State<Conversation?> =
            selectedConversationId?.let { id ->
                application
                    .conversationRepository
                    .observeValue(id)
                    .collectAsState(null)
            } ?: mutableStateOf(null)

        val selectedConversation = remember(selectedConversationId, selectedConversationValue) {
            derivedStateOf {
                if (selectedConversationId != null) {
                    selectedConversationValue.value
                } else {
                    null
                }
            }
        }

        var isLoading by remember { mutableStateOf(false) }

        val onExecutingConversation = {
            isLoading = true
        }

        val onConversationFinishedExecuting = {
            isLoading = false

            if (selectedConversationId != null) {
                scope.launch {
                    val conversation = application.conversationRepository.getValue(selectedConversationId!!)
                    val lastIndex = conversation?.getChats()?.lastIndex ?: 0

                    delay(500)
                    conversationListState.animateScrollToItem(lastIndex)
                }
            }
        }

        val onConversationSelected: (ConversationId) -> Unit = { conversationId ->
            selectedConversationId = conversationId
            selectedScreen = Screen.CONVERSATION
        }

        val onNewConversation: (KoreAgent) -> Unit = { agent ->
            val newId = application.conversationRepository.createConversation(agent)
            onConversationSelected(newId)

            scope.launch {
                onExecutingConversation()
                application.conversationRepository.runConversation(selectedConversationId!!)
                onConversationFinishedExecuting()
            }
        }

        Box(
            modifier = modifier,
        ) {
            when (selectedScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        agentConversationsList = allConversations.value.values.toList(),
                        onCreateConversationSelected = {
                            selectedScreen = Screen.SELECTION
                        },
                        onConversationSelected = { conversation ->
                            onConversationSelected(conversation.id)
                        },
                    )
                }

                Screen.SELECTION -> {
                    AgentSelectionScreen(
                        modifier = Modifier.fillMaxSize(),
                        onSubmit = { agentDefinition ->
                            val agent = application.createAgent(agentDefinition)
                            onNewConversation(agent)
                        },
                        onBackClicked = {
                            selectedScreen = Screen.HOME
                        },
                    )
                }

                Screen.CONVERSATION -> {
                    selectedConversation.value?.let {
                        ConversationScreen(
                            modifier = Modifier.fillMaxSize(),
                            listState = conversationListState,
                            conversation = it,
                            isLoading = isLoading,
                            onChatSent = { input ->
                                selectedConversationId?.let { id ->
                                    scope.launch {
                                        onExecutingConversation()
                                        application.conversationRepository.addUserChat(id, input)
                                        onConversationFinishedExecuting()
                                    }
                                }
                            },
                            onBackClicked = {
                                selectedConversationId = null
                                selectedScreen = Screen.HOME
                            },
                        )
                    }
                }
            }
        }
    }
}
