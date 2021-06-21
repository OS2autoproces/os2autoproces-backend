<h1>Byg- og kør vejledning</h1>
<p>
Kodebasen består af 2 selvstændige kode-projekter, som er beskrevet uafhængigt af hinanden nedenfor.
</p>

<h2>AD Organisationsimporter</h2>
<p>
Hvis man åbner solution filen i Visual Studio, er projektet klar til at blive kompileret og afviklet på helt standard vis.
Tryk evt bare på F5 for at afvikle som standard i Visual Studio, hvorefter der gerne skulle åbne sig et terminal vindue, der viser
output fra kørslen.
</p>

<p>
<b>Bemærk!</b> hvis man ikke er joined med et AD domæne, så kan koden ikke afvikle.
</p>

<p>
Det forudsættes at der er læseadgang til et AD, og at den bruger der afvikler koden har de
fornødne læserettigheder i AD'et (almindelig Domain User adgange er typisk nok,
men afhænger af det konkrete AD's default rettigheder til Domain Users).
</p>

<p>
<b>Bemærk!</b> hvis man ikke har en OS2autoproces Backend kørende, som integrationen kan pege på, så kan den ej heller afvikle.
</p>

<h3>Konfiguration</h3>
<p>
App.config indeholder konfigurationen til løsningen. Her angiver man følgende indstillinger, som formodentligt
skal tilpasses inden man effektivt kan afvikle løsningen, og trække relevante data ud af AD'et.
  
<ul>
  <li><b>CronSchedule</b>. CRON udtryk for hvor ofte integrationen afvikles (hvor ofte der læses data fra AD'et)</li>
  <li><b>ADUrl</b>. LDAP pegepind til den OU i AD'et hvor man ønsker at læse data fra (der læses fra denne OU og hele vejen ned gennem AD'et)</li>
  <li><b>ApiKey</b>. Nøgle der anvendes til at kalde API'et på OS2autoproces Backenden. Skal sættes op i OS2autoproces Backenden for den myndighed man "tester" kaldet for</li>
  <li><b>ApiUrl</b>. URL pegepind til OS2autoproces BAckend API'et. Tilpasses til den maskine hvor man har sit udviklingssetup kørende med OS2Autoproces Backenden.</li>
  <li><b>OUsToIgnore</b>. Komma-separeret liste over distinguishedNames på OU'ere der skal udelades af indlæsningen (inkluderer alle underliggende OU'ere)</li>
</ul>
</p>

<h2>OS2autoproces Backend</h2>
<p>
Den nemmeste måde at bygge og afvikle OS2autoproces Backenden er via kommandolinjen, hvor man kan anvende Maven til formålet. Bemærk at man skal anvende Java 8 til at kompilere og bygge projektet. Man kan ikke anvende nyere Java versioner som Java 11 eller lignende.
</p>
  
<pre>
# kompiler projektet
$ mvn clean install

# kør projektet
$ mvn spring-boot:run
</pre>

<p>
Når man kompilerer projektet, danner den en fed JAR fil, placeret i target folderen. Denne JAR fil indeholder det fulde projekt, inkl alle afhængigheder, hvilket gør deployment betydeligt nemmere.
</p>

<p>
Til udviklingsformål er der tilføjet et Spring Maven Plugin til pom.xml filen, der gør det muligt at afvikle projektet via "mvn spring-boot:run". Plugin'et er konfiguret til at hive folderen "config" med ind på classpath, så certifikater, konfigurationsfiler m.m. kan vedligeholdes i denne folder på en nem måde, mens man sidder og udvikler.
</p>

<h3>Konfiguration</h3>
<p>
Projektet har en default konfiguration, som indeholder de indstillinger der formodentligt aldrig skal ændres. Disse befinder sig i "src/main/resources/default.properties", og er konfiguration af frameworks.
</p>

<p>
Herudover findes en ekstern konfiguration i "config" folderen, som det er relevant at tilpasse mens man udvikler. Det er samme konfiguration man typisk vil opsætte og tilpasse i forbindelse med produktionsdeployments. Konfigurationsfilen hedder her "application.properties", og de relevante indstillinger er beskrevet nedenfor
</p>

<ul>
  <li><b>datasource.xxx</b>. Disse 3 indstillinger indeholder hhv url, brugernavn og kodeord til MySQL databasen</li>
  <li><b>scheduled.enabled</b>. I test/udvikling sættes denne blot til "true". I produktion skal der kun være én server der har værdien sat til "true", alle andre skal have den sat til "false". Hvis den er sat til "true" kører skedulerede jobs i applikationen. Det er uhensigtsmæssigt at batch/skedulerede jobs kører parallelt på flere servere.</li>
  <li><b>kitos.xxx</b>. Disse 5 indstillinger peget på KITOS, og anvendes til udlæsning af it-system stamdata. Det er ikke en forudsætning til udvikling, og kan udelades udenfor produktionsmiljøet. I produktion skal man have en konkret konto i KITOS til udlæsning af data.</li>
  <li><b>cloud.aws.xxx</b>. Disse 5 indstillinger anvendes til integration til AWS miljøet, der brugers til at gemme bilag samt sende emails. Kan udelades under udvikling, så længe man ikke uploader bilag.</li>
  <li><b>s3.*</b>. Disse indstillinger udpeger den S3 bucket i AWS som bruges af ovenstående.</li>
  <li><b>saml.*</b>. Disse indstillinger konfigurerer SAML opsætningen (se detaljer <a href="https://bitbucket.org/digitalidentity_dk/saml-module/">her</a>)</li>
  <li><b>stsorgsync.url</b>. Pegepind til en kørende instans af OS2sync. Anvendes til indlæsning af organisationsdata fra kommuner der anvender FK Organisation som kilde til OS2autoproces. Er kun nødvendig hvis der skal indlæses organisationsdata fra FK Organisation.</li>
  <li><b>server.ssl.*</b>. Opsætning af SSL certifikat. Bør ikke anvendes i produktion, men kan anvendes under udvikling.</li>
  <li><b>management.*</b>. Opsætning af brugernavn/kodeord til Actuator endpoints</li>
</ul>
