package backend.response;

import lombok.Data;

@Data
public class ComicUpdateRequest {
	private String name;
	private String description;
	private String writer;
	private String tagName;
}
