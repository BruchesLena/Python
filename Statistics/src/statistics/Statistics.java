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

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

public class Statistics {

	public static void main(String[] args) throws ParserConfigurationException, UnsupportedEncodingException, FileNotFoundException, SAXException, IOException {
		// TODO Auto-generated method stub
			
		}	
	
	public static List<Substitution> getSubstitution(String word) {
		List<Substitution> substitutions = new ArrayList<>();
		HashSet<String> basicForms = new HashSet<>();
		int end = 0;
		String flex = null;
		AbstractWordNet instance = WordNetProvider.getInstance();
		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms(word);
		if (possibleGrammarForms == null) {
			return null;
		}
		for (GrammarRelation g : possibleGrammarForms) {
			TextElement textElement = g.getWord();
			String basicForm = textElement.getBasicForm();
			basicForms.add(basicForm);
		}
		for (String baseForm : basicForms) {
		outer:	for (int w = 0; w < word.length(); w++) {
				for (int b = 0; b < baseForm.length(); b++) {
					if (word.charAt(w) != baseForm.charAt(b)) {
						end = word.length() - w;
						flex = baseForm.substring(b);
						substitutions.add(new Substitution(end, flex));
						break outer;
					}
					if (b == baseForm.length()-1 && w < word.length()-1) {
						end = word.length() - b -1;
						flex = "";
						substitutions.add(new Substitution(end, flex));
						break outer;
					}
					if (w == word.length() - 1 && b < baseForm.length()-1) {
						end = 0;
						flex = baseForm.substring(b+1);
						substitutions.add(new Substitution(end, flex));
						break outer;
					}
					if (word.charAt(w) == baseForm.charAt(b)) {
						if (w == word.length()-1 && b == baseForm.length()-1) {
							substitutions.add(new Substitution(0, ""));
							break outer;
						}
						w++;
						continue;
					}					

				}
			}
		}
		return substitutions;
	}
	
	public static Entry getEntry(String previousWord, String homonym, String lemma) {
		ContextElement prev = new ContextElement(new FullSubstitution(getLastLetters(previousWord.toLowerCase()), getSubstitution(previousWord.toLowerCase())));
		FullSubstitution hom = new FullSubstitution(getLastLetters(homonym.toLowerCase()), getSubstitution(homonym.toLowerCase()));
		FullSubstitution lem = getLemma(homonym.toLowerCase(), lemma.toLowerCase());
		Entry entry = new Entry(hom, prev, lem.substitutions.get(0));
		return entry;
	}
	
	public static HashSet<PartOfSpeech> getPartsOfSpeech(String word)  {
		AbstractWordNet instance = WordNetProvider.getInstance();
		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms(word.toLowerCase());
		HashSet<PartOfSpeech> partsOfSpeech = new HashSet<>();
		if (possibleGrammarForms == null) {
			return null;
		}
		for (GrammarRelation gr : possibleGrammarForms) {
			TextElement textElement = gr.getWord();
			MeaningElement[] concepts = textElement.getConcepts();
			for (MeaningElement m : concepts) {
				PartOfSpeech partOfSpeech = m.getPartOfSpeech();
				if (partOfSpeech != null) {
					partsOfSpeech.add(partOfSpeech);
				}
			}
		}
		return partsOfSpeech;
	}
	
	public static FullSubstitution getLemma(String word, String lemma) {
		List<Substitution> substitutions = new ArrayList<>();
		int end = 0;
		String flex = null;
		outer:	for (int w = 0; w < word.length(); w++) {
			for (int b = 0; b < lemma.length(); b++) {
				if (word.charAt(w) != lemma.charAt(b)) {
					end = word.length() - w;
					flex = lemma.substring(b);
					substitutions.add(new Substitution(end, flex));
					break outer;
				}
				if (b == lemma.length()-1 && w < word.length()-1) {
					end = word.length() - b -1;
					flex = "";
					substitutions.add(new Substitution(end, flex));
					break outer;
				}
				if (w == word.length() - 1 && b < lemma.length()-1) {
					end = 0;
					flex = lemma.substring(b+1);
					substitutions.add(new Substitution(end, flex));
					break outer;
				}
				if (word.charAt(w) == lemma.charAt(b)) {
					if (w == word.length()-1 && b == lemma.length()-1) {
						substitutions.add(new Substitution(0, ""));
						break outer;
					}
					w++;
					continue;
				}

			}
		}
		return new FullSubstitution(getLastLetters(lemma), substitutions);
	}
	
	public static String getLastLetters(String word) {
		if (word.length() >= 3) {
			return word.substring(word.length() - 3);
		}
		if (word.length() < 3) {
			return word;
		}
		return null;
	}
	
	public static boolean isPunctuationMark(String word) {
		String[] marks = {".", ",", "?", "!", "\"", ":", ";", "-", "(", ")"};
		for (String mark : marks) {
			if (mark.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNumber(String word) {
		String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		for (String number : numbers) {
			if (word.contains(number)) {
				return true;
			}
		}
		return false;
	}
	
	public static HashMap<Entry, Integer> formHashMap(List<Entry> statistics) {
		HashMap<Entry, Integer> dictionary = new HashMap<Entry, Integer>();
		for (Entry entry : statistics) {
			Set<Entry> entries = dictionary.keySet();			
			if (entries.size() == 0) {
				dictionary.put(entry, 1);
				continue;
			}
			Object[] ents = entries.toArray();
			for (int i = 0; i < ents.length; i++) {
				if (i == ents.length - 1 && !ents[i].toString().equals(entry.toString())) {
					dictionary.put(entry, 1);
				}
				else if (!ents[i].toString().equals(entry.toString())) {
					continue;
				}
				else {
					int value = dictionary.get(ents[i]);
					value = value + 1;
					dictionary.replace((Entry) ents[i], dictionary.get(ents[i]), value);
					//dictionary.put((Entry) ents[i], value);
					break;
				}
			}

		}
		return dictionary;
	}
	
	public static HashMap<FullSubstitution, Integer> getHomonymNumber(HashMap<Entry, Integer> dictionary) {
		HashMap<FullSubstitution, Integer> homonyms = new HashMap<FullSubstitution, Integer>();
		Set<Entry> entriesInDict = dictionary.keySet();
		for (Entry entryInDict : entriesInDict) {
			FullSubstitution homonym = entryInDict.homonym;
			if (homonyms.size() == 0) {
				homonyms.put(homonym, dictionary.get(entryInDict));
				continue;
			}
			Object[] homs = homonyms.keySet().toArray();
			for (int i = 0; i < homs.length; i++) {
				if (i == homs.length - 1 && !homs[i].toString().equals(homonym.toString())) {
					homonyms.put(homonym, dictionary.get(entryInDict));
				}
				else if (!homs[i].toString().equals(homonym.toString())) {
					continue;
				}
				else {
					int value = dictionary.get(entryInDict);
					value = value + homonyms.get(homs[i]);
					homonyms.put((FullSubstitution) homs[i], value); //переделать на replace
					break;
				}
			}
		}
		return homonyms;
	}
	
	public static HashMap<Bigram, Integer> countBigrams(HashMap<Entry, Integer> dictionary) {
		HashMap<Bigram, Integer> bigrams = new HashMap<Bigram, Integer>();
		Set<Entry> entriesInDict = dictionary.keySet();
		for (Entry entry : entriesInDict) {
			Bigram bigram = new Bigram(entry.homonym, entry.contextElement);
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
					bigrams.put((Bigram)keyBigrams[i], value); //переделать на replace
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
				if (!entry.contextElement.toString().equals(bigram.contextElement.toString()) | !entry.homonym.toString().equals(bigram.homonym.toString())) {
					continue;
				}
				else if (entry.contextElement.toString().equals(bigram.contextElement.toString()) && entry.homonym.toString().equals(bigram.homonym.toString())) {
					entry.probability = (float) ((dictionary.get(entry)*1.0) / (bigrams.get(bigram)*1.0));
				}
			}
		}
		return dictionary;
	}
	
	public static List<String> visualizeProbability(HashMap<Entry, Integer> dictionary, HashMap<Bigram, Integer> bigrams) {
		List<String> visualizing = new ArrayList<>();
		Set<Entry> entriesInDict = dictionary.keySet();
		Set<Bigram> bigram = bigrams.keySet();
		for (Entry entryInDict : entriesInDict) {
			FullSubstitution homonym = entryInDict.homonym;
			ContextElement contextElement = entryInDict.contextElement;
			for (Bigram b : bigram) {
				if (b.contextElement.toString().equals(contextElement.toString()) && b.homonym.toString().equals(homonym.toString())) {
					visualizing.add(entryInDict.toString() + " (" + dictionary.get(entryInDict) + " / " + bigrams.get(b) + ")");
				}
			}
		}
		return visualizing;
	}
	
	public static String subToWord(String word, Substitution sub) {
		String newWord = word.substring(0, word.length() - sub.letters);
		newWord = newWord + sub.ending;
		return newWord;
	}
	
	public static HashMap<Entry, Integer> getStatistics() throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<Entry> dictionary = new ArrayList<>();	
		File folder = new File("D:/Лена/NoAmbig");
		List<File> folderFiles = getXmlFiles(folder);
		for (int j = 0; j < 100; j++) { //значения для тренировки
		DocumentBuilderFactory f  = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder builder = f.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(folderFiles.get(j)),"UTF-8")));
		NodeList nodeList = doc.getElementsByTagName("token");
		System.out.println("nodeList.getLength = " + nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node != null) {
			if (getSubstitution(getTextFromToken(node))!=null && getSubstitution(getTextFromToken(node)).size() > 1) {
				String lemma = getLemmaFromToken(node);
					if (i > 1) {
					Node prevNode = nodeList.item(i-1);
					if (prevNode != null) {
					if (getSubstitution(getTextFromToken(prevNode)) == null) {
							continue;
						}
					if (getPartsOfSpeech(getTextFromToken(prevNode)) != null | isPunctuationMark(getTextFromToken(prevNode)) | isNumber(getTextFromToken(prevNode))) {
						Entry entry = getEntry(getTextFromToken(prevNode), getTextFromToken(node), lemma);
						entry.contextElement.position = "-1";
						dictionary.add(entry);										
						}
					}
				}
					if (i > 2) {
						Node node2 = nodeList.item(i-2);
						if (node2 != null) {
						if (getSubstitution(getTextFromToken(node2)) == null) {
							continue;
						}
						if (getPartsOfSpeech(getTextFromToken(node2)) != null | isPunctuationMark(getTextFromToken(node2)) | isNumber(getTextFromToken(node2))) {
							Entry entry = getEntry(getTextFromToken(node2), getTextFromToken(node), lemma);
							entry.contextElement.position = "-2";
							dictionary.add(entry);
						}
						}
					}
					if (i < nodeList.getLength()) {
						Node node2 = nodeList.item(i+1);
						if (node2 != null) {
						if (getSubstitution(getTextFromToken(node2)) == null) {
							continue;
						}
						if (getPartsOfSpeech(getTextFromToken(node2)) != null | isPunctuationMark(getTextFromToken(node2)) | isNumber(getTextFromToken(node2))) {
							Entry entry = getEntry(getTextFromToken(node2), getTextFromToken(node), lemma);
							entry.contextElement.position = "+1";
							dictionary.add(entry);
						}
						}
					}
					if (i < nodeList.getLength() - 2) {
						Node node2 = nodeList.item(i+2);
						if (node2 != null) {
						if (getSubstitution(getTextFromToken(node2)) == null) {
							continue;
						}
						if (getPartsOfSpeech(getTextFromToken(node2)) != null | isPunctuationMark(getTextFromToken(node2)) | isNumber(getTextFromToken(node2))) {
							Entry entry = getEntry(getTextFromToken(node2), getTextFromToken(node), lemma);
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
		HashMap<Entry, Integer> dict = formHashMap(dictionary);
		System.out.println("form HashMap");
		HashMap<Bigram, Integer> bigrams = countBigrams(dict);
		System.out.println("count bigrams");
		dict = countProbability(dict, bigrams);
		System.out.println("count probability");
		return dict;		
	}
	
	public static String getLemmaFromToken(Node node) {
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			Element tfr = (Element) element.getFirstChild();
			Element v = (Element) tfr.getFirstChild();
			Element l = (Element) v.getFirstChild();			
			return l.getAttribute("t");
		}
		return null;
	}
	
	public static String getTextFromToken(Node node) {
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			return element.getAttribute("text").toLowerCase();
		}
		return null;
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


}
