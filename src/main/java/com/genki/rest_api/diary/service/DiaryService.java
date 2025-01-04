package com.genki.rest_api.diary.service;

import com.genki.rest_api.diary.dto.DiaryResponseDto;
import com.genki.rest_api.diary.entity.DiaryEntity;
import com.genki.rest_api.diary.exception.DiaryIOException;
import com.genki.rest_api.diary.exception.DiaryImageNotSupportedException;
import com.genki.rest_api.diary.exception.DiaryNotFoundException;
import com.genki.rest_api.diary.form.DiaryRegistrationForm;
import com.genki.rest_api.diary.form.DiaryUpdateForm;
import com.genki.rest_api.diary.repository.DiaryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
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
     * 日記を全件取得
     *
     * @return 日記レスポンスDTO
     */
    public List<DiaryResponseDto> getAllDiaries() {
        return diaryRepository.findAll()
                .stream()
                .map(DiaryResponseDto::of)
                .toList();
    }

    /**
     * タイトルが入力に一致した日記を取得
     *
     * @param title 日記タイトル
     * @return 日記レスポンスDTOリスト
     */
    public List<DiaryResponseDto> getDiaries(String title) {
        return diaryRepository.findByTitleContaining(title)
                .stream()
                .map(DiaryResponseDto::of)
                .toList();
    }

    /**
     * 日記を登録
     *
     * @param diaryRegistrationForm 日記登録フォーム
     * @param multipartFile         画像ファイル
     * @return 日記レスポンスDTO
     */
    public DiaryResponseDto registerDiary(DiaryRegistrationForm diaryRegistrationForm, MultipartFile multipartFile) {
        DiaryEntity diaryEntity = new DiaryEntity();
        diaryEntity.setTitle(diaryRegistrationForm.title());
        diaryEntity.setContent(diaryRegistrationForm.content());
        diaryRepository.save(diaryEntity);

        if (!multipartFile.isEmpty()) {
            saveDiaryImage(diaryEntity, multipartFile);
        }
        return DiaryResponseDto.of(diaryEntity);
    }

    /**
     * 日記を1件取得
     *
     * @param id ID
     * @return 日記レスポンスDTO
     */
    public DiaryResponseDto getDiaryById(long id) {
        return DiaryResponseDto.of(getDiaryEntityById(id));
    }

    /**
     * 日記を更新
     *
     * @param id              ID
     * @param diaryUpdateForm 日記更新フォーム
     * @param multipartFile   画像ファイル
     * @return 日記レスポンスDTO
     */
    public DiaryResponseDto updateDiary(long id, DiaryUpdateForm diaryUpdateForm, MultipartFile multipartFile) {
        DiaryEntity diaryEntity = getDiaryEntityById(id);

        updateIfNotBlank(diaryUpdateForm.title(), diaryEntity::setTitle);
        updateIfNotBlank(diaryUpdateForm.content(), diaryEntity::setContent);

        if (!multipartFile.isEmpty()) {
            saveDiaryImage(diaryEntity, multipartFile);
        }
        return DiaryResponseDto.of(diaryRepository.save(diaryEntity));
    }

    /**
     * 日記エンティティを1件取得
     *
     * @param id ID
     * @return 日記エンティティ
     */
    private DiaryEntity getDiaryEntityById(long id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new DiaryNotFoundException(
                                messageSource.getMessage(
                                        "errors.api.diary.search.id.not.found",
                                        new Object[]{id},
                                        Locale.getDefault()
                                )
                        )
                );
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
        Path diaryImageFilePath = getDiaryImageIdDirPath(id);
        diaryRepository.deleteById(id);
        deleteImageDir(diaryImageFilePath);
    }

    /**
     * ファイルを上位のディレクトリまで再帰的に削除
     *
     * @param filePath ファイルパス
     */
    private void deleteImageDir(Path filePath) {
        try {
            FileSystemUtils.deleteRecursively(filePath);
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
                    ),
                    e);
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
     * 日記画像のファイルパスを取得
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

    /**
     * 日記画像を取得
     *
     * @param id ID
     * @return 日記画像パス
     */
    public Path getImagePathById(long id) {
        String imageFileName = getDiaryEntityById(id).getImagePath();
        if (StringUtils.isBlank(imageFileName)) {
            throw new DiaryNotFoundException(
                    messageSource.getMessage(
                            "errors.api.diary.search.image.not.found",
                            new Object[]{id},
                            Locale.getDefault()
                    )
            );
        }
        return getDiaryImageFilePath(id, imageFileName);
    }

    /**
     * 日記画像をbyte配列で取得
     *
     * @param imageFilePath 画像ファイルパス
     * @return 日記画像のbyte配列
     */
    public byte[] readImageAsBytes(Path imageFilePath, long id) {
        try {
            return Files.readAllBytes(imageFilePath);
        } catch (IOException e) {
            throw new DiaryNotFoundException(
                    messageSource.getMessage(
                            "errors.api.diary.search.image.not.found",
                            new Object[]{id},
                            Locale.getDefault()
                    ),
                    e);
        }
    }

    /**
     * ファイル名からMIMEタイプを取得
     *
     * @param fileName ファイル名
     * @return MIMEタイプ
     */
    public MediaType getMediaType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        switch (extension) {
            case "jpeg", "jpg" -> {
                return MediaType.IMAGE_JPEG;
            }
            case "png" -> {
                return MediaType.IMAGE_PNG;
            }
            case "gif" -> {
                return MediaType.IMAGE_GIF;
            }
            default -> throw new DiaryImageNotSupportedException(
                    messageSource.getMessage(
                            "errors.api.diary.image.extension.not.supported",
                            new Object[]{EXTENSION_LIST},
                            Locale.getDefault()
                    )
            );
        }
    }
}
