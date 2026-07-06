# 07 — Contacts, Groups and Tags

## Obiettivo

Implementare gestione contatti, gruppi, tag, ricerca, filtri e dettaglio contatto.

## Scope

CRUD contatti e organizzazione locale. Non implementare ancora import file completo.

## Schermate

### Contacts

Funzioni:

- lista contatti;
- ricerca;
- filtri per gruppo;
- filtri per tag;
- filtri per status;
- filtri per import source;
- crea contatto;
- modifica contatto;
- archivia;
- elimina definitivamente con conferma forte.

### Contact Detail

Mostrare:

- nome;
- telefono;
- telefono normalizzato;
- email;
- azienda;
- birthday;
- tag;
- gruppo;
- note;
- status;
- task collegati placeholder se non ancora pronti;
- message log placeholder se non ancora pronto.

Azioni:

- edit;
- create task placeholder;
- send WhatsApp placeholder;
- delete.

### Groups

Funzioni:

- lista gruppi;
- crea gruppo;
- modifica gruppo;
- elimina gruppo se non rompe relazioni;
- assegnazione contatto a gruppo.

### Tags

Funzioni:

- aggiungi tag a contatto;
- rimuovi tag;
- crea dinamicamente tag;
- filtro per tag.

## Conferma cancellazione definitiva

Testo inglese:

```text
This will permanently delete the selected contact from OnlyReminder. This action cannot be undone unless you restore a backup.
```

Testo italiano:

```text
Questo eliminerà definitivamente il contatto selezionato da OnlyReminder. L'azione non può essere annullata, salvo ripristino da backup.
```

## Validazioni base

- `displayName` obbligatorio.
- Email valida se presente.
- Birthday valida se presente.
- Telefono può essere vuoto ma va evidenziato se necessario.
- Status default: `active`.

## Checklist implementazione

- [ ] Contacts list implementata.
- [ ] Search implementata.
- [ ] Filtri gruppo/tag/status implementati.
- [ ] Create contact implementato.
- [ ] Edit contact implementato.
- [ ] Contact detail implementato.
- [ ] Archive contact implementato.
- [ ] Permanent delete con conferma forte implementato.
- [ ] Groups list implementata.
- [ ] Create/edit group implementato.
- [ ] Tags su contatto implementati.
- [ ] UI EN/IT completata per queste schermate.
- [ ] Build debug compila.

## Criteri di accettazione

- L'utente può creare e modificare contatti.
- L'utente può cercare e filtrare contatti.
- L'utente può creare gruppi e assegnare contatti.
- L'utente può usare tag.
- La cancellazione definitiva richiede conferma forte.
- Nessun dato sensibile viene loggato.

## Prompt da passare all'agente

```text
Implementa lo step 07 Contacts, Groups and Tags.

Crea CRUD contatti, gruppi e tag, lista, dettaglio, ricerca, filtri, archiviazione e cancellazione definitiva con conferma forte. Non implementare ancora import file, birthday review o WhatsApp sending.

A fine lavoro restituisci implementazione, file modificati, controlli e limiti.
```
