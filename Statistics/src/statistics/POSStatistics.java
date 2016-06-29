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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

public class POSStatistics {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub

		HashMap<Entry, Integer> dictionary = getStatistics();
		Set<Entry> entries = dictionary.keySet();
		for (Entry entry : entries) {
			if (entry.probability > 1) {
				System.out.println(entry);
			}
		}
	}
	
	public static HashMap<Entry, Integer> getStatistics() throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<Entry> dictionary = new ArrayList<>();	
		File folder = new File("D:/Лена/NoAmbig");
		List<File> folderFiles = getXmlFiles(folder);
		for (int j = 0; j < 2000; j++) { //значения для тренировки
		DocumentBuilderFactory f  = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder builder = f.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(folderFiles.get(j)),"UTF-8")));
		NodeList nodeList = doc.getElementsByTagName("token");
		System.out.println("nodeList.getLength = " + nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node != null) {
			if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node))!=null && Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node)).size() > 1) {
				HashSet<PartOfSpeech> partsOfSpeech = Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node));
				if (partsOfSpeech.contains(PartOfSpeech.VERB) && partsOfSpeech.contains(PartOfSpeech.INFN)) {
					continue;
				}
				
				String partOfSpeech = getPOSFromToken(node);
				HashSet<PartOfSpeech> possiblePOS = Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node));
					if (i > 1) {
					Node prevNode = nodeList.item(i-1);
					if (prevNode != null) {
					if (Statistics.getSubstitution(Statistics.getTextFromToken(prevNode)) == null) {
							continue;
						}
					if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(prevNode)) != null | Statistics.isPunctuationMark(Statistics.getTextFromToken(prevNode)) | Statistics.isNumber(Statistics.getTextFromToken(prevNode))) {
						Entry entry = getEntry(Statistics.getTextFromToken(prevNode), possiblePOS, partOfSpeech);
						entry.contextElement.position = "-1";
						dictionary.add(entry);										
						}
					}
				}
					if (i > 2) {
						Node node2 = nodeList.item(i-2);
						if (node2 != null) {
						if (Statistics.getSubstitution(Statistics.getTextFromToken(node2)) == null) {
							continue;
						}
						if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node2)) != null | Statistics.isPunctuationMark(Statistics.getTextFromToken(node2)) | Statistics.isNumber(Statistics.getTextFromToken(node2))) {
							Entry entry = getEntry(Statistics.getTextFromToken(node2), possiblePOS, partOfSpeech);
							entry.contextElement.position = "-2";
							dictionary.add(entry);
						}
						}
					}
					if (i < nodeList.getLength()) {
						Node node2 = nodeList.item(i+1);
						if (node2 != null) {
						if (Statistics.getSubstitution(Statistics.getTextFromToken(node2)) == null) {
							continue;
						}
						if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node2)) != null | Statistics.isPunctuationMark(Statistics.getTextFromToken(node2)) | Statistics.isNumber(Statistics.getTextFromToken(node2))) {
							Entry entry = getEntry(Statistics.getTextFromToken(node2), possiblePOS, partOfSpeech);
							entry.contextElement.position = "+1";
							dictionary.add(entry);
						}
						}
					}
					if (i < nodeList.getLength() - 2) {
						Node node2 = nodeList.item(i+2);
						if (node2 != null) {
						if (Statistics.getSubstitution(Statistics.getTextFromToken(node2)) == null) {
							continue;
						}
						if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node2)) != null | Statistics.isPunctuationMark(Statistics.getTextFromToken(node2)) | Statistics.isNumber(Statistics.getTextFromToken(node2))) {
							Entry entry = getEntry(Statistics.getTextFromToken(node2), possiblePOS, partOfSpeech);
							entry.contextElement.position = "+2";
							dictionary.add(entry);
						}
						}
					}
			}
		}
		}
		}
		System.out.println("dictionary.size = " + dictionary.size());
		HashMap<Entry, Integer> dict = Statistics.formHashMap(dictionary);
		System.out.println("form HashMap");
		HashMap<Bigram, Integer> bigrams = countBigrams(dict);
		System.out.println("count bigrams");
		dict = countProbability(dict, bigrams);
		System.out.println("count probability");
		return dict;		
	}
	
	public static String getPOSFromToken(Node node) {
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			Element tfr = (Element) element.getFirstChild();
			Element v = (Element) tfr.getFirstChild();
			Element l = (Element) v.getFirstChild();
			Element g = (Element) l.getFirstChild();
			return g.getAttribute("v");
		}
		return null;
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
	
	public static HashMap<Bigram, Integer> countBigrams(HashMap<Entry, Integer> dictionary) {
		HashMap<Bigram, Integer> bigrams = new HashMap<Bigram, Integer>();
		Set<Entry> entriesInDict = dictionary.keySet();
		for (Entry entry : entriesInDict) {
			Bigram bigram = new Bigram(entry.getPartsOfSpeech(), entry.contextElement);
			if (bigrams.size() == 0) {
				bigrams.put(bigram, dictionary.get(entry));
				continue;
			}
			Object[] keyBigrams = bigrams.keySet().toArray();
			for (int i = 0; i < keyBigrams.length; i++) {
				if (i == keyBigrams.length - 1 && !keyBigrams[i].toString().equals(bigram.toString())) {
					bigrams.put(bigram, dictionary.get(entry));
				}
				else if (!keyBigrams[i].toString().equals(bigram.toString())) {
					continue;
				}
				else {
					int value = dictionary.get(entry);
					value = value + bigrams.get(keyBigrams[i]);
					bigrams.replace((Bigram) keyBigrams[i], bigrams.get(keyBigrams[i]), value);
					//bigrams.put((Bigram)keyBigrams[i], value);
					break;
				}
			}
		}
		return bigrams;
	}
	
	public static HashMap<Entry, Integer> countProbability(HashMap<Entry, Integer> dictionary, HashMap<Bigram, Integer> bigrams) {
		Set<Entry> entriesInDict = dictionary.keySet();
		Set<Bigram> keyBigrams = bigrams.keySet();
		for (Entry entry : entriesInDict) {
			for (Bigram bigram : keyBigrams) {
				String test = bigram.POSInEngToString();
				String testEntry = entry.getPartsOfSpeech().toString();
				if (!entry.contextElement.toString().equals(bigram.contextElement.toString())) {
					continue;
				}
				else if (entry.contextElement.toString().equals(bigram.contextElement.toString()) && (entry.partsOfSpeech.toString().equals(bigram.POSInEngToString()) | (entry.partsOfSpeech.size() == bigram.partsOfSpeech.size()))) {
					//последнее условие в "или" переделать - элементы идут в разном порядке, поэтому не сравнивается
					entry.probability = (float) ((dictionary.get(entry)*1.0) / (bigrams.get(bigram)*1.0));
					System.out.println(dictionary.get(entry) + "/" + bigrams.get(bigram));
				}
			}
		}
		return dictionary;
	}
	
	public static Entry getEntry(String previousWord, HashSet<PartOfSpeech> possiblePOS, String partOfSpeech) {
		ContextElement prev = new ContextElement(new FullSubstitution(Statistics.getLastLetters(previousWord.toLowerCase()), Statistics.getSubstitution(previousWord.toLowerCase())));
		Entry entry = new Entry(prev, possiblePOS, partOfSpeech);
		return entry;
	}

}
