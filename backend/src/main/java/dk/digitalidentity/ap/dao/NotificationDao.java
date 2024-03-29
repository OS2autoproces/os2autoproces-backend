package dk.digitalidentity.ap.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import dk.digitalidentity.ap.dao.model.Notification;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QNotification;

@RepositoryRestResource(exported = false)
public interface NotificationDao extends JpaRepository<Notification, Long>, QuerydslPredicateExecutor<Notification>, QuerydslBinderCustomizer<QNotification> {

	Notification getByUserIdAndProcess(long userId, Process process);
	Notification getByUserIdAndProcessId(long userId, Long processId);
	List<Notification> getByProcess(Process process);

	@Override
	default void customize(QuerydslBindings bindings, QNotification notification) {

	}
}
