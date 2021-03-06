package kr.co.theplay.api.advice;

import io.swagger.models.auth.In;
import kr.co.theplay.service.api.advice.exception.*;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    /**
     * NOT_FOUND 에 대한 공통처리
     */

    @ExceptionHandler(CommonNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult CommonNotFound(HttpServletRequest request, CommonNotFoundException e) {
        return generateFailResult(request, e);
    }

    /**
     * BAD_REQUEST 에 대한 공통처리
     */

    @ExceptionHandler(CommonBadRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult CommonBadRequest(HttpServletRequest request, CommonBadRequestException e) {
        return generateFailResult(request, e);
    }

    /**
     * CONFLICT 에 대한 공통처리
     */

    @ExceptionHandler(CommonConflictException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult CommonConflict(HttpServletRequest request, CommonConflictException e) {
        return generateFailResult(request, e);
    }

    /**
     * API PARAM VALIDATION 에 대한 공통처리
     */

    @ExceptionHandler(ApiParamNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult apiParamNotValid(HttpServletRequest request, ApiParamNotValidException e) {
        return responseService.getSingleParamFailResult(e.getErrors());
    }

    private CommonResult generateFailResult(HttpServletRequest request, CommonRuntimeException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage(e.getMessage() + ".code")),
                getMessage(e.getMessage() + ".msg", e.getDetailMessages())
        );
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
