package dk.digitalidentity.ap.revision;

import org.hibernate.envers.RevisionListener;

import dk.digitalidentity.ap.security.SecurityUtil;

public class AutoProcessRevisionListener implements RevisionListener {

	public void newRevision(Object revisionEntity) {
		AutoProcessRevision entity = (AutoProcessRevision) revisionEntity;

		long auditorId = (SecurityUtil.getUser() != null) ? SecurityUtil.getUser().getId() : -1;
		String auditorName = (SecurityUtil.getUserId() != null) ? SecurityUtil.getUser().getName() : "system";

		entity.setAuditorId(auditorId);
		entity.setAuditorName(auditorName);
	}
}