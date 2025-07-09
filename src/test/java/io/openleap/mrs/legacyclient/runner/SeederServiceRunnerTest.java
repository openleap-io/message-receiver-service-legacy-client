package io.openleap.mrs.legacyclient.runner;

import io.openleap.mrs.client.ApiClient;
import io.openleap.mrs.legacyclient.service.LegacyClientService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@SpringBootTest
public class SeederServiceRunnerTest {
    @Mock
    private ApiClient apiClient;

    @Mock
    private LegacyClientService legacyClientService;

    @Mock
    Logger logger;

    @InjectMocks
    private SeederServiceRunner seederServiceRunner;

    @Test
    void runLogsErrorWhenNoArgumentsProvided() throws NoSuchFieldException, IllegalAccessException {
        String[] args = {};

        Field loggerField = SeederServiceRunner.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(seederServiceRunner, logger);

        seederServiceRunner.run(args);
        verify(logger).error("No input file provided. Please specify a file path as an argument.");
    }

    @Test
    void runCallsSeedDataWithFirstArgument() {
        String[] args = {"inputFile.txt"};
        SeederServiceRunner spyRunner = spy(seederServiceRunner);
        doNothing().when(spyRunner).seedData(anyString());
        spyRunner.run(args);
        verify(spyRunner).seedData("inputFile.txt");
    }

    @Test
    void seedDataLogsErrorWhenFileNotFound() throws NoSuchFieldException, IllegalAccessException {
        String invalidFilePath = "nonexistentFile.txt";

        Field loggerField = SeederServiceRunner.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(seederServiceRunner, logger);

        seederServiceRunner.seedData(invalidFilePath);

        verify(logger).error(contains("Error reading file:"));
    }
}
