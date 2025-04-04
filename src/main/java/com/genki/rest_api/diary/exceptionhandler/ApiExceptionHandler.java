package com.genki.rest_api.diary.exceptionhandler;

import com.genki.rest_api.diary.dto.ApiDetailErrorResponseDto;
import com.genki.rest_api.diary.dto.ApiErrorResponseDto;
import com.genki.rest_api.diary.exception.DiaryIOException;
import com.genki.rest_api.diary.exception.DiaryImageNotSupportedException;
import com.genki.rest_api.diary.exception.DiaryNotFoundException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;
import java.util.Optional;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    /**
     * 独自エラーレスポンス
     *
     * @param ex         例外エラー
     * @param body       レスポンスボディ
     * @param headers    レスポンスヘッダ
     * @param statusCode ステータスコード
     * @param request    リクエスト
     * @return レスポンスエンティティ
     */
    public ResponseEntity<Object> createErrorResponse(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        if (statusCode.is5xxServerError()) {
            log.error(ex.getMessage(), ex);
        } else {
            log.warn(ex.getMessage(), ex);
        }
        return createResponseEntity(body, headers, statusCode, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        ApiDetailErrorResponseDto apiDetailErrorResponseDto = new ApiDetailErrorResponseDto();
        Optional.ofNullable(bindingResult.getFieldError("title"))
                .ifPresent(fieldError -> apiDetailErrorResponseDto.setTitle(fieldError.getDefaultMessage()));
        Optional.ofNullable(bindingResult.getFieldError("content"))
                .ifPresent(fieldError -> apiDetailErrorResponseDto.setContent(fieldError.getDefaultMessage()));
        String errorMessage = messageSource.getMessage(
                "errors.general",
                null,
                Locale.getDefault()
        );
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(errorMessage, apiDetailErrorResponseDto);
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleErrorResponseException(
            ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(
                messageSource.getMessage(
                        "errors.api.diary.image.file.size",
                        null,
                        Locale.getDefault()
                )
        );
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleConversionNotSupported(
            ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponseDto apiErrorResponseDto = new ApiErrorResponseDto(ex.getMessage());
        return createErrorResponse(ex, apiErrorResponseDto, headers, status, request);
    }

    /**
     * 日記が見つからない例外エラーハンドラ
     *
     * @param ex 例外エラー
     * @return APIエラーレスポンスDTO
     */
    @ExceptionHandler(DiaryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponseDto handleDiaryNotFoundException(DiaryNotFoundException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiErrorResponseDto(ex.getMessage());
    }

    /**
     * 日記画像の形式例外エラーハンドラ
     *
     * @param ex 例外エラー
     * @return APIエラーレスポンスDTO
     */
    @ExceptionHandler(DiaryImageNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiErrorResponseDto handleDiaryImageNotSupportedException(DiaryImageNotSupportedException ex) {
        log.warn(ex.getMessage(), ex);
        return new ApiErrorResponseDto(ex.getMessage());
    }

    /**
     * 日記IO系例外エラーハンドラ
     *
     * @param ex 例外エラー
     * @return APIエラーレスポンスDTO
     */
    @ExceptionHandler(DiaryIOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponseDto handleDiaryIOException(DiaryIOException ex) {
        log.error(ex.getMessage(), ex);
        return new ApiErrorResponseDto(ex.getMessage());
    }
}
