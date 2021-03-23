package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QComment;

@RepositoryRestResource(exported = false)
public interface CommentDao extends JpaRepository<Comment, Long>, QueryDslPredicateExecutor<Comment>, QuerydslBinderCustomizer<QComment> {

	List<Comment> getByProcess(Process process);
	
	@Override
	default void customize(QuerydslBindings bindings, QComment comment) {

	}
}
