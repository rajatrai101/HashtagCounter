/*
* @authon rajatrai101
*/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;

/**
 * Hashtagcounter class containing the main method the reads the file specified
 * in the argument and writes the output to output_file.txt
 *
 */
public class hashtagcounter {
	public static void main(String[] args) {
		/*
		 * Try read from the file got from the argument try-catch block to handle any
		 * errors
		 */
		fibonacciHeap f = new fibonacciHeap();
		try {
			FileWriter writer = new FileWriter(new File("output_file.txt"));
			writer.write("");
			writer.flush();
			writer.close();

			String nextLine;

			/* Read from the file got from the argument */
			File filename = new File(args[0]);
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));

			while ((nextLine = bufferedReader.readLine()) != null && !nextLine.equals("stop")) {
				String[] strArr = nextLine.split(" ");
				if (nextLine.indexOf('#') != -1) {
					strArr[0] = strArr[0].substring(1);
					int count = Integer.parseInt(strArr[1]);
					if (f.hashMap.containsKey(strArr[0])) {
						/* increase the count of the hashtag if it is already present */
						f.increaseKey(f.hashMap.get(strArr[0]).next, count);
					} else {
						/* insert the new hashtag if it is not present already */
						Node ins = f.insert(strArr[0], count);
						Node pointerNode = new Node(null, -1);
						pointerNode.next = ins;
						f.hashMap.put(strArr[0], pointerNode);
					}
				} else {
					// call remove max method with the remNum times
					Integer remNum = Integer.parseInt(strArr[0]);
					try {
						f.printNMaxes(remNum);
					} catch (IOException e) {
						System.err.printf("No such file exiests: " + e.getMessage());
					}
				}
			}
			bufferedReader.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			// Error handling if no filename specified
			System.err.printf("Input file not provided. Arguments array size:" + e.getMessage());
		} catch (FileNotFoundException e) {
			// Error handling if filename did not match any files
			System.err.printf("No such file exiests: " + e.getMessage());
		} catch (IOException e) {
			// Error handling if unable the output file
			System.err.printf("Error initializing output file." + e.getMessage());
		}
	}

}