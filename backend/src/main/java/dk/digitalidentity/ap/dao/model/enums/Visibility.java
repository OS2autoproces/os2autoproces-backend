package dk.digitalidentity.ap.dao.model.enums;

public enum Visibility {
	PERSONAL("Personlig"), MUNICIPALITY("Myndighed"), PUBLIC("Offentlig");
	
	private String value;
	
	private Visibility(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
