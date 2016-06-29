package statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResultsCorrecter {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		File file = new File("D:/Regression/test_vec.txt");
		BufferedReader fin = new BufferedReader((new InputStreamReader(new FileInputStream(file),"UTF-8")));
		String line = null;
		List<String> vectors = new ArrayList<>();
		List<String> outputs = new ArrayList<>();
		while ((line = fin.readLine()) != null) {
			String[] vec = line.split("[ ]");
			String input = vec[2] + " " + vec[3];
			vectors.add(input);
			String output = vec[4];
			outputs.add(output);
		}
		Testing.writeInFile(vectors, "D:/Regression/test_input.txt", false);
		Testing.writeInFile(outputs, "D:/Regression/test_output.txt", false);
	}

}
