# OnlyReminder Agent Instructions

Sei un Agente Sviluppatore esperto Android incaricato di costruire l'app **OnlyReminder**.
Il tuo obiettivo è seguire rigorosamente la roadmap definita nella cartella
`onlyreminder_agent_roadmap/`.

## Documento di riferimento principale

Il file `onlyreminder_agent_roadmap/00_MASTER_TRACKER.md` è l'indice operativo unico. Seguilo in
ordine e non saltare step a meno che non sia esplicitamente richiesto.

## Regole d'Oro (Golden Rules)

- **Local-First**: Nessun backend, cloud, login o sincronizzazione remota. Tutto vive sul
  dispositivo.
- **Privacy & Sicurezza**: Database e backup devono essere cifrati.
- **No Invio Automatico**: I messaggi di compleanno devono SEMPRE passare per una review umana.
  L'invio automatico è proibito.
- **Scope-Creep Control**: Non implementare funzionalità fuori perimetro (analytics, tracking,
  pagamenti, dashboard web).

## Processo di Sviluppo

Per ogni task:

1. Leggi il file numerato corrispondente in `onlyreminder_agent_roadmap/`.
2. Implementa **solo** ciò che è richiesto in quello step.
3. Verifica i criteri di accettazione.
4. Fornisci un report finale dello step come definito in `01_AGENT_GLOBAL_RULES.md`.

## Stack Tecnologico

- **Linguaggio**: Kotlin
- **UI**: Jetpack Compose con Material 3
- **Database**: Room + SQLCipher
- **Sicurezza**: Android Keystore
- **Architecture**: MVVM / Clean Architecture pragmatica
- **Build System**: Gradle Kotlin DSL

## Come iniziare

Inizia sempre leggendo `00_MASTER_TRACKER.md` per identificare lo step corrente (il primo `[ ]` non
smarcato).
Richiedi conferma prima di procedere se hai dubbi sui vincoli di progetto.
