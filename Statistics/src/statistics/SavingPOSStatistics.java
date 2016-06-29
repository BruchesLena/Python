package statistics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class SavingPOSStatistics {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub

		String filename = "POSStatistics.ser";
		HashMap<Entry, Integer> stat = POSStatistics.getStatistics();
			FileOutputStream fos = null;
			ObjectOutputStream out = null;
			try {
				fos = new FileOutputStream(filename);
				out = new ObjectOutputStream(fos);
				out.writeObject(stat);
				out.close();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
	}

}
