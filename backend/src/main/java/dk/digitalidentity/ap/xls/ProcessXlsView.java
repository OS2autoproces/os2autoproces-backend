
package dk.digitalidentity.ap.xls;

import dk.digitalidentity.ap.dao.model.Attachment;
import dk.digitalidentity.ap.dao.model.ItSystem;
import dk.digitalidentity.ap.dao.model.Link;
import dk.digitalidentity.ap.dao.model.OrgUnit;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.dao.model.Service;
import dk.digitalidentity.ap.dao.model.Technology;
import dk.digitalidentity.ap.dao.model.User;
import dk.digitalidentity.ap.dao.model.enums.Domain;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProcessXlsView extends AbstractXlsxView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

		@SuppressWarnings("unchecked")
		List<Process> processes = (List<Process>) model.get("processes");
		@SuppressWarnings("unchecked")
		Map<Long, List<Attachment>> attachmentMap = (Map<Long, List<Attachment>>) model.get("attachmentMap");
		StringBuilder builder;

		// create excel xls sheet
		Sheet sheet = workbook.createSheet("Processer");

		// styles
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFont(headerFont);

		CellStyle normalStyle = workbook.createCellStyle();

		// number style with thousand separator
		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));

		// date style
		CellStyle dateStyle = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

		CellStyle wrapStyle = workbook.createCellStyle();
		wrapStyle.setWrapText(true);

		// create header row
		Row header = sheet.createRow(0);
		createCell(header, 0, "ID", headerStyle);
		createCell(header, 1, "Titel", headerStyle);
		createCell(header, 2, "Resumé", headerStyle);
		createCell(header, 3, "Fase", headerStyle);
		createCell(header, 4, "Status", headerStyle);
		createCell(header, 5, "Statustekst", headerStyle);
		createCell(header, 6, "Indberetter", headerStyle);
		createCell(header, 7, "Faglig kontaktperson", headerStyle);
		createCell(header, 8, "Kontaktperson", headerStyle);
		createCell(header, 9, "Mail", headerStyle);
		createCell(header, 10, "Anden kontakt information", headerStyle);
		createCell(header, 11, "Tilknyttede personer", headerStyle);
		createCell(header, 12, "Synlighed", headerStyle);
		createCell(header, 13, "Fagområder", headerStyle);
		createCell(header, 14, "Afdelinger", headerStyle);
		createCell(header, 15, "Myndighed", headerStyle);
		createCell(header, 16, "Leverandør", headerStyle);
		createCell(header, 17, "Lovparagraf", headerStyle);
		createCell(header, 18, "KLE-nr", headerStyle);
		createCell(header, 19, "KL ID", headerStyle);
		createCell(header, 20, "KL's Arbejdsgangsbank", headerStyle);
		createCell(header, 21, "Beskrivelse", headerStyle);
		createCell(header, 22, "Idéer til løsning", headerStyle);
		createCell(header, 23, "Udfordringer i den nuværende proces", headerStyle);
		createCell(header, 24, "Nuværende systemer", headerStyle);
		createCell(header, 25, "Andre nuværende systemer", headerStyle);
		createCell(header, 26, "Oprettet", headerStyle);
		createCell(header, 27, "Sidst opdateret", headerStyle);
		createCell(header, 28, "Antal gange processen foretages årligt", headerStyle);
		createCell(header, 29, "Tidsforbrug pr. proces i minutter", headerStyle);
		createCell(header, 30, "Forventet automatiseringsgrad", headerStyle);
		createCell(header, 31, "Manuelt tidsforbrug i timer", headerStyle);
		createCell(header, 32, "Forventet årlig effektiviseringspotentiale", headerStyle);
		createCell(header, 33, "Antal medarbejdere der foretager processen", headerStyle);
		createCell(header, 34, "Er borgere påvirket", headerStyle);
		createCell(header, 35, "Er virksomheder påvirket", headerStyle);
		createCell(header, 36, "Forventet timeforbrug på udvikling", headerStyle);
		createCell(header, 37, "Kommentarer til tidsforbrug", headerStyle);
		createCell(header, 38, "I hvor høj grad indgår der faglig vurdering i processen?", headerStyle);
		createCell(header, 39, "I hvor høj grad er processen præget af hyppige ændringer?", headerStyle);
		createCell(header, 40, "I hvor høj grad er processen baseret på struktureret information?", headerStyle);
		createCell(header, 41, "Er der variation i den måde processen løses?", headerStyle);
		createCell(header, 42, "Er de data og informationer, der skal bruges i processen, tilgængelige digitalt i IT-systemer?", headerStyle);
		createCell(header, 43, "Vil en automatiseret løsning bidrage til en højere kvalitet, som er mere ensrettet og med færre fejl?", headerStyle);
		createCell(header, 44, "Vil en automatiseret løsning bidrage til en hurtigere og mere fyldestgørende service?", headerStyle);
		createCell(header, 45, "Vil automatisering frigive tid og nedbringe rutineopgaver, som skaber en bedre trivsel blandt medarbejderne?", headerStyle);
		createCell(header, 46, "I hvor høj grad vurderes det at processen kan automatiseres? ", headerStyle);
		createCell(header, 47, "Teknisk implementering", headerStyle);
		createCell(header, 48, "Organisatorisk implementering", headerStyle);
		createCell(header, 49, "Anvendt teknologi", headerStyle);
		createCell(header, 50, "Skedulering", headerStyle);
		createCell(header, 51, "Automatiseringen anvender følgende systemer/snitflader", headerStyle);
		createCell(header, 52, "Beskrivelse af automatisering", headerStyle);
		createCell(header, 53, "I hvor høj grad indfriede løsningen de forventede gevinster?", headerStyle);
		createCell(header, 54, "Sidst kontrolleret i forhold til §", headerStyle);
		createCell(header, 55, "Løsningen sat i drift", headerStyle);
		createCell(header, 56, "Løsningen taget ud af drift", headerStyle);
		createCell(header, 57, "Kommentar til realiseret gevinster", headerStyle);
		createCell(header, 58, "Sagsreference i ESDH", headerStyle);
		createCell(header, 59, "Links", headerStyle);
		createCell(header, 60, "Bilag", headerStyle);
		createCell(header, 61, "Interne Noter", headerStyle);

		// Create data cells
		int rowCount = 1;
		for (Process process : processes) {
			Row row = sheet.createRow(rowCount++);
			createCell(row, 0, process.getId(), normalStyle);
			createCell(row, 1, process.getTitle(), normalStyle);
			createCell(row, 2, process.getShortDescription(), normalStyle);
			createCell(row, 3, process.getPhase().getValue(), wrapStyle);
			createCell(row, 4, process.getStatus().getValue(), normalStyle);
			createCell(row, 5, process.getStatusText(), normalStyle);
			createCell(row, 6, (process.getReporter() != null) ? process.getReporter().getName() : null, normalStyle);
			createCell(row, 7, (process.getOwner() != null) ? process.getOwner().getName() : null, normalStyle);
			createCell(row, 8, (process.getContact() != null) ? process.getContact().getName() : null, normalStyle);
			createCell(row, 9, (process.getContact() != null) ? process.getContact().getEmail() : null, normalStyle);
			createCell(row, 10, process.getOtherContactEmail(), normalStyle);

			builder = new StringBuilder();
			for (User user : process.getUsers()) {
				if (builder.length() > 0) {
					builder.append("\n");
				}

				builder.append(user.getName());
			}
			createCell(row, 11, builder.toString(), normalStyle);

			createCell(row, 12, process.getVisibility().getValue(), normalStyle);

			builder = new StringBuilder();
			for (Domain domain : process.getDomains()) {
				if (builder.length() > 0) {
					builder.append(",");
				}

				builder.append(domain.getValue());
			}
			createCell(row, 13, builder.toString(), normalStyle);

			builder = new StringBuilder();
			for (OrgUnit ou : process.getOrgUnits()) {
				if (builder.length() > 0) {
					builder.append(",");
				}

				builder.append(ou.getName());
			}
			createCell(row, 14, builder.toString(), normalStyle);

			createCell(row, 15, process.getMunicipalityName(), normalStyle);
			createCell(row, 16, process.getVendor(), normalStyle);
			createCell(row, 17, process.getLegalClause(), normalStyle);
			createCell(row, 18, process.getKle(), normalStyle);
			createCell(row, 19, process.getKlId(), normalStyle);
			createCell(row, 20, process.getKla(), normalStyle);
			createCell(row, 21, stripHTML(process.getLongDescription()), normalStyle);
			createCell(row, 22, stripHTML(process.getSolutionRequests()), normalStyle);
			createCell(row, 23, stripHTML(process.getProcessChallenges()), normalStyle);

			builder = new StringBuilder();
			for (ItSystem itSystem : process.getItSystems()) {
				if (builder.length() > 0) {
					builder.append(",");
				}

				builder.append(itSystem.getName());
			}
			createCell(row, 24, builder.toString(), normalStyle);

			builder = new StringBuilder();
			if (StringUtils.hasLength(process.getItSystemsDescription())) {
				if (builder.length() > 0) {
					builder.append("\n");
				}

				builder.append(process.getItSystemsDescription());
			}
			createCell(row, 25, builder.toString(), normalStyle);

			createCell(row, 26, process.getCreated(), dateStyle);
			createCell(row, 27, process.getLastChanged(), dateStyle);
			createCell(row, 28, process.getTimeSpendOccurancesPerEmployee(), numberStyle);
			createCell(row, 29, process.getTimeSpendPerOccurance(), numberStyle);
			createCell(row, 30, process.getTimeSpendPercentageDigital(), numberStyle);
			createCell(row, 31, process.getTimeSpendPerOccurance() * process.getTimeSpendOccurancesPerEmployee() / 60, numberStyle);
			createCell(row, 32, process.getTimeSpendComputedTotal(), numberStyle);
			createCell(row, 33, process.getTimeSpendEmployeesDoingProcess(), numberStyle);
			createCell(row, 34, process.isTargetsCitizens() ? "Ja" : "Nej", normalStyle);
			createCell(row, 35, process.isTargetsCompanies() ? "Ja" : "Nej", normalStyle);
			createCell(row, 36, process.getExpectedDevelopmentTime() != null ? process.getExpectedDevelopmentTime() : 0, numberStyle);
			createCell(row, 37, process.getTimeSpendComment(), normalStyle);
			createCell(row, 38, process.getLevelOfProfessionalAssessment().getValue(), normalStyle);
			createCell(row, 39, process.getLevelOfChange().getValue(), normalStyle);
			createCell(row, 40, process.getLevelOfStructuredInformation().getValue(), normalStyle);
			createCell(row, 41, process.getLevelOfUniformity().getValue(), normalStyle);
			createCell(row, 42, process.getLevelOfDigitalInformation().getValue(), normalStyle);
			createCell(row, 43, process.getLevelOfQuality().getValue(), normalStyle);
			createCell(row, 44, process.getLevelOfSpeed().getValue(), normalStyle);
			createCell(row, 45, process.getEvaluatedLevelOfRoi().getValue(), normalStyle);
			createCell(row, 46, process.getLevelOfRoutineWorkReduction().getValue(), normalStyle);
			createCell(row, 47, stripHTML(process.getTechnicalImplementationNotes()), normalStyle);
			createCell(row, 48, stripHTML(process.getOrganizationalImplementationNotes()), normalStyle);

			builder = new StringBuilder();
			for (Technology tech : process.getTechnologies()) {
				if (builder.length() > 0) {
					builder.append(",");
				}

				builder.append(tech.getName());
			}
			createCell(row, 49, builder.toString(), normalStyle);

			createCell(row, 50, process.getRunPeriod().getValue(), normalStyle);

			builder = new StringBuilder();
			for (Service service : process.getServices()) {
				if (builder.length() > 0) {
					builder.append(",");
				}

				builder.append(service.getName());
			}
			createCell(row, 51, builder.toString(), normalStyle);

			createCell(row, 52, stripHTML(process.getAutomationDescription()), normalStyle);

			createCell(row, 53, process.getRating(), numberStyle);

			if (process.getLegalClauseLastVerified() == null) {
				createCell(row, 54, "", normalStyle);
			} else {
				createCell(row, 54, process.getLegalClauseLastVerified(), dateStyle);
			}

			if (process.getPutIntoOperation() == null) {
				createCell(row, 55, "", normalStyle);
			} else {
				createCell(row, 55, process.getPutIntoOperation(), dateStyle);
			}

			if (process.getDecommissioned() == null) {
				createCell(row, 56, "", normalStyle);
			} else {
				createCell(row, 56, process.getDecommissioned(), dateStyle);
			}

			createCell(row, 57, stripHTML(process.getRatingComment()), normalStyle);
			createCell(row, 58, process.getEsdhReference(), normalStyle);

			builder = new StringBuilder();
			builder.append("https://www.os2autoproces.eu/details/" + process.getId());
			if (process.getCodeRepositoryUrl() != null && process.getCodeRepositoryUrl().length() > 0) {
				builder.append("\n" + process.getCodeRepositoryUrl());
			}
			for (Link link : process.getLinks()) {
				builder.append("\n" + link.getUrl());
			}
			createCell(row, 59, builder.toString(), normalStyle);

			if (attachmentMap.containsKey(process.getId())) {
				List<Attachment> attachments = attachmentMap.get(process.getId());

				builder = new StringBuilder();
				for (Attachment attachment : attachments) {
					if (builder.length() > 0) {
						builder.append("\n");
					}

					builder.append(attachment.getFileName());
				}

				createCell(row, 60, builder.toString(), normalStyle);
			}
			else {
				createCell(row, 60, "", normalStyle);
			}

			createCell(row, 61, stripHTML(process.getInternalNotes()), normalStyle);
		}

		format(sheet);
	}

	private static void createCell(Row header, int column, String value, CellStyle style) {
		Cell cell = header.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private static void createCell(Row header, int column, double value, CellStyle style) {
		Cell cell = header.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private static void createCell(Row header, int column, int value, CellStyle style) {
		Cell cell = header.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void createCell(Row header, int column, Date value, CellStyle style) {
		Cell cell = header.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private static String stripHTML(String html) {
		if (html == null) {
			return "";
		}

		return html.replaceAll("\\<li\\>", "* ")
				.replaceAll("\\<.*?\\>", "\n")
				.trim();
	}

	private void format(Sheet sheet) {
		sheet.autoSizeColumn(26);
		sheet.autoSizeColumn(27);
		sheet.autoSizeColumn(53);
		sheet.autoSizeColumn(54);
		sheet.autoSizeColumn(55);
	}
}
