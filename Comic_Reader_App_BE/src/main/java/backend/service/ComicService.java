package backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Comic;
import backend.model.Tag;
import backend.model.User;
import backend.repository.ComicRepository;
import backend.repository.TagRepository;
import backend.repository.UserRepository;
import backend.request.ComicRequest;
import backend.response.ComicUpdateRequest;
import jakarta.transaction.Transactional;

@Service
public class ComicService {
	@Autowired
	ComicRepository comicRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	TagRepository tagRepo;

	public Comic saveComic(Long userId, ComicRequest comicRequest) throws Exception {
		String signlessName = StringUtils.stripAccents(comicRequest.getName());

		User user = userRepo.findById(userId).orElseThrow(() -> new Exception("No user found"));
		Comic comic = Comic.builder().name(comicRequest.getName()).description(comicRequest.getDescription())
				.publisher(user).writer(comicRequest.getWriters()).signlessName(signlessName).build();
		comicRepo.save(comic);
		return comic;
	}

	public List<Comic> searchForComicListByNameOrWriter(String searchText){
		List<Comic> comicList = new ArrayList<>();
		comicList = comicRepo.getListOfComicByNameIgnoreCaseContaining(searchText);
		if(comicList ==null || comicList.isEmpty()) {
			comicList = comicRepo.getListOfComicByNameIgnoreCaseLike(searchText);
		}
		if(comicList ==null || comicList.isEmpty()) {
			comicList = comicRepo.getListOfComicByWriterIgnoreCase(searchText);
		}
		if(comicList == null || comicList.isEmpty()) {
			comicList = comicRepo.getListOfComicByWriterIgnoreCaseContaining(searchText);
		}
		return comicList;
	}

	public Comic getComicDetail(Long comicId) throws Exception {
		Comic comic = comicRepo.getComicById(comicId).orElseThrow(() -> new Exception("No comic found"));
		return comic;
	}

	public List<Comic> getComicByTags(String tagName) throws Exception {
		Tag tag = tagRepo.findTagByName(tagName).orElseThrow(() -> new Exception("No Tag found"));
		List<Comic> comicListByTag = comicRepo.getListOfComicByTags(tag);
		return comicListByTag;
	}

	public Comic updateComic(Comic originComic, ComicUpdateRequest comicRequest) {
		Tag tag = tagRepo.findTagByName(comicRequest.getName())
				.orElse(Tag.builder().name(comicRequest.getName()).build());
//		Set<Tag> tagList = originComic.getTags();
//		if (!tagList.contains(tag)) {
//			tagList.add(tag);
//		}
//		originComic.setTags(tagList);
		if (comicRequest.getName() != null) {
			originComic.setName(comicRequest.getName());
		}
		if (comicRequest.getDescription() != null) {
			originComic.setDescription(comicRequest.getDescription());
		}
		if (comicRequest.getWriter() != null) {
			originComic.setWriter(comicRequest.getWriter());
		}
		comicRepo.save(originComic);
		return originComic;
	}
	
	public Comic addTagToComic(Comic comic, String tagName) throws Exception {
		Tag tag = tagRepo.findTagByName(tagName).orElseThrow(() -> new Exception("No Tag Found"));
		System.out.println(comic.getTags());
		comic.getTags().add(tag);
		comicRepo.save(comic);
		return comic;
	}
	
	public void deleteComic(Long comicId) {
		comicRepo.deleteById(comicId);	
	}
	@Transactional
	public List<Comic> getAllComic(){
		List<Comic> comicList = comicRepo.findAllByOrderByUpdatedDateDesc();
		return comicList;
	}
}
