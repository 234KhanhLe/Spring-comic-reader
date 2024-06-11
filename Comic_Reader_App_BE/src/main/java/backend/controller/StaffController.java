package backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.model.User;
import backend.request.RegisterRequest;
import backend.service.UserService;
import backend.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/staff")
public class StaffController {

	@Autowired
	UserService userService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	private final Logger logger = LoggerFactory.getLogger(StaffController.class);

	@PostMapping(path = "/addstaff", consumes = { "application/json" })
	public ResponseEntity<User> addStaff(@RequestBody RegisterRequest registerRequest
			) {
			User newAdmin = null;
				newAdmin = userService.addAdminByAdmin(registerRequest);
				return new ResponseEntity<User>(newAdmin, HttpStatus.CREATED);
	}

	@PatchMapping(path = "/removestaff", consumes = { "application/json" })
	public ResponseEntity<?> removeStaff(@RequestParam(name = "id") Long staffId,
			@RequestHeader("Authorization") String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			Long userId = jwtTokenProvider.getUserIdFromToken(token);
			if (role.equalsIgnoreCase("ROLE_STAFF") && userId != staffId) {
				userService.removeUser(staffId);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
