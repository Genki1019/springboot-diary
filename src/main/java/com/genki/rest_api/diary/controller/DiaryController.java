package com.genki.rest_api.diary.controller;

import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DiaryController {
    private final DiaryService diaryService;

    @GetMapping("/")
    public List<DiaryEntity> index() {
        return diaryService.getAllDiaries();
    }
}
