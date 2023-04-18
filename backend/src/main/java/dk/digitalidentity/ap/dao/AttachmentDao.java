package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Attachment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QAttachment;

@RepositoryRestResource(exported = false)
public interface AttachmentDao extends JpaRepository<Attachment, Long>, QuerydslPredicateExecutor<Attachment>, QuerydslBinderCustomizer<QAttachment> {

	List<Attachment> findAll();
	List<Attachment> findByProcess(Process process);
	Attachment getById(long attachmentId);

	@Override
	default void customize(QuerydslBindings bindings, QAttachment attachment) {

	}
}
