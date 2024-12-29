package com.genki.rest_api.diary.form;

import jakarta.validation.constraints.Size;

public record DairySearchForm(
        @Size(max = 50)
        String title
) {
}
