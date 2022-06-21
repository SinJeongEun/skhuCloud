package com.skhu.cloud.advice;

import com.skhu.cloud.dto.error.CustomException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = CustomException.class)
    public ModelAndView customException(CustomException e) {
        System.out.println("요것은 CustomException");
        ModelAndView view = new ModelAndView();
        view.setViewName("alert");
        view.addObject("msg","다운로드할 파일을 선택해주세요");
        view.addObject("url","/directories?path=" + e.getPath());

        return view;
    }
}
