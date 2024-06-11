package backend.request;

import lombok.Data;

@Data
public class AddTagRequest {
	private String tagName;
	private Long comicId;
}
