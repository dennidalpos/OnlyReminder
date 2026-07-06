# 12 — Birthday Review Core

## Obiettivo

Implementare la funzione centrale di OnlyReminder: scansione giornaliera compleanni, creazione
BirthdayRun, notifica e review obbligatoria prima di qualunque invio.

## Vincolo non negoziabile

L'app non deve mai inviare messaggi birthday subito dopo la scansione background.

Flusso obbligatorio:

1. Job locale trova contatti con compleanno oggi.
2. Crea `BirthdayRun`.
3. Crea `BirthdayRunItem`.
4. Mostra notifica.
5. L'utente apre review.
6. L'utente seleziona/deseleziona/rimuove.
7. Solo dopo azione esplicita può partire invio manuale o API.

## Orario default

```text
09:00
```

Configurabile nelle impostazioni.

## Notifica

EN:

```text
Today there are {count} birthday contacts to review.
```

IT:

```text
Oggi ci sono {count} contatti con compleanno da verificare.
```

## Se l'utente non apre

- Nessun invio automatico.
- Lista pending fino a mezzanotte.
- Dopo mezzanotte status:

```text
not_reviewed
```

## Birthday Review Screen

Mostrare:

- data corrente;
- totale contatti trovati;
- totale selezionati;
- contatti senza telefono;
- contatti con telefono non valido;
- contatti già contattati oggi;
- filtro gruppo;
- filtro tag;
- ricerca;
- checkbox per contatto;
- anteprima messaggio;
- scelta template;
- azioni per contatto;
- azioni massive.

## Azioni per contatto

- Non inviare in questo turno.
- Cancellare definitivamente il contatto.
- Aprire dettaglio contatto.
- Modificare telefono.
- Visualizzare preview messaggio.

## Azioni massive

- Select all.
- Deselect all.
- Send selected.
- Skip selected.
- Delete selected con conferma forte.

## Stati consigliati

BirthdayRun:

- pending_review;
- reviewed;
- sending;
- completed;
- not_reviewed.

BirthdayRunItem:

- pending;
- selected;
- unselected;
- skipped_this_run;
- deleted_contact;
- manual_opened;
- sent;
- failed.

## Checklist implementazione

- [ ] Job giornaliero birthday implementato.
- [ ] Matching giorno/mese compleanno implementato.
- [ ] BirthdayRun creato.
- [ ] BirthdayRunItem creati.
- [ ] Notifica birthday implementata.
- [ ] Nessun invio parte dal job.
- [ ] Review screen implementata.
- [ ] Checkbox selezione implementate.
- [ ] Filtri gruppo/tag/ricerca implementati.
- [ ] Preview messaggio implementata.
- [ ] Cambio template in review implementato.
- [ ] Skip contatto per turno implementato.
- [ ] Delete contatto da review con conferma forte implementato.
- [ ] Select all/deselect all implementati.
- [ ] Marcatura not_reviewed dopo mezzanotte implementata.
- [ ] UI EN/IT completata.
- [ ] Build debug compila.

## Criteri di accettazione

- La scansione giornaliera crea solo una coda revisionabile.
- Nessun messaggio parte automaticamente.
- L'utente può rimuovere contatti dalla run.
- L'utente può cancellare definitivamente contatti.
- L'utente può vedere preview.
- Se la notifica non viene aperta, la run diventa `not_reviewed`.
- L'invio richiede azione esplicita.

## Prompt da passare all'agente

```text
Implementa lo step 12 Birthday Review Core.

Crea scansione giornaliera compleanni, BirthdayRun, BirthdayRunItem, notifica e schermata review completa. Il job deve solo creare una lista revisionabile e notificare: non deve mai inviare messaggi. Implementa filtri, selezione, skip, delete con conferma forte, preview template e status not_reviewed dopo mezzanotte.
```
