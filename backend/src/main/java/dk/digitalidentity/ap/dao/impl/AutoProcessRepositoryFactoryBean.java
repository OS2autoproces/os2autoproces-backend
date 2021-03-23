package dk.digitalidentity.ap.dao.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.envers.repository.support.ReflectionRevisionEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class AutoProcessRepositoryFactoryBean<T extends RevisionRepository<S, ID, N>, S, ID extends Serializable, N extends Number & Comparable<N>> extends EnversRevisionRepositoryFactoryBean {
	private Class<?> revisionEntityClass;

	public AutoProcessRepositoryFactoryBean(Class repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	public void setRevisionEntityClass(Class revisionEntityClass) {
		super.setRevisionEntityClass(revisionEntityClass);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new AutoProcessRepositoryFactory(entityManager, revisionEntityClass);
	}

	private static class AutoProcessRepositoryFactory<T, ID extends Serializable, N extends Number & Comparable<N>> extends JpaRepositoryFactory {
		private final RevisionEntityInformation revisionEntityInformation;
		private final EntityManager entityManager;

		public AutoProcessRepositoryFactory(EntityManager entityManager, Class<?> revisionEntityClass) {
			super(entityManager);
			
			revisionEntityClass = (revisionEntityClass == null) ? DefaultRevisionEntity.class : revisionEntityClass;

			this.revisionEntityInformation = DefaultRevisionEntity.class.equals(revisionEntityClass) ? new DefaultRevisionEntityInformation() : new ReflectionRevisionEntityInformation(revisionEntityClass);
			this.entityManager = entityManager;
		}

		@Override
		protected AutoProcessRepositoryImpl getTargetRepository(RepositoryInformation information) {
			JpaEntityInformation<T, ID> entityInformation = (JpaEntityInformation<T, ID>) getEntityInformation(information.getDomainType());

			return new AutoProcessRepositoryImpl<T, ID, N>(entityInformation, revisionEntityInformation, entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return AutoProcessRepositoryImpl.class;
		}

		@SuppressWarnings("hiding")
		@Override
		public <T> T getRepository(Class<T> repositoryInterface, Object customImplementation) {
			if (RevisionRepository.class.isAssignableFrom(repositoryInterface)) {
				Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(repositoryInterface, RevisionRepository.class);
				Class<?> revisionNumberType = typeArguments[2];

				if (!revisionEntityInformation.getRevisionNumberType().equals(revisionNumberType)) {
					throw new IllegalStateException(String.format(
							"Configured a revision entity type of %s with a revision type of %s " +
							"but the repository interface is typed to a revision type of %s!",
							repositoryInterface,
							revisionEntityInformation.getRevisionNumberType(), revisionNumberType));
				}
			}

			return super.getRepository(repositoryInterface, customImplementation);
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
