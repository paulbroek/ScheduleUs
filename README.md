# ScheduleUs
Programmeerproject UvA, juni 2015  
Paul Broek  
10279741  
OS: **Android**

Probleemomschrijving
------------
Bij het maken van een gezamenlijke afspraak loopt men tegen het probleem aan dat de overlap tussen lege ruimtes in agenda's pas te achterhalen valt als men alle agenda's kent. Deze app zal er om draaien om door middel van het swipen over de weekdagen, vrije tijden af te leiden waarin afspraken (*events*) kunnen worden gepland. Een *initiator* van een *event* kan vrienden uitnodigen om tot gezamenlijke tijdsloten te komen. De meerwaarde van deze app zal zijn dat de gebruikers zo snel en gerieflijk mogelijk tot een gemeenschappelijk tijdslot komen. 

Features
------------
* Globaal weekoverzicht vs gedetailleerd dag overzicht
* User input vriendelijk verwerken, van getallen/swipes naar kloktijden
* Andere ScheduleUs gebruikers inviten.
* Synchroniseren met Parse database voor gebruikersdata en beschikbare tijden van gebruikers
* My Events voor event overzicht en nieuwe time slot invoer

Data
-------------
Iedere gebruiker die is aangesloten bij een event levert zijn eigen 'data'. Een ArrayList<int[]> met 7 time items voor iedere gebruiker levert alle benodigde informatie om bijvoorbeeld een week weer te geven. Data wordt in Maps en ArrayLists opgeslagen. Daarnaast is er een vertaalslag naar de Parse database, waar alles in JSON objecten is.  

Onderdelen
-----------------
* Openingsscherm met de mogelijkheid om een nieuw event aan te maken of eerdere events te wijzigen.
* *New event* scherm waar de *initiator* een titel invoert en dagen selecteert.
* Een *Select Days* scherm waar de gebruiker eerst alle mogelijke dagen ziet met datum. De *initiator* zal dit scherm als eerste zien en ziet daarom bovendien de mogelijkheid om globaal aan te geven wanneer deze afspraak plaats zal hebben (in dagen via een TimePicker). Daarna krijgt hij een blanco overzicht te zien van deze weekdagen. Als hij vervolgens op een van de dagen klikt kan hij finetunen op welke momenten hij op die dag kan door middel van swipes.
* Een overzichts scherm *My Events* met alle events waar de gebruiker aan deelneemt.

Bibliotheken en acknowledgements
-------------------------------------
* Guava library v17.0, used under Apache License 2.0
* Parse.com library en database utilities, code van example app AnyWall voor het registreren en inloggen van gebruikers https://github.com/ParsePlatform/AnyWall/tree/master/AnyWall-android
* Google GSON library v2.3, used under Apache License 2.0
* **Lucasr TwoWayView**, voor het gebruik van een horizontale ListView. Copyright Lucas Rocha, 2013. 
* Font **Unique.ttf**, Open Font License, gemaakt door designer Anna Pocius, http://openfontlibrary.org/en/font/unique

Apache license
----------------
Copyright [2015] [Paul Broek]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Vergelijkbare applicaties
-------------
* Doodle, werkt met vaste blokken en mensen uitnodigen per email. 
* Calendly.com, synchroniseert met Google Calendar, zodat ook nieuwe gezamenlijke afspraken meteen worden "opgeschreven" in ieders agenda. Werkt handig met repeterende of periodieke afspraken. 
* needtomeet.com lijkt het meest op mijn concept plan. Ze richten zich op grote groepen mensen, die allen per dag blokken selecteren waarin ze beschikbaar zijn.

Screenshots
----------------------------

![](docs/home (Mobile).png)
![](docs/log_in (Mobile).png)
![](docs/sign_up (Mobile).png)
![](docs/main (Mobile).png)
![](docs/event_dialog (Mobile).png)
![](docs/new_event (Mobile).png)
![](docs/select_days (Mobile).png)
![](docs/select_days_filled (Mobile).png)
![](docs/select_times (Mobile).png)
![](docs/invite (Mobile).png)
![](docs/my_events (Mobile).png)
![](docs/select_days_participant (Mobile).png)


Eerste schetsen
---------------
![Eerste schets](docs/sketch1.jpg)
