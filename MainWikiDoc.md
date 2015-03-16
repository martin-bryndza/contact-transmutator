# Contact Transmutator #
### Jakub Svoboda ###
### Martina Hlinková ###
### Martin Bryndza ###
### Martin Molnár ###
'''Table of Contents'''

[1. Úvod a špecifikácia projektu](#d53e26.md)

[Úvod](#d53e29.md)

[Členovia projektu, náplň práce a umiestnenie projektu](#d53e36.md)

[Motivácia](#d53e71.md)

[Špecifikácia](#d53e78.md)

[2. Štruktúra projektu](#d53e88.md)

[Vysvetlenie niektorých pojmov](#d53e91.md)

[Štruktúra](#d53e111.md)

[3. Informácie o použitých formátoch vstupných/výstupných dát](#d53e122.md)

[CSV](#d53e125.md)

[ODS](#d53e145.md)

[VCF](#d53e152.md)

[4. Špecifikácia jednotlivých implementácií](#d53e199.md)

[InputFilter](#d53e202.md)

[InternalDocColumnSchema](#d53e224.md)

[InternalDoc2CompiledDoc](#d53e243.md)

[InternalDocAutodetectFormat](#d53e261.md)

[OutputFilter](#d53e268.md)

[VCFHelper](#d53e275.md)

[ReadCSV](#d53e282.md)

[ODSInput](#d53e289.md)

[ReadVCF](#d53e296.md)

[ReadCompiledDoc](#d53e303.md)

[InternalDocColumnSchemaImpl](#d53e311.md)

[InternalDocAutodetectFormatImpl](#d53e330.md)

[InternalDocCompiler](#d53e336.md)

[VCFConverter](#d53e342.md)

[VCFHelperImpl](#d53e349.md)

[WriteCSV](#d53e356.md)

[ODSWrite](#d53e363.md)

[WriteVCF](#d53e370.md)

[VCFTypesEnum](#d53e377.md)

[5. Návod na použitie](#d53e384.md)

[Návod na použitie](#d53e387.md)

'''List of Examples'''

1.1. [Príklad operácie aplikácie](#d53e83.md)

3.1. [Priklad CSV](#d53e136.md)

3.2. [Príklad vCard 3.0](#d53e163.md)

## Chapter 1. Úvod a špecifikácia projektu ##
'''Table of Contents'''

[Úvod](#d53e29.md)

[Členovia projektu, náplň práce a umiestnenie projektu](#d53e36.md)

[Motivácia](#d53e71.md)

[Špecifikácia](#d53e78.md)

## Úvod ##
Projekt Contact Transmutator vznikol ako súčasť výuky predmetu PB138 - Moderní značkovací jazyky a jejich aplikace vyučovanej na fakulte informatiky Masarykovej univerzity počas obdobia jarného semestru 2011 pod vedenim doc. RNDr. Tomáša Pitnera Ph.D.

Cieľom projektu bolo naučit sa pracovať s profesionálnymi nástrojmi pre komunitný vývoj, schopnosť pracovať a spracovávať dokumenty vo formátoch XML a osvojenie si práce so základnými štandardami a technológiami moderných značkovacích jazykov na báze XML a ich aplikacií.

## Členovia projektu, náplň práce a umiestnenie projektu ##
Členovia vývoja tohto projektu sú:

**Jakub Svoboda****=== Note ===
základná kostra projektu, jeho základných rozhraní a implementácií funkcii; príprava filtrov pre CSV** Martin Bryndza
 === Note ===
programovanie grafického GUI; príprava filtrov pre CompiledDoc
**Martina Hlinková****=== Note ===
príprava filtrov pre ODS (Open Document Format - spreadsheets); dokumentácia v DocBook** Martin Molnár
 === Note ===
príprava filtrov pre VCF; testovacie nástroje

Projekt je umiestnený na hostovskej stránke Google Code: [http://code.google.com/p/contact-transmutator](#.md) , kde sa dajú zhliadnuť wikistránky a návod, prípadne stiahnúť projekt.

## Motivácia ##
Adresáre s kontaktami sú neoddelitelnou súčasťou moderného spôsobu života. V dnešnej dobe avšak existuje nepreberné množstvo formátov na uchovávanie kontaktov, adries, čísel, poznámok atď. Problém nastáva ak isté zariadenia preferujú len jeden z formátov. Prenos týchto citlivých dát sa preto stáva nepríjemne zdĺhavou zálezitosťou. ďalsí problém nastáva s mentalitou konkrétnych užívateľov a ich potrieb. Každý jeden užívateľ múže svoje kontakty zaznamenávať inými spôsobmi nielen softwérovo ale i logicky.

Náš program Contact Transmutator je navrhnutý na riešenie tohoto problému, ako transformáciu formátov tak logické tiedenie informácií v štruktúre kontaktov.

## Špecifikácia ##
Contact Transmutator parsuje neštandardne uložené dáta (napr. meno a číslo mobilného telefónu v jednom stĺpci záznamu formátoch VCF, ODS, CSV). Aplikácia je schopná automaticky detekovať rôzne typy dát a ich hodnoty a rozparsovať ich do interného XML formátu. Užívateľovi je tiež umožnené určiť istú základnú parsovaciu logiku pomocou jednoduchého a intuitívneho grafického rozhrania. Navyše si užívateľ môže určiť v akom formáte sa uložia jeho kontakty.

'''Example&nbsp;1.1.&nbsp;Príklad operácie aplikácie'''

Používateľ má kontakty v MS Office Excel. V dokumente neexistuje takmer žiadna globálna logika (jeden riadok záznamu obsahuje niekoľko kontaktov, ktoré majú spolu neurčený vzťah, telefónne čísla a mená sú zmixované v jednom stĺpci, niektoré významné informácie sú v podobe poznámok). Užívateľ chce importovať tieto dáta do svojho mobilného telefónu. Ako prvý krok vyexportuje dáta z MS Office Excel do formátu CSV a použije aplikáciu Contact Transmutator pre zadelenie kontaktov do istej logickej štruktúry a uloženie do formátu, ktorý podporuje jeho mobilný telefón.


## Chapter 2. Štruktúra projektu ##
'''Table of Contents'''

[Vysvetlenie niektorých pojmov](#d53e91.md)

[Štruktúra](#d53e111.md)

## Vysvetlenie niektorých pojmov ##
**''InternalDoc'' = Vnútorný XML dokument, ktorý sa používa na načítanie informácií do Contact Transmutator. Dáta zo vstupných súborov sa parsujú do tohoto jednoduchšieho XML dokumentu. Funguje ako medzičlánok ku CompiledDoc. Neobsahuje informácie o type dát, ktoré sa v ňom nachádzajú.** ''CompiledDoc'' = Vnútorý XML dokument, ktorý sa používa na spracovávanie informácií a ich parsovanie do výstupných filtrov. Obsahuje informácie o typoch dát v jednotlivých stĺpcoch/bunkách zoznamu kontaktov.
**''Stĺpcové schéma'' = Pomocné XML, ktoré obsahuje informácie o jednotlivých stĺpcoch zoznamu.**

## Štruktúra ##
Projekt sa delí na niekoľko samostaných častí, ktoré spolu komunikujú pomocou vnútorných XML dokumentov zmienených vo vysvetlení niektorých pojmov.

Prvá časť, Input, zahŕňa všetky vstupné filtry, ktoré berú zvolený dokument, načítavajú a parsujú z neho informácie, ktoré následne vypisujú do InternalDoc(resp. CompiledDoc pri vCard). V tejto časti sa nehľadí na obsah jednotlivých dokumentov. Z CSV sa parsujú informácie tak, že každý textový reťazec medzi čiarkami sa vloží do jednej položky neformátovaného kontaku a jeden riadok vždy značí jeden kontakt. Z ODS sa otvorí zip a z XML content.xml (nesie informácie len o základnom formátovaní a obsahy jednotlivých tabuliek) sa načítajú informácie taktiež do InternalDoc. Každý riadok tabuliek predstavuje jeden kontakt a text každej bunky sa uloží do samostatnej položky. Výnimkou je VCF, pretože jeho štruktúra umožnuje získavanie informácíí o každej položke v kontakte. Tieto informácie (na začiatku každého riadku) sa ľahko čítajú a je možné vytvorenie CompiledDoc bez medzistupňa.

Druhá časť je úzko spojená s GUI programu. Zahrňuje prevod InternalDoc do CompiledDoc. To znamená, že podľa výberu užívateľa v GUI sa mení XML stĺpcového schématu a následne sa podľa neho upravuje/vytvára CompiledDoc. Táto časť sa môže opakovať ľubovolný počet krát, kým užívateľ nie je spokojný.

Posledná výstupná časť, Output, sa stará o prevod CompiledDoc do zvoleného formátu na miesto určené užívateľom.

## Chapter 3. Informácie o použitých formátoch vstupných/výstupných dát ##
'''Table of Contents'''

[CSV](#d53e125.md)

[ODS](#d53e145.md)

[VCF](#d53e152.md)

## CSV ##
CSV(Comma-seperated values, hodnoty oddelené čiarkami) je jednoduchý súborový formát pre výmenu tabulkových dát. Súbor vo formáte CSV pozostáva z riadkov, v ktorých sú jednotlivé položky oddelené znakom čiarka (,). Hodnoty položiek môžu byť uzavreté do úvodzoviek (""), čo umožňuje, aby text položky obsahoval čiarku. Ak text položky obsahuje úvodzovky, sú zdvojené.

Keďže sa v niektorých jazykoch čiarka používa ako oddelovač desatinných miest, existujú varianty, ktoré používajú iný znak pre oddelovanie položiek než čiarku, najčastejšie bodkočiarku, prípadne tabulátor (taká varianta sa potom označuje ako TSV, Tab-separated values). Variantu s bodkočiarkou používa napríklad Microsoft Excel.

Pre tento formát neexistuje špecifikácia, popis formátu sa však nachádza v RFC 4180. [http://tools.ietf.org/html/rfc4180](#.md)

'''Example&nbsp;3.1.&nbsp;Priklad CSV'''

1995,Opel,Vectra,"klimatizácia, strešné okno",45000

1998,Škoda,"Felicia ""Fun""",,80000

2002,Škoda,Octavia,"klimatizácia, ABS poškodená",70000


## ODS ##
ODS je tabuľkový formát, ktorý je časťou The Open Dokument Format pre kancelárske aplikácie. ODF je opensource špecifikácia založená na XML formáte, vyvinutá organizáciou OASIS a implementovaná v OpenOffice.

ODS je formát vyvinutý pre tabuľkový program Calc, ktorý je súčasťou StarOffice alebo OpenOffice.Tento typ formátu pozostáva z tabuliek schopných nachádzať informácie, používať rôzne deklarácie, pracovať s numerickými dátami, matematickými formulami a grafmi. Každá tabuľka a jej analýza je uzavretá v dokumentovom prelúdiu a epilógu. Prelúdium pozostáva z formátovacích dát, možností použitia matematických formulý, pravidiel pre bunkový obsah, informáciách o zmenách atď. Rozsahy databázy, mená deklarácii a operácií a linky sú uvedené v epilógu.

## VCF ##
vCard je súborový formát pre výmenu osobných dát, predovšetkým elektronické obchodné vizitky. vCards sú najčastejšie priložené v e-mail správach, ale môžu sa vymieňať aj inými cestami, najčastešie prostredníctvom WWW stránok. Môžu obsahovať meno a priezvisko, adresy, telefónne čísla, URL adresy, logá, fotografie ako aj audio klipy. Súbory vCard sú ukladané vo formáte vcf, v ktorých sa ukladajú aj kontakty napríklad v mobilných telefónoch značky Nokia.

Verzia 2.1 je široko využívaná a podporovaná e-mailovými klientami. Verzia formátu vCrad 3.0 je štandardizovaná IETF a jej návrh je obsiahnutý v RFC2425 a RFC2426. [http://tools.ietf.org/html/rfc2425](#.md)[http://tools.ietf.org/html/rfc2426](#.md)

'''Example&nbsp;3.2.&nbsp;Príklad vCard 3.0'''

BEGIN:VCARD

VERSION:3.0

N:Gump;Forrest

FN:Forrest Gump

ORG:Bubba Gump Shrimp Co.

TITLE:Shrimp Man

PHOTO;VALUE=URL;TYPE=GIF:![http://www.example.com/dir_photos/my_photo.gif](http://www.example.com/dir_photos/my_photo.gif)

TEL;TYPE=WORK,VOICE:(111) 555-1212

TEL;TYPE=HOME,VOICE:(404) 555-1212

ADR;TYPE=WORK:;;100 Waters Edge;Baytown;LA;30314;United States of America

LABEL;TYPE=WORK:100 Waters Edge\nBaytown, LA 30314\nUnited States of America

ADR;TYPE=HOME:;;42 Plantation St.;Baytown;LA;30314;United States of America

LABEL;TYPE=HOME:42 Plantation St.\nBaytown, LA 30314\nUnited States of America

EMAIL;TYPE=PREF,INTERNET:forrestgump@example.com

REV:20080424T195243Z

END:VCARD


## Chapter 4. Špecifikácia jednotlivých implementácií ##
'''Table of Contents'''

[InputFilter](#d53e202.md)

[InternalDocColumnSchema](#d53e224.md)

[InternalDoc2CompiledDoc](#d53e243.md)

[InternalDocAutodetectFormat](#d53e261.md)

[OutputFilter](#d53e268.md)

[VCFHelper](#d53e275.md)

[ReadCSV](#d53e282.md)

[ODSInput](#d53e289.md)

[ReadVCF](#d53e296.md)

[ReadCompiledDoc](#d53e303.md)

[InternalDocColumnSchemaImpl](#d53e311.md)

[InternalDocAutodetectFormatImpl](#d53e330.md)

[InternalDocCompiler](#d53e336.md)

[VCFConverter](#d53e342.md)

[VCFHelperImpl](#d53e349.md)

[WriteCSV](#d53e356.md)

[ODSWrite](#d53e363.md)

[WriteVCF](#d53e370.md)

[VCFTypesEnum](#d53e377.md)

## InputFilter ##
### rozhranie ###
Rozhranie vstupného filtru, ktorého konštruktor s parametrami na nastavenie možností pre načitanie vstupného súboru. Ak sa načíta súbor, ktorý nie je v žiadnom validnom a používanom formáte XML (myslí sa tým formát používaný v Contact Transmutator), vytvorí sa nový prázdny dokument a metóda getColumnSchema() vráti NULL. Ak sa načíta súbor, ktorý je vo validnom a používanom formate XML, pri použití metódy read() sa vygeneruje InternalDocColumnSchema a metóda getColumnSchema vracia túto vygenerovanú schému.

**public Document read()**<br /> Metóda načítavajúca informácie zo zvoleného súboru a vytvárajúca InternalDoc pre ODS a CSV alebo priamo CompiledDoc v pripade VCF .<br /> Return - Dokument v tvare InternalDoc alebo CompiledDoc.
**public InternalDocColumnSchema getColumnSchema()**<br /> Táto metóda vracia schému dokumentu.<br /> Return - Logicka schéma dokumentu.

## InternalDocColumnSchema ##
### rozhranie ###
Metódy tohoto rozhrania popisujú a pracujú s datovými typmi v stĺpcoch i InternalDoc:

**čo sa nachádza v jednotlivych stĺpcoch** ktoré stĺpce by sa mali rozdeliť a ako
**ktoré stĺpce by sa mali spojiť a ako**

Na svoju práco využíva vnútorný privátny XML DOM. Tiedy tohoto rozhrania dokážu vytvoriť, meniť a pýtať sa na vlastnisti stĺpcovej schémy interného XML (InternalDoc).

## InternalDoc2CompiledDoc ##
### rozhranie ###
Metódy tohoto rozhrania vezmú InternalDoc s InternalDocColumnSchema a prevedu ho do CompiledDoc. InternalDoc aj CompiledDoc sú dne formy XML, pomocou ktorých sa v Contact Transmutátore pracuje s dátami. Najdôležitejšími metódami sú:

**''getCompiledValidContacts() ''Vracia správne zostavený CompiledDoc (zodpoveda približnej štruktúre VCF alebo ľahko prevediteľnej do VFC).** ''getCompiledInvalidContacts() ''Vracia rovnaký formát ale predchádzajúca metóda, ale obsahuje i niektoré chyby typu: Informácie sú v dvoch rozdielnych poliach miesto toho aby boli v jednom.

## InternalDocAutodetectFormat ##
### rozhranie ###
Triedy tohoto rozhrania vytvárajú z InternalDoc InternalDocColumnSchema s kadidátnymi typmi autodetekcie.

## OutputFilter ##
### rozhranie ###
Rozhranie tried, ktoré vytvárajú jeden z výstupných formátov. Triedy nedetekujú chyby v dátach, túto funkciu zastáva GUI ešte predtým, než sa zavolá OutputFilter. Konštruktor umožňuje zvolenie mena výstupného súboru a cestu k nemu, kódovanie a niektoré špecifické parametre.

## VCFHelper ##
### rozhranie ###
Trieda je zodpovedná za rozoznávanie, či môže existovať viac polí jedneho typu pre jeden kontakt.

## ReadCSV ##
### trieda implementujúca rozhranie InputFilter ###
Trieda, ktorá nastavuje štruktúru interného XML dokumentu a následné rozparsovanie dát z pôvodného CSV do interného XML dokumentu. Ďalej vytvára novú internú stĺpovú schému.

## ODSInput ##
### trieda implementujúca rozhranie InputFilter ###
Trieda na načítavanie informácií zo súboru typu ODS do interného XML a následné rozparsovanie dát z pôvodného ODS do interného XML dokumentu. Ďalej vytvára novú internú stĺpovú schému.

## ReadVCF ##
### trieda implementujúca rozhranie InputFilter ###
Trieda na načítavanie informácií zo súboru typu VCF do interného XML a následné rozparsovanie dát z pôvodného VCF do interného XML dokumentu. Ďalej vytvára novú internú stĺpovú schému.

## ReadCompiledDoc ##
### trieda implementujúca rozhranie InputFilter ###
Trieda na načítavanie informácií z XML vyvinutého a používaného na spracovávanie informácií v Contact Transmutatore. Ďalej vytvára novú internú stĺpovú schému.

## InternalDocColumnSchemaImpl ##
### trieda implementujúca rozhranie InternalDocColumnSchema ###
Vytvára W3C XML DOM pre pracu so stĺpcami:

**umožňuje autodetekciu** využíva sa pri grafickom rozhraní pre užívateľa
**využíva ju kompilátor pre výstupné informácie**

Metódy tejto triedy dokážu rozoznať všetky možné kombinácie dát, spájať ich a rozdeľovať. Použitie XML DOM zaručuje robustnosť, flexibilitu a ľahké používanie.

## InternalDocAutodetectFormatImpl ##
### trieda implementujúca rozhranie InternalDocAutodetectFormat ###
## InternalDocCompiler ##
### trieda implementujúca rozhranie InternalDoc2CompiledDoc ###
## VCFConverter ##
### trieda ###
Pomocná trieda, ktrorá prevádza názvy typov dát z VCF formy do podoby, ktorej by mal užívateľ rozumieť.

## VCFHelperImpl ##
### trieda implementujúca rozhranie VCFHelper ###
VCF podporuje mnoho rôznych úloh, ale len niektoré sa bežne používajú. Kvôli používaniu tagov ľudmi sa využívajú identifikátory, ktoré sú ľahko pochopiteľné pre ľudské chápanie. GUI dokáže pomocou metód tejto triedy previesť identifikátory na ľahko zrozumiteľné texty.

## WriteCSV ##
### trieda implementujúca rozhranie OutputFilter ###
Tieda obsahuje metódy, ktoré z CompiledDoc vytvárajú CVS súbor so štruktúrou, ktorú požaduje užívateľ a zapisuje ho na vybrane miesto.

## ODSWrite ##
### trieda implementujúca rozhranie OutputFilter ###
Tieda obsahuje metódy, ktoré z CompiledDoc vytvárajú ODS súbor so štruktúrou, ktorú požaduje užívateľ a zapisuje ho na vybrane miesto.

## WriteVCF ##
### trieda implementujúca rozhranie OutputFilter ###
Tieda obsahuje metódy, ktoré z CompiledDoc vytvárajú VFC súbor so štruktúrou, ktorú požaduje užívateľ a zapisuje ho na vybrane miesto.

## VCFTypesEnum ##
### výčet ###
Pomocný výčet pre prácu s textovými identikátormi, ktoré sa nachádzajú napríklad v VCFHelper.

## Chapter 5. Návod na použitie ##
'''Table of Contents'''

[Návod na použitie](#d53e387.md)

## Návod na použitie ##
Po spustení programu sa na obrazovke vykreslí okno Contack Transmutator. Pre transformáciu dokumentu je potrebné zvoliť cestu k tomuto dokumentu. To sa dá dvomi spôsobmi.

**Vypísaním celej cesty do riadku.** Kliknutím na Browse a vyhladaním cesty k tomuto súboru.

Po nájdení/vypísaní cesty je potrebené vybrať v zozname vhodné kódovanie a kliknúť na tlačidlo Next.

V ďalšom okne sa vypíše zoznam, ktorý sa vytvoril z dát vybraného súboru. V hornej lište je nastapotrebné nastaviť, aké informácie sa vypisujú v ktorom stĺpci.

Pokiaľ stĺpec nemá byť vo výslednom zozname, stačí v zozname pre konkrétny stĺpec zadať "Delete this".

Pokiaľ jeden stĺpec obsahu informácie, ktoré by mali byť v dvoch alebo viacerých stĺpcoch, v zozname treba zvoliť funkciu "Split into". Po zvolení sa otvorí nové okno, kde je možne uviesť na koľko stĺpcov sa má zvolený stĺpec rozdeliť a v settings treba určiť určiť, ktorý stĺpec má obsahovať aký typ informácií.

Podobne ako rozdeliť sa dajú aj spojiť stĺpce. Stačí len vybrať možnosť "Add to column" v stĺpci, ktorého obsah chceme presunúť. Otvorí sa okno, kde sa určuje spájaný stĺpec, do ktorého sa majú dopisovať informácie a znak, ktorým sa oddelia informácie. Ďalej je potrebné liknúť na "submit". V tomto výbere je možné vybrať i ďalšie stĺpce, ktoré sa majú pripojiť k spojeniu a taktiež ich poradie.

Na spodnej časti okna sú daľšie tlačitlá na ovládanie programu.

**''Refresh Table'' - výber z vrchnej časti okna sa spracuje a tabulka sa nanovo vypíše.** ''Add Column'' - pridá nový prázdny stĺpec na konci zoznamu.
**''Back'' - návrat na výber súboru a kódovania** ''Next'' - dokončenie zmien na zozname.
**''Cancel'' - zrušenie aplikácie.**

Po kliknutí na "Next" sa otvorí okno s výzvom na zadanie cesty, mena a typu výstupného dokumentu.