package com.genki.rest_api.diary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 日記登録フォーム
 *
 * @param title   日記タイトル
 * @param content 日記本文
 */
public record DiaryRegistrationForm(
        @NotBlank(message = "{errors.api.diary.register.title.required}")
        @Size(max = 100, message = "{errors.api.diary.register.title.max.length}")
        String title,

        @NotBlank(message = "{errors.api.diary.register.content.required}")
        @Size(max = 1000, message = "{errors.api.diary.register.content.max.length}")
        String content) {
}
