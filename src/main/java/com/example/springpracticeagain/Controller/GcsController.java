package com.example.springpracticeagain.Controller;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.springpracticeagain.Service.CloudStorageService;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Charsets.UTF_8;
@Slf4j
@RestController
public class GcsController {

    CloudStorageService cloudStorageService = new CloudStorageService();
    @Value("gs://abi-buckets/Incoming/blob1")
    private Resource gcsFile;

    @Value("gs://abi-buckets/Processing/")
    private Resource processingFile;

    @GetMapping("/read")
    public String readGcsFile() throws IOException {
        return StreamUtils.copyToString(
                this.gcsFile.getInputStream(),
                Charset.defaultCharset());
    }

    @GetMapping("/moveToProcessing")
    public void readIncomingFolder() throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        AtomicInteger itemCount = new AtomicInteger(); // Lazy counting the items in the folder
        Page<Blob> incomingBlobs =
                storage.list(
                        "abi-buckets",
                        Storage.BlobListOption.prefix("Incoming"));
        incomingBlobs.iterateAll().forEach(blob -> {
            itemCount.set(itemCount.get() + 1);     // cant just do itemcount++?? bc lambda.  have to use a wrapper
            //log.info(blob.getName());
        });
        log.info("ITEM COUNT: " + itemCount);

    }

    @GetMapping("/createFiles") // Print Folder Contents
    public void createFiles(){
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get("abi-buckets");
        // make a bunch of objects?
        for (int i = 0; i < 40; i++) {
            BlobId blobId = saveString("Incoming/blob" + i, "abcdegdjejsnyebdek", bucket);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
            Blob blob = storage.create(blobInfo,"abcdegdjejsnyebdek".getBytes(UTF_8));
        }
    }

    @GetMapping("/printFolderContents") // Print Folder Contents
    public void printIncomingFolderContents() throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Page<Blob> incomingPage = folder("Incoming");
     //   Page<Blob> processingPage = folder("Processing");

        incomingPage.iterateAll().forEach(blob -> {
            log.info(blob.getName());
        });
    }

    /**
     * Return folder contents (given directory as string). Might not need its own method, we will see.
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


    // For Testing purposes:


    // Save a string to a blob
    private BlobId saveString(String blobName, String value, Bucket bucket) {
        byte[] bytes = value.getBytes(UTF_8);
        Blob blob = bucket.create(blobName, bytes);
        return blob.getBlobId();
    }

    public void addFiles(BlobId blobId, String newString, Storage storage) throws IOException {
        Blob blob = storage.get(blobId);
        if (blob != null) {
            WritableByteChannel channel = blob.writer();
            channel.write(ByteBuffer.wrap(newString.getBytes(UTF_8)));
            channel.close();
        }
    }

}