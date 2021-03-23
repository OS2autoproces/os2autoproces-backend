using System.Collections.Generic;

namespace OS2autoprocesImporter
{
    class Organisation
    {
        public List<User> users { get; set; }
        public List<OrgUnit> orgUnits { get; set; }
    }
}
