# OnlyReminder — Master Tracker Operativo

Scopo: usare questo file come indice operativo unico per passare le implementazioni all'agente
sviluppatore una alla volta.

Regola di gestione:

- Il tracker non deve contenere storico, date, commenti lunghi o retrospettive.
- Quando una fase è completata e verificata, cambiare solo `[ ]` in `[x]`.
- Non aggiungere note operative nel master: eventuali dettagli restano nei file numerati.
- Non anticipare fasi future se i criteri di accettazione della fase corrente non sono soddisfatti.
- Non aggiungere backend, cloud sync, analytics, account utente, pagamenti, CRM API o dashboard web.

## Modalità d'uso con l'agente

Per ogni step:

1. Copiare all'agente il file numerato corrispondente.
2. Far implementare solo quello step.
3. Richiedere evidenza dei criteri di accettazione.
4. Verificare build/test manuali.
5. Flag nel master solo dopo verifica.
6. Passare allo step successivo.

## Tracker principale

| Stato | ID | File                                   | Implementazione                                                              | Dipende da |
|-------|---:|----------------------------------------|------------------------------------------------------------------------------|------------|
| [x]   | 01 | `01_AGENT_GLOBAL_RULES.md`             | Regole globali agente, confini di prodotto, standard di output               | —          |
| [x]   | 02 | `02_REPOSITORY_BOOTSTRAP.md`           | Repository, struttura cartelle, Gradle, package, base Android                | 01         |
| [x]   | 03 | `03_ANDROID_BASE_ARCHITECTURE.md`      | Architettura app, navigation, theme, dependency injection, moduli pragmatici | 02         |
| [x]   | 04 | `04_SECURITY_AND_ENCRYPTION.md`        | Keystore, cifratura locale, app lock, segreti, policy sicurezza              | 03         |
| [x]   | 05 | `05_DATABASE_AND_MODELS.md`            | Room/SQLCipher, entità, DAO, repository, migrazioni iniziali                 | 04         |
| [x]   | 06 | `06_ONBOARDING_SETTINGS_I18N.md`       | Wizard iniziale, impostazioni, EN/IT, permessi base                          | 05         |
| [x]   | 07 | `07_CONTACTS_GROUPS_TAGS.md`           | CRUD contatti, gruppi, tag, ricerca, filtri, dettaglio                       | 06         |
| [x]   | 08 | `08_IMPORT_CSV_MVP.md`                 | Import CSV, mapping, preview, normalizzazione, deduplica, backup pre-import  | 07         |
| [x]   | 09 | `09_IMPORT_ADVANCED_FORMATS.md`        | Import XLSX, JSON, XML con schema controllato                                | 08         |
| [x]   | 10 | `10_TEMPLATES_ENGINE.md`               | Template messaggi, variabili, preview, default birthday EN/IT                | 07         |
| [x]   | 11 | `11_TASKS_LOCAL_REMINDERS.md`          | Task/follow-up, reminder locali, notifiche, stati task                       | 10         |
| [x]   | 12 | `12_BIRTHDAY_REVIEW_CORE.md`           | Birthday scan, run giornaliera, review obbligatoria, no auto-send            | 10, 11     |
| [x]   | 13 | `13_WHATSAPP_MANUAL_MODE.md`           | Apertura WhatsApp manuale con messaggio precompilato e log                   | 12         |
| [x]   | 14 | `14_BACKUP_RESTORE_EXPORT.md`          | Backup cifrato, restore, export cifrato/non cifrato, retention               | 08, 12     |
| [x]   | 15 | `15_LANDING_PAGE_STATIC.md`            | Landing IT/EN statica, privacy, terms, download, checksum placeholder        | 02         |
| [x]   | 16 | `16_RELEASE_SIGNING_CHECKSUMS.md`      | Build release, signing, APK, checksum, changelog, guida release              | 15         |
| [x]   | 17 | `17_WHATSAPP_BUSINESS_API_ADVANCED.md` | Modalità WhatsApp Business API avanzata, credenziali BYO, queue, retry       | 13, 14     |
| [x]   | 18 | `18_FINAL_QA_ACCEPTANCE.md`            | QA finale, test manuali, regressioni, criteri accettazione MVP/v1            | 16, 17     |

## Ordine raccomandato

### Percorso MVP vendibile

Completare fino allo step 16, lasciando lo step 17 come avanzato/opzionale.

Sequenza MVP:
`01 → 02 → 03 → 04 → 05 → 06 → 07 → 08 → 10 → 11 → 12 → 13 → 14 → 15 → 16 → 18`

### Percorso v1 completa

Includere anche:
`09` e `17`

## Blocco anti-scope-creep

L'agente non deve implementare:

- backend proprietario;
- login cloud;
- sincronizzazione multi-device;
- analytics o tracking;
- pagamenti, licenze o abbonamenti;
- dashboard web;
- CRM API dirette;
- allegati media WhatsApp;
- campagne massive avanzate;
- invio automatico birthday senza review umana.

## Criterio generale di completamento

Uno step è completato solo se:

- la build compila;
- non introduce regressioni evidenti;
- i criteri di accettazione del file numerato sono soddisfatti;
- non vengono aggiunte funzionalità fuori perimetro;
- l'agente restituisce una lista sintetica dei file modificati e dei test eseguiti.
