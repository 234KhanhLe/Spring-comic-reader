package backend.request;

import lombok.Data;

@Data
public class ChapterRequest {
	private String name;
	private Long comicId;
	private String description;
	private String signlessName;
}
