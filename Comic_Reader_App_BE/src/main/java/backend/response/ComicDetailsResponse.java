package backend.response;

import java.util.List;
import java.util.Set;

import backend.model.Chapter;
import backend.model.Comic;
import backend.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ComicDetailsResponse {
	private Comic comic;
	private List<Chapter> chapterList;
	private List<Tag> tagList;
}
