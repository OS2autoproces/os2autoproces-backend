package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QNotification;
import dk.digitalidentity.ap.dao.model.User;

@RepositoryRestResource(exported = false)
public interface NotificationDao extends JpaRepository<Notification, Long>, QueryDslPredicateExecutor<Notification>, QuerydslBinderCustomizer<QNotification> {

	Notification getByUserAndProcess(User user, Process process);
	Notification getByUserAndProcessId(User user, Long processId);
	List<Notification> getByProcess(Process process);

	@Override
	default void customize(QuerydslBindings bindings, QNotification notification) {

	}
}
