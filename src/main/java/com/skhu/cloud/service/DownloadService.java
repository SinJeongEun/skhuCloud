package com.skhu.cloud.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public interface DownloadService {
    //압축의 필요 유무를 리턴하여 true인 경우에만  httpServletResponse.setContentType() 진행한다.
    boolean before(HttpServletResponse httpServletResponse, Queue<String> que_path);

    //단일파일을 다운로드하는 함수.
    void downloadOne(HttpServletResponse httpServletResponse, File file);

    //que_path가 2개이상인 경우에만 호출되어 압축을 진행한다.
    void zipFile(HttpServletResponse httpServletResponse, Queue<String> que_path);

    //압축 진행 시 폴더인 경우만 호출되어 zipOut에 해당 폴더를 추가한다.
    void addFolder(ZipOutputStream zipOut, String relativePath) ;

    //압축 진행 시 팡리인 경우만 호출되어 zipOut에 해당 파일을 write 한다.
    void addFile(File subFile, ZipOutputStream zipOut, String relativePath);

}
