using System;
using System.Collections.Generic;
using System.DirectoryServices;

namespace OS2autoprocesImporter
{
    class Reader
    {
        private string[] ousToIgnore = new string[0];

        public Reader()
        {
            ousToIgnore = Properties.Settings.Default.OUsToIgnore.Split(';');
        }

        public List<ADOrgUnit> ReadOrgUnits()
        {
            List<ADOrgUnit> orgUnits = new List<ADOrgUnit>();

            using (DirectoryEntry startingPoint = new DirectoryEntry(Properties.Settings.Default.ADUrl))
            {
                using (DirectorySearcher searcher = new DirectorySearcher(startingPoint))
                {
                    searcher.PageSize = 500;
                    searcher.Filter = "(objectCategory=organizationalUnit)";
                    searcher.PropertiesToLoad.Add("objectGUID");
                    searcher.PropertiesToLoad.Add("name");
                    searcher.PropertiesToLoad.Add("distinguishedname");

                    using (var resultSet = searcher.FindAll())
                    {
                        foreach (SearchResult res in resultSet)
                        {
                            Guid uuid = new Guid((byte[])res.Properties["objectGUID"][0]);
                            string dn = (string)res.Properties["distinguishedname"][0];
                            string name = (string)res.Properties["name"][0];

                            bool skip = false;
                            foreach (string ouToIgnore in ousToIgnore)
                            {
                                if (ouToIgnore.Trim().Length == 0)
                                {
                                    continue;
                                }

                                if (dn.ToLower().EndsWith(ouToIgnore.ToLower()))
                                {
                                    skip = true;
                                }
                            }

                            if (skip)
                            {
                                continue;
                            }

                            ADOrgUnit ou = new ADOrgUnit();
                            ou.Uuid = uuid.ToString().ToLower();
                            ou.Name = name;
                            ou.Dn = dn;

                            orgUnits.Add(ou);
                        }
                    }
                }
            }

            return orgUnits;
        }

        public List<ADUser> ReadUsers()
        {
            List<ADUser> users = new List<ADUser>();

            using (DirectoryEntry startingPoint = new DirectoryEntry(Properties.Settings.Default.ADUrl))
            {
                using (DirectorySearcher searcher = new DirectorySearcher(startingPoint))
                {
                    searcher.PageSize = 500;
                    searcher.Filter = "(&(objectClass=user)(objectCategory=person))";
                    searcher.PropertiesToLoad.Add("mail");
                    searcher.PropertiesToLoad.Add("objectGUID");
                    searcher.PropertiesToLoad.Add("name");
                    searcher.PropertiesToLoad.Add("distinguishedname");

                    using (var resultSet = searcher.FindAll())
                    {
                        foreach (SearchResult res in resultSet)
                        {
                            Guid uuid = new Guid((byte[])res.Properties["objectGUID"][0]);
                            string dn = (string)res.Properties["distinguishedname"][0];
                            string name = (string)res.Properties["name"][0];
                            string email = null;
                            if (res.Properties.Contains("mail"))
                            {
                                email = (string)res.Properties["mail"][0];
                            }

                            bool skip = false;
                            foreach (string ouToIgnore in ousToIgnore)
                            {
                                if (ouToIgnore.Trim().Length == 0)
                                {
                                    continue;
                                }

                                if (dn.ToLower().EndsWith(ouToIgnore.ToLower()))
                                {
                                    skip = true;
                                }
                            }

                            if (skip)
                            {
                                continue;
                            }

                            ADUser user = new ADUser();
                            user.Dn = dn;
                            user.Email = email;
                            user.Name = name;
                            user.Uuid = uuid.ToString().ToLower();

                            users.Add(user);
                        }
                    }
                }
            }

            return users;
        }
    }
}
