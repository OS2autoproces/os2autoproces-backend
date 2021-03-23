using Quartz;
using System;
using System.Collections.Generic;

namespace OS2autoprocesImporter
{
    internal class SyncJob : IJob
    {
        private static log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public void Execute(IJobExecutionContext context)
        {
            try
            {
                Importer importer = new Importer();
                Reader reader = new Reader();

                log.Info("Attempting to read OUs from Active Directory");
                List<ADOrgUnit> adOrgUnits = reader.ReadOrgUnits();
                log.Info("Found " + adOrgUnits.Count + " OrgUnits");

                log.Info("Attempting to read Users from Active Directory");
                List<ADUser> adUsers = reader.ReadUsers();
                log.Info("Found " + adUsers.Count + " Users");

                Organisation organisation = new Organisation();
                organisation.orgUnits = new List<OrgUnit>();
                organisation.users = new List<User>();

                foreach (var ou in adOrgUnits)
                {
                    OrgUnit orgUnit = new OrgUnit();
                    orgUnit.name = ou.Name;
                    orgUnit.uuid = ou.Uuid;

                    organisation.orgUnits.Add(orgUnit);
                }

                foreach (var u in adUsers)
                {
                    User user = new User();
                    user.name = u.Name;
                    user.uuid = u.Uuid;
                    user.email = u.Email;
                    user.positions = new List<string>();

                    string suffix = u.Dn.ToLower();
                    int idx = suffix.IndexOf(",ou=");
                    if (idx > 0)
                    {
                        suffix = suffix.Substring(idx + 1);
                    }

                    foreach (var ou in adOrgUnits)
                    {
                        if (suffix.Equals(ou.Dn.ToLower()))
                        {
                            user.positions.Add(ou.Uuid);
                            break;
                        }
                    }

                    organisation.users.Add(user);
                }

                foreach (var user in organisation.users)
                {
                    if (user.positions.Count == 0)
                    {
                        log.Warn(user.name + " did not have a position - something smells fishy");
                    }
                }

                log.Info("Importing data to OS2autoproces");
                if (importer.Import(organisation))
                {
                    log.Info("Success!");
                }
            }
            catch (Exception ex)
            {
                log.Error("Failed to synchronize data", ex);
            }
        }
    }
}
