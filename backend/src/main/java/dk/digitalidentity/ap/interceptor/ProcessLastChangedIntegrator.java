package dk.digitalidentity.ap.interceptor;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class ProcessLastChangedIntegrator implements Integrator {

	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		final EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

		listenerRegistry.appendListeners(
				EventType.PRE_COLLECTION_UPDATE,
				new ProcessLastChangedListener()
		);
	}

	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		; // do nothing
	}
}
