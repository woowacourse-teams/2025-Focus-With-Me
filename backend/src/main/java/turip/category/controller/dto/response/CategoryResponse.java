package turip.category.controller.dto.response;

import java.util.List;
import turip.category.domain.Category;

public record CategoryResponse(
        String name
) {
    public static List<CategoryResponse> from(List<Category> categories) {
        return categories.stream()
                .map(category -> new CategoryResponse(category.getName()))
                .toList();
    }
}
