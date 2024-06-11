package backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import backend.model.Role;
@Repository
@EnableJpaRepositories
public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findRoleByName(String name);
}
