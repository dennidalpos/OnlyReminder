# 14 — Backup, Restore and Export

## Obiettivo

Implementare backup locale cifrato, restore, export manuale e retention.

## Scope

Backup/restore locale. Nessun cloud backup.

## Tipi backup

- backup automatico giornaliero;
- backup manuale;
- backup prima di import massivo;
- backup prima di cancellazioni massive.

## Nome file

```text
OnlyReminder_Backup_YYYY-MM-DD_HHMM.orbackup
OnlyReminder_Backup_YYYY-MM-DD_HHMM.sha256
```

## Cartella

L'utente sceglie cartella tramite Storage Access Framework.

Default suggerito:

```text
Documents/OnlyReminder/Backups
```

Non usare path diretto non autorizzato.

## Cifratura backup

Non basarsi solo su Android Keystore se il backup deve essere ripristinabile dopo reinstallazione o
su altro dispositivo.

Implementare:

- password o recovery key backup;
- salt;
- KDF robusto;
- nonce;
- payload cifrato;
- checksum o HMAC;
- versione formato backup;
- verifica integrità.

## Restore

Flusso:

1. Utente seleziona file backup.
2. App verifica formato e integrità.
3. App richiede password/recovery key se prevista.
4. App mostra conferma forte.
5. App crea backup dello stato attuale.
6. App ripristina.
7. App riavvia o ricarica database.

## Export

Due modalità:

1. export cifrato;
2. export leggibile CSV/JSON solo su richiesta esplicita.

Warning export non cifrato EN:

```text
This export is not encrypted. Anyone with access to the file may read your contacts.
```

Warning export non cifrato IT:

```text
Questo export non è cifrato. Chiunque abbia accesso al file potrebbe leggere i tuoi contatti.
```

## Retention

Default:

```text
10 backup
```

Configurabile.

## Checklist implementazione

- [ ] Backup folder picker integrato.
- [ ] Backup manuale cifrato implementato.
- [ ] Backup automatico giornaliero implementato.
- [ ] Backup pre-import implementato.
- [ ] Backup pre-delete-massivo implementato.
- [ ] Formato `.orbackup` versionato.
- [ ] File `.sha256` creato.
- [ ] Verifica integrità implementata.
- [ ] Restore implementato.
- [ ] Backup stato attuale prima di restore implementato.
- [ ] Retention implementata.
- [ ] Export cifrato implementato.
- [ ] Export CSV/JSON non cifrato con warning implementato.
- [ ] UI EN/IT completata.
- [ ] Build debug compila.

## Criteri di accettazione

- L'utente può creare backup cifrato.
- L'utente può ripristinare backup valido.
- Backup corrotto viene rifiutato.
- Retention elimina solo backup eccedenti.
- Export non cifrato richiede conferma esplicita.
- Backup pre-import e pre-delete vengono chiamati.

## Prompt da passare all'agente

```text
Implementa lo step 14 Backup, Restore and Export.

Crea backup locale cifrato in cartella scelta dall'utente, restore con verifica integrità, retention, backup automatico giornaliero, backup pre-import/pre-delete e export manuale cifrato/non cifrato con warning. Non usare cloud. Progetta backup ripristinabile anche dopo reinstallazione usando password/recovery key.
```
