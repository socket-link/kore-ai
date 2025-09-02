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
import link.socket.kore.domain.agent.AgentProvider
import link.socket.kore.domain.agent.KoreAgent
import link.socket.kore.domain.agent.definition.AgentDefinition
import link.socket.kore.domain.app.Application
import link.socket.kore.domain.conversation.Conversation
import link.socket.kore.domain.conversation.ConversationId
import link.socket.kore.domain.model.llm.AI_Configuration
import link.socket.kore.ui.agent.AgentCreationScreen
import link.socket.kore.ui.conversation.ConversationScreen
import link.socket.kore.ui.home.HomeScreen
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography

enum class Screen {
    HOME,
    AGENT_CREATION,
    CONVERSATION,
}

fun Application.createAgent(
    config: AI_Configuration<*, *>,
    agentDefinition: AgentDefinition,
): KoreAgent = AgentProvider.createAgent(
    config = config,
    scope = CoroutineScope(Dispatchers.IO),
    definition = agentDefinition,
    conversationRepository = conversationRepository,
)

@Composable
fun App(
    config: AI_Configuration<*, *>,
    modifier: Modifier = Modifier,
) {
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
                application.conversationRepository.runConversation(
                    config = config,
                    conversationId = selectedConversationId!!,
                )
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
                            selectedScreen = Screen.AGENT_CREATION
                        },
                        onConversationSelected = { conversation ->
                            onConversationSelected(conversation.id)
                        },
                    )
                }

                Screen.AGENT_CREATION -> {
                    AgentCreationScreen(
                        modifier = Modifier.fillMaxSize(),
                        onSubmit = { agentDefinition ->
                            val agent = application.createAgent(
                                config = config,
                                agentDefinition = agentDefinition,
                            )
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
                                        application.conversationRepository.addUserChat(
                                            config = config,
                                            conversationId = id,
                                            input = input,
                                        )
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
