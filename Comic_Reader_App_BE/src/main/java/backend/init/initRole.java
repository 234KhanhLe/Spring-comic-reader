package backend.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Role;
import backend.repository.RoleRepository;

@Service
public class initRole {
	@Autowired
	RoleRepository roleRepo;

	public void initializeRole() {
		Role role1 = new Role("ROLE_STAFF");
		roleRepo.save(role1);
		Role role2 = new Role("ROLE_USER");
		roleRepo.save(role2);
		Role role3 = new Role("ROLE_PUBLISHER");
		roleRepo.save(role3);
	}
}
