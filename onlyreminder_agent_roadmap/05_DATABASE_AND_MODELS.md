# 05 — Database and Models

## Obiettivo

Implementare database locale cifrato, modelli dati, DAO, repository e migrazioni iniziali.

## Scope

Creare la persistenza base dell'app. Non implementare ancora UI completa per tutte le entità.

## Requisiti tecnici

- Room Database.
- SQLCipher o soluzione equivalente.
- Chiave gestita con Android Keystore dove appropriato.
- Migrazioni versionate.
- Repository data layer.
- Domain models separati dalle entity DB quando utile.

## Entità minime

### Contact

Campi:

- id;
- firstName;
- lastName;
- displayName;
- phone;
- normalizedPhone;
- email;
- company;
- birthday;
- groupId opzionale;
- source;
- notes;
- status;
- lastContactDate;
- marketingConsent;
- privacyConsent;
- createdAt;
- updatedAt;
- deletedAt.

### Group

Campi:

- id;
- name;
- description;
- color;
- createdAt;
- updatedAt.

### Tag

Usare tabella dedicata:

- Tag;
- ContactTagCrossRef.

### Custom Fields

Usare tabella dedicata:

- ContactCustomField;
- contactId;
- key;
- value.

### Template

Campi:

- id;
- name;
- language;
- channel;
- body;
- variables;
- isDefault;
- whatsappApprovedTemplateName;
- createdAt;
- updatedAt.

### Task / Reminder

Campi:

- id;
- title;
- description;
- contactId;
- groupId;
- type;
- dueDateTime;
- repeatRule;
- priority;
- status;
- templateId;
- sendMode;
- createdAt;
- completedAt.

### BirthdayRun

Campi:

- id;
- date;
- status;
- totalFound;
- totalSelected;
- totalSkipped;
- totalSent;
- totalFailed;
- createdAt;
- reviewedAt;
- completedAt.

### BirthdayRunItem

Campi:

- id;
- birthdayRunId;
- contactId;
- status;
- generatedMessagePreview;
- errorMessage;
- createdAt;
- updatedAt.

### MessageLog

Campi:

- id;
- contactId;
- templateId;
- taskId;
- birthdayRunId;
- channel;
- mode;
- status;
- errorMessage;
- payloadPreview;
- sentAt;
- createdAt.

### Settings

Usare DataStore per impostazioni semplici; DB solo se necessario.

Default:

- language = `en`;
- defaultCountryCode = `+39`;
- birthdayNotificationTime = `09:00`;
- birthdayApiBatchDelaySeconds = `3`;
- backupRetentionCount = `10`.

## Hard delete

Implementare funzioni distinte:

- archivia contatto;
- elimina definitivamente contatto.

La cancellazione definitiva deve gestire relazioni e dati collegati senza rompere integrità DB.

## Checklist implementazione

- [ ] Room configurato.
- [ ] Cifratura DB configurata.
- [ ] Entity Contact creata.
- [ ] Entity Group creata.
- [ ] Entity Tag e cross-ref create.
- [ ] Entity CustomField creata.
- [ ] Entity Template creata.
- [ ] Entity Task creata.
- [ ] Entity BirthdayRun creata.
- [ ] Entity BirthdayRunItem creata.
- [ ] Entity MessageLog creata.
- [ ] DAO principali creati.
- [ ] Repository principali creati.
- [ ] Migrazione iniziale gestita.
- [ ] DataStore settings default creato.
- [ ] Test base DAO aggiunti se possibile.
- [ ] Build debug compila.

## Criteri di accettazione

- Il DB è cifrato.
- Si possono creare/leggere/aggiornare/cancellare entità base.
- Le relazioni principali funzionano.
- I default settings sono disponibili.
- La cancellazione definitiva non lascia relazioni rotte.

## Prompt da passare all'agente

```text
Implementa lo step 05 Database and Models.

Configura Room con database cifrato, entity, DAO, repository, settings DataStore e migrazione iniziale. Implementa le entità principali: Contact, Group, Tag, CustomField, Template, Task, BirthdayRun, BirthdayRunItem, MessageLog. Mantieni separazione data/domain.

Non implementare ancora UI completa, import o WhatsApp. A fine lavoro restituisci file modificati, test/controlli e limiti noti.
```
