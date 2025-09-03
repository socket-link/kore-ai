package link.socket.kore.domain.model.llm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AiConfigurationFactoryTest {

    @Test
    fun `default aiConfiguration uses Gemini 2_5 Flash with Google provider`() {
        val config = aiConfiguration()
        assertSame(LLM_Gemini._2_5_Flash, config.llm)
        assertSame(AI_Provider.Google, config.clientProvider)
        assertEquals("gemini-2.5-flash", config.llm.name)
    }

    @Test
    fun `aiConfiguration for Gemini model uses Google provider`() {
        val config = aiConfiguration(LLM_Gemini._2_5_Pro)
        assertSame(LLM_Gemini._2_5_Pro, config.llm)
        assertSame(AI_Provider.Google, config.clientProvider)
    }
}
