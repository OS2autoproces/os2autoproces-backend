package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Bookmark;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QBookmark;

@RepositoryRestResource(exported = false)
public interface BookmarkDao extends JpaRepository<Bookmark, Long>, QuerydslPredicateExecutor<Bookmark>, QuerydslBinderCustomizer<QBookmark> {

	Bookmark getByUserIdAndProcess(long userId, Process process);
	Bookmark getByUserIdAndProcessId(long userId, Long processId);
	List<Bookmark> getByUserId(long userId);
	
	@Override
	default void customize(QuerydslBindings bindings, QBookmark bookmark) {

	}
}
