package com.genki.rest_api.diary.repository;

import com.genki.rest_api.diary.entity.DiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日記リポジトリ
 */
@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {
    /**
     * タイトル部分一致検索
     *
     * @param title 日記タイトル
     * @return 日記エンティティリスト
     */
    List<DiaryEntity> findByTitleContaining(String title);
}
