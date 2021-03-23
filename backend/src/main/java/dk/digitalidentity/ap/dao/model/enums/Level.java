package dk.digitalidentity.ap.dao.model.enums;

public enum Level {
	VERY_HIGH("I høj grad"),
	HIGH("I nogen grad"),
	LOW("I mindre grad"),
	VERY_LOW("Slet ikke"),
	UNKNOWN("Ved ikke"),
	NOT_SET("Ikke forholdt sig");
	
	private String value;
	
	private Level(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
