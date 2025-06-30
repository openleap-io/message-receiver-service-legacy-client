package io.openleap.mrs.legacyclient.util;

import io.openleap.mrs.client.model.*;
import io.openleap.mrs.client.model.Attachment;

import java.util.List;
import java.util.stream.Collectors;

public class MessageDataGenerator {

    public static MessageRequest generateEmailMessageRequest(String subject, String body, List<io.openleap.mrs.legacyclient.util.Attachment> attachments, List<String> recipientIds) {
        EmailChannel channel = new EmailChannel();
        channel.setChannelType((Channel.ChannelTypeEnum.EMAIL));

        Message msg = new CustomMessage();
        msg.setMessageType(Message.MessageTypeEnum.CUSTOM);
        msg.setSubject(subject);
        msg.setBody(body);
        msg.setAttachments(attachments.stream().map(MessageDataGenerator::getAttachment).toList());

        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setRecipients(List.of(getRecipient(channel, recipientIds)));
        messageRequest.setMessage(msg);

        return messageRequest;
    }


    private static io.openleap.mrs.client.model.Attachment getAttachment(io.openleap.mrs.legacyclient.util.Attachment att) {
        io.openleap.mrs.client.model.Attachment attachment = new Attachment();
        attachment.setContentType(att.contentType());
        attachment.setUrl(att.url());
        attachment.setName(att.name());
        attachment.setBase64Data(att.base64Data());
        return attachment;
    }

    private static Recipient getRecipient(EmailChannel channel, List<String> recipientId) {
        Recipient recipient = new Recipient();
        recipient.setChannel(channel);
        recipient.setId(String.join(",", recipientId));
        return recipient;
    }

}
