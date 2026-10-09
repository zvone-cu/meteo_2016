## Meteo

Rest api client koji svakog sata čupa podatke sa službenih DHMZ servisa [https://vrijeme.hr/hrvatska_n.xml](https://vrijeme.hr/hrvatska_n.xml), za par odabranih gradova (tablica SELEKCIONIRANI_GRADOVI), te ih pohranjuje u mysql bazu podataka, te frontend sučelje koje prikazuje podatke o temperaturi, tlaku, vlazi i vjetru za odabrani vremenski period u obliku klasičnog 2D prikaza ili aktualne ruže vjetrova.

<p align="center">
<a href="meteo1.png"><img src="meteo1.png" width="30%"></a>
<a href="meteo2.png"><img src="meteo2.png" width="30%"></a>
</p>
<p align="center">
<a href="meteo3.png"><img src="meteo3.png" width="30%"></a>
<a href="meteo4.png"><img src="meteo4.png" width="30%"></a>
</p>

## Korištene tehnologije
- JSF kao frontend
- Java kao backend
- Maven kao build engine
- Eclipse kao preporučeni IDE
- Mysql kao baza podataka
- Tomcat server kao deploy target

## Deploy

### Prerekviziti za deploy:

1. Mysql
2. Apache Tomcat

### Build & deploy:

1. git clone [https://github.com/zvone-cu/meteo_2016.git](https://github.com/zvone-cu/meteo_2016.git)
2. importati maven project u eclipse
2. kreirati bazu podataka METEO_DHMZ sa ovlaštenim userom 
3. kreirati radne tablice pomoću **./resources/ddlovi.sql**
4. updejatati konekcijske parametre baze (user/pwd) u sve **persistence.xml** fajlove u projektu
5. generirati **meteo_2016.war** deploy artefakt
6. deployati meteo_2016.war artefakt na tomcat server. 
   ako se želi da maven obavlja automatski deploy an tomcat, updejtati glavni **pom.xml** parametrima tomcata
7. webappu se pristupa browserom na **<tomcat adresa>/meteo_2016**

 
## License

This project is licensed under the GNU General Public License v3.0 - see the LICENSE file for details.  



