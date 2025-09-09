package link.socket.kore.domain.capability

import link.socket.kore.domain.tool.FunctionProvider

interface Capability {

    val tag: String
        get() = "Capability"

    val impl: Pair<String, FunctionProvider>
}
