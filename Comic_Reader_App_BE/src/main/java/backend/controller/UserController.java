package backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.model.User;
import backend.repository.UserRepository;
import backend.request.LoginRequest;
import backend.request.RegisterRequest;
import backend.service.UserService;
import backend.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping(path = "api/v1/auth/registration", consumes = { "application/json" })
	public ResponseEntity<User> registerUser(@RequestBody RegisterRequest registerRequest) {
		User user = userService.userRegister(registerRequest);
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	}

	@PostMapping(path = "api/v1/auth/login", consumes = { "application/json" })
	public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
		User user = userService.userLogin(loginRequest);
		String token = jwtTokenProvider.generateToken(user);
		Map<String, String> response = new HashMap<>();
		response.put("token", token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping(path = "api/v1/auth/changerole", consumes = { "application/json" })
	public ResponseEntity<?> changeRole(@RequestHeader("Authorization") String authHeader) throws Exception {

		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			String role = jwtTokenProvider.getUserRoleFromToken(token);
			if (role.equalsIgnoreCase("ROLE_USER")) {
				userService.registerToBePublisher(jwtTokenProvider.getUserIdFromToken(token));
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "api/v1/user/detail/{userId}")
	public ResponseEntity<?> getUserDetail(@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable Long userId) {
		User user = null;
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring("Bearer ".length());
			Long loggedUserId = jwtTokenProvider.getUserIdFromToken(token);
			if(loggedUserId.equals(userId)) {
				try {
				user = userService.getUserDetail(userId);
					return new ResponseEntity<>(user, HttpStatus.OK);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} else {
			try {
				user = userService.getUserDetail(userId);
			} catch (Exception e) {				
				e.printStackTrace();
			}
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

//	@GetMapping(path = "api/v1/user/profile", consumes = { "application/json" })
//	public ResponseEntity<?> getUserProfile(@RequestParam(name = "userId") Long userId) {
//		try {
//			User user = userService.getUserDetail(userId);
//			return new ResponseEntity<>(user, HttpStatus.OK);
//		} catch (Exception e) {
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
}
