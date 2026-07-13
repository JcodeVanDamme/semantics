package com.github.jcodevandamme.semantics.app.services.domain.logger;

import com.github.jcodevandamme.semantics.app.dto.DTOFactory;
import com.github.jcodevandamme.semantics.app.dto.util.HistoryDto;
import com.github.jcodevandamme.semantics.app.dto.util.TripleActionDto;
import org.apache.jena.atlas.io.IO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class HistoryLogger {

    private final Path historyPath;
    private final ObjectMapper mapper;

    public HistoryLogger(
            @Value("${domain.storage.directory}") String dir,
            @Value("${domain.storage.filename.history}") String historyName
    ) {
        this.historyPath = Paths.get(dir).resolve(historyName);
        this.mapper = new ObjectMapper();
    }

    public synchronized void logDomainAction(HistoryDto action) throws IOException {
        Path parentDir = historyPath.getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        String json = mapper.writeValueAsString(action);
        Files.writeString(historyPath, json + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public HistoryDto[] flushHistory() {
        if (Files.notExists(historyPath)) {
            return new HistoryDto[0];
        }

        try (Stream<String> lines = Files.lines(historyPath)) {
            return lines
                    .map(line -> mapper.readValue(line, HistoryDto.class))
                    .filter(Objects::nonNull)
                    .toArray(HistoryDto[]::new);

        } catch (IOException e) {
            System.err.println("Parsing of Domain History failed.");
            return new HistoryDto[0];
        }
    }
}
