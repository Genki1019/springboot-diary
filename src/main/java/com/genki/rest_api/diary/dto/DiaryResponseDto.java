package com.genki.rest_api.diary.dto;

import java.time.LocalDateTime;

/**
 * 日記レスポンスDTO
 *
 * @param id        日記ID
 * @param title     日記タイトル
 * @param content   日記本文
 * @param imagePath 画像パス
 * @param createdAt 登録日時
 * @param updatedAt 更新日時
 */
public record DiaryResponseDto(
        long id,
        String title,
        String content,
        String imagePath,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
