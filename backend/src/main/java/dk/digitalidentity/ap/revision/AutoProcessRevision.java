package dk.digitalidentity.ap.revision;

import javax.persistence.Entity;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "revisions")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@RevisionEntity(AutoProcessRevisionListener.class)
public class AutoProcessRevision extends DefaultRevisionEntity {
	private static final long serialVersionUID = 9113880695800122023L;

	private long auditorId;
	private String auditorName;
}
