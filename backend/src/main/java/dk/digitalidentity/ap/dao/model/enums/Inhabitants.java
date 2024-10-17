package dk.digitalidentity.ap.dao.model.enums;

import lombok.Getter;

@Getter
public enum Inhabitants {
	BELOW_30000("Under 30.000 indbyggere"),
	BELOW_45000("30.000 - 44.999 indbyggere"),
	BELOW_60000("45.000 - 59.999 indbyggere"),
	BELOW_100000("60.000 – 99.999 indbyggere"),
	ABOVE_100000("Over 100.000 indbyggere");

	private String value;

	private Inhabitants(String value) {
		this.value = value;
	}
}
