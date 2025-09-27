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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import link.socket.kore.data.ConversationRepository
import link.socket.kore.domain.agent.KoreAgent
import link.socket.kore.domain.agent.KoreAgentFactory
import link.socket.kore.domain.ai.configuration.AIConfigurationFactory
import link.socket.kore.domain.chat.Conversation
import link.socket.kore.domain.chat.ConversationId
import link.socket.kore.domain.koog.KoogAgentFactory
import link.socket.kore.ui.agent.AgentCreationScreen
import link.socket.kore.ui.conversation.ConversationScreen
import link.socket.kore.ui.home.HomeScreen
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography

@Composable
fun App(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val conversationRepository = remember { ConversationRepository(scope) }
    val aiConfigurationFactory = remember { AIConfigurationFactory() }
    val koogAgentFactory = remember { KoogAgentFactory() }

    val selectedConfig = remember {
        mutableStateOf(aiConfigurationFactory.getDefaultConfiguration())
    }

    val agentFactory = remember {
        KoreAgentFactory(conversationRepository, scope)
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
        val selectedScreen: MutableState<Screen> = remember { mutableStateOf(Screen.HOME) }
        val selectedConversationId: MutableState<ConversationId?> = remember { mutableStateOf(null) }
        val conversationListState = rememberLazyListState()

        val selectedConversationValue: State<Conversation?> =
            selectedConversationId.value?.let { id ->
                conversationRepository
                    .observeValue(id)
                    .collectAsState(null)
            } ?: mutableStateOf(null)

        val selectedConversation: State<Conversation?> = remember(selectedConversationId, selectedConversationValue) {
            derivedStateOf {
                if (selectedConversationId.value != null) {
                    selectedConversationValue.value
                } else {
                    null
                }
            }
        }

        val isLoading: MutableState<Boolean> = remember { mutableStateOf(false) }

        val onExecutingConversation = {
            isLoading.value = true
        }

        val onConversationFinishedExecuting: () -> Unit = {
            isLoading.value = false

            val conversationId = selectedConversationId.value
            if (conversationId != null) {
                scope.launch {
                    val conversation = conversationRepository.getValue(conversationId)
                    val lastIndex = conversation?.getChats()?.lastIndex ?: 0

                    delay(500)
                    conversationListState.animateScrollToItem(lastIndex)
                }
            }
        }

        val onConversationSelected: (ConversationId) -> Unit = { conversationId ->
            selectedConversationId.value = conversationId
            selectedScreen.value = Screen.CONVERSATION
        }

        val onNewConversation: (KoreAgent) -> Unit = { agent ->
            val newId = conversationRepository.createConversation(agent)
            onConversationSelected(newId)

            scope.launch {
                onExecutingConversation()

                conversationRepository.runConversation(
                    config = selectedConfig.value,
                    conversationId = selectedConversationId.value!!,
                )

                val koogAgent = koogAgentFactory.createKoogAgent(
                    aiConfiguration = selectedConfig.value,
                    agent = agent,
                )

                koogAgent.run("")

                onConversationFinishedExecuting()
            }
        }

        Box(
            modifier = modifier,
        ) {
            when (selectedScreen.value) {
                Screen.HOME -> {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        agentConversationsList = allConversations.value.values.toList(),
                        onCreateConversationSelected = {
                            selectedScreen.value = Screen.AGENT_CREATION
                        },
                        onConversationSelected = { conversation ->
                            onConversationSelected(conversation.id)
                        },
                    )
                }

                Screen.AGENT_CREATION -> {
                    AgentCreationScreen(
                        modifier = Modifier.fillMaxSize(),
                        selectedConfig = selectedConfig.value,
                        aiConfigurationFactory = aiConfigurationFactory,
                        agentFactory = agentFactory,
                        onAgentCreated = { agent ->
                            selectedScreen.value = Screen.CONVERSATION
                            onNewConversation(agent)
                        },
                        onBackClicked = {
                            selectedScreen.value = Screen.HOME
                        },
                    )
                }

                Screen.CONVERSATION -> {
                    selectedConversation.value?.let { conversation ->
                        ConversationScreen(
                            modifier = Modifier.fillMaxSize(),
                            listState = conversationListState,
                            conversation = conversation,
                            isLoading = isLoading.value,
                            onChatSent = { input ->
                                selectedConversationId.value?.let { id ->
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
                                selectedConversationId.value = null
                                selectedScreen.value = Screen.HOME
                            },
                        )
                    }
                }
            }
        }
    }
}
