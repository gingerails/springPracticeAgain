package com.example.springpracticeagain.Service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Charsets.UTF_8;

@Slf4j
@Service
public class CloudStorageService {

    /**
     * Return folder contents as Page of blobs. (given directory as string).
     */
    public Page getFolder(String directoryPrefix) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Page<Blob> blobs =
                storage.list(
                        "abi-buckets",
                        Storage.BlobListOption.prefix(directoryPrefix));
        return blobs;
    }

    /**
     * Given a Page of blobs, iterate through and log the count.
     * Next steps are to compare num items to the static config numbers
     */
    public void countItemsInFolder(Page<Blob> folder) {
        AtomicInteger itemCount = new AtomicInteger(); // Lazy counting the items in the folder
        folder.iterateAll().forEach(blob -> {
            itemCount.set(itemCount.get() + 1);     // cant just do itemcount++?? bc lambda.  have to use a wrapper
        });
        log.info("ITEM COUNT: " + itemCount);
    }

    /**
     * for testing purposes only.
     * adds 40 items to the incoming folder
     */
    public void createDummyFiles(){
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get("abi-buckets");
        // make a bunch of objects?
        for (int i = 0; i < 40; i++) {
            BlobId blobId = saveString("Incoming/blob" + i, "abcdegdjejsnyebdek", bucket);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
            storage.create(blobInfo,"abcdegdjejsnyebdek".getBytes(UTF_8));
        }
    }

    // Save a string to a blob - for creating dummy text files
    public BlobId saveString(String blobName, String value, Bucket bucket) {
        byte[] bytes = value.getBytes(UTF_8);
        Blob blob = bucket.create(blobName, bytes);
        return blob.getBlobId();
    }

    public void updateBlob(BlobId blobId, String newString, Storage storage) throws IOException {
        Blob blob = storage.get(blobId);
        if (blob != null) {
            WritableByteChannel channel = blob.writer();
            channel.write(ByteBuffer.wrap(newString.getBytes(UTF_8)));
            channel.close();
        }
    }


}
