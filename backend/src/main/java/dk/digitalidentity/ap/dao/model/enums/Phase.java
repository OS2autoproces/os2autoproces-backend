package dk.digitalidentity.ap.dao.model.enums;

public enum Phase {
	IDEA("Idé"), PREANALYSIS("Fornalayse"), SPECIFICATION("Specifikation"), DEVELOPMENT("Udvikling"), IMPLEMENTATION("Implementering"), OPERATION("Drift"), DECOMMISSIONED("Taget ud af drift");
	
	private String value;
	
	private Phase(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
