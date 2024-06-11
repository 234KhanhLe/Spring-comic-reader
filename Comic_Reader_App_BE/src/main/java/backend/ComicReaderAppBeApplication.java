package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import backend.init.initChapter;
import backend.init.initComic;
import backend.init.initRole;
import backend.init.initTag;
import backend.init.initUser;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ComicReaderAppBeApplication {

	@Autowired
	initRole initRole;
	@Autowired
	initUser initUser;
	@Autowired
	initTag initTag;
	@Autowired
	initComic initComic;
	@Autowired
	initChapter initChapter;
	
	
	public static void main(String[] args) {
		SpringApplication.run(ComicReaderAppBeApplication.class, args);
	}

	@PostConstruct
	public void init() {
		initRole.initializeRole();
		initUser.initializeStaff();
		initUser.initializePublisher();
		initUser.initalizeUser();
		initTag.initializeTag();
		initComic.initializeComic();
		initChapter.initalizeChapter();
	}
}
