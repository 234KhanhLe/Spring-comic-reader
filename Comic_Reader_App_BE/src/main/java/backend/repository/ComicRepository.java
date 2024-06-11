package backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import backend.model.Comic;
import backend.model.Tag;
@Repository
@EnableJpaRepositories
public interface ComicRepository extends JpaRepository<Comic, Long> {
	List<Comic> getListOfComicByNameIgnoreCaseContaining(String name);
	
	List<Comic> getListOfComicByNameIgnoreCaseLike(String name);
	
	List<Comic> getListOfComicByWriterIgnoreCase(String writer);
	
	List<Comic> getListOfComicByWriterIgnoreCaseContaining(String writer);
	
	List<Comic> getListOfComicByTags(Tag tag);
	
	Optional<Comic> getComicById(Long id);
	
	List<Comic> findAllByOrderByUpdatedDateDesc();
}
