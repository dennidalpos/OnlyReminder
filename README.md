# OnlyReminder

OnlyReminder è un'applicazione Android locale dedicata a professionisti e piccole attività per la
gestione di contatti, task, compleanni e follow-up tramite WhatsApp, mantenendo il pieno controllo
della privacy.

## Struttura del Progetto

- `app/`: Applicazione Android nativa (Kotlin/Compose).
- `landing/`: Sito web statico di presentazione.
- `media-assets/`: Risorse grafiche e video.
- `docs/`: Documentazione tecnica e guide.
- `agents.md`: Istruzioni e regole per gli agenti AI.

## Caratteristiche Principali

- **Local-First**: Tutti i dati risiedono esclusivamente sul dispositivo.
- **Privacy**: Database cifrato e nessun tracciamento esterno.
- **Review Obbligatoria**: Nessun invio automatico di messaggi; ogni azione richiede conferma
  dell'utente.
- **Integrazione WhatsApp**: Supporto per modalità manuale e WhatsApp Business API avanzata.
- **Architettura**: Approccio Clean/MVVM pragmatico per facilità di manutenzione.

## Come Iniziare (Sviluppatori)

1. Apri la cartella radice `OnlyReminder/` in Android Studio.
2. Assicurati che l'Android SDK sia correttamente configurato.
3. Esegui il "Gradle Sync".
4. Consulta la [Guida alla Compilazione](docs/android-build-guide.md) per i dettagli su come generare l'APK.

Per approfondimenti:
- [Guida alla Release](docs/release-guide.md)
- [Integrazione WhatsApp](docs/whatsapp-api-guide.md)
- [Formati Importazione](docs/import-format-guide.md)
- [Backup e Ripristino](docs/backup-restore-guide.md)
- [Note GDPR e Privacy](docs/gdpr-notes.md)
