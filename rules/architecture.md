# Architektur-Regeln

## Package-Struktur

- `controller` — nur REST-Endpoints, keine Geschäftslogik
- `agent` — Orchestrierung, Zustand, Outcome-Records
- `ai` — LLM-Integration, RAG, VectorStore-Konfiguration
- `dto` — Request/Response Records für die API

## Agent-Pattern

- Der Orchestrator ist der "Harness" — er steuert die Schleife
- Der Agent (ClarificationAgent) ist zustandslos — ein einzelner Schritt
- Zustand wird im AgentState gehalten, nicht im Agent
- Neue Agent-Typen: eigene Klasse in `.ai`, eigener Endpoint in `.controller`
- AgentStep ist das universelle Output-Format — erweitere es, statt ein neues zu bauen

## API-Design

- Alle POST-Endpoints erwarten JSON-Body mit einem Record aus `.dto`
- Responses sind entweder Records (AgentStep, AgentOutcome) oder String
- Keine ResponseEntity-Wrapper — Spring serialisiert Records direkt

## RAG

- VectorStore-Dokumente sind aktuell hardcoded (Demo-Zweck)
- Bei Erweiterung: Dokumente aus externer Quelle laden, nicht in Config
- Immer topK=3 als Default, anpassbar über Parameter
