package dk.digitalidentity.ap.dao.model.enums;

public enum Status {
	REJECTED("Afvist"), FAILED("Mislykket"), PENDING("Afventer"), INPROGRESS("Igang"), NOT_RATED("Ikke vurderet"), NOT_RELEVANT("Ikke relevant mere");
	
	private String value;
	
	private Status(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
