package com.genki.rest_api.diary.form;

import jakarta.validation.constraints.Size;

public record DairySearchForm(
        @Size(max = 100, message = "{errors.api.diary.search.title.max.length}")
        String title
) {
}
