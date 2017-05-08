//package pkg;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by Sarahwang on 4/24/17.
 * modified by Lifen Yan on 5/6/17
 * modified by Chunwen Xiong on 5/7/17
 */

public class ThreadClass {
	public static void main(String[] args) throws InterruptedException, IOException {
		// int maxRange = 20;
		// int capacity = 5;
		int num_consumers = 2; // Specify on cmd line; default is two. 

		if (args.length > 0) {
			try {
	            // Parse the string argument into an integer value.
	            num_consumers = Integer.parseInt(args[0]);
	        }
	        catch (NumberFormatException nfe) {
	            // The first argument isn't a valid integer.  Print
	            // an error message, then exit with an error code.
	            System.out.println("The Command-Line Argument must be an integer.");
	            System.exit(1);
	        }
		}
		

		// read maxRange and capacity from console, within range 1 ~ 50000
		int maxRange = 0; // the max integer producer can reach
		int capacity = 0; // buffer size
		Scanner in = new Scanner(System.in);
		do {
			System.out.printf("Specify the max range within 1 ~ 50000: ");
			maxRange = in.nextInt();
		} while (maxRange <= 0 || maxRange > 50000);

		do {
			System.out.printf("Specify the buffer size within 1 ~ 50000: ");
			capacity = in.nextInt();
		} while (capacity <= 0 || capacity > 50000);

		in.close();

		System.out.println("*** maxRange: " + maxRange + ", capacity: " + capacity + ", num of consumer: " + num_consumers + " ***");

		// PC is object of a class that has both produce() and consume() methods
		final PC pc = new PC(maxRange, capacity);

		// Create producer thread
		Thread t0 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					pc.produce();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		// Create consumer thread
		Thread[] consumer_threads = new Thread[num_consumers];
		for (int i = 0; i < num_consumers; i++) {
			final int nob = i + 1;
			consumer_threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						pc.consume(nob);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		// start threads
		t0.start();
		for (int i = 0; i < num_consumers; i++) {
			consumer_threads[i].start();
		}

		// Wait for them to finish.
		t0.join();
		for (int i = 0; i < num_consumers; i++) {
			consumer_threads[i].join();
		}

		System.out.println("finished");
	}

	// This class has a list, producer (adds items to list and consumer (removes items).
	public static class PC {
		// Create a list shared by producer and consumer
		Queue<Integer> list = new LinkedList<>();
		int capacity; // buffer size
		int maxRange; // the max integer producer can reach
		int value_consumed = 0; // consumer will exit when value_consumed = maxRange
		int value_produced = 0; // producer will exit when value_produced > maxRange

		PC(int maxRange, int capacity) {
			this.maxRange = maxRange;
			this.capacity = capacity;
		}

		// Function called by producer thread
		public void produce() throws InterruptedException {
			while (value_produced <= maxRange) {
				synchronized (this) {
					// producer thread waits while list is full
					while (list.size() > 0)
						wait();

					while (list.size() < capacity && value_produced <= maxRange) {
						System.out.println("Producer produced-" + value_produced);
						list.offer(value_produced++); // to insert the jobs in the list
					}

					notify(); // notifies the consumer thread that now it can start consuming
				}

				Thread.sleep(1000); // makes the working of program easier to understand
			}
		}

		// Function called by consumer thread
		public void consume(int nob) throws InterruptedException {
			while (value_consumed < maxRange) {
				synchronized (this) {
					// consumer thread waits while list is empty and not all values are produced yet
					while (list.size() == 0 && value_produced < maxRange)
						wait();

					if (list.size() != 0) {
						value_consumed = list.poll(); // to retrieve the first job in the list
						System.out.println("#" + nob + " Consumer consumed-" + value_consumed);
						notify(); // Wake up producer or other consumer threads
					}
				}
				
				Thread.sleep(1000);
			}
		}
	}
}