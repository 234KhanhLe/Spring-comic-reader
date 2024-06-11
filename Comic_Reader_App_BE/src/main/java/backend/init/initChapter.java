package backend.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import backend.model.Chapter;
import backend.model.Comic;
import backend.repository.ChapterRepository;
import backend.repository.ComicRepository;

@Service
public class initChapter {
	@Autowired
	ChapterRepository chapterRepository;

	@Autowired
	ComicRepository comicRepository;

	public void initalizeChapter() {
		Faker faker = new Faker();
		try {
			Comic comic = comicRepository.getComicById((long) 1).orElseThrow(() -> new Exception("No comic found"));
			Chapter chapter = Chapter.builder().name("Chapter 1").description(faker.book().publisher()).comic(comic)
					.build();
			chapterRepository.save(chapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
