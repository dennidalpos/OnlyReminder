# 10 — Templates Engine

## Obiettivo

Implementare gestione template messaggi, variabili, preview e template birthday default EN/IT.

## Scope

CRUD template e rendering locale. Non inviare ancora messaggi.

## Canali template

- `whatsapp_manual`;
- `whatsapp_business_api`;
- `generic_reminder`.

## Variabili supportate

```text
{first_name}
{last_name}
{full_name}
{company}
{birthday}
{custom.field_name}
```

## Template default birthday

Inglese:

```text
Happy birthday {first_name}! Wishing you a wonderful day.
```

Italiano:

```text
Tanti auguri {first_name}! Ti auguro una splendida giornata.
```

## Funzioni UI

- lista template;
- crea template;
- modifica template;
- duplica template;
- elimina template;
- preview template;
- mostra variabili disponibili;
- imposta default birthday;
- canale template;
- lingua opzionale;
- nome template WhatsApp approvato per API mode.

## Rendering

Regole:

- variabili mancanti devono produrre fallback sicuro;
- preview deve evidenziare variabili non risolte;
- non crashare su template malformati;
- supportare emoji, link e testo libero.

## Warning commerciale

Se il template contiene parole configurabili come:

- promo;
- offerta;
- sconto;
- discount;
- deal;
- link HTTP/HTTPS;

mostrare warning:

Inglese:

```text
This message may be promotional. Make sure you have a proper legal basis before sending it.
```

Italiano:

```text
Questo messaggio potrebbe essere promozionale. Assicurati di avere una base giuridica adeguata prima di inviarlo.
```

## Checklist implementazione

- [ ] Template list implementata.
- [ ] Create template implementato.
- [ ] Edit template implementato.
- [ ] Duplicate template implementato.
- [ ] Delete template implementato.
- [ ] Preview implementata.
- [ ] Variabili base implementate.
- [ ] Custom fields variable resolver predisposto.
- [ ] Template birthday default EN creato.
- [ ] Template birthday default IT creato.
- [ ] Default birthday template configurabile.
- [ ] Warning commerciale implementato.
- [ ] UI EN/IT completata.
- [ ] Build debug compila.

## Criteri di accettazione

- L'utente può creare e modificare template.
- Il rendering sostituisce correttamente le variabili.
- La preview funziona con contatti reali.
- Emoji e link sono mantenuti.
- Il template birthday default esiste.
- Il warning commerciale appare quando appropriato.

## Prompt da passare all'agente

```text
Implementa lo step 10 Templates Engine.

Crea CRUD template, rendering variabili, preview, duplicazione, default birthday EN/IT e warning template potenzialmente commerciale. Non implementare invio WhatsApp. Il motore template deve essere riutilizzabile da Birthday Review, reminder e WhatsApp manuale/API.
```
