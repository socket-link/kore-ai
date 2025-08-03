# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build & Test
```bash
# Build the project
./gradlew build

# Run desktop application
./gradlew run

# Package desktop application for distribution
./gradlew package

# Run Android app
./gradlew installDebug

# Run all tests
./gradlew allTests

# Run JVM-specific tests
./gradlew jvmTest

# Run iOS tests (simulator)
./gradlew iosSimulatorArm64Test
./gradlew iosX64Test
```

### Code Quality
```bash
# Format code with ktlint
./gradlew ktlintFormat

# Check code formatting
./gradlew ktlintCheck

# Run Android lint
./gradlew lint
```

### Documentation
```bash
# Generate API documentation
./gradlew dokkaHtml

# Generate Javadocs
./gradlew generateJavadocs
```

## High-Level Architecture

KoreAI is a Kotlin Multiplatform library for creating AI Agents and Assistants that can interact with OpenAI's models. The architecture follows a layered approach:

### Core Model Layer (`model/`)
- **Agents**: Specialized AI chatbots defined by `AgentDefinition` classes with domain-specific prompts
  - `LLMAgent`: Interface for OpenAI interaction with function calling support
  - `KoreAgent`: Concrete implementation managing conversations and tool execution
  - **Bundled Agents**: Pre-built agents in `bundled/` (general, code, prompt, reasoning categories)

- **Conversations**: Chat session management
  - `Conversation`: Container for agent + chat history
  - `ConversationHistory`: Manages chat message sequences
  - `Chat`: Message types (Text, CSV, System)

- **Capabilities**: Modular tool system
  - `AgentCapability`: Agent spawning and delegation
  - `IOCapability`: File operations and CSV parsing
  - `FunctionProvider`: Defines available tools for agents

### UI Layer (`ui/`)
- **Compose Multiplatform** UI with three main screens:
  - `HomeScreen`: List existing conversations or create new ones
  - `AgentSelectionScreen`: Choose and configure agent types
  - `ConversationScreen`: Active chat interface with agent

- **State Management**: Uses `ConversationRepository` for persistent chat storage

### Data Layer (`data/`)
- **Repository Pattern**: Generic `Repository<K,V>` with observable state
- **ConversationRepository**: Specialized for managing agent conversations

### Platform Targets
- **Android**: Native Android app in `androidApp/`
- **Desktop/JVM**: Desktop app in `desktopApp/`
- **iOS**: iOS app in `iosApp/` (Xcode project)

## Key Patterns

### Agent System
Agents are defined by extending `AgentDefinition` with:
- `name`: Display name
- `prompt`: System prompt defining behavior
- `neededInputs`/`optionalInputs`: Configuration parameters
- Tone and seriousness settings for response style

### Function Calling
Agents can be equipped with tools via `FunctionProvider`:
- `FunctionDefinition.StringReturn`: Text-based functions
- `FunctionDefinition.CSVReturn`: Structured data functions
- Tools are automatically exposed to OpenAI for function calling

### Multiplatform Structure
- `commonMain`: Shared business logic and UI
- `androidMain`: Android-specific implementations
- `jvmMain`: Desktop-specific implementations  
- `iosMain`: iOS-specific implementations

## Important Dependencies
- **OpenAI Kotlin**: `openai-kotlin` for LLM integration
- **Turtle**: Shell script execution capabilities
- **Compose Multiplatform**: UI framework
- **Ktor**: HTTP client for different platforms

## Configuration
- OpenAI API credentials configured via `local.properties`
- ktlint formatting uses IntelliJ IDEA code style
- Gradle 8.2.1 with Kotlin Multiplatform plugin