package dk.digitalidentity.ap.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dk.digitalidentity.ap.dao.FormDao;
import dk.digitalidentity.ap.dao.model.Form;
import dk.formonline.formoperationer.FinanslovIndgangType;
import dk.formonline.formoperationer.FormHierarkiHentOutputType;
import dk.formonline.formoperationer.HovedOmraadeType;
import dk.formonline.formoperationer.OpgaveOmraadeType;
import dk.formonline.formoperationer.OpgaveType;
import dk.formonline.formoperationer.ServiceOmraadeType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FormService {

	@Autowired
	private FormDao formDao;
	
	@Autowired
	private KleToFormService kleToFormService;

	@Autowired
	private FormService formService;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ProcessService processService;

	public Form save(Form form) {
		return formDao.save(form);
	}

	public Form getByCode(String code) {
		return formDao.getByCode(code);
	}

	public List<Form> findAll() {
		return formDao.findAll();
	}

	public long count() {
		return formDao.count();
	}
	
	@Transactional
	public void updateFormData() {
		List<Form> updatedFormList = getUpdatedFormList();
		List<Form> formList = formService.findAll();
		List<Form> formsWithLegalClauseChanges = new ArrayList<>();

		for (Form updatedForm : updatedFormList) {
			boolean found = false;
			boolean change = false;

			for (Iterator<Form> iterator = formList.iterator(); iterator.hasNext();) {
				Form form = iterator.next();

				if (form.getCode().equals(updatedForm.getCode())) {
					found = true;

					if (!form.getDescription().equals(updatedForm.getDescription())) {
						change = true;
					}
					
					if (!Objects.equals(form.getLegalClause(), updatedForm.getLegalClause())) {
						change = true;
						formsWithLegalClauseChanges.add(form);
					}

					iterator.remove();
					break;
				}
			}

			if (found && change) {
				Form form = formService.getByCode(updatedForm.getCode());
				form.setDescription(updatedForm.getDescription());
				form.setLegalClause(updatedForm.getLegalClause());

				formService.save(form);
			}
			else if (!found) {
				formService.save(updatedForm);
			}
		}
		
		if (formsWithLegalClauseChanges.size() > 0) {
			log.info("Found " + formsWithLegalClauseChanges.size() + " FORM records with changes to legal clauses");

			processService.updateLegalClauses(formsWithLegalClauseChanges);
		}
		
		kleToFormService.setFormLoaded();
	}
	
	private List<Form> getUpdatedFormList() {
		List<Form> updatedFormList = new ArrayList<Form>();

		HttpEntity<FormHierarkiHentOutputType> responseMainEntity = restTemplate.getForEntity("http://api.form-online.dk/resources/", FormHierarkiHentOutputType.class);

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
			form.setLegalClause("");
			updatedFormList.add(form);
			
			for (HovedOmraadeType hovedOmraadeType : serviceOmraade.getHovedOmraade()) {
				Form form2 = new Form();
				String desc2 = hovedOmraadeType.getNavnTekst();
				form2.setDescription(desc2.substring(0, Math.min(desc2.length(), 255)));
				form2.setCode(hovedOmraadeType.getHovedOmraadeNummerIdentifikator());
				
				buildLegalClause(form2, hovedOmraadeType.getFinanslovIndgang());
				
				updatedFormList.add(form2);
				
				for (OpgaveOmraadeType opgaveOmraadeType : hovedOmraadeType.getOpgaveOmraade()) {
					Form form3 = new Form();
					String desc3 = opgaveOmraadeType.getNavnTekst();
					form3.setDescription(desc3.substring(0, Math.min(desc3.length(), 255)));
					form3.setCode(opgaveOmraadeType.getOpgaveOmraadeNummerIdentifikator());					
					updatedFormList.add(form3);

					buildLegalClause(form3, opgaveOmraadeType.getFinanslovIndgang());
					
					for (OpgaveType opgaveType : opgaveOmraadeType.getOpgave()) {
						Form form4 = new Form();
						String desc4 = opgaveType.getNavnTekst();
						form4.setDescription(desc4.substring(0, Math.min(desc4.length(), 255)));
						form4.setCode(opgaveType.getOpgaveNummerIdentifikator());
						
						buildLegalClause(form4, opgaveType.getFinanslovIndgang());

						updatedFormList.add(form4);
					}
				}
			}

		}
		
		log.info("Done processing forms");

		return updatedFormList;
	}

	private void buildLegalClause(Form form, List<FinanslovIndgangType> finansLovIndgangTypes) {
		StringBuilder builder = new StringBuilder();
		for (FinanslovIndgangType finansLovIndgangType : finansLovIndgangTypes) {
			String legalClause = finansLovIndgangType.getFinanslovParagrafTekst() + " (" + finansLovIndgangType.getFinanslovReference() + ")";
			
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(legalClause);
		}

		String legalClause = builder.toString();
		if (legalClause.length() > 3000) {
			legalClause = legalClause.substring(0, 3000);
		}

		form.setLegalClause(legalClause);
	}
}
