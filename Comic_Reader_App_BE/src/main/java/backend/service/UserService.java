package backend.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Role;
import backend.model.User;
import backend.repository.RoleRepository;
import backend.repository.UserRepository;
import backend.request.LoginRequest;
import backend.request.RegisterRequest;

@Service
public class UserService {
	@Autowired
	UserRepository userRepo;

	@Autowired
	RoleRepository roleRepo;

	public User userRegister(RegisterRequest registerRequest) {
		Role role = roleRepo.findRoleByName("ROLE_USER");
		User user = User.builder().username(registerRequest.getUsername())
				.password(DigestUtils.sha256Hex(registerRequest.getPassword())).email(registerRequest.getEmail())
				.role(role).build();
		userRepo.save(user);
		return user;
	}

	public User userLogin(LoginRequest loginRequest) throws Exception {
		User user = userRepo.findUserByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new Exception("No user found"));

		if (!DigestUtils.sha256Hex(loginRequest.getPassword()).equals(user.getPassword())) {
			throw new Exception("Wrong password");
		}
		return user;
	}

	public User addAdminByAdmin(RegisterRequest registerRequest) {
		Role role = roleRepo.findRoleByName("ROLE_STAFF");
		User admin = User.builder().username(registerRequest.getUsername())
				.password(DigestUtils.sha256Hex(registerRequest.getPassword())).email(registerRequest.getEmail())
				.role(role).build();
		userRepo.save(admin);
		return admin;
	}

	public User registerToBePublisher(Long userId) throws Exception {
		Role role = roleRepo.findRoleByName("ROLE_PUBLISHER");
		User user = userRepo.findById(userId).orElseThrow(() -> new Exception("No user found"));
		System.out.println(user);
		user.setRole(role);
		userRepo.save(user);
		return user;
	}

	public User getUserDetail(Long userId) throws Exception {
		User user = userRepo.findById(userId).orElseThrow(() -> new Exception("Some thing wrong"));
		return user;
	}
	
	public void removeUser(Long userId) {
		userRepo.deleteById(userId);
	}

}
