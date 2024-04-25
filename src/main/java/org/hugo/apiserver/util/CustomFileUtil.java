package org.hugo.apiserver.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${org.hugo.upload.path}") // lombok께 아니고 스프링꺼를 써야함!
    private String updatePath;

    @PostConstruct // 생성자대신에 많이쓴다. 뭔가 초기화시켜야할 때 쓴다고 함.
    public void init() {
        File tempFolder = new File(updatePath);

        if (!tempFolder.exists()) {
            boolean created = tempFolder.mkdir();
            if (created) {
                log.info("Directory created successfully: " + updatePath);
            } else {
                log.error("Failed to create directory: " + updatePath);
                // 예외 또는 적절한 처리
            }
        } else {
            log.info("Directory already exists: " + updatePath);
        }

        updatePath = tempFolder.getAbsolutePath();

        log.info("-------------------------------");
        log.info(updatePath);
    }

    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
        if (files == null || files.isEmpty()) {
            return null;
        }

        ArrayList<String> uploadNames = new ArrayList<>();

        for (MultipartFile file : files) {
            String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename(); //uuid+원래파일명
            Path savePath = Paths.get(updatePath, savedName); // 경로 생성

            try {
                Files.copy(file.getInputStream(), savePath); // 카피를 이용해 저장(예외처리가 필요함)
                uploadNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return uploadNames;
    }
}
