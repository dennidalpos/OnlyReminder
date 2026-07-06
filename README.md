# OnlyReminder

OnlyReminder è un'applicazione Android locale dedicata a professionisti e piccole attività per la
gestione di contatti, task, compleanni e follow-up tramite WhatsApp, mantenendo il pieno controllo
della privacy.

## Struttura del Progetto

- `android-app/`: Applicazione Android nativa (Kotlin/Compose).
- `landing/`: Sito web statico di presentazione.
- `media-assets/`: Risorse grafiche e video.
- `onlyreminder_agent_roadmap/`: Roadmap di sviluppo per agenti AI.

## Caratteristiche Principali

- **Local-First**: Tutti i dati risiedono esclusivamente sul dispositivo.
- **Privacy**: Database cifrato e nessun tracciamento esterno.
- **Review Obbligatoria**: Nessun invio automatico di messaggi; ogni azione richiede conferma
  dell'utente.
- **Integrazione WhatsApp**: Supporto per modalità manuale e WhatsApp Business API avanzata.

## Come Iniziare (Sviluppatori)

1. Apri la cartella radice `OnlyReminder/` in Android Studio.
2. Assicurati che l'Android SDK sia correttamente configurato (il file `local.properties` è già presente).
3. Esegui il "Gradle Sync".
4. Build > Build APK per generare il pacchetto di installazione.

Per maggiori dettagli, consulta la cartella `docs/`.
