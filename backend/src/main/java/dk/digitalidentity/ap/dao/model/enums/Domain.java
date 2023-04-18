package dk.digitalidentity.ap.dao.model.enums;

public enum Domain {
	ADMINISTRATION("Administration"),
	EMPLOYMENT("Beskæftigelse"),
	PROFESSION("Erhverv"),
	FAMILY("Familie"),
	EDUCATION("Uddannelse"),
	DEMOCRACY("Demokrati"),
	ENVIRONMENT("Miljø"),
	TECHNIQUE("Teknik"),
	SOCIAL("Social"),
	HEALTH("Sundhed"),
	SUSTAINABILITY("Bæredygtighed"),
	HR("HR"),
	IT("IT"),
	CULTURE("Kultur"),
	ECONOMY("Økonomi");
	
	private String value;
	
	private Domain(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
