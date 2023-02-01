package com.example.springpracticeagain.Controller;


import java.io.IOException;
import java.nio.charset.Charset;

import com.example.springpracticeagain.Service.CloudStorageService;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GcsController {

    CloudStorageService cloudStorageService = new CloudStorageService();
    @Value("gs://abi-buckets/Incoming/blob1")
    private Resource gcsFile;

    @GetMapping("/read")
    public String readGcsFile() throws IOException {
        return StreamUtils.copyToString(
                this.gcsFile.getInputStream(),
                Charset.defaultCharset());
    }

    @GetMapping("/countIncoming")
    public void readIncomingFolder() {
        Page<Blob> incomingFolderBlobs = cloudStorageService.getFolder("Incoming");
        cloudStorageService.countItemsInFolder(incomingFolderBlobs); // log the number of items in the incoming folder

    }

    /**
     * for testing purposes only
     */
    @GetMapping("/createFiles") // Print Folder Contents
    public void createFiles(){
        cloudStorageService.createDummyFiles();
    }

    @GetMapping("/printFolderContents") // Print Folder Contents
    public void printIncomingFolderContents() {
        Page<Blob> incomingPage = cloudStorageService.getFolder("Incoming");
        incomingPage.iterateAll().forEach(blob -> log.info(blob.getName()));
    }


}