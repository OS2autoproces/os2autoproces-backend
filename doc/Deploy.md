<h1>Pakketering og deployment</h1>
<p>
Kodebasen består af 2 selvstændige kode-projekter, som er beskrevet uafhængigt af hinanden nedenfor.
</p>

<h2>AD Organisationsimporter</h2>
<p>
Der ligger en <a href="https://jrsoftware.org/isinfo.php">InnoSetup</a> fil i Installer folderen i dette kode-projekt.
Den kan anvendes af InnoSetup til at bygge en installations EXE fil, indeholder alle de relevante filer.
Den sørger samtidig for at installere komponenten som en Windows Service når man kører EXE filen.
</p>

<p>
Man kan også anvende andre værktøjer til at danne en EXE/MSI installer, men der er kun udarbejdet scripts til InnoSetup. Man kan
tage et kig i InnoSetup iss filen for at se hvilke filer der skal kopieres ind under installationen, og hvad der skal afvikles
i Post-Install (installation af Windows Service). Scriptet er simpelt og nemt at overskue.
</p>

<p>
<b>Bemærk!</b> man skal formodentligt tilpasse iss filen, så stier m.m. passer til der hvor man har kildekoden placeret
på den maskine hvor man bygger EXE installeren.
</p>

<h2>OS2autoproces Backend</h2>
<p>
OS2autoproces er en Java applikation, der er afhænging af konfigurationsfiler samt certifikat/keystore filer i forbindelse med deployment.
Byggeprocessen for OS2autoproces sikrer at applikationen og dens framework afhængigheder pakkteres i en fed JAR fil, så til deployment
har man alene brug for
<ul>
  <li>Den fede JAR file</li>
  <li>En konfigurationsfil</li>
  <li>En keystore fil</li>
</ul>
</p>

<p>
Man kan udelade konfigurationsfilen hvis man blot tilføjer alle konfigurationsparametre som miljøvariable i et Docker deployment. Dette
kan gøres som beskrevet nedenfor
</p>

<h3>Docker</h3>
<p>
Man kan bygge og pakktere OS2autoproces i en Docker container på følgende måde

<ol>
  <li>Kompiler OS2autoproces til en fed JAR fil</li>
  <li>Kopier den fede JAR fil ind i en folder ved navn "deploy"</li>
  <li>Lav en tekst fil ved navn "run.sh" og kopier den ind i "deploy" folderen</li>
  <li>Byg en Docker container med indholdet af "deploy" folderen</li>
  <li>Start Docker containeren vha Docker Compose, Kubernetes eller andet Docker deployment værktøj</li>
</ol>
</p>

<h4>1 - Kompiler OS2autoproces</h4>
<p>
JAR filen kan kompileres vha Maven og en terminal vindue som vist nedenfor
<pre>
$ mvn clean install
</pre>
</p>
  
<h4>2 - Kopier fed JAR til "deploy"</h4>
<p>
Start med at lave en deploy folder, og kopier så JAR filen derind som vist nedenfor
<pre>
$ mkdir Deploy
$ cp target/os2autoproces-backend-1.0.0.jar deploy/
</pre>
</p>
  
<h4>3 - Lav run.sh script fil</h4>
<p>
Vi har brug for et shell script der kan starte OS2autoproces. Lav en tekst-fil ved navn run.sh og kopier den ind i "deploy" folderen. Filen skal have følgende indhold

<pre>
#!/bin/bash

java -Dloader.path=config/* -jar os2autoproces-backend-1.0.0.jar
</pre>
</p>
  
<h4>4 - Byg Docker container</h4>
<p>
Når både JAR og run.sh fil ligger i "deploy" folderen, kan man bygge en container vha en Dockerfile. Lav en fil ved navn Dockerfile, og giv den følgende indhold
<pre>
FROM openjdk:8-jre

WORKDIR /home
COPY /deploy /home
RUN chmod +x /home/run.sh
EXPOSE 9090

ENTRYPOINT ["/bin/bash", "run.sh"]
</pre>
</p>

<p>
Man bygger efterfølgende containeren via følgende kommandolinje. Dette forudsætter at man har Docker installeret.
  
<pre>
docker build -t os2autoproces:latest .
</pre>
</p>
  
<h4>5 - Start Docker container</h4>
<p>
Docker containeren kan nu startes i produktionsmiljøet (hvordan man får containeren til produktionsmiljøet er udenfor scope af denne vejledning, men Dockerhub kunne være en mekanisme til dette). Til dette formål kan man bruge forskellige Docker værktøjer. Et sådan værktøj er Docker Compose, og der er nedenfor vist en simpel docker-compose.yml fil, som indeholder de fornødne opsætninger til at starte containeren. Bemærk at "environment" sektionen indeholder alt konfigurationen, og at der mountes en "config" folder, som skal indeholder certifikatet der refereres til i SAML opsætningen.
  
<pre>
version: "2.4"
services:
  backend:
    image: os2autoproces:latest
    ports:
      - 9090:9090
    environment:
      dataSource.url: "jdbc:mysql://localhost:3306/ap?useSSL=false"
      dataSource.username: "root"
      dataSource.password: "Test1234"

      scheduled.enabled: "true"

      ... osv ...
    volumes:
      - ./config:/home/config
</pre>
</p>

<p>
Der bør laves en "config" folder, hvor man kan kopiere keystore filen ind, så den kan læses af Docker containeren. Man kan også vælge at have hele environment sektionen i en application.propeties fil, som så placeres i "config" folderen.
</p>

<p>
Man kan evt starte med en kopi af indholdet af application.properties filen fra kodebasen, og så tilpasse efter behov.
</p>
