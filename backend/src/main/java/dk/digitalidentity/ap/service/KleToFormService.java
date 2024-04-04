package dk.digitalidentity.ap.service;

import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.ap.config.ApplicationContextProvider;
import dk.digitalidentity.ap.dao.FormDao;
import dk.digitalidentity.ap.dao.KleDao;
import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.dao.model.Kle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KleToFormService {
	private static final String WELL_KNOWN_KLE = "01.02.27";
	private boolean kleLoaded = false;
	private boolean formLoaded = false;
	
	@Autowired
	private KleDao kleDao;
	
	@Autowired
	private FormDao formDao;
	
	@Transactional(rollbackFor = Exception.class)
	public void setKleLoaded() {
		kleLoaded = true;
		map();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void setFormLoaded() {
		formLoaded = true;
		map();
	}
	
	// this is a very hackish bit of code, but as it is only intended to run ONCE
	// for any given installation, we can live with it. It will never be used at runtime.
	private synchronized void map() {
		if (kleLoaded && formLoaded) {			
			Kle wellKnownKle = kleDao.getByCode(WELL_KNOWN_KLE);
			if (wellKnownKle == null) {
				log.error("Could not find KLE " + WELL_KNOWN_KLE + " - initialization code will not work");
				return;
			}
			
			// we KNOW that this specific KLE is getting mapped to at least one FORM,
			// so if it already has one, we can skip initialization ;)
			if (wellKnownKle.getForms() != null && wellKnownKle.getForms().size() > 0) {
				log.info("KLE already mapped to FORM");
				return;
			}
			
			log.info("Mapping KLE to FORM");

			try {
				Resource resource = ApplicationContextProvider.getApplicationContext().getResource("classpath:KLE_FORM_2018-07-11.csv");
				String content = IOUtils.toString(resource.getInputStream());
				
				String[] lines = content.split("\n");
				for (String line : lines) {
					String[] tokens = line.split(";");
					if (tokens.length == 2) {
						String kleCode = tokens[0];
						String[] formCodes = tokens[1].split(",");
						
						if (formCodes.length == 0 || formCodes[0].length() == 0) {
							continue;
						}

						Kle kle = kleDao.getByCode(kleCode);
						if (kle == null) {
							log.warn("Could not map FORM to KLE " + kleCode);
							continue;
						}

						kle.setForms(new ArrayList<>());

						for (String formCode : formCodes) {
							Form form = formDao.getByCode(formCode);
							if (form == null) {
								log.warn("Could not find FORM " + formCode + " intended for KLE " + kleCode);
								continue;
							}
							
							kle.getForms().add(form);
						}
						
						kleDao.save(kle);
					}
				}
			}
			catch (Exception ex) {
				log.error("Failed to map KLE to FORM", ex);
			}
		}
	}
}
