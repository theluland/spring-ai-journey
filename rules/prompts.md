# Prompt-Regeln

## Sprache

- System Prompts: Deutsch (Zielgruppe ist deutschsprachig)
- Structured Output Schema: Englisch (JSON-Feldnamen)

## Struktur

- System Prompt definiert: Rolle, Regeln, Output-Format (via `converter.getFormat()`)
- User Prompt enthält: FALLTEXT + KONTEXT (RAG)
- Keine Beispiele (Few-Shot) im System Prompt — das Modell folgt dem Schema

## Regeln für neue Agents

- Jeder Agent definiert seinen eigenen System Prompt
- JSON-Schema kommt immer aus BeanOutputConverter, nie manuell
- `action`-Feld ist immer ein String-Enum — dokumentiere gültige Werte im Prompt
