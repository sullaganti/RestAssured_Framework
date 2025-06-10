package Utilities.POJO.requests;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class POST_Pet {
	private List<String> photoUrls;
	private String name;
	private int id;
	private Category category;
	private List<TagsItem> tags;
	private String status;

	@Data
	@Builder
	public static class TagsItem{
		private String name;
		private int id;
	}

	@Data
	@Builder
	public static class Category{
		private String name;
		private int id;
	}
}