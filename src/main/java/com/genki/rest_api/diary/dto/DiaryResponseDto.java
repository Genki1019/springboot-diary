package com.genki.rest_api.diary.dto;

import com.genki.rest_api.diary.entity.DiaryEntity;

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
    /**
     * 日記エンティティをDTOに変換
     *
     * @param diaryEntity 日記エンティティ
     * @return 日記レスポンスDTO
     */
    public static DiaryResponseDto of(DiaryEntity diaryEntity) {
        return new DiaryResponseDto(
                diaryEntity.getId(),
                diaryEntity.getTitle(),
                diaryEntity.getContent(),
                diaryEntity.getImagePath(),
                diaryEntity.getCreatedAt(),
                diaryEntity.getUpdatedAt()
        );
    }
}
