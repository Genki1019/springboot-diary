package com.genki.rest_api.diary.service;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.exception.DiaryIOException;
import com.genki.rest_api.diary.exception.DiaryImageNotSupportedException;
import com.genki.rest_api.diary.exception.DiaryNotFoundException;
import com.genki.rest_api.diary.form.DiaryUpdateForm;
import com.genki.rest_api.diary.repository.DiaryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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

    private final String IMAGE_DIR_PATH = "images";
    private final List<String> EXTENSION_LIST = List.of("png", "jpg", "jpeg", "gif");

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
    public DiaryEntity registerDiary(String title, String content, MultipartFile multipartFile) {
        DiaryEntity diaryEntity = new DiaryEntity();
        diaryEntity.setTitle(title);
        diaryEntity.setContent(content);
        diaryRepository.save(diaryEntity);

        if (!multipartFile.isEmpty()) {
            saveDiaryImage(diaryEntity, multipartFile);
        }
        return diaryEntity;
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
    public DiaryEntity updateDiary(long id, DiaryUpdateForm diaryUpdateForm, MultipartFile multipartFile) {
        DiaryEntity diaryEntity = getDiaryById(id);

        updateIfNotBlank(diaryUpdateForm.title(), diaryEntity::setTitle);
        updateIfNotBlank(diaryUpdateForm.content(), diaryEntity::setContent);

        if (!multipartFile.isEmpty()) {
            saveDiaryImage(diaryEntity, multipartFile);
        }
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

    /**
     * ディレクトリを作成
     *
     * @param path ファイルパス
     */
    public void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new DiaryIOException(
                    messageSource.getMessage(
                            "errors.api.diary.image.file.is.blank",
                            null,
                            Locale.getDefault()
                    ), e);
        }
    }

    /**
     * 画像ディレクトリのパスを取得
     *
     * @return ディレクトリパス
     */
    private Path getDiaryImageDirPath() {
        return Path.of(IMAGE_DIR_PATH);
    }

    /**
     * 日記IDごとの画像ディレクトリのパスを取得
     *
     * @param id ID
     * @return ディレクトリパス
     */
    private Path getDiaryImageIdDirPath(long id) {
        return Path.of(IMAGE_DIR_PATH, String.valueOf(id));
    }

    /**
     * 日記画像のファイルパスを呪録
     *
     * @param id       ID
     * @param fileName 画像ファイル名
     * @return 画像ファイルパス
     */
    private Path getDiaryImageFilePath(long id, String fileName) {
        return Path.of(IMAGE_DIR_PATH, String.valueOf(id), fileName);
    }

    /**
     * 登録する画像ファイル名を取得
     *
     * @param multipartFile 画像ファイル
     * @return 画像ファイル名
     */
    private String createDiaryImageFileName(MultipartFile multipartFile) {
        String originalDiaryImageFileName = multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalDiaryImageFileName);
        return UUID.randomUUID() + "." + extension;
    }

    /**
     * 新しい画像を保存
     *
     * @param path          画像パス
     * @param multipartFile 画像ファイル
     */
    private void restoreDiaryImage(Path path, MultipartFile multipartFile) {
        try {
            multipartFile.transferTo(path);
        } catch (IOException e) {
            throw new DiaryIOException(
                    messageSource.getMessage(
                            "errors.api.diary.image.file.is.blank",
                            null,
                            Locale.getDefault()
                    ),
                    e);
        }
    }

    /**
     * 日記画像を保存
     *
     * @param diaryEntity   日記エンティティ
     * @param multipartFile 画像ファイル
     */
    private void saveDiaryImage(DiaryEntity diaryEntity, MultipartFile multipartFile) {
        if (!isDiaryImageExtensionSupported(multipartFile)) {
            throw new DiaryImageNotSupportedException(
                    messageSource.getMessage(
                            "errors.api.diary.image.extension.not.supported",
                            new Object[]{EXTENSION_LIST},
                            Locale.getDefault()
                    )
            );
        }
        createDirectories(getDiaryImageDirPath());
        long diaryId = diaryEntity.getId();
        createDirectories(getDiaryImageIdDirPath(diaryId));
        String diaryImageFileName = createDiaryImageFileName(multipartFile);
        Path diaryImageFilePath = getDiaryImageFilePath(diaryId, diaryImageFileName);
        restoreDiaryImage(diaryImageFilePath, multipartFile);

        if (StringUtils.isNotBlank(diaryEntity.getImagePath())) {
            Path oldDiaryImageFilePath = getDiaryImageFilePath(diaryId, diaryEntity.getImagePath());
            try {
                Files.deleteIfExists(oldDiaryImageFilePath);
            } catch (IOException e) {
                throw new DiaryIOException(
                        messageSource.getMessage(
                                "errors.api.diary.image.file.is.blank",
                                null,
                                Locale.getDefault()
                        ),
                        e);
            }
        }
        diaryEntity.setImagePath(diaryImageFileName);
        diaryRepository.save(diaryEntity);
    }

    /**
     * 画像の拡張子がサポートされているか
     *
     * @param multipartFile 画像ファイル
     * @return true=サポートされている, false=サポートされていない
     */
    private boolean isDiaryImageExtensionSupported(MultipartFile multipartFile) {
        String originalImageFileName = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalImageFileName)) {
            return false;
        }
        String extension = FilenameUtils.getExtension(originalImageFileName).toLowerCase();
        return EXTENSION_LIST.contains(extension);
    }
}
