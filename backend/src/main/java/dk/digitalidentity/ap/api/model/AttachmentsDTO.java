package dk.digitalidentity.ap.api.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentsDTO {
	private List<MultipartFile> files = new ArrayList<>();
}
