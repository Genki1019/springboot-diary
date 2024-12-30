package com.genki.rest_api.diary.service;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.exception.DiaryNotFoundException;
import com.genki.rest_api.diary.form.DiaryUpdateForm;
import com.genki.rest_api.diary.repository.DiaryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * 日記サービス
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    private final MessageSource messageSource;

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
     * 日記を全件取得
     *
     * @return 日記エンティティ
     */
    public List<DiaryEntity> getAllDiaries() {
        return diaryRepository.findAll();
    }

    /**
     * タイトルが入力に一致した日記を取得
     *
     * @param title 日記タイトル
     * @return 日記エンティティリスト
     */
    public List<DiaryEntity> getDiaries(String title) {
        return diaryRepository.findByTitleContaining(title);
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

    /**
     * 日記を1件取得
     *
     * @param id ID
     * @return 日記エンティティ
     */
    public DiaryEntity getDiaryById(long id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryNotFoundException(
                        messageSource.getMessage(
                                "errors.api.diary.search.id.not.found",
                                new Object[]{id},
                                Locale.getDefault()
                        )
                ));
    }

    /**
     * 日記を更新
     *
     * @param id              ID
     * @param diaryUpdateForm 日記更新フォーム
     * @return 日記エンティティ
     */
    public DiaryEntity updateDiary(long id, DiaryUpdateForm diaryUpdateForm) {
        DiaryEntity diaryEntity = getDiaryById(id);

        updateIfNotBlank(diaryUpdateForm.title(), diaryEntity::setTitle);
        updateIfNotBlank(diaryUpdateForm.content(), diaryEntity::setContent);
        return diaryRepository.save(diaryEntity);
    }

    /**
     * 値が空でない場合に更新処理を実行
     *
     * @param value   更新する値
     * @param updater 更新処理
     */
    private void updateIfNotBlank(String value, Consumer<String> updater) {
        if (StringUtils.isNotBlank(value)) {
            updater.accept(value);
        }
    }

    /**
     * 日記を削除
     *
     * @param id ID
     */
    public void deleteDiary(long id) {
        diaryRepository.deleteById(id);
    }
}
