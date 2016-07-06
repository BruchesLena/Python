package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StatForGuessing {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub

		HashMap<FullSubWithPOS, Integer> statistics = getStatisticsForGuessing();
		for (FullSubWithPOS f : statistics.keySet()) {
			if (f.probability > 1.0) {
				System.out.println("!!" + f);
			}
		}
	}
	
	public static HashMap<FullSubWithPOS, Integer> getStatisticsForGuessing() throws ParserConfigurationException, UnsupportedEncodingException, FileNotFoundException, SAXException, IOException {
		HashMap<FullSubWithPOS, Integer> statistics = new HashMap<FullSubWithPOS, Integer>();
		Set<FullSubWithPOS> keyFullSubWithPOS = statistics.keySet();
		File folder = new File("D:/Лена/NoAmbig");
		List<File> files = getXmlFiles(folder);
		for (int j = 0; j < 200; j++) { //значения для тренировки
			DocumentBuilderFactory f  = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(files.get(j)),"UTF-8")));
			NodeList nodeList = doc.getElementsByTagName("token");
		//	System.out.println("nodeList.getLength = " + nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null) {
					String wordForm = Statistics.getTextFromToken(node).toLowerCase();
					String partOfSpeech = Statistics.getPOSFromToken(node);
					if (Statistics.getPartsOfSpeech(wordForm) != null && Statistics.getPartsOfSpeech(wordForm).size() == 1) {
						FullSubWithPOS current = new FullSubWithPOS(new FullSubstitution(Statistics.getLastLetters(wordForm), Statistics.getSubstitution(wordForm)), partOfSpeech);
						if (statistics.isEmpty()) {
							statistics.put(current, 1);
							continue;
						}
						else {
							Object[] fullSub = keyFullSubWithPOS.toArray();
							for (int k = 0; k < fullSub.length; k++) {
								if (k == fullSub.length - 1 && !fullSub[k].toString().equals(current.toString())) {
									statistics.put(current, 1);
								}
								else if (fullSub[k].toString().equals(current.toString())) {
									int value = statistics.get(fullSub[k]);
									value++;
									statistics.replace((FullSubWithPOS) fullSub[k], statistics.get(fullSub[k]), value);
									break;
								}
							}
						}
					}
				}
			}
		}
		HashMap<FullSubstitution, Integer> fullSubsNumber = countFullSubstitutions(statistics);
		statistics = countProbability(statistics, fullSubsNumber);
		return statistics;
	}
	
	public static HashMap<FullSubstitution, Integer> countFullSubstitutions(HashMap<FullSubWithPOS, Integer> statistics) {
		HashMap<FullSubstitution, Integer> processed = new HashMap<FullSubstitution, Integer>();
		Set<FullSubWithPOS> keyFS = statistics.keySet();
		for (FullSubWithPOS fs : keyFS) {
			if (processed.isEmpty()) {
				processed.put(fs.fullSubstitution, statistics.get(fs));
			}
			else {
				Object[] fullSubs = processed.keySet().toArray();
				for (int i = 0; i < fullSubs.length; i++) {
					if (i == fullSubs.length - 1 && !fs.fullSubstitution.toString().equals(fullSubs[i].toString())) {
						processed.put(fs.fullSubstitution, statistics.get(fs));
					}
					else if (fs.fullSubstitution.toString().equals(fullSubs[i].toString())) {
						int value = statistics.get(fs) + processed.get(fullSubs[i]);
						processed.replace((FullSubstitution) fullSubs[i], processed.get(fullSubs[i]), value);
						break;
					}
				}
			}
		}
		return processed;
	}
	
	public static HashMap<FullSubWithPOS, Integer> countProbability (HashMap<FullSubWithPOS, Integer> statistics, HashMap<FullSubstitution, Integer> fullSubsNumber) {
		Set<FullSubWithPOS> ketStat = statistics.keySet();
		Set<FullSubstitution> keyFullSubs = fullSubsNumber.keySet();
		for (FullSubWithPOS stat : ketStat) {
			for (FullSubstitution fullSub : keyFullSubs) {
				if (stat.fullSubstitution.toString().equals(fullSub.toString())) {
					stat.probability = statistics.get(stat)*1.0 / fullSubsNumber.get(fullSub)*1.0;
//					if (stat.probability > 1) {
//						System.out.println(stat.toString() + " : " + statistics.get(stat) + "/" + fullSubsNumber.get(fullSub));	
//					}
				}
			}
		}
		return statistics;
	}
	
	private static List<File> getXmlFiles(File folder) {
		List<File> folderFiles = new ArrayList<File>();
		File[] files = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				boolean isTxt = name.endsWith(".xml");
				return isTxt;
			}
		});
		if (files != null) {
			folderFiles.addAll(Arrays.asList(files));
		}

		File[] folders = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				boolean isFolder = dir.isDirectory();
				return isFolder;
			}

		});
		if (folders != null) {
			for (int f = 0; f < folders.length; f++) {
				File newFile = folders[f];
				List<File> filesList = getXmlFiles(newFile);
				folderFiles.addAll(filesList);
			}
		}
		return folderFiles;

	}

}
