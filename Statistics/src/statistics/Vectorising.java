package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.utils.MorphologicUtils;
import com.onpositive.wikipedia.dumps.builder.Porter;


public class Vectorising {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException{
		// TODO Auto-generated method stub

		//writeInFile(format(parseFile("D:/Regression/banks_test_etalon.xml")), "D:/Regression/test_vec.txt", false);
		List<String> twits = new ArrayList<>();
		List<String> vectors = new ArrayList<>();
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder builder = f.newDocumentBuilder();
		File file = new File("path");
		Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));
		NodeList nodeList = doc.getElementsByTagName("column");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (Node.ELEMENT_NODE == node.getNodeType()) {
				Element element = (Element) node;
				if (element.getAttribute("name").equals("text")) {
					twits.add(element.getTextContent());
				}
			}
		}
		for (String twit : twits) {
			vectors.add(createVector(getStat("PATH"), twit).toString());
		}
		
		
		
	}
	
	public static List<String> getVectors(String path) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<String> twits = new ArrayList<>();
		List<String> vectors = new ArrayList<>();
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder builder = f.newDocumentBuilder();
		File file = new File(path);
		Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));
		NodeList nodeList = doc.getElementsByTagName("column");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (Node.ELEMENT_NODE == node.getNodeType()) {
				Element element = (Element) node;
				if (element.getAttribute("name").equals("text")) {
					twits.add(element.getTextContent());
				}
			}
		}
		for (String twit : twits) {
			vectors.add(createVector(getStat("PATH"), twit).toString());
		}
		return vectors;
	}
	
	public static List<String> getStat(String path) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<String> twits = new ArrayList<>();
		List<String> stems = new ArrayList<>();
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder builder = f.newDocumentBuilder();
		File file = new File(path);
		Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));
		NodeList nodeList = doc.getElementsByTagName("column");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (Node.ELEMENT_NODE == node.getNodeType()) {
				Element element = (Element) node;
				if (element.getAttribute("name").equals("text")) {
					twits.add(element.getTextContent());
				}
			}
		}
		for (String twit : twits) {
			List<IToken> wordforms = tokens(twit);
			stems.addAll(getStemsFromTwit(wordforms));
		}
		HashMap<String, Integer> frequency = countStems(stems);
		return getMiddleStems(frequency);
	}
	
	public static List<String> parseFile(String path)throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<String> twits = new ArrayList<String>();
		List<String> buffered = new ArrayList<String>();
		List<String> twitsWithEval = new ArrayList<String>();

		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		f.setValidating(false);
		DocumentBuilder builder = f.newDocumentBuilder();
		File file = new File(path);
		Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8")));
		NodeList nodeList = doc.getElementsByTagName("column");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (Node.ELEMENT_NODE == node.getNodeType()) {
				Element element = (Element) node;
				if (element.getAttribute("name").equals("id") |
						element.getAttribute("name").equals("text") | 
						element.getAttribute("name").equals("sberbank") | 
						element.getAttribute("name").equals("vtb") | 
						element.getAttribute("name").equals("gazprom") | 
						element.getAttribute("name").equals("alfabank") | 
						element.getAttribute("name").equals("bankmoskvy") | 
						element.getAttribute("name").equals("raiffeisen") | 
						element.getAttribute("name").equals("uralsib") | 
						element.getAttribute("name").equals("rshb")) {
					if (!element.getTextContent().equals("NULL")) {
					twits.add(element.getTextContent());
					}
				}
			}
		}
		for (int i = 1; i < twits.size(); i++) {
			if (twits.get(i).equals("0") | 
					twits.get(i).equals("1") | 
					twits.get(i).equals("-1")) {
				String current = " " + twits.get(i-2) + " " + twits.get(i-1).length() + " " + isPunctuationMark(twits.get(i-1)) + " " + twits.get(i);
				buffered.add(current);
			}
		}
		for (String str : buffered) {
			if (!str.startsWith("0") && 
					!str.startsWith("1") && 
					!str.startsWith("-1")) {
				twitsWithEval.add(str);
			}
		}
		return twitsWithEval;
	}
	
	public static void writeInFile(List<String> data, String path, boolean b) throws IOException {
		FileWriter writer = new FileWriter(path, b);
		for (String s : data) {
			writer.write(s + "\n");
			writer.flush();
		}		
}
	
	public static List<String> format (List<String> processed) {
		List<String> result = new ArrayList<String>();
		for (String str : processed) {
			if (str.startsWith(" 1") |
					str.startsWith(" 2") |
					str.startsWith(" 3") |
					str.startsWith(" 4") |
					str.startsWith(" 5") |
					str.startsWith(" 6") |
					str.startsWith(" 7") |
					str.startsWith(" 8") |
					str.startsWith(" 9"))
			result.add(str);
		
		}
		return result;
	}
	
	public static Integer isPunctuationMark(String twit) {
		if (twit.endsWith("!")) {
			return 1;
		}
		return 0;
	}
	
	public static List<IToken> tokens(String line) {
		final List<IToken> tokenize = new PrimitiveTokenizer().tokenize(line);
		final WordFormParser wordFormParser = new WordFormParser(
				WordNetProvider.getInstance());
		wordFormParser.setIgnoreCombinations(true);
		final List<IToken> process = wordFormParser.process(tokenize);
		return process;
	}

	public static List<String> getStemsFromTwit(List<IToken> tokens) {
		tokens = MorphologicUtils.getWithNoConflicts(tokens);
		List<String> stems = new ArrayList<>();
		for (IToken token : tokens) {
			if (token instanceof WordFormToken) {
				String wordform = token.getShortStringValue();
				String stem = Porter.stem(wordform);
				stems.add(stem);
			}
		}
		return stems;
	}
	
	public static HashMap<String, Integer> countStems(List<String> stems) {
		HashMap<String, Integer> frequency = new HashMap<>();
		for (String stem : stems) {
			if (frequency.isEmpty()) {
				frequency.put(stem, 1);
			}
			else if (frequency.containsKey(stem)) {
				int value = frequency.get(stem);
				value++;
				frequency.put(stem, value);
			}
			else {
				frequency.put(stem, 1);
			}
		}
		return frequency;
	}
	
	public static List<String> getMiddleStems(HashMap<String, Integer> frequency) {
		List<String> middle = new ArrayList<>();
		Set<String> stems = frequency.keySet();
		for (String stem : stems) {
			if (frequency.get(stem) < 100 && frequency.get(stem) > 5) {
				middle.add(stem);
			}
		}
		return middle;
	}
	
	public static List<Integer> createVector(List<String> middle, String twit) {
		List<Integer> vector = new ArrayList<>();
		for (String stem : middle) {
			if (twit.contains(stem)) {
				vector.add(1);
			}
			else {
				vector.add(0);
			}
		}
		return vector;
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
}
