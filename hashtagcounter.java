/*
* @authon rajatrai101
*/

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Hashtagcounter class containing the main method the reads the file specified
 * in the argument and writes the output to output_file.txt
 *
 */
public class hashtagcounter {
	public static void main(String[] args) throws Exception {
		/* Read from the file got from the argument */
		try {
			FileWriter writer = new FileWriter(new File("output_file.txt"));
			writer.write("");
			writer.flush();
			writer.close();
			
			FibonacciHeap f = new FibonacciHeap();
			f.hashMap = new HashMap<String, Node>();
			String str;
			
			/* Read from the file got from the argument*/
			File filename = new File(args[0]);
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));
	
			while ((str = bufferedReader.readLine()) != null && !str.equals("stop")) {
				String[] strArr = str.split(" ");
				if (str.indexOf('#') != -1) {
					strArr[0] = strArr[0].substring(1);
					int val = Integer.parseInt(strArr[1]);
					if (f.hashMap.containsKey(strArr[0])) {
						/*increase the value of the key if the node is already present*/
						f.increaseKey(f.hashMap.get(strArr[0]).next, val);
					} else {
						/*insert the new node if the key is not present already*/
						Node ins = f.insert(val, strArr[0]);
						Node pointerNode = new Node(-1, null);
						pointerNode.next = ins;
						f.hashMap.put(strArr[0], pointerNode);
					}
				} else {
					// call remove max method with the remNum times
					Integer remNum = Integer.parseInt(strArr[0]);
					f.removeNMaxes(remNum);
				}
			}
			bufferedReader.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			// Error handling code here.
			System.err.printf("Input file not provided. Arguments array size:" + e.getMessage());
		}
	}

}