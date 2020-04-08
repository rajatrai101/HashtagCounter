import java.io.*;

//Main/hashtagcounter class with the main io login and processing 
public class hashtagcounter {
	public static void main(String[] args) {
		MaxFibonacciHeap fibHeap = new MaxFibonacciHeap(args.length > 1 ? args[1] : null);
		try {
			if (args.length > 1) {// if the output file's name is specified
				File outputFile = new File(args[1]);
				if (outputFile.exists())// check if it already exists
					outputFile.delete();// delete the old one
				outputFile.createNewFile();// create new output file
			}

			File filename = new File(args[0]);// create a new file object from the arguments list
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));

			String nextLine;
			// read line by line from the input file
			while ((nextLine = bufferedReader.readLine()) != null) {
				if (nextLine.equals("stop"))// stop case
					break;
				String[] strArr = nextLine.split(" ");
				if (nextLine.startsWith("#")) {// if the line has hashtag
					strArr[0] = strArr[0].substring(1);
					int count = Integer.parseInt(strArr[1]);
					if (fibHeap.hasKey(strArr[0]))// check if already present
						fibHeap.increaseKey(strArr[0], count);// increase key if yes
					else
						fibHeap.insert(strArr[0], count);// insert if no
				} else
					fibHeap.printNHighest(Integer.parseInt(strArr[0]));// read the number from the line and process
																		// output for the same
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// Error handling if filename did not match any files
			System.err.print("No such file exists: " + e.getMessage());
		} catch (IOException e) {
			// Error handling if unable to create the output file
			System.err.print("Error occurred with the output file." + e.getMessage());
		}
	}

}