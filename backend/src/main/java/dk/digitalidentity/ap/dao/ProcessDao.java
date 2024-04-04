package dk.digitalidentity.ap.dao;

import com.querydsl.core.BooleanBuilder;
import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QProcess;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@CrossOrigin(exposedHeaders = "x-csrf-token")
public interface ProcessDao extends JpaRepository<Process, Long>, QuerydslPredicateExecutor<Process>, QuerydslBinderCustomizer<QProcess> {

	@RestResource(exported = false)
	List<Process> findByLastChangedBetween(Date begin, Date end);

	@RestResource(exported = false)
	List<Process> findByLastChangedBefore(Date tts);

	@RestResource(exported = false)
	@Modifying
	@Transactional
	@Query(value = "UPDATE process SET no_changes_notification_date = CURRENT_TIMESTAMP WHERE id = :processId", nativeQuery = true)
	void setNoChangesNotificationDate(Long processId);

	@RestResource(exported = false)
	List<Process> findByForm(String code);
	
	@RestResource(exported = false)
	@Query(value = "SELECT * FROM process" +
	               "  WHERE phase = 'OPERATION'" +
	               "  AND LENGTH(legal_clause) > 0" +
	               "  AND (legal_clause_last_verified < SUBDATE(CURRENT_TIMESTAMP, INTERVAL 6 MONTH) OR legal_clause_last_verified IS NULL)",
	               nativeQuery = true)
	List<Process> getByLegalClausePendingVerification();

	@RestResource(exported = false)
	@Query(value = "SELECT search_words FROM process WHERE search_words IS NOT NULL", nativeQuery = true)
	List<String> getAllSearchWords();

	@RestResource(exported = false)
	@Modifying
	@Transactional
	@Query(value = "UPDATE process SET visibility = :visibility WHERE id = :processId", nativeQuery = true)
	void setProcessVisibility(long processId, String visibility);

	@RestResource(exported = false)
	long countByPhase(Phase phase);

	@RestResource(exported = false)
	long countByPhaseAndCvr(Phase phase, String cvr);

	@RestResource(exported = false)
	long countByCvr(String cvr);

	@RestResource(exported = false)
	long countBySepMepFalseAndKlaProcessFalse();

	@RestResource(exported = false)
	long countByCvrAndSepMepFalseAndKlaProcessFalse(String cvr);

	@RestResource(exported = false)
	long countByItSystemsContaining(ItSystem itSystem);

	@RestResource(exported = false)
	long countByCvrAndItSystemsContaining(String cvr, ItSystem itSystem);

	@RestResource(exported = false)
	long countByTechnologiesContaining(Technology technology);

	@RestResource(exported = false)
	long countByCvrAndTechnologiesContaining(String cvr, Technology technology);

	@Override
	default void customize(QuerydslBindings bindings, QProcess root) {
		bindings.bind(root.lastChanged).all((path, value) -> {
			Iterator<? extends Date> it = value.iterator();

			return Optional.ofNullable(path.after(it.next()));
		});

		bindings.bind(root.created).all((path, value) -> {
			Iterator<? extends Date> it = value.iterator();

			return Optional.ofNullable(path.after(it.next()));
		});
		
		bindings.bind(root.domains).all((path, value) -> {
			BooleanBuilder builder = new BooleanBuilder();

			Iterator<? extends List<Domain>> iterator = value.iterator();
			while (iterator.hasNext()) {
				List<Domain> domains = iterator.next();

				for (Domain domain : domains) {
					builder.or(path.contains(domain));
				}
			}

			return Optional.of(builder);
		});

		bindings.bind(root.technologies).all((path, value) -> {
			BooleanBuilder builder = new BooleanBuilder();

			Iterator<? extends List<Technology>> iterator = value.iterator();
			while (iterator.hasNext()) {
				List<Technology> technologies = iterator.next();

				for (Technology technology : technologies) {
					builder.or(path.contains(technology));
				}
			}

			return Optional.of(builder);
		});

		bindings.bind(root.itSystems).all((path, value) -> {
			BooleanBuilder builder = new BooleanBuilder();

			Iterator<? extends List<ItSystem>> iterator = value.iterator();
			while (iterator.hasNext()) {
				List<ItSystem> systems = iterator.next();

				for (ItSystem itSystem : systems) {
					builder.or(path.contains(itSystem));

					// a hack to search in services as well when searching on ITSystems. The services are created with the same name as the IT-System. Therefore, search in services on system.name
					builder.or(root.services.any().name.equalsIgnoreCase(itSystem.getName()));
				}
			}

			return Optional.of(builder);
		});
	}
}