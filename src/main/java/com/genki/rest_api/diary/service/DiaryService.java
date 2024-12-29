package com.genki.rest_api.diary.service;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.repository.DiaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日記サービス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    /**
     * 日記DTOを生成
     *
     * @param diaryEntity 日記エンティティ
     * @return 日記DTO
     */
    public DiaryResponseDto convertToDiaryResponseDto(DiaryEntity diaryEntity) {
        return new DiaryResponseDto(
                diaryEntity.getId(),
                diaryEntity.getTitle(),
                diaryEntity.getContent(),
                diaryEntity.getImagePath(),
                diaryEntity.getCreatedAt(),
                diaryEntity.getUpdatedAt()
        );
    }

    /**
     * 日記DTOリストを生成
     *
     * @param diaryEntityList 日記エンティティリスト
     * @return 日記DTOリスト
     */
    public List<DiaryResponseDto> convertToDiaryResponseDtoList(List<DiaryEntity> diaryEntityList) {
        return diaryEntityList.stream()
                .map(this::convertToDiaryResponseDto)
                .toList();
    }

    /**
     * 日記を全件取得する
     *
     * @return 日記エンティティ
     */
    public List<DiaryEntity> getAllDiaries() {
        return diaryRepository.findAll();
    }

    /**
     * 日記を登録
     *
     * @param title   タイトル
     * @param content 内容
     * @return 日記エンティティ
     */
    public DiaryEntity registerDiary(String title, String content) {
        DiaryEntity diaryEntity = new DiaryEntity();
        diaryEntity.setTitle(title);
        diaryEntity.setContent(content);
        return diaryRepository.save(diaryEntity);
    }
}
