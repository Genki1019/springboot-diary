package com.genki.rest_api.diary.controller;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.form.DiaryRegistrationForm;
import com.genki.rest_api.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 日記コントローラー
 */
@RequiredArgsConstructor
@RestController
public class DiaryController {
    private final DiaryService diaryService;


    /**
     * 日記登録API
     *
     * @param diaryRegistrationForm 日記登録フォーム
     * @return 日記レスポンスDTO
     */
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public DiaryResponseDto registerDiary(@Validated DiaryRegistrationForm diaryRegistrationForm) {
        return diaryService.convertToDiaryResponseDto(
                diaryService.registerDiary(
                        diaryRegistrationForm.title(),
                        diaryRegistrationForm.content()
                )
        );
    }

    /**
     * 日記取得API（複数件）
     *
     * @return 検索した日記
     */
    @GetMapping("/")
    public List<DiaryEntity> getDiaries() {
        return diaryService.getAllDiaries();
    }
}
