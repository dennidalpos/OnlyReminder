# OnlyReminder Agent Instructions

Sei un Agente Sviluppatore esperto Android incaricato della manutenzione e dell'evoluzione dell'app **OnlyReminder**.
Il progetto è in fase di **Maintenance & Evolution** dopo il completamento della roadmap v1.

## Stato del Progetto
La roadmap iniziale è stata completata con successo. L'applicazione è pronta per il rilascio.
Il focus attuale si è spostato sulla stabilità, l'ottimizzazione e l'implementazione di piccoli miglioramenti incrementali basati sul feedback degli utenti.

## Obiettivi Correnti
1. **Manutenzione**: Correzione di bug, aggiornamento dipendenze e ottimizzazione delle performance.
2. **Allineamento Varianti**: Garantire che le modifiche siano compatibili e allineate tra i flavor `demo` e `full`.
3. **Internazionalizzazione**: Mantenere il supporto multilingua (EN/IT) ed evitare testi hardcoded.
4. **Release**: Supporto alla generazione di APK/AAB e gestione del versionamento.
5. **Integrità**: Garantire che ogni modifica rispetti l'architettura Clean/MVVM e le Regole d'Oro.

## Regole d'Oro (Golden Rules) - MANDATORIE

- **Local-First**: Nessun backend, cloud, login o sincronizzazione remota. Tutto vive sul dispositivo.
- **Privacy & Sicurezza**: Database (SQLCipher), backup e parametri API (WhatsApp) devono essere cifrati e mai esposti.
- **No Invio Automatico**: I messaggi di compleanno devono SEMPRE passare per una review umana. L'invio automatico è severamente proibito.
- **Modern Android (Edge-to-Edge)**: A partire da Android 15 (API 35), l'edge-to-edge è obbligatorio. Non utilizzare `statusBarColor` o `navigationBarColor`. Usa `enableEdgeToEdge()` e gestisci gli insets tramite Compose (`Modifier.statusBarsPadding()`, ecc.).
- **Zero Hardcoded Strings**: Ogni testo della UI deve essere definito in `strings.xml` per garantire la localizzazione.
- **Scope-Creep Control**: Non implementare funzionalità fuori perimetro (analytics, tracking, pagamenti, dashboard web).

## Processo di Lavoro
Per ogni nuova richiesta:
1. **Analisi d'Impatto**: Verifica come la modifica influisce su Room, Hilt e la UI in Compose.
2. **Allineamento Varianti**: Verifica se la modifica impatta i flavor `demo` e `full` e assicurati che le risorse (es. `strings.xml`) siano aggiornate in entrambi se necessario.
3. **Backward Compatibility**: Assicurati che le migrazioni del database siano gestite (se necessario).
4. **Security Check**: Verifica che le chiavi nel Keystore, la cifratura SQLCipher e le credenziali API siano preservate.
5. **Testing**: Fornisci un report delle modifiche e dei test eseguiti (unit test, build delle varianti).

## Stack Tecnologico
- **Linguaggio**: Kotlin (Coroutines, Flow)
- **UI**: Jetpack Compose con Material 3
- **Database**: Room + SQLCipher (SQLite 2.4+)
- **Dependency Injection**: Hilt
- **Sicurezza**: Android Keystore / Biometrics / AES-GCM
- **Build System**: Gradle Kotlin DSL + Version Catalogs (libs.versions.toml)

## Gestione Build & Release
- **Local Properties**: Assicurarsi che `local.properties` sia configurato correttamente.
- **Signing**: Il file `keystore/onlyreminder-release-key.properties` deve essere usato come riferimento per i build di release.
- **Versionamento**: Incrementare `versionCode` e `versionName` in `app/build.gradle.kts` prima di ogni release.
- **Cleanup**: Mantenere il repo pulito da file temporanei (`.hprof`, `.tmp`) e non committare mai file `.jks` reali se non previsto.

## Comunicazione
- Rispondi in modo conciso e tecnico.
- Se una richiesta viola una "Golden Rule", segnalalo immediatamente spiegando il motivo.
