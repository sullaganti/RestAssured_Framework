package Utilities.POJO;

import Utilities.POJO.requests.POST_Pet;

import java.util.Arrays;

public class Builder {

public static POST_Pet POST_PetRequestBody(String name, int id, String status) {
        return POST_Pet.builder()
                .name(name)
                .id(id)
                .status(status)
                .photoUrls(Arrays.asList("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                .category(POST_Pet.Category.builder().name("Dog").id(1).build())
                .tags(Arrays.asList(POST_Pet.TagsItem.builder().name("tag1").id(1).build()))
                .build();
    }
}
