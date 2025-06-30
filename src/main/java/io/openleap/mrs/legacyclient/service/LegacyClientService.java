package io.openleap.mrs.legacyclient.service;

import io.openleap.mrs.client.model.MessageRequest;
import io.openleap.mrs.legacyclient.util.MessageDataGenerator;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class LegacyClientService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(LegacyClientService.class);

    public MessageRequest generateMessageRequest(String cfg) {
        var recipients = new ArrayList<String>();
        var cc = new ArrayList<String>();
        var bcc = new ArrayList<String>();
        StringBuilder subject = new StringBuilder();
        StringBuilder text = new StringBuilder();
        var attachments = new ArrayList<File>();


        var lines = cfg.split("\\R");
        for (var line : lines) {

            line = line.trim().replace(" ", "");

            if (line.startsWith("An:")) {
                recipients.add(line.split("An:")[1]);
            }
            if (line.startsWith("CC:")) {
                cc.add(line.split("CC:")[1]);
            }
            if (line.startsWith("BCC:")) {
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
                text.append("<br/>");
                text.append(pathToAttachment);
                var attachment = new File(pathToAttachment);
                if (attachment.exists()) {
                    attachments.add(attachment);
                }
            }
            if (line.startsWith("AnlageD:")) {
                var pathToAttachment = line.split("AnlageD:")[1].trim();
                text.append("<br/>");
                text.append(pathToAttachment);
                var attachment = new File(pathToAttachment);
                if (attachment.exists()) {
                    attachments.add(attachment);
                }
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
}
