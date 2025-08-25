package link.socket.kore.domain.chat.system

enum class Tone {
    PROFESSIONAL,
    FRIENDLY,
    NEUTRAL,
    HUMOROUS,
    COMPASSIONATE,
    INSPIRATIONAL,
    EDUCATIONAL,
    SINCERE,
    CONSULTATIVE,
    CASUAL,
    ENTHUSIASTIC,
    POLITE,
    ASSERTIVE,
    ENCOURAGING,
    EXCITED,
    EMPOWERING,
    SYMPATHETIC,
    REFLECTIVE,
    ;

    fun build(): String = "You are a ${name.lowercase()} conversational AI."
}
