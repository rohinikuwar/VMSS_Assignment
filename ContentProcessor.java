import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ContentProcessor {

	public static void main(String[] args) {
		if (args.length != 1) {

			//Printing below line if the single command line argument is not provided
			System.out.println("Please use the following line to run the code: java ContentProcessor <file_path>");
			return;
		}

		//To retreive file_path from command line argument
		String filePath = args[0];
		AtomicInteger totalLines = new AtomicInteger(0);
		AtomicInteger totalSpaces = new AtomicInteger(0);
		AtomicInteger totalTabs = new AtomicInteger(0);
		Map<Character, AtomicInteger> specialCharacterCounts = new ConcurrentHashMap<>();

		//Creating an ExecutorService instance that manages a thread pool with a fixed number of threads
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		//Below lines to open and read the file
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			br.lines().forEach(line -> {
				executorService.execute(() -> {
					processLine(line, totalLines, totalSpaces, totalTabs, specialCharacterCounts);
				});
			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}

		while (!executorService.isTerminated()) {
			// Wait for all threads to finish
		}

		//Calling the printResults method to display all the required things
		printResults(totalLines.get(), totalSpaces.get(), totalTabs.get(), specialCharacterCounts);
	}

	//The processLine method processes each line of text
	private static void processLine(
			String line,
			AtomicInteger totalLines,
			AtomicInteger totalSpaces,
			AtomicInteger totalTabs,
			Map<Character, AtomicInteger> specialCharacterCounts){
		totalLines.incrementAndGet();

		for (char c : line.toCharArray()) {
			if (c == ' ') {
				totalSpaces.incrementAndGet();
			} else if (c == '\t') {
				totalTabs.incrementAndGet();
			} else if ("&$%#@*".indexOf(c) != -1) {
				specialCharacterCounts.computeIfAbsent(c, k -> new AtomicInteger(0)).incrementAndGet();
			}
		}
	}
	
    //The printResults method displays the final statistics for all required things.
	private static void printResults(
			int totalLines,
			int totalSpaces,
			int totalTabs,
			Map<Character, AtomicInteger> specialCharacterCounts
			) {
		System.out.println("Total number of lines = " + totalLines);
		System.out.println("Total number of spaces = " + totalSpaces);
		System.out.println("Total number of tabs = " + totalTabs);
		for (Map.Entry<Character, AtomicInteger> entry : specialCharacterCounts.entrySet()) {
			System.out.println("Total number of " + entry.getKey() + " character = " + entry.getValue());
		}
	}
}
