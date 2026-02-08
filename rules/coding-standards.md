# Coding Standards

## Java

- Java 21 — verwende Records für DTOs und Value Objects
- Keine `var` in Methodensignaturen — explizite Typen für Lesbarkeit
- Constructor Injection (kein @Autowired auf Feldern)
- Lombok nur für echte POJOs mit Zustand (z.B. AgentState), nicht für Records

## Spring Boot

- Konfiguration in application.yaml, nicht .properties
- Keine Secrets in application.yaml — verwende Umgebungsvariablen (`${...}`)
- @Component für Services, @Configuration für Beans, @RestController für Endpoints
- Kein @RequestMapping auf Klassenebene — explizite @GetMapping/@PostMapping

## Spring AI

- Structured Output über BeanOutputConverter — keine manuelle JSON-Parserei
- System Prompt und User Prompt immer trennen (nicht alles in einen Prompt)
- RAG-Kontext immer über RagRetriever holen, nie direkt im Controller

## Tests

- Jeder neue Endpoint braucht mindestens einen Integration-Test
- Test-Framework: JUnit 5 + Spring Boot Test
- Testklassen enden auf *Tests (nicht *Test)
