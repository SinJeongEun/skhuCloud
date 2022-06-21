package com.skhu.cloud.controller;

import com.skhu.cloud.dto.error.CustomException;
import com.skhu.cloud.service.Impl.DownloadServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/download")
public class DownloadController {

    private final DownloadServiceImpl downloadService;

    @GetMapping("/checked")
    public void downloadChecked(@RequestParam String nowPath, @RequestParam(value = "checkedFiles", required = false)List<String> checkedFiles, HttpServletResponse httpServletResponse) throws CustomException  {
        Queue<String> que_checked = new LinkedList<>();
        if(checkedFiles == null || checkedFiles.size() == 0) {
            throw new CustomException(nowPath);
        }
        for(String s : checkedFiles) {
            que_checked.add(s);
        }

        //압축을 진행 한 경우만 httpServletResponse 값을 세팅한다.
        boolean toZip = downloadService.before(httpServletResponse,que_checked);
        if(toZip) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.setContentType("application/zip");
        }
    }

}
