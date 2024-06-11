package backend.response;

import java.util.List;

import backend.model.Attachment;
import backend.model.Chapter;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChapterResponse {
	private Long previousId;
	private Long latterId;
	private Chapter currentChapter;
	private List<Attachment> attachmentList;
}
