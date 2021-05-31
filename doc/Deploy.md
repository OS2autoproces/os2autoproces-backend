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

</p>
  
