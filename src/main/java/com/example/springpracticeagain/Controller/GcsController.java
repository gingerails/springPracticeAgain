package com.example.springpracticeagain.Controller;


import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GcsController {

    @Value("gs://abis-test/my-file.txt")
    private Resource gcsFile;

    @GetMapping("/read")
    public String readGcsFile() throws IOException {
        System.out.println("hhhh");
        return StreamUtils.copyToString(
                gcsFile.getInputStream(),
                Charset.defaultCharset());
    }
}