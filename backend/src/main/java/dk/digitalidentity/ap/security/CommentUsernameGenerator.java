package dk.digitalidentity.ap.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.AuditorAware;

import dk.digitalidentity.ap.dao.model.User;

public class CommentUsernameGenerator implements AuditorAware<String> {
	private static Map<String, String> municipalityNames;
		
	@Override
	public String getCurrentAuditor() {
		String name = null;
		String municipalityName = null;
		
		User user = SecurityUtil.getUser();
		if (user != null) {
			name = user.getName();

			if (municipalityNames.containsKey(user.getCvr())) {
				municipalityName = municipalityNames.get(user.getCvr());
			}
			else {
				municipalityName = user.getCvr();
			}
		}

		return name + ", " + municipalityName;
	}
	
	static {
		municipalityNames = new HashMap<>();

		municipalityNames.put("66137112", "Albertslund kommune");
		municipalityNames.put("60183112", "Allerød kommune");
		municipalityNames.put("29189692", "Assens kommune");
		municipalityNames.put("58271713", "Ballerup kommune");
		municipalityNames.put("29189765", "Billund kommune");
		municipalityNames.put("26696348", "Bornholms Regionskommune");
		municipalityNames.put("65113015", "Brøndby kommune");
		municipalityNames.put("29189501", "Brønderslev kommune");
		municipalityNames.put("12881517", "Dragør kommune");
		municipalityNames.put("29188386", "Egedal kommune");
		municipalityNames.put("29189803", "Esbjerg kommune");
		municipalityNames.put("31210917", "Fanø kommune");
		municipalityNames.put("29189714", "Favrskov kommune");
		municipalityNames.put("29188475", "Faxe kommune");
		municipalityNames.put("29188335", "Fredensborg kommune");
		municipalityNames.put("69116418", "Fredericia kommune");
		municipalityNames.put("11259979", "Frederiksberg kommune");
		municipalityNames.put("29189498", "Frederikshavn kommune");
		municipalityNames.put("29189129", "Frederikssund kommune");
		municipalityNames.put("29188327", "Furesø kommune");
		municipalityNames.put("29188645", "Faaborg-Midtfyn kommune");
		municipalityNames.put("19438414", "Gentofte kommune");
		municipalityNames.put("62761113", "Gladsaxe kommune");
		municipalityNames.put("65120119", "Glostrup kommune");
		municipalityNames.put("44023911", "Greve kommune");
		municipalityNames.put("29188440", "Gribskov kommune");
		municipalityNames.put("29188599", "Guldborgsund Kommune");
		municipalityNames.put("29189757", "Haderslev kommune");
		municipalityNames.put("29188416", "Halsnæs kommune");
		municipalityNames.put("29189587", "Hedensted Kommune");
		municipalityNames.put("64502018", "Helsingør kommune");
		municipalityNames.put("63640719", "Herlev kommune");
		municipalityNames.put("29189919", "Herning kommune");
		municipalityNames.put("29189366", "Hillerød kommune");
		municipalityNames.put("29189382", "Hjørring kommune");
		municipalityNames.put("29189447", "Holbæk kommune");
		municipalityNames.put("29189927", "Holstebro kommune");
		municipalityNames.put("29189889", "Horsens kommune");
		municipalityNames.put("55606617", "Hvidovre kommune");
		municipalityNames.put("19501817", "Høje-taastrup kommune");
		municipalityNames.put("70960516", "Hørsholm kommune");
		municipalityNames.put("29189617", "Ikast-Brande kommune");
		municipalityNames.put("11931316", "Ishøj kommune");
		municipalityNames.put("29189439", "Jammerbugt kommune");
		municipalityNames.put("29189595", "Kalundborg kommune");
		municipalityNames.put("29189706", "Kerteminde kommune");
		municipalityNames.put("29189897", "Kolding kommune");
		municipalityNames.put("64942212", "Københavns kommune");
		municipalityNames.put("29189374", "Køge kommune");
		municipalityNames.put("29188955", "Langeland kommune");
		municipalityNames.put("29188548", "Lejre kommune");
		municipalityNames.put("29189935", "Lemvig kommune");
		municipalityNames.put("29188572", "Lolland kommune");
		municipalityNames.put("11715311", "Lyngby-taarbæk kommune");
		municipalityNames.put("45973328", "Læsø kommune");
		municipalityNames.put("29189455", "Mariagerfjord kommune");
		municipalityNames.put("29189684", "Middelfart kommune");
		municipalityNames.put("41333014", "Morsø kommune");
		municipalityNames.put("29189986", "Norddjurs kommune");
		municipalityNames.put("29188947", "Nordfyns Kommune");
		municipalityNames.put("29189722", "Nyborg kommune");
		municipalityNames.put("29189625", "Næstved kommune");
		municipalityNames.put("32264328", "Odder kommune");
		municipalityNames.put("35209115", "Odense kommune");
		municipalityNames.put("29188459", "Odsherred kommune");
		municipalityNames.put("29189668", "Randers Kommune");
		municipalityNames.put("29189463", "Rebild Kommune");
		municipalityNames.put("29189609", "Ringkøbing-Skjern kommune");
		municipalityNames.put("18957981", "Ringsted kommune");
		municipalityNames.put("29189404", "Roskilde kommune");
		municipalityNames.put("29188378", "Rudersdal kommune");
		municipalityNames.put("65307316", "Rødovre kommune");
		municipalityNames.put("23795515", "Samsø kommune");
		municipalityNames.put("29189641", "Silkeborg Kommune");
		municipalityNames.put("29189633", "Skanderborg kommune");
		municipalityNames.put("29189579", "Skive kommune");
		municipalityNames.put("29188505", "Slagelse kommune");
		municipalityNames.put("68534917", "Solrød kommune");
		municipalityNames.put("29189994", "Sorø kommune");
		municipalityNames.put("29208654", "Stevns kommune");
		municipalityNames.put("29189951", "Struer kommune");
		municipalityNames.put("29189730", "Svendborg kommune");
		municipalityNames.put("29189978", "Syddjurs kommune");
		municipalityNames.put("29189773", "Sønderborg kommune");
		municipalityNames.put("29189560", "Thisted Kommune");
		municipalityNames.put("29189781", "Tønder kommune");
		municipalityNames.put("20310413", "Tårnby kommune");
		municipalityNames.put("19583910", "Vallensbæk kommune");
		municipalityNames.put("29189811", "Varde kommune");
		municipalityNames.put("29189838", "Vejen kommune");
		municipalityNames.put("29189900", "Vejle Kommune");
		municipalityNames.put("29189471", "Vesthimmerlands kommune");
		municipalityNames.put("29189846", "Viborg kommune");
		municipalityNames.put("29189676", "Vordingborg kommune");
		municipalityNames.put("28856075", "Ærø Kommune");
		municipalityNames.put("29189854", "Aabenraa kommune");
		municipalityNames.put("29189420", "Aalborg kommune");
		municipalityNames.put("55133018", "Aarhus kommune");
	}
}
