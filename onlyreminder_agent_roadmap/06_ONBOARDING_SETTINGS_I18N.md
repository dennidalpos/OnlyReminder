# 06 — Onboarding, Settings and I18N

## Obiettivo

Implementare wizard iniziale, impostazioni base, localizzazione inglese/italiano e richiesta
permessi essenziali.

## Scope

Creare esperienza iniziale e schermata settings base. Non implementare ancora feature business
complete.

## Lingue

Default:

```text
English
```

Seconda lingua:

```text
Italiano
```

Risorse Android:

```text
res/values/strings.xml
res/values-it/strings.xml
```

## Wizard iniziale

Step obbligatori:

1. Lingua.
2. App Lock.
3. Modalità invio.
4. WhatsApp API setup solo se selezionata modalità API.
5. Permessi.
6. Cartella backup.
7. Avviso privacy.

## Modalità invio

Opzioni:

- Reminder only;
- Manual WhatsApp;
- WhatsApp Business API.

Default consigliato:

```text
Reminder only
```

## Permessi

Gestire:

- notifiche;
- exact alarm solo se richiesto;
- cartella backup via Storage Access Framework;
- import file via picker quando necessario.

Non chiedere permessi prima che servano, salvo onboarding esplicito.

## Privacy notice

Inglese:

```text
OnlyReminder stores your data locally and encrypts its database and backups. You are responsible for ensuring that imported contacts are processed lawfully and that messages are sent only when appropriate.
```

Italiano:

```text
OnlyReminder conserva i dati localmente e cripta database e backup. Sei responsabile di trattare i contatti importati in modo lecito e di inviare messaggi solo quando appropriato.
```

## Settings

Implementare:

- language;
- appLockEnabled;
- sendMode;
- defaultCountryCode;
- birthdayNotificationTime;
- birthdayApiBatchDelaySeconds;
- backupFolderUri;
- backupRetentionCount;
- app lock timeout;
- wipe data placeholder/flow protetto.

## WhatsApp API setup nello step onboarding

Solo form UI e secure save dei campi se già disponibile:

- WhatsApp Business Account ID;
- Phone Number ID;
- Access Token;
- Default approved template name;
- Language code.

Non implementare ancora invio API.

## Checklist implementazione

- [ ] `strings.xml` inglese creato.
- [ ] `strings.xml` italiano creato.
- [ ] Language step implementato.
- [ ] App Lock step implementato.
- [ ] Send Mode step implementato.
- [ ] WhatsApp API setup UI condizionale creato.
- [ ] Permissions step implementato.
- [ ] Backup folder picker implementato.
- [ ] Privacy notice step implementato.
- [ ] Persistenza completamento onboarding.
- [ ] Settings screen base implementata.
- [ ] Default settings applicati.
- [ ] Build debug compila.

## Criteri di accettazione

- Al primo avvio appare wizard.
- L'utente può selezionare EN/IT.
- Le stringhe cambiano correttamente.
- L'utente può attivare app lock.
- L'utente può scegliere send mode.
- L'utente può selezionare cartella backup.
- L'avviso privacy viene mostrato.
- Onboarding non si ripete dopo completamento.

## Prompt da passare all'agente

```text
Implementa lo step 06 Onboarding, Settings and I18N.

Crea wizard iniziale con lingua, app lock, modalità invio, setup API condizionale, permessi, cartella backup e privacy notice. Implementa settings base e risorse EN/IT. Non implementare ancora import, birthday review, WhatsApp sending o backup reale.

A fine lavoro restituisci implementazione, file modificati, controlli eseguiti e limiti noti.
```
