<h1>Værktøjer, Sprog og Frameworks</h1>
Kodebasen består af 2 selvstændige kode-projekter, som er beskrevet uafhængigt af hinanden nedenfor.

<h2>AD Organisationsimporter</h2>
Kildekoden til dette kode-projekt findes <a href="../OS2autoprocesImporter">her</a>.

<h3>Formål</h3>
Formålet med kode projektet er at danne en simpel integration mellem en myndigheds Active Directory (AD), til indlæsning af stamdata oplysninger om brugere og enheder ind i OS2autoproces. Det er ikke en forudsætning at man indlæser organisationsdata, men hvis man ikke gør det, så vil der ikke være nogen enheder i OS2autoproces for den myndighed, og kun de brugere som aktivt har logget ind i løsningen (brugere oprettes ved første login, hvis de ikke findes i forvejen).

<h3>Sprog</h3>
Projektet er lavet i C#, specifikt .NET 4.5 platformen.

<h3>Værktøjer</h3>
Visual Studio kan anvendes til at indlæse projektet, og kompilere, debugge og afvikle programmet. Der er ikke behov for yderligere værktøjer end Visual Studio til at arbejde med kildekoden.

<h3>Frameworks</h3>
C# projektet gør brug af nogle få frameworks til at håndtere forretningslogikken. De primære er

<ul>
  <li><b>Quartz</b>. Anvendes som et skeduleringsværktøj, til at afvikle periodiske indlæsninger af data fra AD.</li>
  <li><b>RestSharp</b>. Anvendes som REST klient til at overføre data til OS2autoproces API.</li>
  <li><b>Topshelf</b>. Anvendes til at pakke koden ind i en såkaldt Windows Service, så det er nemt at starte/stoppe koden i myndighedens Microsoft Server miljø.</li>
</ul>

<h2>OS2autoproces Backend</h2>
Kildekoden til dette kode-projekt findes <a href="../backend">her</a>.

<h3>Formål</h3>
Backenden holder data/tilstand for alle processer, klassifikationer og organisationsdata der anvendes af OS2autoproces løsningen. Backenden udstiller sikrede API'er til anvendes af hhv Frontenden, AD Organisationsimporter og andre 3.parts integrationer.

<h3>Sprog</h3>
Backenden er skrevet i Java, specifikt Java 8.

<h3>Værktøjer</h3>
Det anbefales at man bruger et IDE som IntelliJ, Eclipse eller tilsvarende for at arbejde med kildekoden. Der medfølgender Maven pom.xml filer til at bygge projektet, og man kan anvende disse til at loade projektet ind i sit IDE.

Ud over Java 8, et IDE og Maven 3, er der ikke andre forudsætninger for at kunne arbejde med kildekoden.

<h3>Frameworks</h3>
Backenden baserer sig primært på Spring frameworket. Specifikt anvendes følgende Spring moduler fra Spring Frameworket

<ul>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
  <li><b>xx</b>. xx</li>
</ul>
