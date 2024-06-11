package backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.model.Attachment;
import backend.model.Chapter;

@Repository
public interface AttachmentRepostory extends JpaRepository<Attachment, Long> {

	List<Attachment> getByChapter(Chapter chapter);
	
	Attachment getAttachmentById(Long id);
}
