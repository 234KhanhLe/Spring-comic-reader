package backend.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import backend.model.Comic;
import backend.model.User;
import backend.repository.ComicRepository;
import backend.service.UserService;

@Service
public class initComic {
	@Autowired
	ComicRepository comicRepository;

	@Autowired
	UserService userService;

	public void initializeComic() {
		Faker faker = new Faker();
		try {
			User user = userService.getUserDetail((long) 2);
			Comic comic = Comic.builder().name(faker.name().username())
					.signlessName(faker.name().nameWithMiddle())
					.description(faker.demographic().demonym())
					.publisher(user).writer(faker.artist().name()).build();
			comicRepository.save(comic);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
