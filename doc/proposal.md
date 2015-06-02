# Proposal programmeerproject
Paul Broek
10279741
Android

Probleemomschrijving
------------
Bij het maken van een gezamenlijke afspraak loopt men tegen het probleem aan dat de overlap tussen lege ruimtes in agenda's pas te achterhalen valt als men alle agenda's kent. Deze app zal er om draaien om door middel van het swipen over de weekdagen, vrije tijden af te leiden waarin afspraken (*events*) kunnen worden gepland. Een *initiator* van een *event* kan vrienden uitnodigen via Facebook of WhatsApp. Omdat ik me enkel op Android OS richt zullen de meeste gebruikers via een web client hun voorkeuren moet aangeven, maar dit valt voor zover ik nu kan bezien buiten het bereik van dit vak. De meerwaarde van deze app zal zijn dat de gebruikers zo snel en gerieflijk mogelijk tot een gemeenschappelijk tijdslot komen. Bestaande apps en sites voor dit probleem leunen erg zwaar op gefixeerde blokken van tijdsloten, terwijl door de swipe manier mogelijk een onvoorspelbare oplossing kan worden gevonden. 

Features
------------
* Globaal weekoverzicht vs gedetailleerd dag overzicht
* User input heel vriendelijk verwerken, van getallen/swipes naar kloktijden
* Swipe-invoer EN handmatig
* Facebook en WhatsApp invites sturen en ontvangen.

Data
-------------
Iedere gebruiker die is aangesloten bij een event levert zijn eigen 'data'. Een ArrayList<Day> met 7 items voor iedere gebruiker levert alle benodigde informatie om een week weer te geven. Daarnaast zal een SQLite implementatie nodig zijn om alle geregistreerde *events* op te slaan en op meerdere toestellen te raadplegen, eventueel uit te breiden met gebruikers gegevens.

Onderdelen
------------
* Openingsscherm met de mogelijkheid om een nieuw event aan te maken of eerdere events te wijzigen.
* *New event* scherm waar de *initiator* een titel met optionele omschrijving invoert en betrokken vrienden/personen selecteert d.m.v. Facebook / WhatsApp API.
* Een *Submit preferences* scherm waar de gebruiker eerst alle mogelijke dagen ziet met datum. De *initiator* zal dit scherm als eerste zien en ziet daarom bovendien de mogelijkheid om globaal aan te geven wanneer deze afspraak plaats zal hebben (in dagen). Daarna krijgt hij een blanco overzicht te zien van deze weekdage. Als hij vervolgens op een van de dagen klikt kan hij finetunen op welke momenten hij op die dag kan. 

Platform
-------------
Voor het uitnodigen van vrienden wordt de invitable_friends API van Facebook gebruikt; zeer gangbaar en goed gedocumenteerd.

Mogelijke problemen
-------------
* De mogelijkheid tot corrupte databases als een gebruiker zijn preferenties invoert, terwijl de initiator bepaalde dagen uit het event heeft gehaald. 

Vergelijkbare applicaties
-------------
* Doodle, werkt met vaste blokken
* 
Globale eerste schets
---------------
![Eerste schets](sketch1.png)
