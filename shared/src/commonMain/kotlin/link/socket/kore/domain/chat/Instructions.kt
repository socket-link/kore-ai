package link.socket.kore.domain.chat

data class Instructions(
    val prompt: String,
) {
    fun build(): String = prompt
}
