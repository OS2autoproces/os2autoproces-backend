package dk.digitalidentity.ap.task;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dk.digitalidentity.ap.api.model.OrgUnitDTO;
import dk.digitalidentity.ap.api.model.OrganisationDTO;
import dk.digitalidentity.ap.api.model.UserDTO;
import dk.digitalidentity.ap.service.OrganisationService;
import dk.digitalidentity.ap.task.model.STSHierarchy;
import dk.digitalidentity.ap.task.model.STSHierarchyWrapper;
import dk.digitalidentity.ap.task.model.STSOU;
import dk.digitalidentity.ap.task.model.STSPosition;
import dk.digitalidentity.ap.task.model.STSUser;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class STSOrganisationService {

	@Value("${stsorgsync.url}")
	private String stsOrgSyncUrl;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private OrganisationService organisationImporter;

	@Transactional(rollbackFor = Exception.class)
	public synchronized void loadOrganisation(String cvr) throws Exception {
		log.info("Attempting to fetch organisation data from STS Organisation for CVR " + cvr);
		
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("cvr", cvr);
		HttpEntity<Object> headers = new HttpEntity<>(headerMap);
		
		ResponseEntity<String> keyResponse = restTemplate.exchange(stsOrgSyncUrl, HttpMethod.GET, headers, String.class);
		
		if (keyResponse.getStatusCodeValue() != 200) {
			log.error("Synchronization (getKey) failed for " + cvr + ": " + keyResponse.getStatusCodeValue());
			return;
		}

		String key = keyResponse.getBody();
		
		ResponseEntity<STSHierarchyWrapper> response = null;
		for (int i = 0; i < 15; i++) { // 45 minutes total
			Thread.sleep(2 * 75 * 1000); // sleep 2,5 minutes before attempting to read again

			response = restTemplate.getForEntity(stsOrgSyncUrl + "/" + key, STSHierarchyWrapper.class);

			if (response.getStatusCodeValue() != 404) {
				break;
			}
		}

		if (response.getStatusCodeValue() != 200) {
			log.error("Synchronization (getResponse) failed for " + cvr + ": " + response.getStatusCodeValue());
			return;
		}

		STSHierarchyWrapper hierarchyWrapper = response.getBody();
		if (hierarchyWrapper.getStatus() != 0) {
			log.error("Synchronization (HierarchyStatus) failed for " + cvr + ": " + hierarchyWrapper.getStatus());
			return;
		}

		STSHierarchy hierarchy = hierarchyWrapper.getResult();
		
		OrganisationDTO organisation = new OrganisationDTO();
		organisation.setOrgUnits(new ArrayList<>());
		organisation.setUsers(new ArrayList<>());

		for (STSUser stsUser : hierarchy.getUsers()) {
			UserDTO user = new UserDTO();
			user.setEmail(stsUser.getEmail());
			user.setName(stsUser.getName());
			user.setUuid(stsUser.getUuid());
			user.setPositions(new ArrayList<>());

			for (STSPosition stsPosition : stsUser.getPositions()) {
				if (!user.getPositions().contains(stsPosition.getUuid())) {
					user.getPositions().add(stsPosition.getUuid());
				}
			}
			
			organisation.getUsers().add(user);
		}

		for (STSOU stsOU : hierarchy.getOus()) {
			OrgUnitDTO ou = new OrgUnitDTO();
			ou.setName(stsOU.getName());
			ou.setUuid(stsOU.getUuid());
			
			organisation.getOrgUnits().add(ou);
		}
		
		organisationImporter.bigImport(organisation, cvr);
	}
}
