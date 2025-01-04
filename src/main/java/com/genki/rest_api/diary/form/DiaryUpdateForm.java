package com.genki.rest_api.diary.form;

import jakarta.validation.constraints.Size;

public record DiaryUpdateForm(
        @Size(max = 100, message = "{errors.api.diary.register.title.max.length}")
        String title,

        @Size(max = 1000, message = "{errors.api.diary.register.content.max.length}")
        String content) {
}
