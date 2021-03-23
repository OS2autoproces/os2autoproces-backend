import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormKleMapper {
	private static Map<String, List<String>> kleToForm = new HashMap<>();

	public static void main(String[] args) {
		try {
			byte[] content = Files.readAllBytes(Paths.get("./FORM_KLE_2018-07-11.csv"));
			String contentStr = new String(content, Charset.forName("UTF-8"));

			String[] lines = contentStr.split("\n");
			for (String line : lines) {
				int idx = line.indexOf(";");

				if (idx > 0) {
					String form = line.substring(0, idx);
					String residue = line.substring(idx + 1);
					residue = residue.replace("\"", "");
					String[] kles = residue.split(";");

					for (String kle : kles) {
						kle = kle.trim();

						if (kle.length() == 0) {
							continue;
						}

						if (Character.isDigit(kle.charAt(0))) {
							if (kleToForm.containsKey(kle)) {
								List<String> forms = kleToForm.get(kle);
								forms.add(form);
							}
							else {
								List<String> forms = new ArrayList<>();
								forms.add(form);
								kleToForm.put(kle, forms);
							}
						}
					}
				}
			}

			FileOutputStream fos = new FileOutputStream("./KLE_FORM_2018-07-11.csv");
			for (String kle : kleToForm.keySet()) {
				fos.write(kle.getBytes(Charset.forName("UTF-8")));
				fos.write(";".getBytes(Charset.forName("UTF-8")));

				boolean first = true;
				for (String form : kleToForm.get(kle)) {
					if (!first) {
						fos.write(",".getBytes(Charset.forName("UTF-8")));
					}

					fos.write(form.getBytes(Charset.forName("UTF-8")));
					first = false;
				}

				fos.write("\n".getBytes(Charset.forName("UTF-8")));
			}
			fos.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

