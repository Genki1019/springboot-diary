package com.genki.rest_api.diary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiaryUpdateForm(
        @NotBlank
        @Size(max = 50)
        String title,

        @NotBlank
        @Size(max = 500)
        String content) {
}
