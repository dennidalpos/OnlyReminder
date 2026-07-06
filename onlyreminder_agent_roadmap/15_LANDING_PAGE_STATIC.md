# 15 — Static Landing Page

## Obiettivo

Creare landing page statica bilingue IT/EN per presentare OnlyReminder e scaricare APK.

## Scope

Landing statica, nessun backend, nessun form, nessun analytics.

## Struttura

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

## Vincoli

- Nessun analytics.
- Nessun tracking.
- Nessun form.
- Nessun cookie non essenziale.
- Nessuna libreria remota se evitabile.
- CSS locale.
- JS minimo locale.

## Hero EN

Titolo:

```text
OnlyReminder
```

Subtitle:

```text
Contact tasks, birthday reminders and WhatsApp follow-ups.
```

Descrizione:

```text
Import your contacts, review daily birthday reminders, and send personalized WhatsApp follow-ups from your Android phone.
```

CTA:

```text
Download APK
```

## Hero IT

Titolo:

```text
OnlyReminder
```

Subtitle:

```text
Task contatti, promemoria compleanni e follow-up WhatsApp.
```

Descrizione:

```text
Importa i tuoi contatti, verifica ogni giorno i compleanni e invia follow-up WhatsApp personalizzati dal tuo telefono Android.
```

CTA:

```text
Scarica APK
```

## Sezioni consigliate

- Local-first privacy.
- Import contacts.
- Birthday Review.
- WhatsApp manual follow-ups.
- Encrypted backup.
- Download APK.
- Checksum.
- Changelog.
- Privacy.
- Terms.

## APK download

File atteso:

```text
landing/assets/apk/OnlyReminder-latest.apk
```

Checksum:

```text
landing/assets/apk/checksums.txt
```

## Checklist implementazione

- [ ] Landing EN index creata.
- [ ] Landing EN download creata.
- [ ] Landing EN privacy creata.
- [ ] Landing EN terms creata.
- [ ] Landing IT index creata.
- [ ] Landing IT download creata.
- [ ] Landing IT privacy creata.
- [ ] Landing IT terms creata.
- [ ] CSS locale creato.
- [ ] JS locale minimo creato.
- [ ] Link download APK predisposto.
- [ ] Checksum section predisposta.
- [ ] Changelog collegato.
- [ ] Nessun analytics/tracking/form.
- [ ] Responsive base verificato.

## Criteri di accettazione

- Le pagine EN e IT sono navigabili.
- Il download punta al percorso APK corretto.
- Privacy e terms sono presenti.
- Changelog è presente.
- Nessun tracking è incluso.
- La landing funziona staticamente.

## Prompt da passare all'agente

```text
Implementa lo step 15 Static Landing Page.

Crea landing statica bilingue EN/IT con index, download, privacy, terms, CSS locale, JS minimo, changelog e predisposizione download APK/checksum. Non aggiungere form, analytics, tracking, cookie o dipendenze remote non necessarie.
```
