import java.util.*;

public class PageReplacementSimulator {
    // Simulates page references
    static class PageReferenceGenerator {
        private int P; // Virtual memory size
        private int s; // Starting location
        private int e; // Size of locus
        private int m; // Rate of motion
        private double t; // Probability of transition
        private Random random;

        public PageReferenceGenerator(int P, int s, int e, int m, double t) {
            this.P = P;
            this.s = s;
            this.e = e;
            this.m = m;
            this.t = t;
            this.random = new Random();
        }

        public List<Integer> generateReferences(int length) {
            List<Integer> references = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                // Generate a page reference within the current locus
                int pageRef = s + random.nextInt(e);
                references.add(pageRef % P);

                // Check for transition to a new location
                if (random.nextDouble() < t) {
                    s = random.nextInt(P); // New random starting location
                } else {
                    s = (s + 1) % P; // Move locus to the right
                }
            }
            return references;
        }
    }

    // Simulates FIFO page replacement algorithm
    static int simulateFIFO(List<Integer> references, int frameCount) {
        Queue<Integer> frames = new LinkedList<>();
        Set<Integer> pageSet = new HashSet<>();
        int pageFaults = 0;

        for (int page : references) {
            if (!pageSet.contains(page)) {
                pageFaults++;
                if (frames.size() == frameCount) {
                    int removedPage = frames.poll();
                    pageSet.remove(removedPage);
                }
                frames.offer(page);
                pageSet.add(page);
            }
        }

        return pageFaults;
    }

    // Simulates LRU page replacement algorithm
    static int simulateLRU(List<Integer> references, int frameCount) {
        LinkedHashSet<Integer> frames = new LinkedHashSet<>();
        int pageFaults = 0;

        for (int page : references) {
            if (!frames.contains(page)) {
                pageFaults++;
                if (frames.size() == frameCount) {
                    int leastUsed = frames.iterator().next();
                    frames.remove(leastUsed);
                }
            } else {
                frames.remove(page);
            }
            frames.add(page);
        }

        return pageFaults;
    }

    public static void main(String[] args) {
        // Parameters for page reference generation
        int P = 1000; // Virtual memory size
        int s = 0;    // Starting location
        int e = 10;   // Size of locus
        int m = 200;  // Rate of motion
        double t = 0.1; // Probability of transition

        // Create a page reference generator
        PageReferenceGenerator generator = new PageReferenceGenerator(P, s, e, m, t);

        // Generate a list of page references
        List<Integer> references = generator.generateReferences(1000);

        // Number of frames in physical memory
        int frameCount = 50;

        // Simulate FIFO and LRU algorithms
        int fifoFaults = simulateFIFO(references, frameCount);
        int lruFaults = simulateLRU(references, frameCount);

        // Print results
        System.out.println("The page fault using FIFO replacement algorithm:");
        System.out.println(fifoFaults + " times");
        System.out.println("The page fault using LRU replacement algorithm:");
        System.out.println(lruFaults + " times");
    }
}