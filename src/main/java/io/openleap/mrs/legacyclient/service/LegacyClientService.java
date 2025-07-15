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
import java.util.regex.Pattern;

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

            if (!isReceiptOverriden && Pattern.compile("^An\\s*:\\s*").matcher(line).find()) {
                recipients.add(line.split("^An\\s*:")[1]);
            }
            if (!isReceiptOverriden && Pattern.compile("^CC\\s*:\\s*").matcher(line).find()) {
                cc.add(line.split("^CC\\s*:")[1]);
            }
            if (!isReceiptOverriden && Pattern.compile("^BCC\\s*:\\s*").matcher(line).find()) {
                bcc.add(line.split("^BCC\\s*:")[1]);
            }
            if (Pattern.compile("^Betreff\\s*:\\s*").matcher(line).find()) {
                subject.append(line.split("^Betreff\\s*:")[1].trim());
            }
            if (Pattern.compile("^Text\\s*:\\s*").matcher(line).find()) {
                text.append(line.split("^Text\\s*:").length > 0 ? line.split("^Text\\s*:")[1] : "");
                text.append("<br/>");
            }
            if (Pattern.compile("^Anlage\\s*:\\s*").matcher(line).find()) {
                var pathToAttachment = line.split("^Anlage\\s*:")[1].trim();

                handleAttachments(pathToAttachment, text, attachments);


            }
            if (Pattern.compile("^AnlageD\\s*:\\s*").matcher(line).find()) {
                var pathToAttachment = line.split("^AnlageD\\s*:")[1].trim();


                handleAttachments(pathToAttachment, text, attachments);
            }
            if (Pattern.compile("^ProgName\\s*:\\s*").matcher(line).find()) {
                text.append("<br/>");
                text.append("\nProgName: ");
                text.append(line.split("^ProgName\\s*:")[1].trim());
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
