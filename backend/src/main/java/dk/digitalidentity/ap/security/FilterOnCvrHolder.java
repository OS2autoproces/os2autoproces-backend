package dk.digitalidentity.ap.security;

public class FilterOnCvrHolder {
	private static ThreadLocal<Boolean> filterOnCvrHolder = new ThreadLocal<>();

	public static Boolean get() {
		return filterOnCvrHolder.get();
	}

	public static void set(Boolean filter) {
		filterOnCvrHolder.set(filter);
	}

	public static void clear() {
		filterOnCvrHolder.remove();
	}
}
