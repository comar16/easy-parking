# Easy Parking

### Introduzione
Il seguente progetto presenta lo sviluppo di un’applicazione server side pensata per la corretta gestione interna
delle zone di parcheggio nelle aree urbane.
Nel dettaglio, consente la prenotazione a distanza di un posteggio per veicoli di diversa natura,
negli spazi riservati a tale scopo. Al fine di comprenderne meglio l’utilità, si riportano di seguito,
alcune delle sue funzionalità.
Previa registrazione dell’utente, consente l’individuazione delle aree di parcheggio all’interno di
una determinata città con semplice indicazione della disponibilità di un posto auto. Individuato il
posteggio, per la sua prenotazione, si procede con il pagamento digitale del ticket, la cui tariffa viene
determinata in base alla colorazione delle strisce e al tempo che si desidera sostare.
La corretta esecuzione di tale procedimento permette, inoltre, una più efficace amministrazione delle
aree di parcheggio da parte della polizia locale.
In conclusione, l’applicazione in questione ha il doppio obiettivo di facilitare il conducente nella ricerca di un
adeguato posteggio per il proprio veicolo, in grado di soddisfare meglio le sue esigenze ottimizzando
i tempi, nonché, grazie al sostegno della digitalizzazione, si presta come valido aiuto per le guardie
municipali incaricate della conduzione del traffico urbano.

### Analisi dei requisiti e funzionalità
Le funzionalità che si pone si suddividono in 3 categorie:
 - _**Pubbliche**_
 - _**Utente**_
 - _**Amministratore**_

Le funzionalità _**pubbliche**_ comprendono:
 - La login sia di un utente che di un amministratore per effettuare l'accesso al proprio account, alle proprie informazioni e a tutti i servizi
 - La Sign up in cui ogni utente non registrato può farlo inserendo tutti i dati richiesti (nome, cognome, email, password)
 - La Forgot Password, che permette sia all’utente che all'amministratore di ricevere una nuova password nel caso in cui l’avesse dimenticata
   
Le funzionalità _**utente**_ comprendono:
 - Cambio password
 - Ricerca delle aree di sosta disponibili, sulla base di svariati filtri che può attribuire quali:
   - luogo di destinazione
   - funzionalità dell'area di sosta (Car, HandicapCar, ElectricCar, Bus, Camper, Roulette, Motorcycle, LoadingAndUnloadingZone)
   - colorazione delle strisce delle aree di sosta, ognuna con la propria tariffa (White, Blue, Yellow, Pink, Green)
   - dimensione e tipo dell'area di sosta (Nastro, Spina, Pettine)
   - veicolo da parcheggiare
 - Caricamento d'immagini della propria patente
 - Download delle immagini della propria patente
 - Aggiunta di veicoli
 - Visualizzazione di:
   - Profilo utente
   - Tutti i veicoli inseriti e che, potranno essere scelti per semplificare la ricerca
   - Tutti i modelli di veicoli, che saranno attribuiti al veicolo al momento della creazione per ricavarne le  dimensioni 
   - Tutte le colorazioni delle strisce e le relative tariffe orarie/giornaliere/settimanali/mensili
   - Tutti i tipi e le dimensioni delle aree di sosta
   - Tutti i ticket generati
   - Tutti le multe (pagate/non pagate)
 - Generazione di un ticket con relativa prenotazione dell'area di sosta
 - Pagamento dei ticket
 - Pagamento di eventuali multe
 
Le funzionalità _**amministratore**_ (Membro della Polizia Locale) comprendono:
  - Caricamento d'immagini del proprio tesserino
  - Download delle immagini del proprio tesserino
  - Inserimento/aggiornamento/rimozione di una colorazione di strisce
  - Inserimento/aggiornamento/rimozione di un'area di sosta
  - Inserimento/aggiornamento/rimozione di una nuova tariffa (oraria/giornaliera/settimanale/mensile)
  - Gestione stalli con componentistica IoT danneggiata
  - Inserimento/aggiornamento di multe
  - Visualizzazione di:
    - Profilo amministratore
    - Lista degli utenti
    - Lista degli amministratori
    - Lista delle multe di un certo utente
    - Tutte le aree di sosta
    - Tutti le colorazioni di strisce delle aree di sosta
    - Tutti i tipi e le dimensioni delle aree di sosta

### API e Documentazione Swagger

Sono state sviluppate svariate API, affinché tutte le funzionalità fossero implementate.
La documentazione delle stesse si trova al seguente link: [Documentazione EasyParking Swagger](https://app.swaggerhub.com/apis/comar_16/EasyParking/1.0.0)


### Schema ER DataBase
Il servizio di database scelto per EasyParking è MySql, che permette di gestire un database relazionale.
Il Database realizzato è composto dalle seguenti tabelle, per il raggiungimento di determinati obiettivi:

 - Account : tutte le informazioni riguardante ogni singolo account tra cui credenziali, ruolo (User/Admin) ed eventuali dati di reset password
 - User : tutte le informazioni riguardante ogni singolo utente tra cui anagrafica e stato (Approved, Rejected, Pending)
 - License : tutte le informazioni sulla patente inserita dall'utente
 - Admin : tutte le informazioni riguardante ogni singolo amministratore tra cui anagrafica e stato (Approved, Rejected, Pending)
 - PoliceCard : tutte le informazioni sul tesserino delle forze dell'ordine
 - Ticket : tutte le informazioni sui ticket generati tra cui prezzo totale e scadenza
 - Fine : tutte le informazioni sulle multe tra cui punti da rimuovere, prezzo totale e scadenza
 - PaymentInfo : tutte le informazioni sui pagamenti
 - ParkingArea : tutte le informazioni sulle aree di sosta tra cui colorazione strisce, dimensione, tipo e stato (Free, Busy, Damaged, Deleted) 
 - ParkingAreaColor : tutte le informazioni sulla colorazione delle strisce e le relative tariffe
 - ParkingAreaTypeDimension : tutte le informazioni su dimensioni e tipi delle aree di sosta
 - Vehicle : tutte le informazioni sui veicoli inseriti dall'utente e relativa targa
 - ModelVehicle : tutte le informazioni sui vari modelli di veicoli disponibili
 

![ER Database Diagram](./EasyParkingDB.png)


###Use Case Diagram
Sono stati creati degli Use Case Diagram disponibili in ./UseCaseDiagram/ nei formati .puml e .png. Questi,
descrivono le funzioni o servizi offerti dal sistema, così come sono percepiti e utilizzati dagli attori che interagiscono col sistema stesso,
nei seguenti 3 casi d’uso:

- Funzionalità pubbliche
- Funzionalità utente
- Funzionalità amministratore
#### Funzionalità pubbliche
![Use Case D](./UseCaseDiagram/EasyParkingUseCasePublic.png)
#### Funzionalità utente
![Use Case D](./UseCaseDiagram/EasyParkingUseCaseUser.png)
#### Funzionalità amministratore
![Use Case D](./UseCaseDiagram/EasyParkingUseCaseAdmin.png)

###Sequence Diagram
Sono stati creati dei Sequence Diagram disponibili in ./SequenceDiagram/ nei formati .puml e .png. Questi, descrivono la successione di operazioni
e messaggi di risposta tra i vari componenti dell’intero sistema, nei seguenti 3 casi
d’uso:
 - Login
 - Aggiunta veicolo
 - Pagamento multa
#### Login
![Use Case D](./SequenceDiagram/EasyParkingSequenceLogin.png)
#### Aggiunta veicolo
![Use Case D](./SequenceDiagram/EasyParkingSequenceAddVehicle.png)
#### Pagamento multa
![Use Case D](./SequenceDiagram/EasyParkingSequencePaymentFine.png)

### Struttura del codice
- Linguaggio di programmazione: Java
- Framework: Spring Boot
- Realizzazione dei package seguendo il modello:
    - configuration (configurazioni d'interfaccia con DataBase, MailTrap e PayPal)
    - controller (gestione delle richieste, chiamando i metodi dai vari services)
    - entity (vero e proprio mapping delle tabelle del DataBase)
    - repository (gestione delle query)
    - security (gestione dell'autenticazione)
    - service (insieme di metodi pronti a svolgere operazioni)
    - utils (modelli specifici di request, response e regex)

### Piano di Testing

I tests implementati si suddividono in:

* Test per i Service: per verificate la correttezza delle operazioni svolte dai vari metodi
    * Test per le operazioni pubbliche
    * Test per le operazioni dell'utente
    * Test per le operazioni dell'amministratore
* Test per i Controllers: per verificare la correttezza degli end points e dello status code restituito
    * Test per il Controller pubblico
    * Test per il Controller utente
    * Test per il Controller amministratore
  
#### Rapporto sull'andamento dei test

I log del successo dei test sono visualizzabili al seguente link: [Tests Result EasyParking](./TestResults-EasyParking.html)
### Ipotetici miglioramenti futuri

Il sistema server side attualmente non predispone di:
- un servizio di chat tra clienti e assistenza
- una gestione di suddivisione tariffaria Comunale
- la creazione di nuovi amministratori (membri della polizia locale) approvati dagli stessi

Queste migliorie potrebbero essere apportate per avere un’esperienza utente migliore.