# OnlyReminder — Istruzioni di progetto per agente AI sviluppatore

## 1. Obiettivo del progetto

Sviluppare **OnlyReminder**, un'app Android locale per singoli commerciali, professionisti, aziende locali e utenti CRM che vogliono gestire promemoria, follow-up e messaggi WhatsApp verso contatti/clienti importati da file.

L'app deve vivere **interamente sul telefono**, senza backend proprietario, senza sincronizzazione cloud e senza analytics. Deve essere progettata con logica **privacy by default**, archivio locale cifrato, backup locale cifrato e distribuzione tramite APK firmato scaricabile da landing page statica.

## 2. Nome, package e distribuzione

### Nome app visibile

```text
OnlyReminder
```

### Package Android

```text
com.onlyreminder.app
```

### Nome APK release

```text
OnlyReminder-v1.0.0-release.apk
```

### Keystore

```text
onlyreminder-release-key.jks
```

Il progetto deve includere configurazione release per Android Studio e build Gradle, con istruzioni per firmare l'APK.

---

## 3. Perimetro MVP

### Incluso nell'MVP

- App Android nativa.
- Lingua default inglese.
- Lingua italiana disponibile.
- Import contatti da:
  - CSV;
  - Excel/XLSX;
  - JSON;
  - XML.
- Archivio locale cifrato.
- PIN/biometria all'apertura.
- Gruppi di contatti/clienti.
- Tag dinamici.
- Campi custom.
- Deduplicazione contatti.
- Mapping guidato colonne durante import.
- Anteprima import prima del salvataggio.
- Validazione e normalizzazione telefono.
- Reminder locali.
- Birthday review giornaliera.
- Template messaggi configurabili.
- Template con testo, emoji, link e variabili.
- Modalità WhatsApp manuale.
- Modalità WhatsApp Business API con credenziali fornite dal cliente.
- Coda locale invii.
- Log locale degli invii.
- Backup locale cifrato in cartella scelta dall'utente.
- Export manuale.
- Restore da backup.
- Landing page statica IT/EN.
- APK scaricabile dalla landing.
- Cartella asset media completa.
- Predisposizione futura Google Play Store.

### Escluso dall'MVP

- Backend proprietario.
- Account utente cloud.
- Multi-operatore.
- Sincronizzazione tra dispositivi.
- Google Drive backup.
- Analytics.
- Tracking.
- CRM API dirette.
- Dashboard web.
- Licenze/abbonamenti.
- Campagne massive avanzate.
- Allegati media WhatsApp.
- Play Store release completa.

---

## 4. Stack tecnico richiesto

Usare Android nativo.

### Tecnologia consigliata

- Kotlin.
- Jetpack Compose.
- Material 3.
- Room Database.
- SQLCipher o soluzione equivalente per database cifrato.
- Android Keystore per gestione chiavi locali.
- DataStore per preferenze cifrate/non sensibili.
- WorkManager per job periodici.
- AlarmManager per reminder precisi dove necessario.
- Android Notification API.
- Document Picker / Storage Access Framework per import/export/backup.
- Libreria CSV.
- Libreria XLSX.
- Parser JSON.
- Parser XML.
- Retrofit/OkHttp per WhatsApp Business API Mode.
- Gradle Kotlin DSL.

### Nota architetturale

L'app deve essere progettata con separazione modulare:

- `data`;
- `domain`;
- `ui`;
- `feature-import`;
- `feature-contacts`;
- `feature-groups`;
- `feature-tasks`;
- `feature-templates`;
- `feature-birthday`;
- `feature-whatsapp`;
- `feature-backup`;
- `feature-settings`;
- `feature-security`.

---

## 5. Struttura repository richiesta

```text
OnlyReminder/
  android-app/
    app/
      build.gradle.kts
      src/
        main/
          AndroidManifest.xml
          java/com/onlyreminder/app/
          res/
            values/
            values-it/
            drawable/
            mipmap/
    core/
      common/
      security/
      database/
      notifications/
      storage/
    data/
      contacts/
      groups/
      templates/
      tasks/
      birthday/
      whatsapp/
      backup/
    domain/
      contacts/
      groups/
      templates/
      tasks/
      birthday/
      whatsapp/
      backup/
    feature-import/
    feature-contacts/
    feature-groups/
    feature-tasks/
    feature-templates/
    feature-birthday/
    feature-whatsapp/
    feature-backup/
    feature-settings/
    feature-onboarding/
    feature-security/
    keystore/
      README.md
      onlyreminder-release-key.example.properties
    docs/
      android-build-guide.md
      release-guide.md
      whatsapp-api-guide.md
      import-format-guide.md
      backup-restore-guide.md
      gdpr-notes.md

  landing/
    en/
      index.html
      download.html
      privacy.html
      terms.html
    it/
      index.html
      download.html
      privacy.html
      terms.html
    assets/
      css/
        style.css
      js/
        main.js
      images/
      apk/
        OnlyReminder-latest.apk
        checksums.txt
      changelog/
        changelog.md

  media-assets/
    logo/
    icons/
    splash/
    screenshots/
    mockups/
    social/
    banners/
    store/
    video/

  README.md
  PROJECT_PLAN.md
```

---

## 6. Lingue

L'app deve essere bilingue:

- default: inglese;
- seconda lingua: italiano.

Usare risorse Android separate:

```text
res/values/strings.xml
res/values-it/strings.xml
```

La landing deve avere:

```text
landing/en/
landing/it/
```

I template messaggi possono essere creati dall'utente in qualsiasi lingua. Per MVP non serve selezionare automaticamente il template in base alla lingua del contatto.

---

## 7. Brand visual

### Stile

```text
sales automation
```

### Palette

```text
blu / nero / bianco
```

### Tono grafico

- Professionale.
- Commerciale.
- Pulito.
- Orientato a produttività e follow-up.
- Non giocoso.
- Non consumer entertainment.

### Posizionamento

Inglese:

```text
OnlyReminder — Contact tasks, birthday reminders and WhatsApp follow-ups.
```

Italiano:

```text
OnlyReminder — Task contatti, promemoria compleanni e follow-up WhatsApp.
```

---

## 8. Wizard iniziale

Al primo avvio l'app deve mostrare un wizard di configurazione.

### Step 1 — Lingua

Opzioni:

- English;
- Italiano.

Default:

```text
English
```

### Step 2 — App Lock

L'utente deve poter attivare:

- PIN;
- biometria.

Default consigliato:

```text
enabled
```

Se il dispositivo non supporta biometria, mostrare solo PIN.

### Step 3 — Modalità invio

Opzioni:

1. Reminder only.
2. Manual WhatsApp.
3. WhatsApp Business API.

### Step 4 — Configurazione WhatsApp API

Mostrare solo se l'utente seleziona `WhatsApp Business API`.

Campi:

- WhatsApp Business Account ID;
- Phone Number ID;
- Access Token;
- Default approved template name;
- Language code template, se necessario;
- Test connection.

Il token e ogni credenziale devono essere salvati solo localmente e cifrati.

Mostrare avviso:

```text
Your WhatsApp API credentials are stored only on this device and encrypted locally. You are responsible for their security and for the lawful use of the WhatsApp Business Platform.
```

Italiano:

```text
Le tue credenziali WhatsApp API vengono salvate solo su questo dispositivo e cifrate localmente. Sei responsabile della loro sicurezza e dell'uso corretto della WhatsApp Business Platform.
```

### Step 5 — Permessi

Richiedere i permessi necessari per:

- notifiche;
- reminder/alarm precisi, se richiesti dal sistema Android;
- accesso cartella backup tramite Storage Access Framework;
- import file tramite file picker.

### Step 6 — Cartella backup

L'utente deve scegliere una cartella locale, ad esempio:

```text
Documents/OnlyReminder/Backups
```

Non usare backup cloud nell'MVP.

### Step 7 — Avviso privacy

Mostrare un avviso breve:

Inglese:

```text
OnlyReminder stores your data locally and encrypts its database and backups. You are responsible for ensuring that imported contacts are processed lawfully and that messages are sent only when appropriate.
```

Italiano:

```text
OnlyReminder conserva i dati localmente e cripta database e backup. Sei responsabile di trattare i contatti importati in modo lecito e di inviare messaggi solo quando appropriato.
```

---

## 9. Modello dati

### Contact

Campi minimi:

```text
id: UUID
firstName: String?
lastName: String?
displayName: String
phone: String?
normalizedPhone: String?
email: String?
company: String?
birthday: LocalDate?
tags: List<String>
groupId: UUID?
source: String?
notes: String?
status: ContactStatus
lastContactDate: LocalDateTime?
marketingConsent: Boolean?
privacyConsent: Boolean?
customFields: Map<String, String>
createdAt: LocalDateTime
updatedAt: LocalDateTime
deletedAt: LocalDateTime?
```

### ContactStatus

```text
active
archived
deleted
```

Nel MVP è richiesta anche cancellazione definitiva.

### Group

```text
id: UUID
name: String
description: String?
color: String?
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

### Template

```text
id: UUID
name: String
language: String?
channel: TemplateChannel
body: String
variables: List<String>
isDefault: Boolean
whatsappApprovedTemplateName: String?
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

### TemplateChannel

```text
whatsapp_manual
whatsapp_business_api
generic_reminder
```

### Task / Reminder

```text
id: UUID
title: String
description: String?
contactId: UUID?
groupId: UUID?
type: TaskType
dueDateTime: LocalDateTime
repeatRule: String?
priority: TaskPriority
status: TaskStatus
templateId: UUID?
sendMode: SendMode
createdAt: LocalDateTime
completedAt: LocalDateTime?
```

### TaskType

```text
birthday
follow_up
custom
```

### TaskPriority

```text
low
normal
high
urgent
```

### TaskStatus

```text
pending
done
skipped
failed
not_reviewed
```

### SendMode

```text
reminder_only
manual_whatsapp
whatsapp_business_api
```

### BirthdayRun

Rappresenta una scansione giornaliera compleanni.

```text
id: UUID
date: LocalDate
status: BirthdayRunStatus
totalFound: Int
totalSelected: Int
totalSkipped: Int
totalSent: Int
totalFailed: Int
createdAt: LocalDateTime
reviewedAt: LocalDateTime?
completedAt: LocalDateTime?
```

### BirthdayRunStatus

```text
pending_review
reviewed
sending
completed
not_reviewed
```

### BirthdayRunItem

```text
id: UUID
birthdayRunId: UUID
contactId: UUID
status: BirthdayRunItemStatus
generatedMessagePreview: String?
errorMessage: String?
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

### BirthdayRunItemStatus

```text
selected
unselected
skipped_this_run
deleted_contact
manual_opened
sent
failed
pending
```

### MessageLog

```text
id: UUID
contactId: UUID
templateId: UUID?
taskId: UUID?
birthdayRunId: UUID?
channel: String
mode: SendMode
status: MessageStatus
errorMessage: String?
payloadPreview: String?
sentAt: LocalDateTime?
createdAt: LocalDateTime
```

### MessageStatus

```text
pending
sending
sent
failed
skipped
manual_opened
```

### Settings

```text
language: String
appLockEnabled: Boolean
sendMode: SendMode
defaultCountryCode: String
birthdayNotificationTime: LocalTime
birthdayApiBatchDelaySeconds: Int
backupFolderUri: String?
backupRetentionCount: Int
whatsappBusinessAccountId: String?
whatsappPhoneNumberId: String?
whatsappAccessTokenEncrypted: String?
defaultBirthdayTemplateId: UUID?
```

Default richiesti:

```text
language = "en"
defaultCountryCode = "+39"
birthdayNotificationTime = "09:00"
birthdayApiBatchDelaySeconds = 3
backupRetentionCount = 10
```

---

## 10. Import contatti

### Formati supportati

- CSV;
- XLSX;
- JSON;
- XML.

L'utente è responsabile di esportare e normalizzare i dati dal proprio CRM prima dell'import.

### Campi importabili

- nome;
- cognome;
- display name;
- telefono;
- email;
- azienda;
- data nascita;
- tag;
- gruppo;
- origine CRM;
- note;
- data ultimo contatto;
- consenso marketing;
- consenso privacy;
- stato;
- campi custom.

### Flusso import

1. Utente seleziona file.
2. App rileva formato.
3. App mostra anteprima.
4. App propone mapping colonne/campi.
5. Utente conferma o modifica mapping.
6. App normalizza telefoni.
7. App valida dati.
8. App rileva duplicati.
9. App mostra report.
10. Utente conferma import.
11. App crea backup cifrato prima di salvare.
12. App salva i contatti nel database cifrato.

### Deduplicazione

Criteri:

1. `normalizedPhone`, se presente.
2. `email`, se presente.
3. combinazione `firstName + lastName + company`, come criterio debole.

L'utente deve poter scegliere:

- skip duplicate;
- update existing;
- create new anyway.

### Normalizzazione telefoni

Default:

```text
Italy +39
```

Regole:

```text
333 1234567 -> +393331234567
0039 333 1234567 -> +393331234567
+39 333 1234567 -> +393331234567
```

Regole operative:

- rimuovere spazi;
- rimuovere trattini;
- rimuovere parentesi;
- convertire `00` iniziale in `+`;
- se manca prefisso internazionale, aggiungere `+39`;
- segnalare numeri sospetti;
- consentire correzione manuale.

---

## 11. Birthday review giornaliera

Questa è una funzione centrale.

### Requisito fondamentale

```text
The app must never send birthday messages immediately after the daily background scan.
The app must first create a reviewable local queue, notify the user, allow contact review/removal/selection, and only after explicit user action start sending selected messages.
```

Italiano:

```text
L'app non deve mai inviare messaggi di compleanno subito dopo la scansione giornaliera.
Deve prima creare una lista locale verificabile, notificare l'utente, permettere revisione/rimozione/selezione e solo dopo un'azione esplicita dell'utente avviare l'invio dei selezionati.
```

### Orario notifica

Default:

```text
09:00
```

Modificabile nelle impostazioni.

### Processo giornaliero

Ogni giorno:

1. Un job locale controlla i contatti con `birthday` uguale al giorno/mese corrente.
2. Crea un `BirthdayRun`.
3. Crea un `BirthdayRunItem` per ogni contatto trovato.
4. Mostra una notifica Android.

Testo notifica inglese:

```text
Today there are {count} birthday contacts to review.
```

Testo notifica italiano:

```text
Oggi ci sono {count} contatti con compleanno da verificare.
```

### Se l'utente non apre la notifica

- Nessun invio automatico.
- La lista resta pending fino a mezzanotte.
- Dopo mezzanotte viene marcata come:

```text
not_reviewed
```

### Schermata Birthday Review

La schermata deve mostrare:

- data corrente;
- totale contatti trovati;
- totale selezionati;
- contatti senza telefono;
- contatti con telefono non valido;
- contatti già contattati oggi;
- filtro per gruppo;
- filtro per tag;
- ricerca;
- checkbox per ogni contatto;
- anteprima messaggio;
- scelta template;
- azioni per contatto;
- azioni massive.

### Azioni per contatto

Ogni contatto nella review deve permettere:

1. Non inviare in questo turno.
2. Cancellare definitivamente il contatto.
3. Aprire dettaglio contatto.
4. Modificare telefono.
5. Visualizzare anteprima messaggio.

Nota: se il contatto viene escluso solo per il turno corrente, deve potersi ripresentare l'anno successivo.

### Azioni massive

- Select all.
- Deselect all.
- Send selected.
- Skip selected.
- Delete selected, con conferma forte.

### Conferma cancellazione definitiva

Mostrare conferma esplicita:

```text
This will permanently delete the selected contact from OnlyReminder. This action cannot be undone unless you restore a backup.
```

Italiano:

```text
Questo eliminerà definitivamente il contatto selezionato da OnlyReminder. L'azione non può essere annullata, salvo ripristino da backup.
```

---

## 12. Invio WhatsApp

### Modalità 1 — Reminder only

- L'app mostra solo reminder.
- Nessuna apertura WhatsApp.
- Nessun invio API.

### Modalità 2 — Manual WhatsApp

Flusso:

1. Utente seleziona contatti.
2. Utente preme `Start manual sending`.
3. App genera messaggio dal template.
4. App apre WhatsApp o WhatsApp Business con chat e messaggio precompilato.
5. Utente preme invia manualmente.
6. Utente torna all'app.
7. App propone contatto successivo.
8. App marca il contatto come `manual_opened`.

In questa modalità non deve esistere invio automatico reale.

### Modalità 3 — WhatsApp Business API

Flusso:

1. Utente configura credenziali API nel wizard o impostazioni.
2. Utente seleziona contatti dalla Birthday Review o da task/follow-up.
3. Utente preme `Send selected automatically`.
4. App crea coda locale.
5. App invia un messaggio ogni 3 secondi.
6. App mostra progresso.
7. App registra log.
8. App permette pausa.
9. App permette retry failed.
10. App produce riepilogo finale.

### Delay default batch API

```text
1 messaggio ogni 3 secondi
```

Questo valore deve essere modificabile nelle impostazioni.

### Coda invii

Stati:

```text
pending
sending
sent
failed
skipped
retry_scheduled
```

### Schermata progresso

Mostrare:

- totale;
- inviati;
- falliti;
- pending;
- contatto corrente;
- ultimo errore;
- pulsante pausa;
- pulsante stop;
- pulsante retry failed;
- export report.

Esempio:

```text
Sending birthday messages

Sent: 348
Failed: 12
Pending: 640

Pause
Retry failed
Export report
```

### Fallback

Se API fallisce per credenziali mancanti/scadute:

- mostrare errore chiaro;
- permettere aggiornamento credenziali;
- permettere fallback a modalità manuale.

---

## 13. Template messaggi

### Requisiti

Supportare:

- testo;
- emoji;
- link;
- variabili;
- preview;
- template birthday;
- template follow-up;
- template per gruppo.

### Variabili base

```text
{first_name}
{last_name}
{full_name}
{company}
{birthday}
{custom.field_name}
```

### Template birthday default inglese

```text
Happy birthday {first_name}! Wishing you a wonderful day.
```

### Template birthday default italiano

```text
Tanti auguri {first_name}! Ti auguro una splendida giornata.
```

### Template locale vs WhatsApp API

Distinguere:

1. Template locale usato per WhatsApp manuale.
2. Template WhatsApp Business API approvato nel Business Account del cliente.

Per API Mode, il template può richiedere un nome approvato, ad esempio:

```text
birthday_wishes_v1
```

L'app deve permettere di salvare:

```text
whatsappApprovedTemplateName
```

---

## 14. Privacy e GDPR

### Principi applicativi

OnlyReminder deve essere progettata come app locale privacy-by-default:

- nessun backend;
- nessun cloud;
- nessun analytics;
- nessun tracking;
- database cifrato;
- backup cifrati;
- token cifrati;
- app lock;
- export dati;
- cancellazione dati;
- wipe completo;
- log locale cancellabile.

### Responsabilità utilizzatore

L'app deve mostrare un avviso iniziale:

```text
OnlyReminder is a local productivity tool. You are responsible for ensuring that imported contacts are processed lawfully and that messages are sent only where appropriate.
```

Italiano:

```text
OnlyReminder è uno strumento locale di produttività. Sei responsabile di trattare i contatti importati in modo lecito e di inviare messaggi solo quando appropriato.
```

### Consenso

Campi presenti ma opzionali:

```text
marketingConsent
privacyConsent
```

Regole:

- L'app non blocca gli auguri se manca il consenso marketing.
- L'app deve mostrare un warning se il template contiene contenuto commerciale/promozionale.
- L'app deve offrire filtro opzionale:

```text
Send only to contacts with marketing consent
```

Italiano:

```text
Invia solo ai contatti con consenso marketing
```

### Warning template commerciale

Se il template contiene link, promo, offerta, sconto o parole configurabili come commerciali, mostrare warning:

```text
This message may be promotional. Make sure you have a proper legal basis before sending it.
```

Italiano:

```text
Questo messaggio potrebbe essere promozionale. Assicurati di avere una base giuridica adeguata prima di inviarlo.
```

---

## 15. Backup locale

### Tipo backup

- Locale.
- Cifrato.
- In cartella scelta dall'utente.
- Nessun backup cloud nell'MVP.

### Quando fare backup

- Backup automatico giornaliero.
- Backup manuale.
- Backup automatico prima di ogni import massivo.
- Backup automatico prima di cancellazioni massive.

### Nome file

```text
OnlyReminder_Backup_YYYY-MM-DD_HHMM.orbackup
OnlyReminder_Backup_YYYY-MM-DD_HHMM.sha256
```

Esempio:

```text
OnlyReminder_Backup_2026-07-06_0900.orbackup
OnlyReminder_Backup_2026-07-06_0900.sha256
```

### Retention

Default:

```text
10 backup
```

Configurabile nelle impostazioni.

### Restore

Flusso:

1. Utente seleziona file backup.
2. App verifica integrità.
3. App richiede conferma.
4. App crea backup dello stato attuale.
5. App ripristina.
6. App riavvia schermata principale.

### Export manuale

Prevedere:

1. export cifrato;
2. export leggibile CSV/JSON solo su richiesta esplicita.

Mostrare warning per export non cifrato:

```text
This export is not encrypted. Anyone with access to the file may read your contacts.
```

Italiano:

```text
Questo export non è cifrato. Chiunque abbia accesso al file potrebbe leggere i tuoi contatti.
```

---

## 16. Sicurezza locale

### Requisiti

- Database cifrato.
- Chiavi conservate tramite Android Keystore.
- Token WhatsApp API cifrato.
- PIN/biometria.
- Auto-lock dopo inattività.
- Wipe completo dati.
- Nessuna telemetria.
- Nessun log sensibile in chiaro.

### Auto-lock

Default consigliato:

```text
5 minuti
```

Configurabile:

- immediately;
- 1 minute;
- 5 minutes;
- 15 minutes;
- never, sconsigliato.

---

## 17. Schermate principali

### Onboarding

- Language.
- App Lock.
- Sending Mode.
- WhatsApp API Setup.
- Permissions.
- Backup Folder.
- Privacy Notice.

### Home / Dashboard

Mostrare:

- birthday review di oggi;
- task pending;
- follow-up imminenti;
- ultimo backup;
- stato modalità invio;
- shortcut import;
- shortcut nuovo task;
- shortcut template.

### Contacts

- lista contatti;
- ricerca;
- filtri;
- gruppo;
- tag;
- stato;
- import source;
- dettaglio;
- modifica;
- cancellazione.

### Contact Detail

- dati base;
- telefono normalizzato;
- email;
- azienda;
- birthday;
- tag;
- gruppo;
- note;
- task collegati;
- message log;
- azioni:
  - create task;
  - send WhatsApp;
  - edit;
  - delete.

### Groups

- lista gruppi;
- crea gruppo;
- modifica gruppo;
- assegna contatti;
- filtra contatti per gruppo.

### Templates

- lista template;
- crea template;
- modifica template;
- preview;
- variabili disponibili;
- duplicazione template;
- impostazione default birthday.

### Tasks

- lista task;
- filtri per stato;
- filtri per data;
- dettaglio task;
- crea task singolo;
- crea task per gruppo;
- collega template;
- completa/skippa/fallisce.

### Birthday Review

Schermata centrale per compleanni.

### Sending Queue

- progresso invio;
- pausa;
- stop;
- retry failed;
- export report.

### Backup & Restore

- cartella backup;
- backup ora;
- lista backup;
- restore;
- export.

### Settings

- lingua;
- app lock;
- modalità invio;
- credenziali WhatsApp API;
- orario birthday notification;
- delay batch API;
- default country code;
- backup retention;
- privacy;
- wipe data;
- about.

---

## 18. Landing page statica

### Requisiti

- Statica.
- IT/EN.
- Nessun form.
- Nessun analytics.
- Nessun tracking.
- Download APK.
- Privacy policy.
- Terms.
- Changelog.
- Checksum.

### Struttura

```text
landing/
  en/
    index.html
    download.html
    privacy.html
    terms.html
  it/
    index.html
    download.html
    privacy.html
    terms.html
  assets/
    css/
      style.css
    js/
      main.js
    images/
    apk/
      OnlyReminder-latest.apk
      checksums.txt
    changelog/
      changelog.md
```

### Hero inglese

```text
OnlyReminder
Contact tasks, birthday reminders and WhatsApp follow-ups.
```

Subtitle:

```text
Import your contacts, review daily birthday reminders, and send personalized WhatsApp follow-ups from your Android phone.
```

### Hero italiano

```text
OnlyReminder
Task contatti, promemoria compleanni e follow-up WhatsApp.
```

Subtitle:

```text
Importa i tuoi contatti, verifica ogni giorno i compleanni e invia follow-up WhatsApp personalizzati dal tuo telefono Android.
```

### CTA inglese

```text
Download APK
```

### CTA italiano

```text
Scarica APK
```

---

## 19. Cartella asset media

Creare:

```text
media-assets/
  logo/
    onlyreminder-logo.svg
    onlyreminder-logo.png
    onlyreminder-logo-dark.png
    onlyreminder-logo-light.png
  icons/
    android-icon-foreground.svg
    android-icon-background.svg
    favicon.ico
    favicon-32x32.png
    favicon-192x192.png
  splash/
    splash-screen.svg
    splash-screen.png
  screenshots/
    en/
    it/
  mockups/
    phone-home.png
    phone-birthday-review.png
    phone-template-editor.png
    phone-sending-queue.png
  social/
    og-image-en.png
    og-image-it.png
  banners/
    landing-hero.png
    download-banner.png
  store/
    feature-graphic.png
    promo-graphic.png
    app-icon-512.png
  video/
    demo-placeholder.md
```

Gli asset devono rispettare palette blu/nero/bianco e stile sales automation.

---

## 20. Build e release

### Requisiti

- Build debug.
- Build release.
- APK firmato.
- Keystore configurabile.
- File `checksums.txt`.
- Changelog.
- Versionamento semantico.

### Versione iniziale

```text
1.0.0
```

### Output release

```text
OnlyReminder-v1.0.0-release.apk
checksums.txt
changelog.md
```

### Documentazione release

Creare:

```text
android-app/docs/release-guide.md
```

Deve spiegare:

- come generare keystore;
- come configurare signing;
- come produrre APK release;
- come calcolare checksum;
- dove copiare APK nella landing;
- come aggiornare changelog.

---

## 21. Criteri di accettazione MVP

### Import

- L'app importa correttamente CSV.
- L'app importa correttamente XLSX.
- L'app importa correttamente JSON.
- L'app importa correttamente XML.
- L'utente può mappare campi.
- L'utente vede anteprima.
- L'app rileva duplicati.
- L'app normalizza numeri italiani con `+39`.
- L'app segnala numeri sospetti.
- L'app crea backup prima dell'import.

### Sicurezza

- Database cifrato.
- Token WhatsApp cifrato.
- Backup cifrato.
- PIN/biometria funzionante.
- Nessun dato inviato a server proprietari.
- Nessun analytics.

### Birthday Review

- L'app crea run giornaliero.
- L'app mostra notifica alle 09:00 default.
- L'utente può cambiare orario.
- Se l'utente non apre la notifica, nessun invio parte.
- Dopo mezzanotte la run diventa `not_reviewed`.
- L'utente può selezionare/deselezionare contatti.
- L'utente può saltare un contatto per il turno.
- L'utente può cancellare definitivamente un contatto.
- L'utente può vedere anteprima messaggio.
- L'utente può inviare solo dopo azione esplicita.

### WhatsApp Manual Mode

- L'app apre WhatsApp con messaggio precompilato.
- L'invio resta manuale.
- L'app passa al contatto successivo.
- L'app registra `manual_opened`.

### WhatsApp Business API Mode

- L'utente può inserire credenziali.
- Le credenziali sono cifrate.
- L'app può testare configurazione.
- L'app invia coda selezionata.
- Delay default: 3 secondi.
- L'app mostra progresso.
- L'app registra sent/failed.
- L'app consente retry failed.
- L'app consente fallback manuale.

### Backup

- L'utente sceglie cartella backup.
- L'app crea backup cifrato.
- L'app crea backup manuale.
- L'app crea backup automatico.
- L'app crea backup prima di import massivo.
- L'app ripristina backup.
- L'app verifica integrità.

### Landing

- Landing EN funzionante.
- Landing IT funzionante.
- Download APK presente.
- Privacy e terms presenti.
- Changelog presente.
- Checksum presente.
- Nessun analytics/tracking/form.

### Build

- Progetto apribile in Android Studio.
- Build debug funzionante.
- Build release funzionante.
- APK firmato generato.
- Keystore documentato.
- Package `com.onlyreminder.app`.

---

## 22. Prompt operativo per agente AI

Usare questo prompt come istruzione principale per l'agente sviluppatore:

```text
You are an AI software development agent. Build the OnlyReminder Android MVP according to the specifications in this document.

The app is a local-first Android application for a single sales professional. It imports contacts from CSV, XLSX, JSON and XML, stores them in an encrypted local database, manages contact groups, tasks, birthday reminders, WhatsApp message templates, local encrypted backups and a static bilingual landing page with downloadable signed APK.

Do not add a proprietary backend, cloud sync, analytics, telemetry, user accounts, payment system or CRM API integrations.

The most important functional constraint is this:
The app must never send birthday messages immediately after the daily background scan. The app must first create a reviewable local queue, notify the user, allow contact review/removal/selection, and only after explicit user action start manual or API-based sending of selected messages.

Implement three send modes:
1. Reminder only.
2. Manual WhatsApp, where the app opens WhatsApp with a prefilled message and the user manually sends it.
3. WhatsApp Business API, where the user provides their own Meta/WhatsApp credentials, stored locally encrypted, and the app sends selected messages through a local queue with a default delay of 3 seconds per message.

Default language is English. Italian must be available.
Default birthday notification time is 09:00 and must be configurable.
Default phone country code is Italy +39 and must be configurable.
Brand style is sales automation with blue/black/white palette.

Create all required Android modules, data models, UI screens, background jobs, backup/restore logic, release signing documentation, landing page and media asset folders.
```

---

## 23. Priorità di sviluppo consigliata

### Fase 1 — Setup progetto

- Creare repository.
- Creare Android project.
- Configurare package.
- Configurare Compose.
- Configurare moduli.
- Configurare risorse EN/IT.
- Creare README e docs base.

### Fase 2 — Database e sicurezza

- Database cifrato.
- Modelli dati.
- Repository.
- Android Keystore.
- App lock PIN/biometria.

### Fase 3 — Import

- File picker.
- Parser CSV.
- Parser XLSX.
- Parser JSON.
- Parser XML.
- Mapping.
- Preview.
- Deduplica.
- Normalizzazione telefono.
- Salvataggio contatti.

### Fase 4 — Contatti, gruppi, tag

- Lista contatti.
- Dettaglio.
- Modifica.
- Gruppi.
- Tag.
- Ricerca.
- Filtri.

### Fase 5 — Template

- CRUD template.
- Variabili.
- Preview.
- Template birthday default.

### Fase 6 — Reminder e birthday review

- Job giornaliero.
- Notifica.
- BirthdayRun.
- BirthdayRunItem.
- Review screen.
- Not reviewed a mezzanotte.
- Azioni skip/delete/send selected.

### Fase 7 — WhatsApp

- Manual WhatsApp mode.
- WhatsApp API settings.
- Token cifrato.
- Coda invio.
- Delay 3 secondi.
- Progress screen.
- Retry failed.
- Log messaggi.

### Fase 8 — Backup

- Cartella scelta utente.
- Backup cifrato.
- Restore.
- Export.
- Retention.

### Fase 9 — Landing e asset

- Landing EN.
- Landing IT.
- Download page.
- Privacy.
- Terms.
- Changelog.
- Asset folders.

### Fase 10 — Release

- Keystore.
- Build release.
- APK firmato.
- Checksum.
- Copia APK in landing.
- Documentazione finale.

---

## 24. Note finali

OnlyReminder deve rimanere semplice, locale e vendibile come APK.

Non trasformare l'MVP in una piattaforma SaaS.

Il valore principale è:

- importare contatti facilmente;
- ricordare follow-up e compleanni;
- permettere revisione umana prima dell'invio;
- ridurre dimenticanze commerciali;
- mantenere dati e backup sul telefono;
- offrire una modalità WhatsApp manuale semplice e una modalità API avanzata per clienti con WhatsApp Business Platform.
