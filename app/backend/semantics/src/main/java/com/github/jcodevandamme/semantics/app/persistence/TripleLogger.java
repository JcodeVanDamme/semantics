package com.github.jcodevandamme.semantics.app.persistence;

import com.github.jcodevandamme.semantics.rdf.model.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class TripleLogger  {

    private final Path journalPath;

    public TripleLogger(
            @Value("${rdf.storage.directory:./data}") String dir,
            @Value("${rdf.storage.journal:journal.log}") String journalName
    ) {
        this.journalPath = Paths.get(dir).resolve(journalName);
    }

    public synchronized void registerUpdate(UpdateType action, Triple t) throws IOException {
        if (journalPath.getParent() != null) {
            Files.createDirectories(journalPath.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(journalPath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            switch (action) {
                case ADD -> writer.write("+");
                case DELETE -> writer.write("-");
            }

            // ⚠️ WICHTIGER FIX für deinen Parser (siehe Erklärung unten):
            writer.write(" ");
            writer.write((String) t.s().value());
            writer.write(",");
            writer.write((String) t.p().value());
            writer.write(",");
            writer.write((String) t.o().value());
            writer.write(",");
            writer.write(String.valueOf(t.o().isLiteral()));
            writer.newLine();
        }
    }
}