package dk.digitalidentity.ap.api;

import dk.digitalidentity.ap.dao.model.Logo;
import dk.digitalidentity.ap.dao.model.Municipality;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.LogoService;
import dk.digitalidentity.ap.service.MunicipalityService;
import dk.digitalidentity.ap.service.S3Service;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@Slf4j
@RequestMapping("/api/logo")
@SecurityRequirement(name = "Authorization")
@Tag(name = "Logo API")
public class LogoApi {
	
	@Autowired
	private S3Service s3service;

	@Autowired
	private MunicipalityService municipalityService;

	@Autowired
	private LogoService logoService;

	@PostMapping(path = "/{cvr}")
	public ResponseEntity<?> uploadLogo(@RequestBody MultipartFile file, @PathVariable("cvr") String cvr) {
		return upload(file, cvr);
	}

	private ResponseEntity<?> upload(MultipartFile logo, String cvr) {
		if (logo == null || logo.getOriginalFilename().isEmpty()) {
			log.warn("Tried to upload logo for municipality " + cvr + " but file or filename was not present");
			ResponseEntity.badRequest().build();
		}

		Municipality municipality = municipalityService.getByCvr(cvr);
		if (municipality == null) {
			return ResponseEntity.notFound().build();
		}

		if (!SecurityUtil.canEditMunicipalityInfo(municipality)) {
			log.warn("User " + SecurityUtil.getUser().getId() + " does not have write access to municipality info for " + municipality.getCvr());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		try (InputStream inputStream = logo.getInputStream()) {
			String s3ObjectUrl = s3service.writeFile(inputStream, logo.getOriginalFilename());
			if (s3ObjectUrl != null) {
				Logo municipalityLogo = new Logo();
				municipalityLogo.setFileName(logo.getOriginalFilename());
				municipalityLogo.setUrl(s3ObjectUrl);

				// delete old logo if any
				if (municipality.getLogo() != null) {
					String url = municipality.getLogo().getUrl();
					String fileName = url.substring(url.lastIndexOf("/") + 1);
					s3service.delete(fileName);

					logoService.delete(municipality.getLogo());
				}

				municipalityLogo.setMunicipality(municipality);
				municipalityLogo = logoService.save(municipalityLogo);

				return ResponseEntity.ok(municipalityLogo);
			}
		}
		catch (IOException e) {
			log.error("Error occured while uploading logo to S3. ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to upload logo. Try again.");
		}

		return ResponseEntity.badRequest().build();
	}

}
