package link.socket.kore.domain.model.llm

import link.socket.kore.domain.model.ai.AI
import link.socket.kore.domain.model.ai.configuration.aiConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AiConfigurationFactoryTest {

    @Test
    fun `default aiConfiguration uses Gemini 2_5 Flash with Google provider`() {
        val config = aiConfiguration()
        assertSame(LLM_Gemini.Flash_2_5, config.llm)
        assertSame(AI._Google, config.clientProvider)
        assertEquals("gemini-2.5-flash", config.llm.name)
    }

    @Test
    fun `aiConfiguration for Gemini model uses Google provider`() {
        val config = aiConfiguration(LLM_Gemini.Pro_2_5)
        assertSame(LLM_Gemini.Pro_2_5, config.llm)
        assertSame(AI._Google, config.clientProvider)
    }
}
