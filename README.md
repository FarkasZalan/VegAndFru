# VegAndFru
Egyetemi projekt(mobil alkfejl)
(ha nem giten nyitod meg hanem egyből leclonoztad akkor akkor csak a readme átláthatósága végett lehet érdemes ott megnyitnod és elolvasni ezt)

## Specifikációk
Az alkalmazásban meg van valósítva az autentikált bejelentkezés illetve a regisztráció is.
Regisztráció után közvetlenül a profil oldalra kerül a felhasználó így nem kell megadni ismét ugyanazokat az adatokat amit az imént megadott, de természetesen kijelentkezhet és
akár egy másik felhasználóval is bejelentkezhet.
Az a felhasználó aki már regisztrált viszont nem tudja a jelszavát annak van lehetősége a fiókjához tartozó email megadása után egy jelszó módosító emailt kérni.

### Felhasználó típusok
3 fajta felhasználó típust lehet regisztrálni, 2 vásárló típusút (magánszemály és cég/vállalat), illetve 1 eladó típust ami minden esetben csak cég lehet. 
A kötelező adatok megadása után ami eladó esetén még egy plusz opcionális adatot is tartalmaz (áruház képe) indul a regisztráció ami (kép feltöltés esetén rövid várakozási idő után) ha sikeres, akkor kerül át a felhasználó a profiljára ahol megtekintheti az ÁfSZ-t, ügyfélszolgálatot(az én elérhetőségeim probléma esetére), az ellenőrzés után a profilja adatait valamint a vevők esetében a rendelési előzményeit, eladó esetén pedig a beérkezett rendeléseket illetve a boltját kezelheti az adott felhasználó.

### Adat módosítás
Bármilyen adatot módosíthat a felhasználó a profiljában és ha sikeres akkor (képcsere illetve feltöltés esetén rövid várakozási idő után) áttekintheti a mentett adatait.

### Bolt kezelése
Az eladó kezelheti a boltját oly módon, hogy megtekintheti a már hozzáadott termékeit (ha van neki) és újakat adhat hozzá, ahol az eladó megadhatja, hogy a létrehozandó termékét darabszám vagy súly (kg) szerint szeretné beárazni valamint opcionálisan képet is rendelhet termékéhez, és sikeres mentés esetén vissza kerül a termék listához, ahol át tudja tekinteni eddigi termékeit és törölni illetve módosítani is tudja azokat. (CRUD műveletek megvalósítása)

### Rendelések megtekintése
A vásárlók és az eladók megtekinthetik a már korábban leadott rendeléseiket listaszerűen rendezve és az azokhoz tartozó nyugtákat.

### Főoldal
A főoldalon az üzletek jelennek meg amiknek az oldal maga generálja a szállítási időt illetve kiszállítási díjat (fix 3-5 nap országosan 5000Ft)
Az oldalon lehetőség van keresni oly módon, hogy a fenti keresőmezőbe beírhatja a keresendő kifejezést, amire megjelennek azon üzletek listája ahol egyezés van (akár csak résszóban is) a bolt neve vagy pedig a boltban kapható termékek megnevezésével.
Ha bármelyik boltra rákattint a felhasználó, akkor felhozza a boltban kapható termékek listáját. A termékeket berakhatja a kosárban és a kosár megnyitásával áttekintheti a rendelni kívánt termékek listáját és innen navigálhat el a fizetéshez

### Bolt
Minden bolton belül mindenki számára megtekinthetőek az adott üzlet termékei és az eladót illetve a nem bejelentkezett felhasználót(de a termék oldalon lehetősége van a nem bejelentkezett felhasználónak bejelentkezni/regisztrálni) leszámítva mindenki hozzáadhatja a kiválasztott terméket a kosarához, hogyha a készletet nem meghaladó mennyiséget szeretne rendelni.

### Kosár
Ha egy terméket a kosárba tesz a felhasználó akkor ezeket a termékeket tudja törölni a kosárból illetve módosítani a mennyiségen (ha 0-t ír be vagy kitörli a mennyiséget úgy értelmezi mintha a törlésre nyomott volna).
Egyszerre csak egy fajta boltból lehet rendelni terméket, ha másik bolt termékét szeretné betenni a kosárba úgy, hogy már van egy különböző boltból termék akkor lehetőség van üríteni a kosarat és betenni az újat. A termékek alatt pedig megjelenik a végösszeg is(ami még nem tartalmazza a szállítási költséget).

### Rendelés leadása
A vásárlók a kosárból kerülhetnek a fizetői oldalra. A felhasználónak nem kell megadnia semmilyen adatot, ugyanis a megadott adatai megjelennek tehát nem kell neki semmit egyesével beírnia, de ha szeretné akkor még ott szerkesztheti is azokat(fiókhoz irányít). Sikeres fizetés után jön egy értesítés a vevőnek (NotificationManager), a sikeres rendelésével kapcsolatban és ha arra rákattint akkor megtekintheti a leadott rendelését, de ezt a nyugtatát megtekintheti később mint ahogy az összes többi előzőleg leadott rendelését is a profilján belüli 'rendeléseim' menüpontra kattintva. Természetesen ha sikerült a fizetés, akkor az eladó cég is a profilján belüli 'rendelések' menüpontban tekintehti meg a rendeléseket.


## Egyéb tudnivalók
- Az alkalmazásban meg vannak valósítva a komplex index alapú lekérdezések is (BoltKezeleseActivity.java és BoltOldalActivity.java) ahol előbbinél a jelenleg bejelentkezett felhasználó (eladó) tekintheti meg a termékeit, utóbbinál pedig az adott bolt termékeinek kilistázása és sorrendbe rendezése van megvalósítva.
- Activity és DAO adatmodell meg van valósítva.
- ConstraintLayouton kívűl még 3 másik layout meg van valósítva (LinearLayout, RelativeLayout, CoordinatorLayout).
- Az alkalmazásban több helyen is meg van valósítva 1 Lifecycle Hook, mégpedig az onResume(), például a BoltKezeleseActivity.java-ban olyan szerepet tölt be, hogyha létrehoz az eladó egy új terméket vagy módosít egy már meglévőt akkor ha visszanavigál a termékekhez akkor lefrissűl a lista és azt jeleníti meg vagy a TermekOldalActivity.java-n belül hogyha elnavigálna onnan egy eladó a profiljába és kijelentkezik majd ha visszatér a termék oldalhoz akkor lefrissűljön hogy a nem bejelentkezett felhasználónak szóló adatok jelenjenek meg.
- 22 különböző Activytit használtam (plusz 1 a kosár számlálóhoz)
- Beviteli mezők mindenhol meg van valósítva a neki megfelelő típusnak
- Az alkalmazás resszponzív minden képernyőn, telefonon és tableten is minden activyti jól látható, nem esik szét és minden funkció üzemel. Az elforgatásra hogy jól nézzen ki úgy oldottam meg, hogy letiltottam a forgatás lehetőségét (memory leak kikerülése miatt) és így nem fordul el az alkalmazás kivéve 2 oldalt (ASZF és ügyfélszolgálat) hogy szemléltessem 
a land layoutot. Az ASZF elforgatott verziójában a vissza gomb felkerül a szöveg fölé, ügyfélszolgálat esetén pedig megcserélődik a Facebook és a Discord elérhetőség illetve a vissza gomb is feljebb kerül (gyakvezzel beszélve elvileg elég 1-2 oldalon szemléltetve a land layout a lényeg hogy máshol elforgatásra is legyen resszponzív, minden jól látszódjon)
- 2 különböző animációt használtam, egy balról beúszósat az egy oszlopos elemekhez(pl üzletek, kosár, rendelések) és egy alulról enyhén beúszós megjelenőset a 2 oszlopos elemekhez(termékek)
- 3 androidos erőforrást használok amihez kell android-permission:
	- Az egyik az INTERNET és ezzel az adatbázissal létesítek kapcsolatot illetve ezáltal nyitom meg a facebookot és a discordot is
	- Második az értesítéshez (notification managgerhez) használt permission a POST_NOTIFICATIONS
	- A harmadik pedig a telefon eléréséhez szükséges permission, a CALL_PHONE
- Minden activyti ami létezik az alkalmazásban valamilyen módon elérhető (felhasználó típustól függ)

Minden egyéb értékeléshez segítő szempontot már fentebb leírtam (pl lifecycle hook, autentikáció...)


Az alkalmazásban eddig használt felhasználók a szemléltetéshez:
elado1@x.com
     .
     .
     .
elado9@x.com

Plusz a saját vásárló fiókom: farkaszalan2001@gmail.com
Ezeken kívűl bármilyen másik email címet lehet regisztrálni ami még nincs az adatbázisban.

Alap eladói fiók belépés elado1-9@x.com (itt nyilván 8 bolt van alapból és mindegyikhez az email egységes csak a szám különbözik középen, pl a tesco fiókjába elado2@x.com pl)
jelszó: 123456
