package uaic.ebs.io;

import uaic.ebs.model.GameStorePublication;
import uaic.ebs.model.GameStoreSubscription;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileWriter {

    public static void writePublications(List<GameStorePublication> publications, String filePath) throws IOException {
        Path path = Path.of(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (GameStorePublication pub : publications) {
                writer.write(pub.toString());
                writer.newLine();
            }
        }
    }

    public static void writeSubscriptions(List<GameStoreSubscription> subscriptions, String filePath)
            throws IOException {
        Path path = Path.of(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (GameStoreSubscription sub : subscriptions) {
                writer.write(sub.toString());
                writer.newLine();
            }
        }
    }
}
