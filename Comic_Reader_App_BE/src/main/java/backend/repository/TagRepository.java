package backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import backend.model.Tag;

@Repository
@EnableJpaRepositories
public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findTagByName(String name);

	@Query(value = "SELECT t.* FROM comic c JOIN comic_tag ct ON c.id = ct.comic_id JOIN tag t ON ct.tag_id = t.id WHERE c.id = :comicId", nativeQuery = true)
	List<Tag> findTagsByComicId(Long comicId);
}
