# my_virtual_shelf
# 📚 Libreria Virtuale Android (Java)

**Libreria Virtuale** è un'applicazione Android nativa sviluppata in Java che permette di catalogare e gestire la propria collezione di libri e manga. L'app consente di acquisire l'ISBN di un volume tramite la fotocamera dello smartphone, recuperare automaticamente i dettagli online (Titolo, Autore, Copertina) e salvarli in un database locale sul dispositivo.

---

## 🚀 Caratteristiche Principali

* 📸 **Scansione ISBN Rapida**: Riconoscimento istantaneo del codice a barre tramite fotocamera.
* 🌐 **Recupero Automatico Dati**: Integrazione con le API pubbliche di *Open Library* per scaricare titolo, autori e copertina.
* 💾 **Database Locale**: Salva l'intera libreria direttamente sul dispositivo per una consultazione offline rapida.
* 🔍 **Ricerca e Filtro**: Ricerca istantanea nella propria collezione per titolo o autore.
* 📖 **Stato di Lettura**: Gestisci lo stato dei volumi (*"Da leggere"*, *"In lettura"*, *"Completato"*).
* ✏️ **Personalizzazione e Modifica**: Possibilità di correggere i dati inseriti e cambiare lo stato di lettura con un tocco.
* 🗑️ **Gestione Semplice**: Eliminazione rapida di un volume tramite pressione prolungata.

---

## 🛠️ Tech Stack & Librerie

| Componente | Tecnologia |
| :--- | :--- |
| **Linguaggio** | Java |
| **Architettura** | Android Jetpack (Activity, Views, DAO) |
| **Fotocamera** | [CameraX](https://developer.android.com/training/camerax) |
| **Scansione Barcode** | [Google ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning) |
| **Database Locale** | [Room Database](https://developer.android.com/training/data-storage/room) |
| **Rete & API** | [Retrofit 2](https://square.github.io/retrofit/) + Gson Converter |
| **Caricamento Immagini** | [Glide](https://github.com/bumptech/glide) |
| **Fonte Dati** | [Open Library API](https://openlibrary.org/dev/docs/api/books) |

---

## 📋 Requisiti del Sistema

* **Android Studio**: Jellyfish (2024.1.1) o superiore.
* **JDK**: 11 o 17.
* **Minimum SDK**: API 24 (Android 7.0 Nougat).
* **Target SDK**: API 34 o superiore.

---

