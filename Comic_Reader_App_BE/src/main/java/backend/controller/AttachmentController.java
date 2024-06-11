package backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.model.Chapter;
import backend.service.AttachmentService;
import backend.service.ChapterService;
import backend.utils.JwtTokenProvider;

@RestController
public class AttachmentController {
	@Autowired
	AttachmentService attachmentService;

	@Autowired
	ChapterService chapterService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@PostMapping(path = "api/v1/attachment/upload")
	public ResponseEntity<?> addAttachmentInZip(@RequestPart MultipartFile zipFile,
			@RequestParam(name = "id") Long id) {
		Chapter chapter = chapterService.getChapterById(id);
		attachmentService.uploadAttachmentZip(zipFile, chapter);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	@DeleteMapping("api/v1/attachment/delete")  
	public ResponseEntity<?> deleteAttachment(@RequestParam("attachmentID") Long attachmentId,
			@RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (role.equalsIgnoreCase("ROLE_PUBLISHER")) {
				attachmentService.deleteAttachment(attachmentId);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
