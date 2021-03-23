using System.Collections.Generic;

namespace OS2autoprocesImporter
{
    class User
    {
        public string uuid { get; set; }
        public string name { get; set; }
        public string email { get; set; }
        public List<string> positions { get; set; }
    }
}
