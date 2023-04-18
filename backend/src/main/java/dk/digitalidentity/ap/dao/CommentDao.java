package dk.digitalidentity.ap.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Comment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QComment;

@RepositoryRestResource(exported = false)
public interface CommentDao extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment>, QuerydslBinderCustomizer<QComment> {

	List<Comment> getByProcess(Process process);
	
	List<Comment> getByCreatedBetween(Date start, Date end);
	
	@Override
	default void customize(QuerydslBindings bindings, QComment comment) {

	}
}
