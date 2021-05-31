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
Man kan bygge og pakktere OS2autoproces i en Docker container på følgende måde

<ol>
  <li>Kompiler OS2autoproces til en fed JAR fil</li>
  <li>Kopier den fede JAR fil ind i en folder ved navn "deploy"</li>
  <li>Lav en tekst fil ved navn "run.sh" og kopier den ind i "deploy" folderen</li>
  <li>Byg en Docker container med indholdet af "deploy" folderen</li>
  <li>Start Docker containeren vha Docker Compose, Kubernetes eller andet Docker deployment værktøj</li>
<ol>

<h4>1 - Kompiler OS2autoproces</h4>
<p>
</p>
  
<h4>2 - Kompiler OS2autoproces</h4>
<p>
</p>
  
<h4>3 - Kompiler OS2autoproces</h4>
<p>
</p>
  
<h4>4 - Kompiler OS2autoproces</h4>
<p>
</p>
  
<h4>5 - Kompiler OS2autoproces</h4>
<p>
</p>
