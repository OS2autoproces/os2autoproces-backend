package dk.digitalidentity.ap.security;

public class FilterMyOrganisationHolder {
	private static ThreadLocal<Boolean> filterMyOrganisation = new ThreadLocal<>();

	public static Boolean get() {
		return filterMyOrganisation.get();
	}

	public static void set(Boolean filter) {
		filterMyOrganisation.set(filter);
	}

	public static void clear() {
		filterMyOrganisation.remove();
	}
}
