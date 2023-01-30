package com.example.springpracticeagain.Service;

import com.example.springpracticeagain.Controller.GcsController;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static com.google.common.base.Charsets.UTF_8;

@Service
public class CloudStorageService {

    @Autowired
    GcsController gcsController;

    public void getIncomingFolderContents() throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of("abi-buckets", "Incoming");
        Blob blob = storage.get(blobId);
        if (blob != null) {
            byte[] prevContent = blob.getContent();
            System.out.println(new String(prevContent, UTF_8));
            WritableByteChannel channel = blob.writer();
            channel.write(ByteBuffer.wrap("Updated content".getBytes(UTF_8)));
            channel.close();
        }

    }


}
