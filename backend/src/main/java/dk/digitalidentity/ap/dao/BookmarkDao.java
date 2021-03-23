package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Bookmark;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QBookmark;
import dk.digitalidentity.ap.dao.model.User;

@RepositoryRestResource(exported = false)
public interface BookmarkDao extends JpaRepository<Bookmark, Long>, QueryDslPredicateExecutor<Bookmark>, QuerydslBinderCustomizer<QBookmark> {

	Bookmark getByUserAndProcess(User user, Process process);
	Bookmark getByUserAndProcessId(User user, Long processId);
	List<Bookmark> getByUser(User user);
	
	@Override
	default void customize(QuerydslBindings bindings, QBookmark bookmark) {

	}
}
