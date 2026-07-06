# 13 — WhatsApp Manual Mode

## Obiettivo

Implementare modalità WhatsApp manuale: apertura WhatsApp/WhatsApp Business con messaggio
precompilato, invio manuale da parte dell'utente e avanzamento contatto successivo.

## Vincolo

In questa modalità non deve esistere invio automatico reale.

L'app prepara e apre la chat. L'utente deve premere invia dentro WhatsApp.

## Flusso

1. Utente seleziona contatti da Birthday Review o task.
2. Utente preme `Start manual sending`.
3. App genera messaggio dal template.
4. App apre WhatsApp o WhatsApp Business con chat e messaggio precompilato.
5. Utente invia manualmente.
6. Utente torna all'app.
7. App propone contatto successivo.
8. App marca item/log come `manual_opened`.

## Requisiti

- Supportare telefono normalizzato.
- Gestire telefono mancante/non valido.
- Gestire WhatsApp non installato.
- Fallback con copia messaggio.
- Log locale `manual_opened`.
- Non marcare come `sent` perché l'app non può verificare invio manuale reale.
- Permettere skip del contatto corrente.
- Permettere stop flusso.

## UI

Schermata manual sending:

- contatto corrente;
- progresso;
- preview messaggio;
- pulsante open WhatsApp;
- pulsante skip;
- pulsante stop;
- prossimo contatto.

## Checklist implementazione

- [ ] Intent apertura WhatsApp implementato.
- [ ] Intent apertura WhatsApp Business valutato/implementato se possibile.
- [ ] Messaggio precompilato implementato.
- [ ] Generazione messaggio da template implementata.
- [ ] Flow prossimo contatto implementato.
- [ ] Gestione telefono invalido implementata.
- [ ] Gestione WhatsApp non installato implementata.
- [ ] Fallback copia messaggio implementato.
- [ ] Log `manual_opened` implementato.
- [ ] Nessun auto-send implementato.
- [ ] UI EN/IT completata.
- [ ] Build debug compila.

## Criteri di accettazione

- L'app apre WhatsApp con messaggio precompilato.
- L'utente deve inviare manualmente.
- L'app non dichiara `sent` se non può verificarlo.
- L'app prosegue al contatto successivo.
- Gli errori sono chiari.
- Birthday Review mantiene il vincolo di azione esplicita.

## Prompt da passare all'agente

```text
Implementa lo step 13 WhatsApp Manual Mode.

Crea modalità manuale: da Birthday Review o task selezionati, genera messaggio template, apre WhatsApp con testo precompilato, lascia invio all'utente, torna all'app e passa al contatto successivo. Registra solo manual_opened, non sent. Gestisci telefono invalido, WhatsApp non installato e fallback copia messaggio.
```
