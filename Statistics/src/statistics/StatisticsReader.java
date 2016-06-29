package statistics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class StatisticsReader {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Entry, Integer> readStatistics() {
		String filename = "Statistics.ser";
		HashMap<Entry, Integer> dictionary = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			dictionary = (HashMap<Entry, Integer>) in.readObject();
			in.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return dictionary;
	}

}
