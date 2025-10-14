package jplus.test;

public class TestExample {
    private int count = 0;

    public TestExample(int start) {
        this.count = start;
    }

    public void increment() {
        ++count;
    }

    public void printCount() {
        if (count > 0) {
            System.out.println("Count: " + count);
        } else {
            System.out.println("No count.");
        }
    }

    public void loopExample() {
        for (int i = 0; i < count; i++) {
            System.out.println("Index: " + i);
        }
    }

    public static void main(String[] args) {
        TestExample example = new TestExample(1);
        example.increment();
        example.printCount();
        example.loopExample();
    }
}
