# 18 — Final QA and Acceptance

## Obiettivo

Eseguire controllo finale di accettazione tecnica, funzionale, privacy e release.

## Scope

Verifica finale. Non introdurre nuove feature salvo bugfix necessari.

## Regole

- Non aggiungere scope.
- Non riscrivere architettura se non necessario.
- Correggere solo bug bloccanti o regressioni.
- Verificare build debug e release.
- Verificare assenza backend/tracking.
- Verificare vincolo birthday no auto-send.

## Test manuali minimi

### Avvio e onboarding

- [ ] Primo avvio mostra wizard.
- [ ] Lingua EN funziona.
- [ ] Lingua IT funziona.
- [ ] App lock configurabile.
- [ ] Send mode configurabile.
- [ ] Backup folder selezionabile.
- [ ] Privacy notice mostrato.

### Contatti

- [ ] Crea contatto.
- [ ] Modifica contatto.
- [ ] Cerca contatto.
- [ ] Filtra per gruppo.
- [ ] Filtra per tag.
- [ ] Archivia contatto.
- [ ] Elimina definitivamente con conferma.

### Import

- [ ] CSV import funziona.
- [ ] Preview prima del salvataggio.
- [ ] Mapping colonne funziona.
- [ ] Normalizzazione +39 funziona.
- [ ] Deduplica funziona.
- [ ] Report import mostra warning/errori.
- [ ] Backup pre-import chiamato.

### Template

- [ ] Crea template.
- [ ] Modifica template.
- [ ] Preview variabili.
- [ ] Template birthday EN esiste.
- [ ] Template birthday IT esiste.
- [ ] Warning commerciale appare se necessario.

### Task e reminder

- [ ] Crea task.
- [ ] Notifica locale funziona.
- [ ] Completa task.
- [ ] Skippa task.
- [ ] Nessun invio automatico parte da task.

### Birthday Review

- [ ] Scan trova compleanni di oggi.
- [ ] BirthdayRun viene creato.
- [ ] BirthdayRunItem vengono creati.
- [ ] Notifica viene mostrata.
- [ ] Nessun invio parte dalla scansione.
- [ ] Review mostra contatti.
- [ ] Selezione/deselezione funziona.
- [ ] Skip contatto funziona.
- [ ] Delete contatto con conferma funziona.
- [ ] Preview messaggio funziona.
- [ ] not_reviewed dopo mezzanotte funziona o è testabile.

### WhatsApp manuale

- [ ] Apre WhatsApp con messaggio precompilato.
- [ ] Invio resta manuale.
- [ ] Passa al contatto successivo.
- [ ] Log `manual_opened` creato.
- [ ] Gestione telefono invalido.
- [ ] Gestione WhatsApp non installato.

### Backup/restore

- [ ] Backup manuale cifrato.
- [ ] Restore backup valido.
- [ ] Backup corrotto rifiutato.
- [ ] Retention funziona.
- [ ] Export non cifrato mostra warning.

### WhatsApp API, se implementato

- [ ] Credenziali salvate cifrate.
- [ ] Token mascherato.
- [ ] Test connection.
- [ ] Invio parte solo dopo conferma.
- [ ] Delay rispettato.
- [ ] Pause/stop/retry failed funzionano.
- [ ] Fallback manuale disponibile.
- [ ] Nessun token nei log.

### Landing/release

- [ ] Landing EN navigabile.
- [ ] Landing IT navigabile.
- [ ] Privacy presente.
- [ ] Terms presenti.
- [ ] Download APK presente.
- [ ] Checksum presente.
- [ ] Changelog presente.
- [ ] Nessun analytics/tracking/form.
- [ ] Build debug funziona.
- [ ] Build release firmata funziona.

## Verifica anti-scope

Controllare che non siano stati aggiunti:

- backend proprietario;
- cloud sync;
- analytics;
- tracking;
- account utente;
- pagamenti;
- dashboard web;
- CRM API dirette;
- invio birthday automatico senza review.

## Criterio finale di accettazione

La release è accettabile solo se:

- non ci sono crash bloccanti;
- dati locali sono cifrati;
- backup/restore funzionano;
- birthday review non invia automaticamente;
- WhatsApp manuale resta manuale;
- landing e APK sono coerenti;
- documentazione release consente ripetizione build.

## Prompt da passare all'agente

```text
Esegui lo step 18 Final QA and Acceptance.

Non aggiungere nuove feature. Esegui verifica completa di onboarding, contatti, import, template, task, Birthday Review, WhatsApp manuale, backup/restore, landing e release. Controlla soprattutto che non esistano backend/tracking e che la birthday scan non possa mai inviare automaticamente. Correggi solo bug bloccanti o regressioni e restituisci report finale sintetico.
```
