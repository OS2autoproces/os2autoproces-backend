package dk.digitalidentity.ap.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.envers.repository.support.EnversRevisionRepository;
import org.springframework.data.history.AnnotationRevisionMetadata;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.RevisionSort;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.core.EntityInformation;
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
import lombok.extern.log4j.Log4j;

@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
@Log4j
public class AutoProcessRepositoryImpl<T, ID extends Serializable, N extends Number & Comparable<N>> extends QueryDslJpaRepository<T, ID> implements EnversRevisionRepository<T, ID, N> {
	private final EntityInformation<T, ?> entityInformation;
	private final RevisionEntityInformation revisionEntityInformation;
	private final EntityManager entityManager;

	public AutoProcessRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, RevisionEntityInformation revisionEntityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.revisionEntityInformation = revisionEntityInformation;
		this.entityInformation = entityInformation;
		this.entityManager = entityManager;
	}

	public AutoProcessRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager, RevisionEntityInformation revisionEntityInformation, EntityPathResolver resolver) {
		super(entityInformation, entityManager, resolver);
		
		this.entityInformation = entityInformation;
		this.revisionEntityInformation = revisionEntityInformation;
		this.entityManager = entityManager;
	}
	
	@Override
	public Revision<N, T> findLastChangeRevision(ID id) {
		Class<T> type = entityInformation.getJavaType();
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<Number> revisions = reader.getRevisions(type, id);

		if (revisions.isEmpty()) {
			return null;
		}

		N latestRevision = (N) revisions.get(revisions.size() - 1);

		Class<?> revisionEntityClass = revisionEntityInformation.getRevisionEntityClass();

		Object revisionEntity = reader.findRevision(revisionEntityClass, latestRevision);
		RevisionMetadata<N> metadata = (RevisionMetadata<N>) getRevisionMetadata(revisionEntity);
		
		return new Revision<N, T>(metadata, reader.find(type, id, latestRevision));
	}
	
	@Override
	public Revision<N, T> findRevision(ID id, N revisionNumber) {
		return getEntityForRevision(revisionNumber, id, AuditReaderFactory.get(entityManager));
	}
	
	@Override
	public Revisions<N, T> findRevisions(ID id) {
		Class<T> type = entityInformation.getJavaType();
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<? extends Number> revisionNumbers = reader.getRevisions(type, id);

		return revisionNumbers.isEmpty() ? new Revisions<N, T>(Collections.EMPTY_LIST) : getEntitiesForRevisions((List<N>) revisionNumbers, id, reader);
	}
	
	@Override
	public Page<Revision<N, T>> findRevisions(ID id, Pageable pageable) {
		Class<T> type = entityInformation.getJavaType();
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<Number> revisionNumbers = reader.getRevisions(type, id);

		boolean isDescending = RevisionSort.getRevisionDirection(pageable.getSort()).isDescending();

		if (isDescending) {
			Collections.reverse(revisionNumbers);
		}

		if (pageable.getOffset() > revisionNumbers.size()) {
			return new PageImpl<Revision<N, T>>(Collections.<Revision<N, T>>emptyList(), pageable, 0);
		}

		int upperBound = pageable.getOffset() + pageable.getPageSize();
		upperBound = upperBound > revisionNumbers.size() ? revisionNumbers.size() : upperBound;

		List<? extends Number> subList = revisionNumbers.subList(pageable.getOffset(), upperBound);
		Revisions<N, T> revisions = getEntitiesForRevisions((List<N>) subList, id, reader);

		return new PageImpl<Revision<N, T>>(revisions.getContent(), pageable, revisionNumbers.size());
	}
	
	private Revisions<N, T> getEntitiesForRevisions(List<N> revisionNumbers, ID id, AuditReader reader) {
		Class<T> type = entityInformation.getJavaType();
		Map<N, T> revisions = new HashMap<N, T>(revisionNumbers.size());

		Class<?> revisionEntityClass = revisionEntityInformation.getRevisionEntityClass();
		Map<Number, Object> revisionEntities = (Map<Number, Object>) reader.findRevisions(revisionEntityClass, new HashSet<Number>(revisionNumbers));

		for (Number number : revisionNumbers) {
			revisions.put((N) number, reader.find(type, id, number));
		}

		return new Revisions<N, T>(toRevisions(revisions, revisionEntities));
	}

	private Revision<N, T> getEntityForRevision(N revisionNumber, ID id, AuditReader reader) {
		Class<?> type = revisionEntityInformation.getRevisionEntityClass();

		T revision = (T) reader.findRevision(type, revisionNumber);
		Object entity = reader.find(entityInformation.getJavaType(), id, revisionNumber);

		return new Revision<N, T>((RevisionMetadata<N>) getRevisionMetadata(revision), (T) entity);
	}

	private List<Revision<N, T>> toRevisions(Map<N, T> source, Map<Number, Object> revisionEntities) {
		List<Revision<N, T>> result = new ArrayList<Revision<N, T>>();

		for (Map.Entry<N, T> revision : source.entrySet()) {
			N revisionNumber = revision.getKey();
			T entity = revision.getValue();
			RevisionMetadata<N> metadata = (RevisionMetadata<N>) getRevisionMetadata(revisionEntities.get(revisionNumber));

			result.add(new Revision<N, T>(metadata, entity));
		}

		Collections.sort(result);

		return Collections.unmodifiableList(result);
	}

	private RevisionMetadata<?> getRevisionMetadata(Object object) {
		if (object instanceof DefaultRevisionEntity) {
			return new DefaultRevisionMetadata((DefaultRevisionEntity) object);
		}
		else {
			return new AnnotationRevisionMetadata<N>(object, RevisionNumber.class, RevisionTimestamp.class);
		}
	}

	@Override
	public T findOne(ID id) {
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

		return super.findOne(id);
	}

	@Override
	public Page<T> findAll(Predicate predicate, Pageable pageable) {
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
	
	@Override
	public List<T> findAll(Predicate predicate) {
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

		if (freeTextSearch != null) {
			QProcess process = QProcess.process;
			
			Predicate shortDescription = process.shortDescription.contains(freeTextSearch);
			Predicate longDescription = process.longDescription.contains(freeTextSearch);
			Predicate title = process.title.contains(freeTextSearch);
			Predicate searchWords = process.searchWords.contains(freeTextSearch);
			Predicate orgUnitNames = process.orgUnits.any().name.contains(freeTextSearch);
			Predicate reporterName = process.reporter.name.contains(freeTextSearch);
			Predicate contactName = process.contact.name.contains(freeTextSearch);
			Predicate ownerName = process.owner.name.contains(freeTextSearch);
			Predicate kleCode = process.kle.startsWith(freeTextSearch);
			
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
				return ExpressionUtils.allOf(ExpressionUtils.anyOf(shortDescription, longDescription, title, searchWords, orgUnitNames, reporterName, contactName, ownerName, kleCode), p);
			}
			else {
				return ExpressionUtils.anyOf(shortDescription, longDescription, title, searchWords, orgUnitNames, reporterName, contactName, ownerName, kleCode);
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
