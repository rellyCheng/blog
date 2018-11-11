package com.relly.blog.common.exception;

import com.relly.blog.common.model.JsonResult;
import com.relly.blog.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * @author Kartist 2018/8/15 10:50
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * 当spring发现系统出现异常了,且异常的
     * 类型为ServiceException类型,此时就会
     * 回调此方法,并将异常值传递给这个方法,
     * 这时我们就可以在此方法中对业务异常进行
     * 统一处理,例如封装到jsonResult,然后
     * 写到客户端告诉用户.
     */
    @ExceptionHandler({ServiceException.class})
    public Object handleServiceException(ServiceException e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getCauseStr(), e.getCauseObj());
        if (e.getErrorCode() == null) {
            return handResult(request, response, e.getMessage());
        }
        return handResult(request, response, e.getMessage(), e.getErrorCode());
    }

    @ExceptionHandler({ControllerException.class})
    public Object handleControllerException(ControllerException e, HttpServletRequest request, HttpServletResponse response) {
        return handResult(request, response, e.getMessage(), e.getErrorCode());

    }

    @ExceptionHandler({ConstraintViolationException.class})
    public Object handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response) {
        String message = e.getMessage();
        log.error(message);
        return handResult(request, response, message);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        String message = e.getMessage();
        log.error(message);
        return handResult(request, response, message);
    }

    @ExceptionHandler(BindException.class)
    public Object handleServiceException(BindException e, HttpServletRequest request, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        e.getFieldErrors().forEach((fieldError -> builder.append(fieldError.getField()).append(fieldError.getDefaultMessage()).append(",")));
        String message = builder.toString();
        return handResult(request, response, message);
    }

    @ExceptionHandler(value = Exception.class)
    public Object allExceptionHandler(Exception exception, HttpServletRequest request, HttpServletResponse response) {
         log.error("error :", exception);
        if (HttpUtil.isAjaxRequest(request)) {
            HttpUtil.responseUseJsonType(response, new JsonResult(exception));
            return null;
        }
        return getModelAndView(exception.getMessage() + "  " + exception.toString());
    }

    private Object handResult(HttpServletRequest request, HttpServletResponse response, String message) {
        return handResult(request, response, message, JsonResult.ERROR);
    }

    private Object handResult(HttpServletRequest request, HttpServletResponse response, String message, String state) {
        if (HttpUtil.isAjaxRequest(request)) {
            JsonResult jsonResult = new JsonResult();
            jsonResult.setMessage(message);
            jsonResult.setState(state);
            HttpUtil.responseUseJsonType(response, jsonResult);
            return null;
        }
        return getModelAndView(message);
    }

    /**
     * 通用方法,在 modelAndView 中设置值
     *
     * @param attributeValue 需要返回的信息数据
     * @return modelAndView
     */
    private ModelAndView getModelAndView(Object attributeValue) {
        log.info("返回到前端的 错误信息:" + attributeValue);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", attributeValue);
        return modelAndView;
    }


}
