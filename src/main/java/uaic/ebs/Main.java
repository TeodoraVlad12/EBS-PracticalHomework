package uaic.ebs;

import uaic.ebs.generator.GeneratorConfig;
import uaic.ebs.generator.ParallelGenerator;
import uaic.ebs.io.FileWriter;
import uaic.ebs.model.GameStorePublication;
import uaic.ebs.model.GameStoreSubscription;

import java.util.List;

public class Main {

    private static final String GAME_PUBLICATIONS_BASE_FILE_PATH = "generated/publications/";
    private static final String GAME_SUBSCRIPTIONS_BASE_FILE_PATH = "generated/subscriptions/";

    private static final String GAME_PUBLICATIONS_FILE_SUFFIX = "game_store_publications.txt";
    private static final String GAME_SUBSCRIPTIONS_FILE_SUFFIX = "game_store_subscriptions.txt";

    public static void main(String[] args) throws Exception {
        int[] threadCounts = { 1, 2, 4 };

        for (int threads : threadCounts) {
            runBenchmark(threads);
        }
    }

    private static void runBenchmark(int threadCount) throws Exception {
        GeneratorConfig config = GeneratorConfig.defaultConfig().toBuilder()
                .threadCount(threadCount)
                .build();

        System.out.printf("=== Threads: %d | Publications: %,d | Subscriptions: %,d ===%n",
                threadCount, config.getTotalPublications(), config.getTotalSubscriptions());

        ParallelGenerator generator = new ParallelGenerator(config);

        long pubStart = System.nanoTime();
        List<GameStorePublication> publications = generator.generatePublications();
        long pubEnd = System.nanoTime();

        long subStart = System.nanoTime();
        List<GameStoreSubscription> subscriptions = generator.generateSubscriptions();
        long subEnd = System.nanoTime();

        long totalStart = pubStart;
        long totalEnd = subEnd;

        System.out.printf("  Publication generation : %6.2f ms%n", msFrom(pubStart, pubEnd));
        System.out.printf("  Subscription generation: %6.2f ms%n", msFrom(subStart, subEnd));
        System.out.printf("  Total generation time  : %6.2f ms%n", msFrom(totalStart, totalEnd));

        String pubFilePath = GAME_PUBLICATIONS_BASE_FILE_PATH + threadCount + "_threads_"
                + GAME_PUBLICATIONS_FILE_SUFFIX;
        String subFilePath = GAME_SUBSCRIPTIONS_BASE_FILE_PATH + threadCount + "_threads_"
                + GAME_SUBSCRIPTIONS_FILE_SUFFIX;
        FileWriter.writePublications(publications, pubFilePath);
        FileWriter.writeSubscriptions(subscriptions, subFilePath);

        System.out.printf("  Written to: %s, %s%n%n", pubFilePath, subFilePath);
    }

    private static double msFrom(long startNano, long endNano) {
        return (endNano - startNano) / 1_000_000.0;
    }
}
