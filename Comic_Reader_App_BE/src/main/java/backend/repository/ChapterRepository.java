package backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import backend.model.Chapter;
import backend.model.Comic;

@Repository
@EnableJpaRepositories
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

	List<Chapter> getAllChapterByComicOrderByCreatedDate(Comic comic);
	
	Chapter getChapterByIdAndComic(Long Id, Comic comic);
	
	Chapter getChapterById(Long Id);
}
