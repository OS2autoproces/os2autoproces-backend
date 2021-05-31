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

<h2>OS2autoproces Backend<h2>
