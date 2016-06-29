package statistics;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class ReadingStatForGuessing {

	@SuppressWarnings("unchecked")
	public static HashMap<FullSubWithPOS, Integer> readStatistics() {
		String filename = "StatisticsForGuessing.ser";
		HashMap<FullSubWithPOS, Integer> dictionary = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			dictionary = (HashMap<FullSubWithPOS, Integer>) in.readObject();
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
