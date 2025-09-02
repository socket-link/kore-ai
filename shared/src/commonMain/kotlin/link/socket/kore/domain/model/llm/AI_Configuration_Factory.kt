@file:Suppress("unused")

package link.socket.kore.domain.model.llm

import link.socket.kore.domain.model.tool.Tool_Claude
import link.socket.kore.domain.model.tool.Tool_Gemini
import link.socket.kore.domain.model.tool.Tool_OpenAI

/**
 * Developer-friendly helpers to configure the LLM provider + model without dealing with generics.
 *
 * Examples:
 * - Default (Gemini Flash):
 *   val config = aiConfiguration()
 *
 * - Explicit Gemini model:
 *   val config = aiConfiguration(LLM_Gemini._2_5_Pro)
 *
 * - Explicit Claude model:
 *   val config = aiConfiguration(LLM_Claude.Sonnet_3_7)
 *
 * - Explicit OpenAI model (when available):
 *   val config = aiConfiguration(LLM_ChatGPT /* model TBD */)
 */

/**
 * Default configuration.
 * Currently defaults to Google Gemini 2.5 Flash.
 */
fun aiConfiguration(): AI_Configuration<Tool_Gemini, LLM_Gemini> =
    aiConfiguration(LLM_Gemini._2_5_Flash)

/** Create a configuration for Google Gemini models. */
fun aiConfiguration(model: LLM_Gemini): AI_Configuration<Tool_Gemini, LLM_Gemini> =
    AI_Configuration(
        llm = model,
        clientProvider = AI_ClientProvider.Google,
    )

/** Create a configuration for Anthropic Claude models. */
fun aiConfiguration(model: LLM_Claude): AI_Configuration<Tool_Claude, LLM_Claude> =
    AI_Configuration(
        llm = model,
        clientProvider = AI_ClientProvider.Anthropic,
    )

/**
 * Create a configuration for OpenAI ChatGPT models.
 * Note: concrete ChatGPT models are not defined yet; this overload is provided for API completeness.
 */
fun aiConfiguration(model: LLM_ChatGPT): AI_Configuration<Tool_OpenAI, LLM_ChatGPT> =
    AI_Configuration(
        llm = model,
        clientProvider = AI_ClientProvider.OpenAI,
    )
