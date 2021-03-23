package dk.digitalidentity.ap.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.ap.api.model.OrgUnitDTO;
import dk.digitalidentity.ap.api.model.OrganisationDTO;
import dk.digitalidentity.ap.api.model.OrganisationImportResponse;
import dk.digitalidentity.ap.api.model.UserDTO;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.User;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class OrganisationService {

	@Autowired
	private UserService userService;

	@Autowired
	private OrgUnitService orgUnitService;

	@Autowired
	private EntityManager entityManager;

	public OrganisationImportResponse bigImport(OrganisationDTO organisation, String cvr) throws Exception {
		OrganisationImportResponse response = new OrganisationImportResponse();

		List<OrgUnit> ous = parseOrgUnits(organisation.getOrgUnits(), cvr);
		List<User> users = parseUsers(organisation.getUsers(), ous, cvr);

		String errorMsg = null;
		if ((errorMsg = validateUsers(users)) != null) {
			log.warn("Rejecting payload due to bad users: " + errorMsg);
			throw new Exception(errorMsg);
		}
		else if ((errorMsg = validateOUs(ous)) != null) {
			log.warn("Rejecting payload due to bad ous: " + errorMsg);
			throw new Exception(errorMsg);
		}

		List<User> existingUsers = userService.getByCvrIncludingInactive(cvr);
		List<OrgUnit> existingOUs = orgUnitService.getByCvrIncludingInactive(cvr);

		processOrgUnits(cvr, ous, existingOUs, response);
		softDeleteOUs(cvr, ous, existingOUs, response);

		processUsers(cvr, users, existingUsers, existingOUs, response);
		softDeleteUsers(cvr, users, existingUsers, response);

		return response;
	}

	private List<OrgUnit> parseOrgUnits(List<OrgUnitDTO> orgUnits, String cvr) {
		List<OrgUnit> result = new ArrayList<>();

		for (OrgUnitDTO dto : orgUnits) {
			OrgUnit orgUnit = new OrgUnit();
			orgUnit.setActive(true);
			orgUnit.setCvr(cvr);
			orgUnit.setName(dto.getName());
			orgUnit.setUuid(dto.getUuid().toLowerCase());
			
			result.add(orgUnit);
		}
		
		return result;
	}

	private List<User> parseUsers(List<UserDTO> users, List<OrgUnit> orgUnits, String cvr) {
		List<User> result = new ArrayList<>();
		
		for (UserDTO dto : users) {
			User user = new User();
			user.setActive(true);
			user.setCvr(cvr);
			user.setEmail(dto.getEmail());
			user.setName(dto.getName());
			user.setUuid(dto.getUuid().toLowerCase());
			user.setPositions(new ArrayList<>());
			
			for (String posUuid : dto.getPositions()) {
				boolean found = false;

				for (OrgUnit orgUnit : orgUnits) {
					if (orgUnit.getUuid().equalsIgnoreCase(posUuid.toLowerCase())) {
						user.getPositions().add(orgUnit);
						
						found = true;
						break;
					}
				}
				
				if (!found) {
					log.warn("Failed to find OrgUnit for user position (" + cvr + "). User: " + user.getName() + ", UserUUID: " + user.getUuid().toLowerCase() + ", OrgUnitUUID: " + posUuid.toLowerCase());
				}
			}
			
			result.add(user);
		}

		return result;
	}

	private String validateOUs(List<OrgUnit> ous) {
		for (OrgUnit orgUnit : ous) {
			if (orgUnit.getName() == null || orgUnit.getName().length() == 0) {
				return "OrgUnit with null/empty name not allowed";
			}
			
			if (orgUnit.getUuid() == null) {
				return "OrgUnit with null UUID not allowed";
			}
			
			try {
				UUID.fromString(orgUnit.getUuid());
			}
			catch (Exception ex) {
				return "OrgUnits must have a valid UUID (" + orgUnit.getUuid() + "), encountered error while parsing: " + ex.getMessage();
			}
		}

		return null;
	}

	private String validateUsers(List<User> users) {
		for (User user : users) {
			if (user.getName() == null || user.getName().length() == 0) {
				return "User with null/empty name not allowed";
			}
			
			if (user.getUuid() == null) {
				return "Users with null UUID not allowed";
			}

			try {
				UUID.fromString(user.getUuid());
			}
			catch (Exception ex) {
				return "Users must have a valid UUID (" + user.getUuid() + "), encountered error while parsing: " + ex.getMessage();
			}
		}

		return null;
	}

	private void processOrgUnits(String cvr, List<OrgUnit> newOus, List<OrgUnit> existingOUs, OrganisationImportResponse response) {
		long updated = 0, created = 0;

		for (OrgUnit ou : newOus) {
			boolean found = false;

			for (OrgUnit existingOU : existingOUs) {
				if (ou.getUuid().equals(existingOU.getUuid())) {
					boolean changes = false;
					found = true;

					// If existing OU was soft-deleted we restore it
					if (!existingOU.isActive()) {
						existingOU.setActive(true);
						changes = true;
					}

					// Rename scenario
					if (!ou.getName().equals(existingOU.getName())) {
						existingOU.setName(ou.getName());
						changes = true;
					}

					// Update existing OrgUnit with new data
					if (changes) {
						updated++;
						orgUnitService.save(existingOU);
					}

					break;
				}
			}

			// if not found we're dealing with a totally new OU add it to database
			if (!found) {
				created++;
				orgUnitService.save(ou);
			}
		}

		if (response != null) {
			response.setOusCreated(created);
			response.setOusUpdated(updated);
		}
		
		log.info("Updated " + updated + " ou(s) and  created " + created + " ou(s) for " + cvr);
	}

	private void softDeleteOUs(String cvr, List<OrgUnit> newOus, List<OrgUnit> existingOUs, OrganisationImportResponse response) {
		long deleted = 0;

		for (OrgUnit existingOU : existingOUs) {
			boolean found = false;

			if (!existingOU.isActive()) {
				continue;
			}

			for (OrgUnit ou : newOus) {
				if (ou.getUuid().equals(existingOU.getUuid())) {
					found = true;
				}
			}

			if (!found) {
				existingOU.setActive(false);
				orgUnitService.save(existingOU);
				deleted++;
			}
		}
		
		if (response != null) {
			response.setOusDeleted(deleted);
		}
		
		log.info("Deleted " + deleted + " ou(s) for " + cvr);
	}

	private void processUsers(String cvr, List<User> newUsers, List<User> existingUsers, List<OrgUnit> existingOrgUnits, OrganisationImportResponse response) {
		List<User> tobeAdded = new ArrayList<>();
		long updated = 0;
		
		for (User newUser : newUsers) {
			boolean found = false;

			for (User existingUser : existingUsers) {
				if (newUser.getUuid().equals(existingUser.getUuid())) {
					found = true;
					boolean changes = false;

					// if existing User was soft-deleted we restore it
					if (!existingUser.isActive()) {
						existingUser.setActive(true);
						changes = true;
					}
					
					// name change
					if (!existingUser.getName().equals(newUser.getName())) {
						existingUser.setName(newUser.getName());
						changes = true;
					}
					
					// email change
					if ((existingUser.getEmail() == null && newUser.getEmail() != null) ||
						(existingUser.getEmail() != null && newUser.getEmail() == null) ||
						(existingUser.getEmail() != null && newUser.getEmail() != null) && !existingUser.getEmail().equals(newUser.getEmail())) {

						existingUser.setEmail(newUser.getEmail());
						changes = true;
					}
					
					// if there are new positions add them to existing object
					for (OrgUnit newOrgUnit : newUser.getPositions()) {
						boolean foundPosition = false;
						
						for (OrgUnit existingOrgUnit : existingUser.getPositions()) {
							if (existingOrgUnit.getUuid().equals(newOrgUnit.getUuid())) {
								foundPosition = true;
								break;
							}
						}
						
						if (!foundPosition) {
							changes = true;
							existingUser.getPositions().add(newOrgUnit);
						}
					}

					// remove if existing user has positions that are not in the new one
					for (Iterator<OrgUnit> iterator = existingUser.getPositions().iterator(); iterator.hasNext();) {
						OrgUnit existingOrgUnit = iterator.next();						
						boolean foundPosition = false;
						
						for (OrgUnit newOrgUnit : newUser.getPositions()) {
							if (existingOrgUnit.getUuid().equals(newOrgUnit.getUuid())) {
								foundPosition = true;
								break;
							}
						}
						
						if (!foundPosition) {
							iterator.remove();
							changes = true;
						}
					}

					if (changes) {
						updated++;
						userService.save(existingUser);
					}

					break;
				}
			}

			if (!found) {
				tobeAdded.add(newUser);
			}
		}

		log.info("Updated " + updated + " user(s) and created " + tobeAdded.size() + " user(s) for " + cvr);

		if (response != null) {
			response.setUsersCreated(tobeAdded.size());
			response.setUsersUpdated(updated);
		}

		bulkInsert(tobeAdded, (existingUsers.size() == 0));
	}
	
	private void bulkInsert(List<User> list, boolean firstRun) {
		if (firstRun) {
			entityManager.flush();

			for (int i = 0; i < list.size() ; ++i) {
				entityManager.persist(list.get(i));
				if (i % 20 == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
			
			entityManager.flush();
			entityManager.clear();
		}
		else {
			userService.save(list);
		}
	}

	private void softDeleteUsers(String cvr, List<User> newUsers, List<User> existingUsers, OrganisationImportResponse response) {
		long deleted = 0;
		
		for (User existingUser : existingUsers) {
			boolean found = false;

			if (!existingUser.isActive()) {
				continue;
			}

			for (User newUser : newUsers) {
				if (newUser.getUuid().equals(existingUser.getUuid())) {
					found = true;
				}
			}

			if (!found) {
				//soft delete
				deleted++;
				existingUser.setActive(false);
				userService.save(existingUser);
			}
		}
		
		if (response != null) {
			response.setUsersDeleted(deleted);
		}

		log.info("Deleted " + deleted + " user(s) for " + cvr);
	}
}
