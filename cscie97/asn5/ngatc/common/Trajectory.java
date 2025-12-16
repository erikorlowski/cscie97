package cscie97.asn5.ngatc.common;

import java.util.LinkedList;

/**
 * Represents a predicted trajectory as a series of time/location benchmarks.
 */
public class Trajectory {
    private LinkedList<Benchmark> benchmarks;

    public Trajectory() {
        this.benchmarks = new LinkedList<>();
    }

    public Trajectory(LinkedList<Benchmark> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public LinkedList<Benchmark> getBenchmarks() {
        return benchmarks;
    }

    public void setBenchmarks(LinkedList<Benchmark> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public void addBenchmark(Benchmark benchmark) {
        this.benchmarks.add(benchmark);
    }

    @Override
    public String toString() {
        return String.format("Trajectory{%d benchmarks}", benchmarks.size());
    }
}
