# hello-spring — Claims Triage Agent

## Zweck

Lern- und Demo-Projekt, das zeigt, wie ein AI-Agent mit Spring Boot 4
und Spring AI gebaut wird. Der Agent entscheidet, ob eine Benutzereingabe
ein Versicherungsschaden ist, und stellt bei Bedarf Rückfragen.

## Architektur

### Schichten

1. **Controller-Schicht** — REST-Endpoints (`/agent/step`, `/agent/run`, `/rag`, `/hello`)
2. **Agent-Schicht** — `AgentOrchestrator` (Schleife/Harness) + `AgentState` (Zustand)
3. **AI-Schicht** — `ClarificationAgent` (LLM-Call mit RAG + Structured Output)
4. **RAG-Schicht** — `RagRetriever` + `VectorStoreConfig` (In-Memory SimpleVectorStore)

### Datenfluss

```
User -> Controller -> Orchestrator -> ClarificationAgent -> [RAG + LLM] -> AgentStep
                          ^                                                    |
                          +-------- Schleife (bis final_decision) -------------+
```

### Schlüssel-Klassen

| Klasse | Paket | Verantwortung |
|--------|-------|---------------|
| `AgentOrchestrator` | `.agent` | Agentic Loop: ruft Agent in Schleife, sammelt Antworten |
| `ClarificationAgent` | `.ai` | Einzelner LLM-Schritt: RAG-Kontext holen, Prompt bauen, Structured Output parsen |
| `AgentStep` | `.agent` | Structured Output Record: action, clarifyingQuestion, isClaim, reason, confidence |
| `AgentState` | `.agent` | Zustand über mehrere Steps: caseText + gesammelte Clarifications |
| `RagRetriever` | `.ai` | Similarity Search auf dem VectorStore |

### Technologie-Stack

- Java 21, Spring Boot 4.0.2, Spring AI 2.0.0-M2
- OpenAI (gpt-4.1-mini) als LLM
- OpenAI text-embedding-3-small für Embeddings
- SimpleVectorStore (In-Memory)
- Lombok, Maven

## Konventionen

- Sprache im Code: Englisch (Klassen, Methoden, Variablen)
- Sprache in Prompts/Dokumentation: Deutsch
- Package-Struktur: `com.roland.hellospring.{controller,agent,ai,dto}`
