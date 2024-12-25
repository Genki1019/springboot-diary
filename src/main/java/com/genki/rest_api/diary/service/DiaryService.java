package com.genki.rest_api.diary.service;

import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.repository.DiaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    public List<DiaryEntity> getAllDiaries() {
        return diaryRepository.findAll();
    }
}
