# 01 — Regole Globali per Agente Sviluppatore

## Obiettivo

Impostare le regole permanenti che l'agente deve rispettare durante tutto lo sviluppo di
OnlyReminder.

## Contesto prodotto

OnlyReminder è un'app Android locale per singoli commerciali, professionisti e piccole attività che
vogliono gestire contatti, task, compleanni e follow-up WhatsApp senza backend proprietario.

## Vincoli non negoziabili

- Nessun backend proprietario.
- Nessun account cloud.
- Nessuna sincronizzazione multi-device.
- Nessun analytics.
- Nessun tracking.
- Nessun SDK pubblicitario.
- Nessuna dashboard web.
- Nessun sistema di pagamento.
- Nessuna integrazione CRM diretta.
- Tutti i dati utente devono vivere sul dispositivo.
- Database locale cifrato.
- Backup locale cifrato.
- Credenziali WhatsApp API cifrate localmente.
- Default lingua: inglese.
- Italiano disponibile.
- Package Android: `com.onlyreminder.app`.
- Nome app visibile: `OnlyReminder`.

## Vincolo funzionale più importante

L'app non deve mai inviare messaggi di compleanno subito dopo la scansione giornaliera.

Flusso obbligatorio:

1. scansione locale dei compleanni;
2. creazione lista locale revisionabile;
3. notifica all'utente;
4. review manuale;
5. selezione/rimozione/skipping contatti;
6. invio solo dopo azione esplicita dell'utente.

Qualsiasi implementazione che salti la review umana è errata.

## Priorità di prodotto

Ordine di importanza:

1. privacy locale;
2. affidabilità dati;
3. review birthday sicura;
4. semplicità d'uso;
5. backup/restore;
6. WhatsApp manuale;
7. WhatsApp Business API solo come modalità avanzata.

## Standard tecnico

Stack:

- Kotlin;
- Jetpack Compose;
- Material 3;
- Room;
- SQLCipher o equivalente;
- Android Keystore;
- DataStore;
- WorkManager;
- AlarmManager solo quando necessario;
- Storage Access Framework;
- Android Notification API;
- Gradle Kotlin DSL.

## Regole operative per ogni step

Per ogni implementazione:

- lavorare solo sullo step richiesto;
- non anticipare feature future;
- mantenere codice leggibile e testabile;
- creare piccoli commit logici se possibile;
- non lasciare TODO bloccanti;
- aggiornare documentazione minima solo quando richiesto;
- mantenere compatibilità con Android Studio;
- non introdurre librerie non necessarie.

## Output richiesto all'agente a fine step

Restituire sempre:

```text
STEP COMPLETED: <numero e nome>

Implemented:
- ...

Files changed:
- ...

Checks performed:
- ...

Known limitations:
- ...

Ready for next step:
yes/no
```

## Criteri di accettazione

- L'agente riconosce e rispetta questi vincoli.
- Nessuna feature fuori scope viene proposta come implementazione automatica.
- Il progetto resta local-first.
- Nessun invio birthday automatico viene introdotto.
