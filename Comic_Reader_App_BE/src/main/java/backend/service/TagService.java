package backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.model.Comic;
import backend.model.Tag;
import backend.repository.TagRepository;
import backend.request.TagRequest;
import jakarta.transaction.Transactional;

@Service
public class TagService {
	@Autowired
	TagRepository tagRepo;

	@Transactional
	public void addTag(TagRequest tagRequest) throws Exception {
		if (tagRepo.findTagByName(tagRequest.getName()) == null) {
			Tag.builder().name(tagRequest.getName()).description(tagRequest.getDescription()).build();
		} else {
			new Exception("Tag already exist");
		}
	}

	@Transactional
	public List<Tag> getSetOfTagsByComic(Comic comic) {
		List<Tag> setTag = tagRepo.findTagsByComicId(comic.getId());
		return setTag;
	}
}
