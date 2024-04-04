package dk.digitalidentity.ap.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryImpl;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;

import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.QOrgUnit;
import dk.digitalidentity.ap.dao.model.QProcess;
import dk.digitalidentity.ap.dao.model.QUser;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.AuthenticatedUser;
import dk.digitalidentity.ap.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Slf4j
public class AutoProcessRepositoryImpl<T, ID, N extends Number & Comparable<N>>
		extends QuerydslJpaPredicateExecutor<T>
		implements RevisionRepository<T, ID, N> {
	private final EntityInformation<T, ?> entityInformation;
	
	// hacks :)
	private final EnversRevisionRepositoryImpl enversImpl;
	private final SimpleJpaRepository<T, ID> jpaRepositoryImpl;

	public AutoProcessRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, RevisionEntityInformation revisionEntityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE, null);
		
		this.entityInformation = entityInformation;
		
		enversImpl = new EnversRevisionRepositoryImpl<T, ID, N>(entityInformation, revisionEntityInformation, entityManager);
		jpaRepositoryImpl = new SimpleJpaRepository<T, ID>(entityInformation, entityManager);
	}

	// sucks, but we have to copy the code (raw, unmodified) from EnversRevisionRepositoryImpl for the next
	// 4 methods, because we cannot extend 2 implementations. We are extending QuerydslJpaPredicateExecutor, and
	// hence have to implement RevisionRepository - but copying four methods is okay, since we can just call
	// our own local instance ;)
	@Override
	public Optional<Revision<N, T>> findLastChangeRevision(ID id) {
		return enversImpl.findLastChangeRevision(id);
	}

	@Override
	public Revisions<N, T> findRevisions(ID id) {
		return enversImpl.findRevisions(id);
	}

	@Override
	public Page<Revision<N, T>> findRevisions(ID id, Pageable pageable) {
		return enversImpl.findRevisions(id, pageable);
	}

	@Override
	public Optional<Revision<N, T>> findRevision(ID id, N revisionNumber) {
		return enversImpl.findRevision(id, revisionNumber);
	}

	public Optional<T> findById(ID id) {
		if (entityInformation.getJavaType().equals(Process.class)) {
			Predicate predicate = QProcess.process.id.eq((Long) id);

			Predicate where = getPredicate(predicate);

			return super.findOne(where);
		}
		else if (entityInformation.getJavaType().equals(User.class)) {
			Predicate predicate = QUser.user.id.eq((Long) id);

			Predicate where = getSameCvrPredicateForUser(predicate);

			return super.findOne(where);
		}
		else if (entityInformation.getJavaType().equals(OrgUnit.class)) {
			Predicate predicate = QOrgUnit.orgUnit.id.eq((Long) id);

			Predicate where = getSameCvrPredicateForOrgUnit(predicate);

			return super.findOne(where);
		}

		// fallback to ordinary JPA lookup ;)
		return jpaRepositoryImpl.findById(id);
	}
	
	@Override
	public Optional<T> findOne(Predicate predicate) {
		if (entityInformation.getJavaType().equals(Process.class)) {
			Predicate where = getPredicate(predicate);

			return super.findOne(where);
		}
		else if (entityInformation.getJavaType().equals(User.class)) {
			Predicate where = getSameCvrPredicateForUser(predicate);

			return super.findOne(where);
		}
		else if (entityInformation.getJavaType().equals(OrgUnit.class)) {
			Predicate where = getSameCvrPredicateForOrgUnit(predicate);

			return super.findOne(where);
		}
		
		return super.findOne(predicate);
	}

	@Override
	public Page<T> findAll(Predicate predicate, Pageable pageable) {
		try {
			if (entityInformation.getJavaType().equals(Process.class)) {
				Predicate where = getPredicate(predicate);
	
				return super.findAll(where, pageable);
			}
			else if (entityInformation.getJavaType().equals(User.class)) {
				Predicate where = getSameCvrPredicateForUser(predicate);
	
				return super.findAll(where, pageable);
			}
			else if (entityInformation.getJavaType().equals(OrgUnit.class)) {
				Predicate where = getSameCvrPredicateForOrgUnit(predicate);
	
				return super.findAll(where, pageable);
			}
	
			return super.findAll(predicate, pageable);
		}
		catch (Exception ex) {
			log.error("Hah 1", ex);
			throw ex;
		}
	}
	
	@Override
	public List<T> findAll(Predicate predicate) {
		try {
			if (entityInformation.getJavaType().equals(Process.class)) {
				Predicate where = getPredicate(predicate);
	
				return super.findAll(where);
			}
			else if (entityInformation.getJavaType().equals(User.class)) {
				Predicate where = getSameCvrPredicateForUser(predicate);
	
				return super.findAll(where);
			}
			else if (entityInformation.getJavaType().equals(OrgUnit.class)) {
				Predicate where = getSameCvrPredicateForOrgUnit(predicate);
	
				return super.findAll(where);
			}
	
			return super.findAll(predicate);
		}
		catch (Exception ex) {
			log.error("Hah 1", ex);
			throw ex;
		}
	}

	// helper methods start here
	
	private Predicate getSameCvrPredicateForUser(Predicate predicate) {
		// Spring Security ensures that a user is logged in, and a CVR number is
		// available before getting here when called through the API, but when
		// internal code (e.g. scheduled tasks) are running, we do not have a
		// currently logged in user, so this code is bypassed in that case
		if (SecurityUtil.getCvr() != null) {
			QUser user = QUser.user;
			Predicate cvr = user.cvr.eq(SecurityUtil.getCvr());
			
			return ExpressionUtils.allOf(predicate, cvr);
		}
		
		return predicate;
	}
	
	private Predicate getSameCvrPredicateForOrgUnit(Predicate predicate) {
		// Spring Security ensures that a user is logged in, and a CVR number is
		// available before getting here when called through the API, but when
		// internal code (e.g. scheduled tasks) are running, we do not have a
		// currently logged in user, so this code is bypassed in that case
		if (SecurityUtil.getCvr() != null) {
			QOrgUnit orgUnit = QOrgUnit.orgUnit;
			Predicate cvr = orgUnit.cvr.eq(SecurityUtil.getCvr());

			return ExpressionUtils.allOf(predicate, cvr);
		}
		
		return predicate;
	}

	private Predicate getPredicate(Predicate predicate) {
		QProcess process = QProcess.process;
		AuthenticatedUser authenticatedUser = SecurityUtil.getUser();

		predicate = handleFreeTextSearch(predicate);

		// processes of type 'GLOBAL_PARENT' automatically becomes visible to all users
		Predicate global = process.type.eq(ProcessType.GLOBAL_PARENT);

		if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_SYSTEM)) {
			// System user always gets exactly what it asks for, no restrictions
			return predicate;
		}
		else if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_SUPERUSER)) {
			Predicate pub = process.visibility.eq(Visibility.PUBLIC);
			Predicate cvr = process.cvr.eq(SecurityUtil.getCvr());
			Predicate superUser = ExpressionUtils.anyOf(global, pub, cvr);
			
			return ExpressionUtils.allOf(predicate, superUser);
		}
		else if (SecurityUtil.getRoles().contains(SecurityUtil.ROLE_LOCAL_SUPERUSER)) {
			Predicate pub = process.visibility.eq(Visibility.PUBLIC);
			Predicate cvrAndMunicipality = ExpressionUtils.allOf(process.cvr.eq(SecurityUtil.getCvr()), process.visibility.eq(Visibility.MUNICIPALITY));
			Predicate reporter = process.reporter.id.eq(authenticatedUser.getId());
			Predicate assignedTo = process.users.any().id.eq(authenticatedUser.getId());

			Set<Predicate> predicates = new HashSet<>();
			for (String positionOrgUnitUuid : authenticatedUser.getPositionOrgUnitUuids()) {
				predicates.add(process.reporter.positions.any().uuid.eq(positionOrgUnitUuid));
			}

			Predicate localSuperUser = ExpressionUtils.anyOf(global, ExpressionUtils.anyOf(predicates), pub, cvrAndMunicipality, reporter, assignedTo);
			
			return ExpressionUtils.allOf(predicate, localSuperUser);
		}
		else {
			Predicate pub = process.visibility.eq(Visibility.PUBLIC);
			Predicate cvrAndMunicipality = ExpressionUtils.allOf(process.cvr.eq(SecurityUtil.getCvr()), process.visibility.eq(Visibility.MUNICIPALITY));
			Predicate reporter = process.reporter.id.eq(authenticatedUser.getId());
			Predicate assignedTo = process.users.any().id.eq(authenticatedUser.getId());
			Predicate normalUser = ExpressionUtils.anyOf(global, pub, cvrAndMunicipality, reporter, assignedTo);
			
			return ExpressionUtils.allOf(predicate, normalUser);
		}
	}

	private Predicate handleFreeTextSearch(Predicate predicate) {
		if (predicate == null) {
			return null;
		}

		List<Predicate> modifiedPredicateHolder = new ArrayList<>();
		String freeTextSearch = analyzePredicateV2(predicate, predicate, modifiedPredicateHolder);
		SecurityUtil.setFreeTextSearch(freeTextSearch);

		if (freeTextSearch != null) {
			QProcess process = QProcess.process;

			Predicate shortDescription = process.shortDescription.contains(freeTextSearch);
			Predicate longDescription = process.longDescription.contains(freeTextSearch);
			Predicate title = process.title.contains(freeTextSearch);
			Predicate searchWords = process.searchWords.contains(freeTextSearch);
			Predicate orgUnitNames = process.orgUnits.any().name.contains(freeTextSearch);
			Predicate reporterName = process.reporter.name.contains(freeTextSearch);
			Predicate contactName = process.contact.name.contains(freeTextSearch);
			Predicate otherContactEmail = process.otherContactEmail.contains(freeTextSearch);
			Predicate ownerName = process.owner.name.contains(freeTextSearch);
			Predicate kleCode = process.kle.startsWith(freeTextSearch);
			Predicate solutionRequests = process.solutionRequests.contains(freeTextSearch);
			Predicate processChallenges = process.processChallenges.contains(freeTextSearch);
			Predicate timeSpendComment = process.timeSpendComment.contains(freeTextSearch);
			Predicate technicalImplementationNotes = process.technicalImplementationNotes.contains(freeTextSearch);
			Predicate organizationalImplementationNotes = process.organizationalImplementationNotes.contains(freeTextSearch);
			Predicate automationDescription = process.automationDescription.contains(freeTextSearch);
			Predicate ratingComment = process.ratingComment.contains(freeTextSearch);

			try {
				// if the freeTextSearch is just a number, perform a search on the process ID and only
				Long id = Long.parseLong(freeTextSearch);

				return process.id.eq(id); 
			}
			catch (Exception ex) {
				; // ignore;
			}

			if (modifiedPredicateHolder.size() > 0) {
				Predicate p = modifiedPredicateHolder.get(0);
				return ExpressionUtils.allOf(ExpressionUtils.anyOf(shortDescription, longDescription, title, searchWords, orgUnitNames, reporterName, contactName, otherContactEmail, ownerName, kleCode, solutionRequests, processChallenges, timeSpendComment, technicalImplementationNotes, organizationalImplementationNotes, automationDescription, ratingComment), p);
			}
			else {
				return ExpressionUtils.anyOf(shortDescription, longDescription, title, searchWords, orgUnitNames, reporterName, contactName, otherContactEmail, ownerName, kleCode, solutionRequests, processChallenges, timeSpendComment, technicalImplementationNotes, organizationalImplementationNotes, automationDescription, ratingComment);
			}
		}

		// nothing found, just return original
		return predicate;
	}

	private String analyzePredicateV2(Predicate originalPredicate, Predicate predicateToAnalyze, List<Predicate> modifiedPredicateHolder) {
		if (predicateToAnalyze instanceof PredicateOperation) {
			PredicateOperation predicateOperation = (PredicateOperation) predicateToAnalyze;

			for (Expression<?> expression : predicateOperation.getArgs()) {
				if (expression instanceof Operation) {
					String result = extractFreeText((Operation) expression);
					if (result != null) {
						Predicate modifiedPredicate = buildPredicateWithoutExpression(originalPredicate, expression);
						if (modifiedPredicate != null) {
							modifiedPredicateHolder.add(modifiedPredicate);
						}

						return result;
					}
					else if (expression instanceof Predicate) {
						// go recursive
						result = analyzePredicateV2(originalPredicate, (Predicate) expression, modifiedPredicateHolder);
						if (result != null) {
							return result;
						}
					}
					else {
						log.warn("Ignoring expression '" + expression.getClass() + "': " + expression);
					}
				}
				else if (log.isDebugEnabled()) {
					log.debug("Expression is not an operation '" + expression.getClass() + "': " + expression);
				}
			}
		}
		else if (predicateToAnalyze instanceof Operation) {
			String result = extractFreeText((Operation) predicateToAnalyze);
			if (result != null) {
				return result;
			}
		}

		return null;
	}
	
	private Predicate buildPredicateWithoutExpression(Predicate originalPredicate, Expression<?> expressionToRemove) {
		List<Expression> result = new ArrayList<>();
		Operator operator = null;

		if (originalPredicate instanceof PredicateOperation) {
			PredicateOperation predicateOperation = (PredicateOperation) originalPredicate;
			operator = predicateOperation.getOperator();
			
			for (Expression<?> expression : predicateOperation.getArgs()) {
				if (expression.equals(expressionToRemove)) {
					continue;
				}

				if (expression instanceof PredicateOperation) {
					Predicate predicate = buildPredicateWithoutExpression((PredicateOperation) expression, expressionToRemove);

					if (predicate != null) {
						result.add(predicate);
					}
				}
				else {
					result.add(expression);
				}
			}
		}
		else if (originalPredicate instanceof Operation) {
			if (!originalPredicate.equals(expressionToRemove)) {
				operator = ((Operation) originalPredicate).getOperator();
				
				result.add(originalPredicate);
			}
		}
		
		if (result.size() > 0) {
			if (result.size() == 1 && result.get(0) instanceof Predicate) {
				return (Predicate) result.get(0);
			}

			return ExpressionUtils.predicate(operator, result.toArray(new Expression[0]));
		}
		
		return null;
	}

	private String extractFreeText(Operation operation) {
		ConstantImpl<T> constantImpl = null;
		PathImpl<T> pathImpl = null;
		String result = null;

		for (Object expression : operation.getArgs()) {
			if (expression instanceof PathImpl) {
				pathImpl = (PathImpl<T>) expression;
			}
			else if (expression instanceof ConstantImpl) {
				constantImpl = (ConstantImpl<T>) expression;
			}
		}

		if (pathImpl != null && constantImpl != null && pathImpl.toString().equals("process.freetext")) {
			result = constantImpl.toString();
		}
		
		return result;
	}
}
