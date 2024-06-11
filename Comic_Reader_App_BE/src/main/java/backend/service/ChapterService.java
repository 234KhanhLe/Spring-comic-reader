package backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Chapter;
import backend.model.Comic;
import backend.repository.ChapterRepository;
import backend.repository.ComicRepository;
import jakarta.transaction.Transactional;

@Service
public class ChapterService {
	@Autowired
	ChapterRepository chapterRepo;

	@Autowired
	ComicRepository comicRepo;

	public List<Chapter> getChaptersFromAComic(Long comicId) throws Exception {
		Comic comic = comicRepo.getComicById(comicId).orElseThrow(() -> new Exception("No comic found"));
		List<Chapter> chapterList = chapterRepo.getAllChapterByComicOrderByCreatedDate(comic);
		return chapterList;
	}

	public Chapter getChapterById(Long id, Long comicId) throws Exception {
		Comic comic = comicRepo.getComicById(comicId).orElseThrow(() -> new Exception("No comic found"));
		Chapter chapter = chapterRepo.getChapterByIdAndComic(id, comic);
		return chapter;
	}

	public Chapter getChapterById(Long chapterId) {
		Chapter chapter = chapterRepo.getChapterById(chapterId);
		return chapter;
	}

	public void getPrevAndLatterIdOfChapter(Long prevId, Long latterId, Long currentId, Long comicId) throws Exception {
		Comic comic = comicRepo.getComicById(comicId).orElseThrow(() -> new Exception("No comic found"));
		List<Chapter> chapterList = chapterRepo.getAllChapterByComicOrderByCreatedDate(comic);
		Chapter chapter = chapterRepo.getChapterByIdAndComic(currentId, comic);
		for (int i = 0; i < chapterList.size(); i++) {
			if (chapterList.get(i).getId().equals(chapter.getId())) {
				if (i == 0) {
					prevId = (long) 0;
				} else {
					prevId = (long) i - 1;
				}
				latterId = (long) i + 1;
			}
		}
	}

	public void deleteChapter(Long chapterId) {
		chapterRepo.deleteById(chapterId);
	}

	@Transactional
	public Chapter addChapter(String name, String description, Comic comic) {
		Chapter chapter = Chapter.builder().name(name).description(description).comic(comic).build();
		chapterRepo.save(chapter);
		return chapter;
	}
}
