package dk.digitalidentity.ap.dao.model.enums;

public enum RunPeriod {
	ONDEMAND("Efter behov"),
	ONCE("Enkeltkørsel"),
	DAILY("Dagligt"),
	WEEKLY("Ugentligt"),
	MONTHLY("Månedligt"),
	QUATERLY("Kvartalsvis"),
	YEARLY("Årligt");
	
	private String value;
	
	private RunPeriod(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
