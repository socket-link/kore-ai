package link.socket.kore.domain.model.llm

import link.socket.kore.domain.ai.AIProvider
import link.socket.kore.domain.model.ai.configuration.aiConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import link.socket.kore.domain.ai.model.AIModel_Gemini

class AiConfigurationFactoryTest {

    @Test
    fun `default aiConfiguration uses Gemini 2_5 Flash with Google provider`() {
        val config = aiConfiguration()
        assertSame(AIModel_Gemini.Flash_2_5, config.llm)
        assertSame(AIProvider._Google, config.clientProvider)
        assertEquals("gemini-2.5-flash", config.llm.name)
    }

    @Test
    fun `aiConfiguration for Gemini model uses Google provider`() {
        val config = aiConfiguration(AIModel_Gemini.Pro_2_5)
        assertSame(AIModel_Gemini.Pro_2_5, config.llm)
        assertSame(AIProvider._Google, config.clientProvider)
    }
}
