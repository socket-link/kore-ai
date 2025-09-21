package link.socket.kore.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import link.socket.kore.data.ConversationRepository
import link.socket.kore.domain.agent.KoreAgent
import link.socket.kore.domain.agent.KoreAgentFactory
import link.socket.kore.domain.agent.bundled.AgentDefinition
import link.socket.kore.domain.ai.DEFAULT_AI_CONFIGURATION
import link.socket.kore.domain.chat.Conversation
import link.socket.kore.domain.chat.ConversationId
import link.socket.kore.domain.config.AI_Configuration
import link.socket.kore.domain.koog.KoogAgentFactory
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

@Composable
fun App(
    modifier: Modifier = Modifier,
) {
    val selectedConfig = remember< MutableState<AI_Configuration>> {
        mutableStateOf(DEFAULT_AI_CONFIGURATION)
    }

    val scope = rememberCoroutineScope()
    val conversationRepository = remember { ConversationRepository(scope) }

    val agentFactory = remember {
        KoreAgentFactory(
            conversationRepository = conversationRepository,
            coroutineScope = scope,
        )
    }

    val koogAgentFactory = remember {
        KoogAgentFactory()
    }

    val allConversations: State<Map<ConversationId, Conversation>> =
        conversationRepository
            .observeValues()
            .collectAsState()

    MaterialTheme(
        colors = themeColors(),
        typography = themeTypography(),
        shapes = themeShapes(),
    ) {
        var selectedScreen by remember { mutableStateOf(Screen.HOME) }
        var selectedConversationId by remember { mutableStateOf<ConversationId?>(null) }

        val conversationListState = rememberLazyListState()

        val selectedConversationValue: State<Conversation?> =
            selectedConversationId?.let { id ->
                conversationRepository
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
                    val conversation = conversationRepository.getValue(selectedConversationId!!)
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
            val newId = conversationRepository.createConversation(agent)
            onConversationSelected(newId)

            scope.launch {
                onExecutingConversation()
                conversationRepository.runConversation(
                    config = selectedConfig.value,
                    conversationId = selectedConversationId!!,
                )
                onConversationFinishedExecuting()
            }

            runBlocking {
                val koogAgent = koogAgentFactory.createKoogAgent(
                    aiConfiguration = selectedConfig.value,
                    agent = agent,
                )
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
                    var partiallySelectedAgent by remember {
                        mutableStateOf<AgentDefinition?>(null)
                    }

                    AgentCreationScreen(
                        modifier = Modifier.fillMaxSize(),
                        selectedAgentDefinition = partiallySelectedAgent,
                        setSelectedAgentDefinitionChanged = { agent ->
                            partiallySelectedAgent = agent
                        },
                        onCreateAgent = { agentDefinition ->
                            val agent = agentFactory.buildAgent(
                                config = selectedConfig.value,
                                definition = agentDefinition,
                                scope = scope,
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
                                        conversationRepository.addUserChat(
                                            config = selectedConfig.value,
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
