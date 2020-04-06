import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;

public class hashtagcounter {
	public static void main(String[] args) {
		FibonacciHeap fibHeap = new FibonacciHeap(args.length > 1 ? args[1] : null);
		try {
			if (args.length > 1) {
				File outputFile = new File(args[1]);
				if (outputFile.exists())
					outputFile.delete();
				outputFile.createNewFile();
			}

			File filename = new File(args[0]);
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));

			String nextLine;

			while ((nextLine = bufferedReader.readLine()) != null) {
				if (nextLine.equals("stop"))
					break;
				String[] strArr = nextLine.split(" ");
				if (nextLine.startsWith("#")) {
					strArr[0] = strArr[0].substring(1);
					int count = Integer.parseInt(strArr[1]);
					if (fibHeap.hasKey(strArr[0]))
						fibHeap.increaseKey(strArr[0], count);
					else
						fibHeap.insert(strArr[0], count);

				} else {
					Integer remNum = Integer.parseInt(strArr[0]);
					fibHeap.printNMaxes(remNum);
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// Error handling if filename did not match any files
			System.err.printf("No such file exiests: " + e.getMessage());
		} catch (IOException e) {
			// Error handling if unable to create the output file
			System.err.printf("Error occured with the output file." + e.getMessage());
		}
	}

}