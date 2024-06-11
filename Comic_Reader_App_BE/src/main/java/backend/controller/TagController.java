package backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import backend.request.TagRequest;
import backend.service.TagService;
import backend.utils.JwtTokenProvider;
import jakarta.transaction.Transactional;

@RestController
public class TagController {
	@Autowired
	TagService tagService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@PostMapping("api/v1/tag/add")
	@Transactional
	public ResponseEntity<?> addTag(@RequestBody TagRequest tagRequest,
			@RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (!role.equalsIgnoreCase("ROLE_USER")) {
				try {
					tagService.addTag(tagRequest);
				} catch (Exception e) {
					return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
				}
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
