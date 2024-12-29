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
        @NotBlank
        @Size(max = 50)
        String title,

        @NotBlank
        @Size(max = 500)
        String content) {
}
