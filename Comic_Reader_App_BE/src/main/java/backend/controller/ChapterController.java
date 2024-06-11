package backend.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.model.Attachment;
import backend.model.Chapter;
import backend.model.Comic;
import backend.request.ChapterRequest;
import backend.response.ChapterResponse;
import backend.service.AttachmentService;
import backend.service.ChapterService;
import backend.service.ComicService;
import backend.utils.JwtTokenProvider;
import jakarta.transaction.Transactional;

@RestController
public class ChapterController {

	@Autowired
	ChapterService chapterService;

	@Autowired
	ComicService comicService;

	@Autowired
	AttachmentService attachmentService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	private final Logger logger = LoggerFactory.getLogger(ChapterController.class);
	
	@GetMapping("api/v1/chapter/{id}")
	@Transactional
	public ResponseEntity<?> getChapter(@PathVariable("id") Long id,
			@RequestParam(name = "comicId", required = true) Long comicId) {
		Chapter chapter = null;
		try {
			chapter = chapterService.getChapterById(id, comicId);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Long prevId = (long) 0;
		Long latterId = (long) 0;
		ChapterResponse response = new ChapterResponse();
		try {
			chapterService.getPrevAndLatterIdOfChapter(prevId, latterId, id, comicId);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<Attachment> attachmentList = attachmentService.getAttachmentList(id, chapter.getComic());
		response.setPreviousId(prevId);
		response.setLatterId(latterId);
		response.setCurrentChapter(chapter);
		response.setAttachmentList(attachmentList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("api/v1/chapter/delete")
	public ResponseEntity<?> deleteChapter(@RequestBody Long ChapterId,
			@RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (!role.equalsIgnoreCase("ROLE_USER")) {
				chapterService.deleteChapter(ChapterId);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("api/v1/chapter/addchapter")
	@Transactional
	public ResponseEntity<?> addNewChapter(@RequestBody ChapterRequest chapterRequest){
		try {
			Comic comic = comicService.getComicDetail(chapterRequest.getComicId());
			Chapter chapter = chapterService.addChapter(chapterRequest.getName(), chapterRequest.getDescription(), comic);
			return new ResponseEntity<>(chapter, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PatchMapping("api/v1/chapter/update")
	public ResponseEntity<?> updateChapter(@RequestParam(name = "attachmentId") Long attachmentId,
			@RequestParam(name = "chapId") Long chapterId, @RequestParam(name = "attachment") MultipartFile attachment,
			@RequestParam(name = "orders", required = false) String imageOrder,
			@RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (role.equalsIgnoreCase("ROLE_PUBLISHER")) {
				Chapter chapter = chapterService.getChapterById(chapterId);
				attachmentService.updateAttachment(attachmentId, attachment, chapter, imageOrder);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	
}
