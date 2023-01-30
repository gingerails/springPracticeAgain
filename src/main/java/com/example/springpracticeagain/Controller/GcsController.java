package com.example.springpracticeagain.Controller;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import com.example.springpracticeagain.Service.CloudStorageService;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Charsets.UTF_8;

@RestController
public class GcsController {

    CloudStorageService cloudStorageService = new CloudStorageService();
    @Value("gs://abi-buckets/my-file.txt")
    private Resource gcsFile;

    @Value("gs://abi-buckets/Processing/")
    private Resource processingFile;

    @GetMapping("/read")
    public String readGcsFile() throws IOException {
        return StreamUtils.copyToString(
                this.gcsFile.getInputStream(),
                Charset.defaultCharset());
    }

    @GetMapping("/folders")
    public void readIncomingFolder() throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Page<Blob> incomingPage = folder("Incoming");
        Page<Blob> processingPage = folder("Processing");

        incomingPage.iterateAll().forEach(blob -> {
                    System.out.println(blob.getName());
                }
                //  blob.copyTo(p)
                // System.out.println(blob.getName()));
        );
    }

    /**
     * Return folder and its contents
     *
     * @param directoryPrefix
     * @return
     */
    private Page folder(String directoryPrefix) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Page<Blob> blobs =
                storage.list(
                        "abi-buckets",
                        Storage.BlobListOption.prefix(directoryPrefix));
        return blobs;
    }


}