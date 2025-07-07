package io.openleap.mrs.legacyclient.service;

import io.openleap.mrs.client.model.MessageRequest;
import io.openleap.mrs.legacyclient.util.MessageDataGenerator;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

@Service
public class LegacyClientService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(LegacyClientService.class);

    public MessageRequest generateMessageRequest(String cfg) throws IOException {
        boolean isReceiptOverriden = false;
        var recipients = new ArrayList<String>();
        var cc = new ArrayList<String>();
        var bcc = new ArrayList<String>();
        StringBuilder subject = new StringBuilder();
        StringBuilder text = new StringBuilder();
        var attachments = new ArrayList<File>();

        String receiptOverride = System.getenv("MAILSEND");
        if (receiptOverride != null && !receiptOverride.isEmpty()) {
            logger.info("MAILSEND environment variable is set, overriding receipt configuration.");
            isReceiptOverriden = true;
            recipients.addAll(Arrays.stream(receiptOverride.split("[;,:]+")).toList());
        }

        var lines = cfg.split("\\R");
        for (var line : lines) {

            line = line.trim().replace(" ", "");

            if (!isReceiptOverriden && line.startsWith("An:")) {
                recipients.add(line.split("An:")[1]);
            }
            if (!isReceiptOverriden && line.startsWith("CC:")) {
                cc.add(line.split("CC:")[1]);
            }
            if (!isReceiptOverriden && line.startsWith("BCC:")) {
                bcc.add(line.split("BCC:")[1]);
            }
            if (line.startsWith("Betreff:")) {
                subject.append(line.split("Betreff:")[1]);
            }
            if (line.startsWith("Text:")) {
                text.append(line.split("Text:").length > 0 ? line.split("Text:")[1] : "");
                text.append("<br/>");
            }
            if (line.startsWith("Anlage:")) {
                var pathToAttachment = line.split("Anlage:")[1].trim();

                handleAttachments(pathToAttachment, text, attachments);


            }
            if (line.startsWith("AnlageD:")) {
                var pathToAttachment = line.split("AnlageD:")[1].trim();


                handleAttachments(pathToAttachment, text, attachments);
            }
            if (line.startsWith("ProgName:")) {
                text.append("\nProgName: ");
                text.append(line.split("ProgName:")[1]);
            }
        }

        return MessageDataGenerator.generateEmailMessageRequest(
                subject.toString(),
                text.toString(),
                attachments.stream().map(file -> {
                    try {
                        return new io.openleap.mrs.legacyclient.util.Attachment(
                                "application/octet-stream",
                                file.toURI().toString(),
                                file.getName(),
                                Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()))
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList(),
                recipients

        );
    }

    private static void handleAttachments(String pathToAttachment, StringBuilder text, ArrayList<File> attachments) throws IOException {
        if (pathToAttachment.contains("*")) {
            String normalizedPath = pathToAttachment.replace("\\", "/");
            int lastSlash = normalizedPath.lastIndexOf('/');

            String dirPath = normalizedPath.substring(0, lastSlash);
            String pattern = normalizedPath.substring(lastSlash + 1);

            Path dir = Paths.get(dirPath);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
                for (Path entry : stream) {
                    File file = entry.toFile();
                    if (file.exists()) {
                        text.append("<br/>");
                        text.append(file.getAbsolutePath());
                        attachments.add(file);
                    }
                }
            }
        } else {
            text.append("<br/>");
            text.append(pathToAttachment);
            var attachment = new File(pathToAttachment);
            if (attachment.exists()) {
                attachments.add(attachment);
            }
        }
    }
}
