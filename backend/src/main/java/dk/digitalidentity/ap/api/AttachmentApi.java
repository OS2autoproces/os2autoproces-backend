package dk.digitalidentity.ap.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dk.digitalidentity.ap.api.model.AttachmentsDTO;
import dk.digitalidentity.ap.dao.AttachmentDao;
import dk.digitalidentity.ap.dao.model.Attachment;
import dk.digitalidentity.ap.dao.model.Process;
import dk.digitalidentity.ap.security.SecurityUtil;
import dk.digitalidentity.ap.service.ProcessService;
import dk.digitalidentity.ap.service.S3Service;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/attachments")
public class AttachmentApi {
	
	@Autowired
	private S3Service s3service;

	@Autowired
	private ProcessService processService;
	
	@Autowired
	private AttachmentDao attachmentDao;

	@GetMapping("/{processId}")
	public ResponseEntity<?> getAttachments(@PathVariable("processId") long processId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}
		
		List<Attachment> attachments = attachmentDao.findByProcess(process);

		// filter out non-public attachments on cross-municipality lookup
		if (!process.getCvr().equals(SecurityUtil.getCvr()) && attachments != null && attachments.size() > 0) {
			attachments = attachments.stream().filter(a -> a.isVisibleToOtherMunicipalities()).collect(Collectors.toList());
		}

		return ResponseEntity.ok(attachments);
	}

	@PostMapping(path = "/{processId}/public")
	public ResponseEntity<?> uploadAttachmentPublic(AttachmentsDTO attachmentsForm, @PathVariable("processId") long processId) {
		return upload(attachmentsForm, processId, true);
	}
	
	@PostMapping(path = "/{processId}")
	public ResponseEntity<?> uploadAttachment(AttachmentsDTO attachmentsForm, @PathVariable("processId") long processId) {
		return upload(attachmentsForm, processId, false);
	}

	private ResponseEntity<?> upload(AttachmentsDTO attachmentsForm, long processId, boolean visibleToOtherMunicipalities) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}

		if (!SecurityUtil.canEdit(process)) {
			log.warn("User " + SecurityUtil.getUser().getId() + " does not have write access to Process " + process.getId());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		List<Attachment> attachments = new ArrayList<>();

		for (MultipartFile file : attachmentsForm.getFiles()) {
			if (file.getOriginalFilename().isEmpty()) {
				continue;
			}
			
			try {
				String s3ObjectUrl = s3service.writeFile(file.getInputStream(), file.getOriginalFilename());
				if (s3ObjectUrl != null) {
					Attachment attachment = new Attachment();
					attachment.setFileName(file.getOriginalFilename());
					attachment.setProcess(process);
					attachment.setUrl(s3ObjectUrl);
					attachment.setVisibleToOtherMunicipalities(visibleToOtherMunicipalities);
					attachments.add(attachment);
				}
			}
			catch (IOException e) {
				// TODO: what happens to file-uploads that succeeded in this case?
				log.error("Error occured while uploading file to S3. ", e);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to upload file. Try again.");
			}
		}

		attachmentDao.saveAll(attachments);

		return ResponseEntity.ok(attachments);
	}

	@DeleteMapping(path = "/{processId}/{attachmentId}")
	public ResponseEntity<?> deleteAttachment(AttachmentsDTO attachmentsForm, @PathVariable("processId") long processId, @PathVariable("attachmentId") long attachmentId) {
		Process process = processService.findOne(processId);
		if (process == null) {
			return ResponseEntity.notFound().build();
		}

		if (!SecurityUtil.canEdit(process)) {
			log.warn("User " + SecurityUtil.getUser().getId() + " does not have edit access to Process " + process.getId());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Attachment attachment = attachmentDao.getById(attachmentId);
		if (attachment == null) {
			return ResponseEntity.notFound().build();
		}

		try {
			attachmentDao.delete(attachment);

			String url = attachment.getUrl();
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			s3service.delete(fileName);
		}
		catch (Exception e) {
			log.error("Error occured while removing file from S3. ", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to upload file. Try again.");
		}
		
		return ResponseEntity.ok().build();
	}
}
