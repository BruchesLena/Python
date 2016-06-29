package statistics;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class ReadingPOSStatistics {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@SuppressWarnings("unchecked")
	public static HashMap<Entry, Integer> readStatistics() {
		String filename = "POSStatistics.ser";
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
