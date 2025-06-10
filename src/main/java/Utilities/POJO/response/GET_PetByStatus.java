package Utilities.POJO.response;

import java.util.List;

import lombok.Data;

@Data
public class GET_PetByStatus {
	private List<String> photoUrls;
	private String name;
	private long id;
	private Category category;
	private String status;
	private List<TagsItem> tags;

	@Data
	public static class Category{
		private String name;
		private long id;
	}

	@Data
	public static class TagsItem{
		private String name;
		private long id;
	}
}