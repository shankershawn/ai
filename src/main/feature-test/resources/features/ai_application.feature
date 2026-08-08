Feature: AI Application
  As a system operator
  I want the AI application to start, call a chat model, and persist embeddings
  So that chat responses are available for vector similarity search

  # ---------------------------------------------------------------------------
  # Application bootstrap
  # ---------------------------------------------------------------------------

  Scenario: Application context starts successfully
    Given the application is configured with OpenAI and pgvector settings
    When the Spring Boot application starts
    Then the application context should load successfully
    And the OpenAI chat model bean should be available
    And the vector store bean should be available
    And the virtual thread task executor bean should be available

  # ---------------------------------------------------------------------------
  # OpenAI-compatible chat
  # ---------------------------------------------------------------------------

  Scenario: Chat model answers a prompt via the configured OpenAI endpoint
    Given the OpenAI base URL is configured to "http://localhost:1234"
    And the OpenAI API key is configured
    And the OpenAI chat model is "google/gemma-4-e4b"
    When a chat prompt "What is survivorship bias?" is sent to the OpenAI chat model
    Then a non-empty text response should be returned

  Scenario: Chat model uses the configured local LMS endpoint
    Given the OpenAI base URL is "http://localhost:1234"
    When a chat request is made
    Then the request should be sent to the local LMS endpoint
    And the response should be treated as an OpenAI-compatible chat completion

  # ---------------------------------------------------------------------------
  # Startup batch chat + vector store ingestion
  # ---------------------------------------------------------------------------

  Scenario: Chat responses are stored in the vector store
    Given the OpenAI chat model returns a text response
    When the response is added to the vector store as a Document
    Then the document should be persisted in the pgvector store
    And the embedding should use dimension 768

  # ---------------------------------------------------------------------------
  # Model and retry configuration
  # ---------------------------------------------------------------------------

  Scenario: OpenAI chat and embedding models are configured
    Given the application configuration is loaded
    Then the OpenAI chat model should be "google/gemma-4-e4b"
    And the OpenAI embedding model should be "text-embedding-nomic-embed-text-v1.5"

  Scenario: Spring AI retry settings are configured
    Given the application configuration is loaded
    Then the retry max attempts should be 2
    And client error retries should be disabled
    And the retry backoff initial interval should be "1s"
    And the retry backoff multiplier should be 2
    And the retry backoff max interval should be "10s"

  # ---------------------------------------------------------------------------
  # pgvector configuration
  # ---------------------------------------------------------------------------

  Scenario: Vector store is configured with HNSW and cosine distance
    Given the application configuration is loaded
    Then the pgvector index type should be "HNSW"
    And the pgvector distance type should be "COSINE_DISTANCE"
    And the embedding dimension should be 768
    And schema initialization should be enabled
    And the content size limit should be 100000

  # ---------------------------------------------------------------------------
  # Datasource configuration (embedded H2 in feature tests)
  # ---------------------------------------------------------------------------

  Scenario: Application connects using H2 datasource settings
    Given the Spring datasource URL is configured for H2
    And datasource credentials are provided via environment variables or defaults
    When the application starts
    Then a connection to the configured H2 database should be established

  Scenario Outline: Datasource can be overridden via environment variables
    Given environment variable "<env_var>" is set to "<value>"
    When the application configuration is resolved
    Then the datasource property "<property>" should equal "<value>"

    Examples:
      | env_var                     | property                        | value                                      |
      | SPRING_DATASOURCE_URL       | spring.datasource.url           | jdbc:h2:mem:override;MODE=PostgreSQL       |
      | SPRING_DATASOURCE_USERNAME  | spring.datasource.username      | testuser                                   |
      | SPRING_DATASOURCE_PASSWORD  | spring.datasource.password      | testpass                                   |

  # ---------------------------------------------------------------------------
  # Health / actuator (dependency present)
  # ---------------------------------------------------------------------------

  Scenario: Actuator endpoints are available
    Given the application has started with Actuator enabled
    When the health endpoint is requested
    Then a successful health response should be returned