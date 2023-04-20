# VegAndFru
Egyetemi projekt(mobil alkfejl)

## Specifikációk
Az alkalmazásban meg van valósítva az autentikált bejelentkezés illetve a regisztráció is.
Regisztráció után közvetlenül a profil oldalra kerül a felhasználó így nem kell megadni ismét ugyanazokat az adatokat amit az imént megadott, de természetesen kijelentkezhet és
akár egy másik felhasználóval is bejelentkezhet.

### Felhasználó típusok
3 fajta felhasználó típust lehet regisztrálni, 2 vásárló típusút (magánszemály és cég), illetve 1 eladó típust ami minden esetben csak cég lehet. 
A kötelező adatok megadása után ami eladó esetén még egy plusz opcionális adatot is tartalmaz (áruház képe) indul a regisztráció ami (kép feltöltés esetén rövid várakozási idő után) ha sikeres akkor kerül át a felhasználó a profiljára ahol megtekintheti az ÁfSZ-t, ügyfélszolgálatot, az ellenőrzés után a profilja adatait valamint a vevők esetében a rendelési előzményeit, eladó esetében pedig boltját kezelheti az adott felhasználó.

### Adat módosítás
Bármilyen adatot módosíthat a felhasználó a profiljában és ha sikeres akkor (képcsere illetve feltöltés esetén rövid várakozási idő után) áttekintheti a mentett adatait.

### Bolt kezelése
Az eladó kezelheti a boltját oly módon, hogy megtekintheti a már hozzáadott termékeit (ha van neki) és újakat adhat hozzá, ahol az eladó megadhatja, hogy a létrehozandó termékét darabszám vagy súly (kg) szerint szeretné beárazni valamint opcionálisan képet is rendelhet termékéhez, és sikeres mentés esetén vissza kerül a termék listához, ahol át tudja tekinteni eddigi termékeit és törölni illetve módosítani is tudja azokat. (CRUD műveletek megvalósítása)

### Rendelések megtekintése
A vásárlók megtekinthetik a már korábban leadott rendeléseiket listaszerűen rendezve.

### A főoldalon lehet keresni, szűrni bizonyos üzletek nevére de akár termékekre is és megjelennek azok a boltok amelyekben kapható a keresendő termék (Lekérdezések megvalósítása)

### A főoldalon az üzletek jelennek meg amiknek az oldal maga generál egyedi szállítási időt illetve kiszállítási díjat (hiszen az oldal cége szállítja ki majd a termékeket)

