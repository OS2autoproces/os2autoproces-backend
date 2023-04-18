package dk.digitalidentity.ap.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.envers.repository.support.ReflectionRevisionEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class AutoProcessRepositoryFactoryBean<T extends RevisionRepository<S, ID, N>, S, ID, N extends Number & Comparable<N>> extends EnversRevisionRepositoryFactoryBean {
	private Class<?> revisionEntityClass;

	public AutoProcessRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new AutoProcessRepositoryFactory<T, ID, N>(entityManager, revisionEntityClass);
	}
	
	private static class AutoProcessRepositoryFactory<T, ID, N extends Number & Comparable<N>> extends JpaRepositoryFactory {
		private final RevisionEntityInformation revisionEntityInformation;
		private final EntityManager entityManager;

		public AutoProcessRepositoryFactory(EntityManager entityManager, Class<?> revisionEntityClass) {
			super(entityManager);
			
			this.entityManager = entityManager;
			this.revisionEntityInformation = Optional.ofNullable(revisionEntityClass)
					.filter(it -> !it.equals(DefaultRevisionEntity.class))
					.<RevisionEntityInformation> map(ReflectionRevisionEntityInformation::new)
					.orElseGet(DefaultRevisionEntityInformation::new);
		}
		
		@Override
		protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

			Object fragmentImplementation = getTargetRepositoryViaReflection(
					AutoProcessRepositoryImpl.class,
					getEntityInformation(metadata.getDomainType()),
					revisionEntityInformation,
					entityManager
			);

			return RepositoryFragments.just(fragmentImplementation);
		}
	}

	private static class DefaultRevisionEntityInformation implements RevisionEntityInformation {
		public Class<?> getRevisionNumberType() {
			return Integer.class;
		}

		public boolean isDefaultRevisionEntity() {
			return true;
		}

		public Class<?> getRevisionEntityClass() {
			return DefaultRevisionEntity.class;
		}
	}
}
