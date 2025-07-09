package io.openleap.mrs.legacyclient.service;

import io.openleap.mrs.client.model.CustomMessage;
import io.openleap.mrs.client.model.Message;
import io.openleap.mrs.client.model.MessageRequest;
import io.openleap.mrs.legacyclient.runner.SeederServiceRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class LegacyClientServiceTest {

    @Autowired
    private LegacyClientService legacyClientService;

    @Test
    void testValidEPDTAFileWithNoAttachments() throws IOException {
        MessageRequest messageRequest = legacyClientService.generateMessageRequest(SeederServiceRunner.getFileContent("src/test/resources/EPDTA1.Cfg"));
        Assertions.assertEquals(1, messageRequest.getRecipients().size());

        CustomMessage customMessage = (CustomMessage) messageRequest.getMessage();
        Assertions.assertNotNull(customMessage);
        Assertions.assertEquals(Message.MessageTypeEnum.CUSTOM, customMessage.getMessageType());
        Assertions.assertEquals("SicherungwurdenachN:\\Data\\Band\\SEPAkopiert!<br/>\n" +
                "ProgName: EPDTA", customMessage.getBody());
        Assertions.assertEquals("COTID:12370-20059:DatentraegerBegleitzettelLastschriften", customMessage.getSubject());
        Assertions.assertTrue(customMessage.getAttachments().isEmpty());
    }

    @Test
    void testValidEPDTAFileWithMultipleAttachments() throws IOException {
        MessageRequest messageRequest = legacyClientService.generateMessageRequest(SeederServiceRunner.getFileContent("src/test/resources/EPDTA2.Cfg"));
        Assertions.assertEquals(1, messageRequest.getRecipients().size());

        CustomMessage customMessage = (CustomMessage) messageRequest.getMessage();
        Assertions.assertNotNull(customMessage);
        Assertions.assertEquals(Message.MessageTypeEnum.CUSTOM, customMessage.getMessageType());
        Assertions.assertEquals("<br/>src/test/resources/EA1.Cfg<br/>src/test/resources/EA2.CfgSicherungwurdenachN:\\Data\\Band\\SEPAkopiert!<br/>\n" +
                "ProgName: EPDTA", customMessage.getBody());
        Assertions.assertEquals("COTID:12370-20059:DatentraegerBegleitzettelLastschriften", customMessage.getSubject());
        Assertions.assertFalse(customMessage.getAttachments().isEmpty());
        Assertions.assertEquals(2, customMessage.getAttachments().size());
    }

    @Test
    void testValidEPDTAFileWithMultipleAttachmentsUsingWildcard() throws IOException {
        MessageRequest messageRequest = legacyClientService.generateMessageRequest(SeederServiceRunner.getFileContent("src/test/resources/EPDTA3.Cfg"));
        Assertions.assertEquals(1, messageRequest.getRecipients().size());

        CustomMessage customMessage = (CustomMessage) messageRequest.getMessage();
        Assertions.assertNotNull(customMessage);
        Assertions.assertEquals(Message.MessageTypeEnum.CUSTOM, customMessage.getMessageType());
        Assertions.assertEquals("COTID:12370-20059:DatentraegerBegleitzettelLastschriften", customMessage.getSubject());
        Assertions.assertFalse(customMessage.getAttachments().isEmpty());
        Assertions.assertEquals(2, customMessage.getAttachments().size());
    }

    @Test
    void testValidEPBCUFile() throws IOException {
        MessageRequest messageRequest = legacyClientService.generateMessageRequest(SeederServiceRunner.getFileContent("src/test/resources/EPBCU48_828be0ee-ace5-43c4-8ef1-15fa5071e03d.Cfg"));
        Assertions.assertEquals(1, messageRequest.getRecipients().size());

        CustomMessage customMessage = (CustomMessage) messageRequest.getMessage();
        Assertions.assertNotNull(customMessage);
        Assertions.assertEquals(Message.MessageTypeEnum.CUSTOM, customMessage.getMessageType());
        Assertions.assertEquals("COTID:906-3414/08331203035-Achtung!Leistungfï¿½rarchiviertenFahrschï¿½ler!", customMessage.getSubject());
        Assertions.assertEquals("FolgendeLeistung(en)wurde(n)fï¿½reinenarchiviertenFahrschï¿½lerï¿½bermittelt.<br/>DieLeistung(en)wird/werdennichtabgerechnetundsindnichtaufderFehlerliste!<br/>DieLeitung(en)mussvonderFahrschuleinderVerwaltungssoftwaregelï¿½schtwerden.<br/>SolltedieserFahrschï¿½lerwiederaktivsein,mussdieFahrschuleeinenneuen<br/>AusbildungsvertragmiteinerneuenKundennummerabschlieï¿½en.<br/>Anschlieï¿½endmussdieLeistungunterderneuenKundenummererfasstwerden.<br/><br/>Leistung:<br/>L-Datum:10.05.2024<br/>Kezi:NOP<br/><br/>Menge:000<br/>Zeit-Ab:0800<br/>Zeit-An:0845<br/>E-Preis:80,00<br/>G-Preis:80,00<br/>GUID:6dcc48d3-bca4-43bd-9b90-dbab4c181a86<br/><br/>Leistung:<br/>L-Datum:10.05.2024<br/>Kezi:NOP<br/><br/>Menge:000<br/>Zeit-Ab:0845<br/>Zeit-An:1015<br/>E-Preis:90,00<br/>G-Preis:180,00<br/>GUID:9012b87f-7117-464c-ad87-4789b0e7d88d<br/><br/>Leistung:<br/>L-Datum:10.05.2024<br/>Kezi:NOP<br/><br/>Menge:000<br/>Zeit-Ab:1015<br/>Zeit-An:1100<br/>E-Preis:90,00<br/>G-Preis:90,00<br/>GUID:4c15fcdc-f372-4664-819f-5df08998ef1b<br/><br/>Leistung:<br/>L-Datum:18.07.2024<br/>Kezi:NOP<br/><br/>Menge:000<br/>Zeit-Ab:2015<br/>Zeit-An:2100<br/>E-Preis:90,00<br/>G-Preis:90,00<br/>GUID:06fd9781-c50b-438f-96a0-35116045768a<br/><br/>Leistung:<br/>L-Datum:18.07.2024<br/>Kezi:NOP<br/><br/>Menge:000<br/>Zeit-Ab:2100<br/>Zeit-An:2145<br/>E-Preis:90,00<br/>G-Preis:90,00<br/>GUID:7cce2f11-5050-43d8-8ac3-4a4aa3ee3f39<br/><br/>Leistung:<br/>L-Datum:19.07.2024<br/>Kezi:NOP<br/><br/>Menge:010<br/>Zeit-Ab:<br/>Zeit-An:<br/>E-Preis:160,00<br/>G-Preis:160,00<br/>GUID:5da280d7-2021-42ec-997c-47a273adeb8c<br/><br/><br/>\n" +
                "ProgName: EPBCPB3", customMessage.getBody());
        Assertions.assertTrue(customMessage.getAttachments().isEmpty());
    }
}
