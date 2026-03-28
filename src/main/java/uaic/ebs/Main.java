package uaic.ebs;

import uaic.ebs.constants.Constants;
import uaic.ebs.generator.GeneratorConfig;
import uaic.ebs.generator.ParallelGenerator;
import uaic.ebs.io.FileWriter;
import uaic.ebs.model.GameStorePublication;
import uaic.ebs.model.GameStoreSubscription;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        GeneratorConfig config = GeneratorConfig.fromFile(Constants.CONFIG_FILE);

        for (int threadCount : config.getThreadCounts()) {
            runBenchmark(config, threadCount);
        }
    }

    private static void runBenchmark(GeneratorConfig config, int threadCount) throws Exception {
        System.out.printf("=== Threads: %d | Publications: %,d | Subscriptions: %,d ===%n",
                threadCount, config.getTotalPublications(), config.getTotalSubscriptions());

        ParallelGenerator generator = new ParallelGenerator(config, threadCount);

        long pubStart = System.nanoTime();
        List<GameStorePublication> publications = generator.generatePublications();
        long pubEnd = System.nanoTime();

        long subStart = System.nanoTime();
        List<GameStoreSubscription> subscriptions = generator.generateSubscriptions();
        long subEnd = System.nanoTime();

        System.out.printf("  Publication generation : %6.2f ms%n", msFrom(pubStart, pubEnd));
        System.out.printf("  Subscription generation: %6.2f ms%n", msFrom(subStart, subEnd));
        System.out.printf("  Total generation time  : %6.2f ms%n", msFrom(pubStart, subEnd));

        String pubFilePath = Constants.PUBLICATIONS_BASE_PATH + threadCount + "_threads_"
                + Constants.PUBLICATIONS_SUFFIX;
        String subFilePath = Constants.SUBSCRIPTIONS_BASE_PATH + threadCount + "_threads_"
                + Constants.SUBSCRIPTIONS_SUFFIX;
        FileWriter.writePublications(publications, pubFilePath);
        FileWriter.writeSubscriptions(subscriptions, subFilePath);

        System.out.printf("  Written to: %s, %s%n%n", pubFilePath, subFilePath);
    }

    private static double msFrom(long startNano, long endNano) {
        return (endNano - startNano) / 1_000_000.0;
    }
}
