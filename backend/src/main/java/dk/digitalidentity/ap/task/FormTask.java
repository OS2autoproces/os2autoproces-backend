package dk.digitalidentity.ap.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import dk.digitalidentity.ap.dao.model.Form;
import dk.digitalidentity.ap.service.FormService;
import dk.digitalidentity.ap.service.KleToFormService;
import dk.formonline.formoperationer.FormHierarkiHentOutputType;
import dk.formonline.formoperationer.HovedOmraadeType;
import dk.formonline.formoperationer.OpgaveOmraadeType;
import dk.formonline.formoperationer.OpgaveType;
import dk.formonline.formoperationer.ServiceOmraadeType;
import lombok.extern.log4j.Log4j;

@Component
@EnableScheduling
@EnableAsync
@Log4j
public class FormTask {

	@Value("${scheduled.enabled:false}")
	private boolean runScheduled;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private FormService formService;
	
	@Autowired
	private KleToFormService kleToFormService;

	@Async
	public void init() {
		if (runScheduled && formService.count() == 0) {
			parse();
		}
	}

	// Run every Saturday at 21:00
	@Scheduled(cron = "0 0 21 * * SAT")
	@Transactional(rollbackFor = Exception.class)
	public synchronized void parse() {
		if (!runScheduled) {
			return;
		}

		log.info("Running Scheduled Task: " + this.getClass().getName());

		List<Form> updatedFormList = null;
		try {
			
			updatedFormList = getUpdatedFormList();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		List<Form> formList = formService.findAll();

		for (Form updatedForm : updatedFormList) {
			boolean found = false;
			boolean nameChange = false;

			for (Iterator<Form> iterator = formList.iterator(); iterator.hasNext();) {
				Form form = iterator.next();

				if (form.getCode().equals(updatedForm.getCode())) {
					found = true;

					if (!form.getDescription().equals(updatedForm.getDescription())) {
						nameChange = true;
					}

					iterator.remove();
					break;
				}
			}

			if (found && nameChange) {
				Form form = formService.getByCode(updatedForm.getCode());
				form.setDescription(updatedForm.getDescription());

				formService.save(form);
			}
			else if (!found) {
				formService.save(updatedForm);
			}
		}
		
		kleToFormService.setFormLoaded();
	}

	public List<Form> getUpdatedFormList() {
		List<Form> updatedFormList = new ArrayList<Form>();

		HttpEntity<FormHierarkiHentOutputType> responseMainEntity = restTemplate.getForEntity("http://www.form-online.dk:8080/FORM-REST/resources/", FormHierarkiHentOutputType.class);

		FormHierarkiHentOutputType formHierarchyOutput = responseMainEntity.getBody();
		
		log.info("Done fetching from form-online");

		// ServiceOmraade				- XX - 			ServiceOmraadeNummerIdentifikator
		//   HovedOmraade				- XX.XX - 		HovedOmraadeNummerIdentifikator
		//     OpgaveOmraade			- XX.XX.XX - 	OpgaveOmraadeNummerIdentifikator
		//       Opgave				    - XX.XX.XX.XX - OpgaveNummerIdentifikator

		for (ServiceOmraadeType serviceOmraade : formHierarchyOutput.getServiceOmraade()) {
			Form form = new Form();
			String desc = serviceOmraade.getNavnTekst();
			form.setDescription(desc.substring(0, Math.min(desc.length(), 255)));
			form.setCode(serviceOmraade.getServiceOmraadeNummerIdentifikator());
			
			updatedFormList.add(form);

			for (HovedOmraadeType hovedOmraadeType : serviceOmraade.getHovedOmraade()) {
				Form form2 = new Form();
				String desc2 = hovedOmraadeType.getNavnTekst();
				form2.setDescription(desc2.substring(0, Math.min(desc2.length(), 255)));
				form2.setCode(hovedOmraadeType.getHovedOmraadeNummerIdentifikator());
				
				updatedFormList.add(form2);
				
				for (OpgaveOmraadeType opgaveOmraadeType : hovedOmraadeType.getOpgaveOmraade()) {
					Form form3 = new Form();
					String desc3 = opgaveOmraadeType.getNavnTekst();
					form3.setDescription(desc3.substring(0, Math.min(desc3.length(), 255)));
					form3.setCode(opgaveOmraadeType.getOpgaveOmraadeNummerIdentifikator());					
					updatedFormList.add(form3);
					
					for (OpgaveType opgaveType : opgaveOmraadeType.getOpgave()) {
						Form form4 = new Form();
						String desc4 = opgaveType.getNavnTekst();
						form4.setDescription(desc4.substring(0, Math.min(desc4.length(), 255)));
						form4.setCode(opgaveType.getOpgaveNummerIdentifikator());
						
						updatedFormList.add(form4);
					}
				}
			}

		}
		
		log.info("Done processing forms");

		return updatedFormList;
	}
}
