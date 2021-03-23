package dk.digitalidentity.ap.dao.model.enums;

public enum Domain {
	ADMINISTRATION("Administration og organisation"),
	WORK("Arbejdsmarked og erhverv"),
	CHILDREN("Børn og læring"),
	DEMOCRACY("Demokrati og involvering"),
	ENVIRONMENT("Miljø, teknik og forsyning"),
	HEALTH("Social og sundhed");
	
	private String value;
	
	private Domain(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
