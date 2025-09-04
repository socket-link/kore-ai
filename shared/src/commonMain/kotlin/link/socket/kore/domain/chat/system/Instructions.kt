package link.socket.kore.domain.chat.system

data class Instructions(
    val prompt: String,
) {
    fun build(): String = prompt
}
