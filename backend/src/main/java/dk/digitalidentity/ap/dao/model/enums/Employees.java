package dk.digitalidentity.ap.dao.model.enums;

import lombok.Getter;

@Getter
public enum Employees {
	BELOW_1500("Under 1.500 medarbejdere"),
	BELOW_3000("1.500 – 2.999 medarbejdere"),
	BELOW_5000("3.000 – 4.999 medarbejdere"),
	BELOW_10000("5.000 – 9.999 medarbejdere"),
	ABOVE_10000("Over 10.000 medarbejdere");


	private String value;
	private Employees(String value) {
		this.value = value;
	}
}
