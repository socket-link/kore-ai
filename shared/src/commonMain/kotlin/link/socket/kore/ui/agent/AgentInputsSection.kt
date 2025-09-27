package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.ui.theme.themeTypography

@Composable
fun AgentInputsSection(
    requiredInputs: List<AgentInput>,
    optionalInputs: List<AgentInput>,
    onAgentSubmission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val inputValues: MutableState<MutableMap<String, AgentInput>> = remember {
        mutableStateOf(mutableMapOf())
    }

    val inputComposable: @Composable (AgentInput) -> Unit = @Composable { input ->
        when (input) {
            is AgentInput.EnumArgs -> {
                EnumInput(
                    modifier = Modifier.fillMaxWidth(),
                    input = input,
                    possibleValues = input.possibleValues,
                    onValueChanged = { value ->
                        inputValues.value[input.key] = value
                    },
                )
            }
            is AgentInput.StringArg -> {
                StringInput(
                    modifier = Modifier.fillMaxWidth(),
                    input = input,
                    onValueChanged = { value ->
                        inputValues.value[input.key] = value
                    },
                )
            }
            is AgentInput.ListArg -> {
                ListInput(
                    modifier = Modifier.fillMaxWidth(),
                    input = input,
                    onValueChanged = { value ->
                        inputValues.value[input.key] = value
                    },
                )
            }
        }
    }

    Column(
        modifier = modifier.wrapContentHeight(),
    ) {
        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            items(requiredInputs) { input ->
                inputComposable(input)
            }

            items(optionalInputs) { input ->
                inputComposable(input)
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAgentSubmission()
                    },
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        style = themeTypography().button,
                        text = "Submit",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
