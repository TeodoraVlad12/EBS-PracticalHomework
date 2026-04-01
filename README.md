# EBS Homework — Game Store Event Generator

## Parallelization

- **Type:** Java threads via `ExecutorService.newFixedThreadPool(n)`
- **Parallelism factors tested:** 1, 2, 4

## Messages Generated

- 10,000 publications
- 10,000 subscriptions

## Benchmark Results

Processor: **Apple M3 Pro**

Runtime: **JDK 21**

Results averaged over 11 runs:

| Threads | Pub Gen (ms) | Sub Gen (ms) | Total (ms) |
|---------|-------------|-------------|-----------|
| 1       | 3.62        | 16.22       | 19.84     |
| 2       | 0.54        | 8.45        | 8.99      |
| 4       | 0.60        | 8.06        | 8.66      |
