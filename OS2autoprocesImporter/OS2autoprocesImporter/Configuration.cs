using Topshelf;

namespace OS2autoprocesImporter
{
    internal static class Configuration
    {
        internal static void Configure()
        {
            HostFactory.Run(configure =>
            {
                configure.Service<Application>(service =>
                {
                    service.ConstructUsing(s => new Application());
                    service.WhenStarted(s => s.Start());
                    service.WhenStopped(s => s.Stop());
                });

                configure.RunAsLocalService();
                configure.SetServiceName("OS2autoprocesImporter");
                configure.SetDisplayName("OS2autoproces Importer");
                configure.SetDescription("Import organizational data from AD to OS2autoproces");
            });
        }
    }
}
