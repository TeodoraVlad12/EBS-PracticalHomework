package uaic.ebs.generator;

import uaic.ebs.model.GameStorePublication;
import uaic.ebs.model.GameStoreSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ParallelGenerator {

    private final GeneratorConfig config;
    private final int threadCount;

    public ParallelGenerator(GeneratorConfig config, int threadCount) {
        this.config = config;
        this.threadCount = threadCount;
    }

    public List<GameStorePublication> generatePublications() throws InterruptedException, ExecutionException {
        int total = config.getTotalPublications();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<List<GameStorePublication>>> futures = new ArrayList<>();
        int[] chunks = splitIntoChunks(total, threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int chunkSize = chunks[t];
            final long seed = System.nanoTime() + t;
            futures.add(executor.submit(() -> {
                GameStorePublicationGenerator gen = new GameStorePublicationGenerator(config, seed);
                return gen.generate(chunkSize);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        List<GameStorePublication> result = new ArrayList<>(total);
        for (Future<List<GameStorePublication>> future : futures) {
            result.addAll(future.get());
        }
        return result;
    }

    public List<GameStoreSubscription> generateSubscriptions() throws InterruptedException, ExecutionException {
        int total = config.getTotalSubscriptions();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<List<GameStoreSubscription>>> futures = new ArrayList<>();
        int[] chunks = splitIntoChunks(total, threadCount);

        Map<String, int[]> fieldCountsPerThread = computeFieldCountsPerThread(total);
        Map<String, int[]> equalityCountsPerThread = computeEqualityCountsPerThread(total);

        for (int t = 0; t < threadCount; t++) {
            final int chunkSize = chunks[t];
            final long seed = System.nanoTime() + t * 1000L;
            final int threadIndex = t;

            Map<String, Integer> threadFieldCounts = extractThreadSlice(fieldCountsPerThread, threadIndex);
            Map<String, Integer> threadEqualityCounts = extractThreadSlice(equalityCountsPerThread, threadIndex);

            futures.add(executor.submit(() -> {
                GameStoreSubscriptionGenerator gen = new GameStoreSubscriptionGenerator(config, seed);
                return gen.generate(chunkSize, threadFieldCounts, threadEqualityCounts);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        List<GameStoreSubscription> result = new ArrayList<>(total);
        for (Future<List<GameStoreSubscription>> future : futures) {
            result.addAll(future.get());
        }
        return result;
    }

    private Map<String, int[]> computeFieldCountsPerThread(int total) {
        Map<String, int[]> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : config.getFieldFrequencies().entrySet()) {
            int globalCount = (int) Math.round(entry.getValue() * total);
            result.put(entry.getKey(), splitIntoChunks(globalCount, threadCount));
        }
        return result;
    }

    private Map<String, int[]> computeEqualityCountsPerThread(int total) {
        Map<String, int[]> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : config.getEqualityFrequencies().entrySet()) {
            String field = entry.getKey();
            double fieldFreq = config.getFieldFrequencies().getOrDefault(field, 0.0);
            int globalFieldCount = (int) Math.round(fieldFreq * total);
            int globalEqualityCount = (int) Math.round(entry.getValue() * globalFieldCount);
            result.put(field, splitIntoChunks(globalEqualityCount, threadCount));
        }
        return result;
    }

    private Map<String, Integer> extractThreadSlice(Map<String, int[]> perThreadMap, int threadIndex) {
        Map<String, Integer> slice = new HashMap<>();
        for (Map.Entry<String, int[]> entry : perThreadMap.entrySet()) {
            slice.put(entry.getKey(), entry.getValue()[threadIndex]);
        }
        return slice;
    }

    private int[] splitIntoChunks(int total, int threads) {
        int[] chunks = new int[threads];
        int base = total / threads;
        int remainder = total % threads;
        for (int i = 0; i < threads; i++) {
            chunks[i] = base + (i < remainder ? 1 : 0);
        }
        return chunks;
    }
}
