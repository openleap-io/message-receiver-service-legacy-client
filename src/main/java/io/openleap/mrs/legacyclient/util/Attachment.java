package io.openleap.mrs.legacyclient.util;

public record Attachment(String contentType, String url, String name, String base64Data) {
}
