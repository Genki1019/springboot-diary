package com.genki.rest_api.diary.controller;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.form.DairySearchForm;
import com.genki.rest_api.diary.form.DiaryRegistrationForm;
import com.genki.rest_api.diary.form.DiaryUpdateForm;
import com.genki.rest_api.diary.service.DiaryService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public DiaryResponseDto registerDiary(
            @Validated DiaryRegistrationForm diaryRegistrationForm,
            @RequestParam("diaryImage") MultipartFile multipartFile) {
        return diaryService.registerDiary(diaryRegistrationForm, multipartFile);
    }

    /**
     * 日記取得API（複数件）
     *
     * @return 日記レスポンスDTOリスト
     */
    @GetMapping("/")
    public List<DiaryResponseDto> getDiaries(@Validated DairySearchForm diarySearchForm) {
        if (StringUtils.isNotBlank(diarySearchForm.title())) {
            return diaryService.getDiaries(diarySearchForm.title());
        }
        return diaryService.getAllDiaries();
    }

    /**
     * 日記取得API（1件）
     *
     * @param id ID
     * @return 日記レスポンスDTO
     */
    @GetMapping("/{id}")
    public DiaryResponseDto getDiary(@PathVariable("id") long id) {
        return diaryService.getDiaryById(id);
    }

    /**
     * 日記更新API
     *
     * @param id              ID
     * @param diaryUpdateForm 日記更新フォーム
     * @return 日記レスポンスDTO
     */
    @PutMapping("/{id}")
    public DiaryResponseDto updateDiary(
            @PathVariable("id") long id,
            @Validated DiaryUpdateForm diaryUpdateForm,
            @RequestParam("diaryImage") MultipartFile multipartFile) {
        return diaryService.updateDiary(id, diaryUpdateForm, multipartFile);
    }

    /**
     * 日記削除API
     *
     * @param id ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiary(@PathVariable("id") long id) {
        diaryService.deleteDiary(id);
    }
}
