package statistics;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TestProb {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<Entry, Integer> dictionary = StatisticsReader.readStatistics();
		List<String> visualization = Statistics.visualizeProbability(dictionary, Statistics.countBigrams(dictionary));
		Analysis.writeInFile(visualization, "D:/Лена/Statistics/Statistics.txt", false);
	}

}
