# 11 — Tasks and Local Reminders

## Obiettivo

Implementare task/follow-up locali, reminder, notifiche e stati task.

## Scope

Task e reminder generici. Birthday Review resta nello step 12.

## Task types

- birthday;
- follow_up;
- custom.

## Priorità

- low;
- normal;
- high;
- urgent.

## Stati

- pending;
- done;
- skipped;
- failed;
- not_reviewed.

## Send modes

- reminder_only;
- manual_whatsapp;
- whatsapp_business_api.

## Funzioni UI

### Tasks list

- lista task;
- filtri per stato;
- filtri per data;
- filtri per priorità;
- ricerca;
- create task;
- edit task;
- complete;
- skip;
- delete.

### Task detail

Mostrare:

- titolo;
- descrizione;
- contatto collegato;
- gruppo collegato;
- due date/time;
- repeat rule;
- priorità;
- status;
- template collegato;
- send mode.

## Reminder

Implementare:

- notifiche locali;
- pianificazione con WorkManager per job non esatti;
- AlarmManager per reminder precisi solo se necessario e permesso disponibile;
- fallback se exact alarm non autorizzato.

## Nota Android

Non promettere precisione assoluta dei job periodici se il sistema non consente exact alarm.

## Checklist implementazione

- [ ] Tasks list implementata.
- [ ] Task detail implementato.
- [ ] Create/edit task implementato.
- [ ] Complete/skip/delete implementati.
- [ ] Filtri stato/data/priorità implementati.
- [ ] Collegamento contatto implementato.
- [ ] Collegamento gruppo implementato se supportato.
- [ ] Collegamento template implementato.
- [ ] Local notification implementata.
- [ ] Scheduling reminder implementato.
- [ ] Fallback exact alarm gestito.
- [ ] UI EN/IT completata.
- [ ] Build debug compila.

## Criteri di accettazione

- L'utente può creare task.
- L'utente riceve notifiche locali.
- L'utente può completare o saltare task.
- I task collegati a contatto sono visibili.
- L'app gestisce permessi notifiche in modo chiaro.
- Nessun invio automatico parte dai task.

## Prompt da passare all'agente

```text
Implementa lo step 11 Tasks and Local Reminders.

Crea task/follow-up locali con CRUD, stati, priorità, collegamento contatto/template, notifiche locali e scheduling. Gestisci WorkManager/AlarmManager in modo corretto rispetto ai limiti Android. Non implementare ancora Birthday Review né invio WhatsApp automatico.
```
