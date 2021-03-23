package dk.digitalidentity.ap.dao.model.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.Level;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.ProcessType;
import dk.digitalidentity.ap.dao.model.enums.Status;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.SecurityUtil;

public class ProcessValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Process.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (target instanceof Process) {
			Process process = (Process) target;
			
			ProcessType type = (process.getType() != null) ? process.getType() : ProcessType.CHILD;
			switch (type) {
				case CHILD:
					if (process.getChildren() != null && process.getChildren().size() > 0) {
						errors.rejectValue("illegalAccessField", "validation.process.children.notparent");
					}
					
					if (process.getParents() != null && process.getParents().size() > 0) {
						boolean globalParent = false;
						
						for (Process parent : process.getParents()) {
							if (parent.getType().equals(ProcessType.GLOBAL_PARENT)) {
								globalParent = true;
							}
						}
						
						switch (process.getVisibility()) {
							case MUNICIPALITY:
								if (globalParent) {
									errors.rejectValue("visibility", "validation.process.visibility.notpublic");
								}
								break;
							case PERSONAL:
								if (globalParent) {
									errors.rejectValue("visibility", "validation.process.visibility.notpublic");
								}
								else {
									errors.rejectValue("visibility", "validation.process.visibility.notmunicipality");
								}
								break;
							case PUBLIC:
								break;
						}
					}
					break;
				case GLOBAL_PARENT:
				case PARENT:
					if (process.getParents() != null && process.getParents().size() > 0) {
						errors.rejectValue("illegalAccessField", "validation.process.parents.notallowed");
					}
					
					if (process.getChildren() != null && process.getChildren().size() > 0) {
						for (Process child : process.getChildren()) {
							
							// because of security, some children may be nulled away (if no read-access) 
							if (child == null) {
								continue;
							}

							if (!child.getType().equals(ProcessType.CHILD)) {
								errors.rejectValue("illegalAccessField", "validation.process.children.noparent");
							}
							else if (process.getType().equals(ProcessType.PARENT) && !child.getCvr().equals(SecurityUtil.getCvr())) {
								errors.rejectValue("illegalAccessField", "validation.process.children.owncvronly");
							}
						}
					}
					break;
			}
			
			// no more validation required for parent processes
			if (!type.equals(ProcessType.CHILD)) {

				// setup hard coded (required) values for parent processes (not the best way
				// to do this - perhaps some interceptor?)
				process.setPhase(Phase.IDEA);
				process.setStatus(Status.INPROGRESS);
				process.setKlaProcess(false);
				process.setTimeSpendComputedTotal(0);
				process.setTimeSpendEmployeesDoingProcess(0);
				process.setTimeSpendOccurancesPerEmployee(0);
				process.setTimeSpendPercentageDigital(0);
				process.setTimeSpendPerOccurance(0);
				process.setLevelOfChange(Level.NOT_SET);
				process.setLevelOfDigitalInformation(Level.NOT_SET);
				process.setLevelOfProfessionalAssessment(Level.NOT_SET);
				process.setLevelOfQuality(Level.NOT_SET);
				process.setLevelOfSpeed(Level.NOT_SET);
				process.setLevelOfRoutineWorkReduction(Level.NOT_SET);
				process.setLevelOfStructuredInformation(Level.NOT_SET);
				process.setLevelOfUniformity(Level.NOT_SET);
				process.setEvaluatedLevelOfRoi(Level.NOT_SET);

				if (type.equals(ProcessType.GLOBAL_PARENT)) {
					process.setVisibility(Visibility.PUBLIC);
				}
				else {
					process.setVisibility(Visibility.MUNICIPALITY);
				}
				
				return;
			}
			
			// validate KLA rules
			if (process.getKla() != null && process.getKla().length() > 0) {
				/* allowed values are on the following form
				10.88.11
				83.13.09.13
				01.66.09.00.08
				*/
				final String regex = "^(([\\d]{2}\\.[\\d]{2}\\.[\\d]{2})|([\\d]{2}\\.[\\d]{2}\\.[\\d]{2}\\.[\\d]{2})|([\\d]{2}\\.[\\d]{2}\\.[\\d]{2}\\.[\\d]{2}\\.[\\d]{2}))$";
				if (!process.getKla().matches(regex)) {
					errors.rejectValue("kla", "validation.process.kla.notvalid");
				}
			}
			
			// validate relationships
			String cvr = SecurityUtil.getCvr();
			if (process.getOrgUnits() != null && process.getOrgUnits().size() > 0) {
				for (OrgUnit orgUnit : process.getOrgUnits()) {
					if (!cvr.equals(orgUnit.getCvr())) {
						errors.rejectValue("illegalAccessField", "validation.process.orgunits.cvr");
						break;
					}
				}
			}
			
			if (process.getContact() != null) {
				if (!cvr.equals(process.getContact().getCvr())) {
					errors.rejectValue("illegalAccessField", "validation.process.contact.cvr");
				}
			}

			if (process.getOwner() != null) {
				if (!cvr.equals(process.getOwner().getCvr())) {
					errors.rejectValue("illegalAccessField", "validation.process.owner.cvr");
				}
			}
			
			if (process.getUsers() != null && process.getUsers().size() > 0) {
				for (User user : process.getUsers()) {
					if (!cvr.equals(user.getCvr())) {
						errors.rejectValue("illegalAccessField", "validation.process.users.cvr");
						break;
					}
				}
			}

			if (process.getPhase() == null) {
				errors.rejectValue("phase", "validation.process.phase.notnull");
			}
			else {
				// fall-through, as rules for a lower-level phase also applies to higher-levels
				switch (process.getPhase()) {
					case OPERATION:
						// no extra required fields in this phase
					case IMPLEMENTATION:
						if (process.getTechnologies() == null) {
							errors.rejectValue("technologies", "validation.process.technologies.notnull");
						}
						else {
							int count = 0;

							// attempting to link to non-existing technologies will add null entries to the list, so size()
							// cannot be used to verify that a real technology has been linked to
							for (Technology technology : process.getTechnologies()) {
								if (technology != null) {
									count++;
								}
							}
							
							if (count == 0) {
								errors.rejectValue("technologies", "validation.process.technologies.notnull");
							}
						}
					case DEVELOPMENT:
						// no extra validation here
					case SPECIFICATION:
						// no extra validation here
					case PREANALYSIS:
						// no extra required fields in this phase
					case IDEA:
						// no extra required fields in this phase
						break;
				}
			}
		}
	}
}
