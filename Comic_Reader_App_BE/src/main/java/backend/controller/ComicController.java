package backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.model.Attachment;
import backend.model.Chapter;
import backend.model.Comic;
import backend.model.Tag;
import backend.request.AddTagRequest;
import backend.request.ComicRequest;
import backend.request.DeleteComicRequest;
import backend.response.ComicDetailsResponse;
import backend.response.ComicUpdateRequest;
import backend.service.AttachmentService;
import backend.service.ChapterService;
import backend.service.ComicService;
import backend.service.TagService;
import backend.utils.JwtTokenProvider;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin
public class ComicController {

	@Autowired
	ComicService comicService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	ChapterService chapterService;

	@Autowired
	TagService tagService;

	@Autowired
	AttachmentService attachmentService;

	private final Logger logger = LoggerFactory.getLogger(ComicController.class);

//	@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.POST })
	@PostMapping("api/v1/comic/createcomic")
	public ResponseEntity<Comic> createComic(@RequestBody ComicRequest comicRequest,
			@RequestHeader("Authorization") String authHeader) {	
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (role.equalsIgnoreCase("ROLE_PUBLISHER")) {
				Comic comic = null;
				Long userId = jwtTokenProvider.getUserIdFromToken(token);
				try {
					comic = comicService.saveComic(userId, comicRequest);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				return new ResponseEntity<>(comic, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
	}

	@GetMapping("api/v1/search")
	public ResponseEntity<List<Comic>> searchComic(@RequestParam(value = "searchText", required = false) String text
			) {
		List<Comic> comicList = new ArrayList<>();
		if(text != null && !text.isEmpty()) {
			comicList = comicService.searchForComicListByNameOrWriter(text);
		}else {
			comicList = comicService.getAllComic();
		}
		return new ResponseEntity<>(comicList, HttpStatus.OK);
	}

	@GetMapping("api/v1/detail/{id}")
	@Transactional
	public ResponseEntity<?> getComicDetail(@PathVariable("id") Long comicId) {
		ComicDetailsResponse comicDetail = new ComicDetailsResponse();
		if (comicId != null) {
			try {
				Comic comic = comicService.getComicDetail(comicId);
				List<Chapter> chapterList = chapterService.getChaptersFromAComic(comicId);
				List<Tag> tagList = tagService.getSetOfTagsByComic(comic);
				if (chapterList != null) {
					comicDetail.setComic(comic);
					comicDetail.setChapterList(chapterList);
				} else {
					return new ResponseEntity<>(comic, HttpStatus.OK);
				}
				comicDetail.setTagList(tagList);
			} catch (Exception e) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(comicDetail, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("api/v1/comic/tag/{name}")
	public ResponseEntity<List<Comic>> getComicListByTag(@PathVariable("name") String tagName) {
		List<Comic> comicList;
		try {
			comicList = comicService.getComicByTags(tagName);
			return new ResponseEntity<>(comicList, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@DeleteMapping("api/v1/comic/delete")
	@Transactional
	public ResponseEntity<?> deleteComic(@RequestBody DeleteComicRequest request,
			@RequestHeader("Authorization") String authHeader) {
		try {
			Comic comic = comicService.getComicDetail(request.getComicId());
			List<Chapter> chapterList = chapterService.getChaptersFromAComic(request.getComicId());
			for (Chapter chapter : chapterList) {
				List<Attachment> attachmentList = attachmentService.getAttachmentList(chapter.getId(), comic);
				for (Attachment attachment : attachmentList) {
					attachmentService.deleteAttachment(attachment.getId());
				}
				chapterService.deleteChapter(chapter.getId());
			}
			comicService.deleteComic(request.getComicId());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("api/v1/comic/update")
	public ResponseEntity<?> updateComic(@RequestParam(name = "id", required = true) Long comicId,
			@RequestBody ComicUpdateRequest comicRequest, @RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (!role.equalsIgnoreCase("ROLE_USER")) {
				try {
					Comic comic = comicService.getComicDetail(comicId);
					comicService.updateComic(comic, comicRequest);
				} catch (Exception e) {
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("api/v1/comic/addTag")
	@Transactional
	public ResponseEntity<?> addTag(@RequestHeader("Authorization") String authHeader,
			@RequestBody AddTagRequest addRequest) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (!role.equalsIgnoreCase("ROLE_USER")) {
				try {
					Comic comic = comicService.getComicDetail(addRequest.getComicId());
					comicService.addTagToComic(comic, addRequest.getTagName());
					return new ResponseEntity<>(HttpStatus.OK);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("api/v1/comic/all")
	@Transactional
	public ResponseEntity<?> getAllComic() {
		List<Comic> comicList = comicService.getAllComic();
		return new ResponseEntity<>(comicList, HttpStatus.OK);
	}
}
