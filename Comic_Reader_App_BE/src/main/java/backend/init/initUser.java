package backend.init;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Role;
import backend.model.User;
import backend.repository.RoleRepository;
import backend.repository.UserRepository;

@Service
public class initUser {
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	public void initializeStaff() {
		Role staffRole = roleRepository.findRoleByName("ROLE_STAFF");
		User staff = User.builder().username("Staff1").password(DigestUtils.sha256Hex("Staff1"))
				.email("staff@gmail.com").role(staffRole).build();
		userRepository.save(staff);
	}

	public void initializePublisher() {
		Role publisherRole = roleRepository.findRoleByName("ROLE_PUBLISHER");
		User publisher = User.builder().username("Publisher1").password(DigestUtils.sha256Hex("Publisher1"))
				.email("publisher1@gmail.com").role(publisherRole).build();
		userRepository.save(publisher);
	}

	public void initalizeUser() {
		Role userRole = roleRepository.findRoleByName("ROLE_USER");
		User user = User.builder().username("User1").password(DigestUtils.sha256Hex("User1")).email("user1@gmail.com")
				.role(userRole).build();
		userRepository.save(user);
	}

}
