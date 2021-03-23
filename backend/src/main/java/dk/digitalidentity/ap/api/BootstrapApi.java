package dk.digitalidentity.ap.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.svenjacobs.loremipsum.LoremIpsum;
import dk.digitalidentity.ap.dao.MunicipalityDao;
import dk.digitalidentity.ap.dao.OrgUnitDao;
import dk.digitalidentity.ap.dao.ProcessDao;
import dk.digitalidentity.ap.dao.TechnologyDao;
import dk.digitalidentity.ap.dao.UserDao;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import dk.digitalidentity.ap.dao.model.enums.Level;
import dk.digitalidentity.ap.dao.model.enums.Phase;
import dk.digitalidentity.ap.dao.model.enums.RunPeriod;
import dk.digitalidentity.ap.dao.model.enums.Status;
import dk.digitalidentity.ap.dao.model.enums.Visibility;
import dk.digitalidentity.ap.security.AuthenticatedUser;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.saml.model.TokenUser;
import lombok.Getter;
import lombok.Setter;

// TODO: move this logic to the test package once we no longer needs this for development
// TODO: actually we should keep it for development as well, and then just add a dev-mode
//       check
@RestController
public class BootstrapApi {
	private ArrayList<String> names;
	private ArrayList<String> surnames;
	private Random random = new Random();
	private LoremIpsum lorem = new LoremIpsum();
	private User user1 = null;

	@Autowired
	private ProcessDao processDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private OrgUnitDao orgUnitDao;
	
	@Autowired
	private TechnologyDao technologyDao;
	
	@Autowired
	private MunicipalityDao municipalityDao;

	@PostConstruct
	private void init() {
		Resource resource = new ClassPathResource("randomuser.csv");
		Reader in;

		names = new ArrayList<>();
		surnames = new ArrayList<>();

		try {
			in = new InputStreamReader(resource.getInputStream());
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

			for (CSVRecord record : records) {
				names.add(record.get("first"));
				surnames.add(record.get("last"));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/bootstrap")
	public ResponseEntity<?> bootstrapData() {
		if (bootstrap(1000, 20, 500)) {
			return ResponseEntity.ok().build();
		}
		
		return new ResponseEntity<String>("Bootstrap can only be used when there is no data in the database.", HttpStatus.BAD_REQUEST);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean bootstrap(int userCount, int ouCount, int processCount) {
		String municipality1 = "29189978";
		String municipality2 = "87654321";
		
		if (processDao.findAll().size() == 0 || userDao.findAll().size() == 0 || orgUnitDao.findAll().size() == 0) {
			
			Municipality m1 = new Municipality();
			m1.setApiKey(UUID.randomUUID().toString());
			m1.setCvr("12345678");
			m1.setName("Demo Kommune 1");
			m1 = municipalityDao.save(m1);

			Municipality m2 = new Municipality();
			m2.setApiKey(UUID.randomUUID().toString());
			m2.setCvr("29189978");
			m2.setName("Syddjurs Kommune");
			m2 = municipalityDao.save(m2);

			Municipality m3 = new Municipality();
			m3.setApiKey(UUID.randomUUID().toString());
			m3.setCvr("87654321");
			m3.setName("Demo Kommune 2");
			m3 = municipalityDao.save(m3);
			
			// this user is for tests
			User user = new User();
			user.setActive(true);
			user.setCvr(municipality1);
			user.setEmail("psu@digital-identity.dk");
			user.setName("Piotr Suski");
			user.setUuid("39223c72-bfd5-4fd1-aee1-6c9a740b8f7f");
			user1 = userDao.save(user);

			user = new User();
			user.setActive(true);
			user.setCvr(municipality1);
			user.setEmail("kommune@digital-identity.dk");
			user.setName("Kommune Bruger");
			user.setUuid("ca6d14e5-03e4-48d8-a6a4-3bc13f6c1704");
			userDao.save(user);

			user = new User();
			user.setActive(true);
			user.setCvr(municipality1);
//			user.setEmail("itminds1@digital-identity.dk");
			user.setName("IT Minds 1");
			user.setUuid("097ff0a8-d853-4bfe-90e0-22ca3287dc24");
			userDao.save(user);
			
			user = new User();
			user.setActive(true);
			user.setCvr(municipality1);
//			user.setEmail("itminds2@digital-identity.dk");
			user.setName("IT Minds 2");
			user.setUuid("982c0709-8e13-4e04-86a2-25b84ca69538");
			userDao.save(user);
			
			user = new User();
			user.setActive(true);
			user.setCvr(municipality1);
//			user.setEmail("itminds3@digital-identity.dk");
			user.setName("IT Minds 3");
			user.setUuid("1848a9f9-6403-4b78-88fa-12352d7b31a6");
			userDao.save(user);

			generateOrgUnitsAndUsers(municipality1, userCount, ouCount);
			generateOrgUnitsAndUsers(municipality2, userCount, ouCount);

			fakeLogin(municipality1, getRandomUserFromDB(municipality1));
			Technology technology = new Technology();
			technology.setName("Tech 1");
			technologyDao.save(technology);

			generateProcesses(municipality1, technology, processCount);
			generateProcesses(municipality2, technology, processCount);

			return true;
		}

		return false;
	}

	private void generateProcesses(String municipality, Technology technology, long processCount) {
		for (int i = 0; i < processCount; i++) {
			fakeLogin(municipality, getRandomUserFromDB(municipality));

			Process p = new Process();
			p.setPhase(Phase.values()[random.nextInt(Phase.values().length)]);
			p.setStatus(Status.values()[random.nextInt(Status.values().length)]);
			if (!p.getStatus().equals(Status.INPROGRESS)) {
				p.setStatusText(lorem.getWords(random.nextInt(10)));
			}
			
			if (!p.getPhase().equals(Phase.IDEA) && !p.getPhase().equals(Phase.PREANALYSIS)) {
				p.setTechnologies(new ArrayList<>());
				p.getTechnologies().add(technology);
			}

			p.setTitle("Demo " + lorem.getWords(random.nextInt(3) + 1, 2));
			p.setLongDescription(lorem.getWords(random.nextInt(49)));
			p.setShortDescription(lorem.getWords(random.nextInt(10)));
			p.setVisibility(Visibility.values()[random.nextInt(Visibility.values().length)]);
			p.setLegalClause(lorem.getWords(random.nextInt(19)));
			if (random.nextInt(3) == 2) {
				p.setLegalClauseLastVerified(new Date());
			}
			
			// bit of randomness to KLE (but not a lot)
			p.setKle((random.nextInt(10) + 10) + "." + (random.nextInt(10) + 10) + ".00");
			p.setKlaProcess(false);
			p.setContact(getRandomUserFromDB(municipality));
			p.setOwner(getRandomUserFromDB(municipality));
			p.setCvr(municipality);

			p.setProcessChallenges(lorem.getWords(random.nextInt(10)));
			p.setSolutionRequests(lorem.getWords(random.nextInt(10)));
			
			if (random.nextInt(3) == 2) {
				p.setTechnicalImplementationNotes(lorem.getWords(random.nextInt(49)));
				p.setOrganizationalImplementationNotes(lorem.getWords(random.nextInt(49)));
			}

			if (random.nextInt(3) == 2) {
				p.setKlId("ID" + random.nextInt(100));
			}
			if (random.nextInt(3) == 2) {
				p.setEsdhReference("ID" + random.nextInt(100));
			}
			
			if (random.nextInt(10) == 2) {
				p.setDecommissioned(new Date());
			}

			p.setRunPeriod(RunPeriod.ONDEMAND);
			p.setVendor("Some Vendor");
			p.setEsdhReference("81928467");
			p.setTimeSpendComment("Some Comment");

			p.setTimeSpendComputedTotal(random.nextInt(3));
			p.setTimeSpendEmployeesDoingProcess(random.nextInt(3));
			p.setTimeSpendOccurancesPerEmployee(random.nextInt(3));
			p.setTimeSpendPerOccurance(random.nextInt(3));
			p.setLevelOfChange(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfDigitalInformation(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfProfessionalAssessment(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfSpeed(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfQuality(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfRoutineWorkReduction(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfStructuredInformation(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setLevelOfUniformity(Level.values()[random.nextInt(Level.values().length - 1)]);
			p.setEvaluatedLevelOfRoi(Level.values()[random.nextInt(Level.values().length - 1)]);

			p.setDomains(new ArrayList<>());
			if (random.nextInt(2) == 1) {
				p.getDomains().add(Domain.values()[random.nextInt(Domain.values().length)]);
			}
			if (random.nextInt(2) == 1) {
				Domain domain = Domain.values()[random.nextInt(Domain.values().length)];

				if (p.getDomains().size() == 0 || !p.getDomains().get(0).equals(domain)) {
					p.getDomains().add(domain);
				}
			}

			// 20% of all processes are associated with user1
			p.setUsers(new ArrayList<>());
			if (municipality.equals(user1.getCvr())) {
				if (random.nextInt(4) == 1) {
					p.getUsers().add(user1);
				}
			}

			p.setItSystemsDescription("xxx");
			
			p.setSearchWords("");

			for (int j = 0; j < 3; j++) {
				p.getUsers().add(getRandomUserFromDB(municipality));
			}

			processDao.save(p);
		}
	}

	private User getRandomUserFromDB(String municipality) {
		List<User> users = userDao.findAll();
		List<User> localUsers = users.stream().filter(u -> u.getCvr().equals(municipality)).collect(Collectors.toList());

		return localUsers.get(random.nextInt(localUsers.size()));
	}

	private void generateOrgUnitsAndUsers(String municipality, int userCount, int ouCount) {
		OrgUnit[] orgUnits = new OrgUnit[ouCount];

		for (int i = 0; i < orgUnits.length; i++) {
			OrgUnit orgUnit = new OrgUnit();
			orgUnit.setActive(true);
			orgUnit.setCvr(municipality);
			orgUnit.setName("My Orgunit " + i + 1);
			orgUnit.setUuid(UUID.randomUUID().toString());
			orgUnits[i] = orgUnit;
		}

		List<User> users = new ArrayList<>();
		for (int i = 0; i < userCount; i++) {
			User user = new User();
			user.setActive(true);
			user.setCvr(municipality);
			NameAndEmail randomNameAndEmail = getRandomNameAndEmail();

			// we do not want emails in the test-environment
			//			user.setEmail(randomNameAndEmail.getEmail());
			user.setName(randomNameAndEmail.getName());
			user.setUuid(UUID.randomUUID().toString());
			user.setPositions(new ArrayList<>());
			user.getPositions().add(orgUnits[random.nextInt(ouCount)]);
			user.getPositions().add(orgUnits[random.nextInt(ouCount)]);
			users.add(user);
		}
		
		orgUnitDao.save(Arrays.asList(orgUnits));
		userDao.save(users);
	}

	private NameAndEmail getRandomNameAndEmail() {
		NameAndEmail nae = new NameAndEmail();

		String name = names.get(random.nextInt(names.size()));
		String surname = surnames.get(random.nextInt(surnames.size()));

		nae.setName(firstCharToUpper(name) + " " + firstCharToUpper(surname));
		nae.setEmail(name + surname + RandomStringUtils.randomAlphanumeric(2) + "@email.com");

		return nae;
	}

	private String firstCharToUpper(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	public static String[] generateRandomWords(int numberOfWords) {
		String[] randomStrings = new String[numberOfWords];
		Random random = new Random();

		for (int i = 0; i < numberOfWords; i++) {
			char[] word = new char[random.nextInt(8) + 3];

			for (int j = 0; j < word.length; j++) {
				word[j] = (char) ('a' + random.nextInt(26));
			}

			randomStrings[i] = new String(word);
		}

		return randomStrings;
	}

	private void fakeLogin(String cvr, User user) {
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(SecurityUtil.ROLE_ADMINISTRATOR));

		TokenUser token = TokenUser.builder().authorities(authorities).cvr(cvr).username("dummy").attributes(new HashMap<>()).build();
		token.getAttributes().put("user", new AuthenticatedUser(user));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("dummy", null, authorities);
		auth.setDetails(token);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// inner class
	@Getter
	@Setter
	private class NameAndEmail {
		private String name;
		private String email;
	}
}
