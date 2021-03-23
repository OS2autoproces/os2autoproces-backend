package dk.digitalidentity.ap.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.CmsEntry;
import dk.digitalidentity.ap.dao.model.QCmsEntry;

@RepositoryRestResource(exported = false)
public interface CmsEntryDao extends JpaRepository<CmsEntry, Long>, QueryDslPredicateExecutor<CmsEntry>, QuerydslBinderCustomizer<QCmsEntry> {	
	CmsEntry getByLabel(String label);
	
	@Override
	default void customize(QuerydslBindings bindings, QCmsEntry cmsEntry) {

	}
}
