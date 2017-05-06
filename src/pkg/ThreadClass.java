package pkg;

/**
 * Created by Sarahwang on 4/24/17.
 * modified by Lifen Yan on 5/6/17
 */
import java.util.LinkedList;
import java.util.Queue;

public class ThreadClass {
    public static void main(String[] args) throws InterruptedException {
        //PC is object of a class that has both produce() and consume() methods
        final PC pc = new PC();

        // Create producer thread
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.produce();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Create consumer thread
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.consume(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.consume(2);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.consume(3);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start both threads
        t0.start();
        t1.start();
        t2.start();
        t3.start();

        // t1 finishes before t2
        t0.join();
        t1.join();
        t2.join();
        t3.join();
        
        System.out.println("finished");
    }

    // This class has a list, producer (adds items to list and consumer (removes items).
    public static class PC {
    	// Create a list shared by producer and consumer
    	Queue<Integer> list = new LinkedList<>();
    	int capacity = 5; // the size of list
    	int maxRange = 20;
    	int value_consumered = 0; // consumer will exit when value_consumed =
    								// maxRange
    	int value_produced = 0; // producer will exit when value_produced > maxRange

    	// Function called by producer thread
    	public void produce() throws InterruptedException {
    		while (value_produced <= maxRange) {
    			synchronized (this) {
    				// producer thread waits while list is full
    				while (list.size() == capacity)
    					wait();

    				System.out.println("Producer produced-" + value_produced);
    				list.offer(value_produced++); // to insert the jobs in the list
    				notify(); // notifies the consumer thread that now it can start
    							// consuming
    			}

    			Thread.sleep(1000); // makes the working of program easier to
    								// understand
    		}
    	}

    	// Function called by consumer thread
    	public void consume(int nob) throws InterruptedException {
    		while (value_consumered < maxRange) {
    			synchronized (this) {
    				// consumer thread waits while list is empty and not all values
    				// are produced yet
    				while (list.size() == 0 && value_produced < maxRange)
    					wait();

    				if (list.size() != 0) {
    					value_consumered = list.poll(); // to retrieve the first job
    													// in the list
    					System.out.println("#" + nob + " Consumer consumed-" + value_consumered);
    					notify(); // Wake up producer or other consumer threads
    				}
    			}
    			Thread.sleep(1000);

    		}
    	}
    }

}

