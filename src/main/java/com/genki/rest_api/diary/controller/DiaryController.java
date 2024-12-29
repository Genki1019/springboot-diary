package com.genki.rest_api.diary.controller;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.form.DairySearchForm;
import com.genki.rest_api.diary.form.DiaryRegistrationForm;
import com.genki.rest_api.diary.service.DiaryService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日記コントローラー
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
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
    public List<DiaryResponseDto> getDiaries(@Validated DairySearchForm diarySearchForm) {
        if (StringUtils.isNotBlank(diarySearchForm.title())) {
            return diaryService.convertToDiaryResponseDtoList(
                    diaryService.getDiaries(diarySearchForm.title())
            );
        }
        return diaryService.convertToDiaryResponseDtoList(diaryService.getAllDiaries());
    }

    /**
     * 日記取得API（1件）
     *
     * @param id ID
     * @return 検索した日記
     */
    @GetMapping("/{id}")
    public DiaryResponseDto getDiary(@PathVariable("id") long id) {
        return diaryService.convertToDiaryResponseDto(diaryService.getDiaryById(id));
    }
}
