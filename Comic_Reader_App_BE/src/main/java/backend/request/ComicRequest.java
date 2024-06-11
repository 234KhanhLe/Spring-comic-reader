package backend.request;

import lombok.Data;

@Data
public class ComicRequest {
	private String name;
	private String description;
	private String writers;
}
