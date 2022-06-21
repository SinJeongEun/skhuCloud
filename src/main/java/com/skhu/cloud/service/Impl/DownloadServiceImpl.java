package com.skhu.cloud.service.Impl;

import com.skhu.cloud.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DownloadServiceImpl implements DownloadService {

    //압축을 진행할 경우에만 return true.
    @Override
    public boolean before(HttpServletResponse httpServletResponse,Queue<String> que_path) {
        if(que_path.size() == 1){ // 단일 파일 다운로드는 압축하지 않는다.
            String path = que_path.peek();
            File file = new File(path);

            if(file.isFile()) {  // que 사이즈 1 &&  파일인 경우
                que_path.clear();
                downloadOne(httpServletResponse,file);
                return false;
            }else {
                // que 사이즈 1 &&  폴더인 경우
                file.delete();
                zipFile(httpServletResponse,que_path); // 압축을 진행한다.
                return true;
            }
        }
        //파일 2개 이상 -> zipFile() 로 압축을 진행한다.
        zipFile(httpServletResponse,que_path);
        return true;
    }

    //단일 파일 다운로드
    @Override
    public void downloadOne(HttpServletResponse httpServletResponse, File file) {
        try(FileInputStream fis = new FileInputStream(file);
            OutputStream out = httpServletResponse.getOutputStream()) {

            int read;
            byte[] buffer = new byte[1024];

            while ((read = fis.read(buffer)) >= 0){
                out.write(buffer,0,read);
            }
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    //다중 파일 및 폴더 압축 진행
    @Override
    public void zipFile(HttpServletResponse httpServletResponse, Queue<String> que_path) {
        //여기서 setHeader 해야 함
        httpServletResponse.setHeader("Content-Disposition","attachment; filename="+
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) +".zip"); // 현재의 시간을 , zipfile name 으로 지정

        try(ZipOutputStream zipOut = new ZipOutputStream(httpServletResponse.getOutputStream())) {
            while (!que_path.isEmpty()) {
                String path = que_path.poll();
                File file = new File(path); // File 객체로 만들어준다.
                String rootAbsolutePath = file.getAbsolutePath(); // 해당 파일의 절대경로를 받는다. /Users/jeunning/myDownloadTest/subFolder
                //파일인 경우
                if (file.isFile()) {
                    addFile(file, zipOut, file.getName());
                    continue;
                }
                // 디렉토리인 경우
                Queue<File> queue = new LinkedList<>();
                queue.add(file); // 디렉토리인 경우에는 queue 에다가 , 해당 디렉토리 를 넣고 진행

                while (!queue.isEmpty()) {
                    File folder = queue.poll();
                    for (File subFile : folder.listFiles()) {
                        // 상대주소로 넘겨 계층구조를 유지하며 압축한다.
                        String subfileAbsolutePath = subFile.getAbsolutePath();  // /Users/jeunning/myDownloadTest/subFolder/sub_sub/sub_sub_sub1.txt
                        String relativePath = subfileAbsolutePath.replace(rootAbsolutePath, file.getName());  // /subFolder/sub_sub/sub_sub_sub1.txt

                        if (subFile.isDirectory()) {
                            queue.add(subFile);
                            addFolder(zipOut, relativePath); // 그래서 여기 relativePath를 주면 addFolder 에서 계층구조 유지하며 압축 진행
                        } else addFile(subFile, zipOut, relativePath); // subFile 이 file 이면 바로 addFile 로 추가해준다.
                    }
                }
            }
            zipOut.closeEntry();
        } catch (IOException e) {
            e.printStackTrace(); // zipOut 도 , close 조금 더 단순화 시킬 수 있을 것 같은데 아쉽다.
        }
    }

    //압축 진행 시, 폴더인 경우 호출된다.
    @Override
    public void addFolder(ZipOutputStream zipOut, String relativePath) {
        relativePath = relativePath.endsWith("/")? relativePath : relativePath + "/";

        try {
            zipOut.putNextEntry(new ZipEntry(relativePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //압축 진행 시, 파일인 경우 호출된다.
    @Override
    public void addFile(File subFile, ZipOutputStream zipOut, String relativePath) {
        try(FileInputStream fis = new FileInputStream(subFile)) {
            zipOut.putNextEntry(new ZipEntry(relativePath));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
