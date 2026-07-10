package com.github.jcodevandamme.semantics.app.persistence;

import com.github.jcodevandamme.semantics.app.services.AppStore;
import com.github.jcodevandamme.semantics.rdf.model.Triple;
import com.github.jcodevandamme.semantics.rdf.provider.ParserTripleProvider;
import com.github.jcodevandamme.semantics.rdf.serialization.TripleStreamSerializer;
import com.github.jcodevandamme.semantics.rdf.tripleStore.TripleStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StorageInitializer {

    private final String dir;
    private final String snapshotFileName;
    private final String updateFileName;
    private final AppStore tripleStore;

    public StorageInitializer(
            AppStore tripleStore,
            @Value("${rdf.storage.directory}") String dir,
            @Value("${rdf.storage.filename.snapshot}") String snapshot,
            @Value("${rdf.storage.filename.update}") String update

    ) {
        this.tripleStore = tripleStore;
        this.dir = dir;
        this.snapshotFileName = snapshot;
        this.updateFileName = update;
    }

    @PostConstruct
    public void initializeStore() throws IOException {
        System.out.println("Initializing Triple Store.");
        initializeSnapshot();
        if (applyUpdates()) {
            generateSnapshot();
        }
        System.out.println("Initialization  finished.");
    }

    private void initializeSnapshot() throws IOException {
        Path storagePath = Paths.get(dir).resolve(snapshotFileName);
        Files.createDirectories(storagePath.getParent());

        if (Files.exists(storagePath)) {
            ParserTripleProvider provider = new ParserTripleProvider(storagePath.toString());
            provider.initTriples(tripleStore);
            System.out.println("Snapshot found, begin Parsing.");

        } else {
            System.out.println("No existing Snapshot found. Starting with an empty TripleStore.");
            Files.createFile(storagePath);
        }
    }

    private boolean applyUpdates() throws IOException {
        System.out.println("Looking for last Session Updates");

        Path updatePath = Paths.get(dir).resolve(updateFileName);
        if (!Files.exists(updatePath)) {
            System.out.println("No Updates found.");
            return false;
        }
        System.out.println("Updates found, begin Update Parsing.");
        try (BufferedReader reader = Files.newBufferedReader(updatePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        }
        System.out.println("Update Parsing finished.");
        Files.deleteIfExists(updatePath);
        return true;
    }

    private void processLine(String line) {
        char operator = line.charAt(0);
        String triple = line.substring(2);
        Triple t = LogParser.parseTriple(triple);

        System.out.println("Read Triple: " + t);

        if (operation(operator) == UpdateType.ADD) {
            tripleStore.create(t);
        } else if (operation(operator) == UpdateType.DELETE) {
            tripleStore.delete(t);
        } else {
            System.err.println("Encountered Faulty Line during Update Parsing:\n" + line);
        }
    }

    private UpdateType operation(char c) {
        return switch (c) {
            case '+' -> UpdateType.ADD;
            case '-' -> UpdateType.DELETE;
            default -> UpdateType.ERROR;
        };
    }

    private void generateSnapshot() throws IOException {
        System.out.println("Generating new Snapshot.");
        Path snapshotPath = Paths.get(dir).resolve(snapshotFileName);
        TripleStreamSerializer.serialize(snapshotPath.toString(), tripleStore);
        System.out.println("New Snapshot generated.");
    }
}
