package io.openleap.mrs.legacyclient.runner;

import io.openleap.mrs.client.ApiClient;
import io.openleap.mrs.client.api.MessageApiApi;
import io.openleap.mrs.client.model.MessageRequest;
import io.openleap.mrs.legacyclient.service.LegacyClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class SeederServiceRunner implements CommandLineRunner {
    static Logger logger = LogManager.getLogger(SeederServiceRunner.class);
    ApiClient apiClient;
    private final LegacyClientService legacyClientService;

    public SeederServiceRunner(ApiClient apiClient, LegacyClientService legacyClientService) {
        this.apiClient = apiClient;
        this.legacyClientService = legacyClientService;
    }


    public void seedData(String filePath) {
        String content = getFileContent(filePath);
        if (content == null) return;


        MessageApiApi message = new MessageApiApi(apiClient);

        MessageRequest messageRequest = null;
        try {
            messageRequest = legacyClientService.generateMessageRequest(content);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        message.sendMessage(messageRequest);

    }

    public static String getFileContent(String filePath) {
        String content = "";
        File file = new File(filePath);
        try {
            // Read file content into a String
            content = Files.readString(file.toPath(), java.nio.charset.StandardCharsets.ISO_8859_1);
            System.out.println("File content:\n" + content);
        } catch (Exception e) {
            logger.error("Error reading file: " + e.getMessage() + e.getCause());
            return null;
        }
        return content;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            logger.error("No input file provided. Please specify a file path as an argument.");
            return;
        }
        seedData(String.valueOf(args[0]));
    }
}
