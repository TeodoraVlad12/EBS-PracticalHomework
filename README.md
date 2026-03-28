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

Results averaged over 3 runs:

| Threads | Pub Gen (ms) | Sub Gen (ms) | Total (ms) |
|---------|-------------|-------------|-----------|
| 1       | 4.58        | 15.74       | 20.32     |
| 2       | 0.55        | 9.42        | 9.97      |
| 4       | 0.56        | 7.19        | 7.75      |
