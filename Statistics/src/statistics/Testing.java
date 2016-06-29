package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;

public class Testing {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		HashMap<Entry, Integer> statistics = StatisticsReader.readStatistics();
		Set<Entry> entries = statistics.keySet();
		List<String> processed = new ArrayList<>();
		int right = 0;
		int ambig = 0;
		int proc = 0;
		int noStat = 0;
		
		File folder = new File("D:/Лена/NoAmbig");
		List<File> folderFiles = getXmlFiles(folder);
		for (int i = 2550; i < 2565; i++) { //изменить значения i на файлы, на которых проводится тестирование
			System.out.println(i);
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(folderFiles.get(i)),"UTF-8")));
			NodeList nodeList = doc.getElementsByTagName("token");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Node node = nodeList.item(j);
				if (getSubstitution(getTextFromToken(node)) == null) {
					if (Statistics.isNumber(getTextFromToken(node)) | Statistics.isPunctuationMark(getTextFromToken(node))) {
						continue;
					}
				}
				
				if (!Analysis.inDictionary(getTextFromToken(node)) && !Statistics.isNumber(getTextFromToken(node)) && !Statistics.isPunctuationMark(getTextFromToken(node))) {
					proc++;
					String lemma = getLemmaFromToken(node);
					String letters = Statistics.getLastLetters(getTextFromToken(node));
					List<Entry> current = new ArrayList<>();
					List<LemmaChoice> lemmas = new ArrayList<>();
					for (Entry entry : entries) {
						if (entry.homonym.lastLetters.equals(letters)) {
							current.add(entry);
						}
					}
					if (current.size() == 0) {
						continue;
					}
						if (j > 0) {
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j-1))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j-1))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-1")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*1.00));
								}
							}
						}
						if (j > 1) {
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j-2))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j-2))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-2")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.93));
								}
							}
						}
						if (j < nodeList.getLength() - 1) {
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j+1))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j+1))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+1")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.97));
								}
							}
						}
						if (j < nodeList.getLength() - 2) {
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j+2))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j+2))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+2")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.88));
								}
							}
						}
						if (lemmas.size() == 0) {						
							continue;
						}
						else {
							lemmas = Analysis.countLemmas(lemmas);				
							LemmaChoice rightLemma = chooseLemma(lemmas);
							if (Statistics.subToWord(getTextFromToken(nodeList.item(j)), rightLemma.lemma).equals(lemma)) {
								right++;
								processed.add(getTextFromToken(node) + " [ ? " + lemma + "]");
							}
							else {
								ambig++;
								processed.add(getTextFromToken(node) + " [ ? " + Statistics.subToWord(getTextFromToken(nodeList.item(j)), rightLemma.lemma) + "]; Right: " + lemma);
							}
						}
					}			
				
				if (getSubstitution(getTextFromToken(node)) != null && getSubstitution(getTextFromToken(node)).size() > 1) {
					String lemma = getLemmaFromToken(node);
					List<Substitution> substitutions = getSubstitution(getTextFromToken(node));
					List<Entry> current = new ArrayList<>();
					List<LemmaChoice> lemmas = new ArrayList<>();
					proc++;
					for (Entry e : entries) {
						if (e.homonym.substitutions.toString().equals(substitutions.toString())) {
							current.add(e);
						}
					}
					if (current.isEmpty()) {
						noStat++;
						processed.add(getTextFromToken(node) + " [No statistics/before]");
						continue;
					}
					if (j > 0) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j-1))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j-1))));
						for (Entry ent : current) {
							if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("-1")) {
								lemmas.add(new LemmaChoice(ent.lemma, ent.probability*1.00));
							}
						}
					}
					if (j > 1) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j-2))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j-2))));
						for (Entry ent : current) {
							if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("-2")) {
								lemmas.add(new LemmaChoice(ent.lemma, ent.probability*0.93));
							}
						}
					}
					if (j < nodeList.getLength() - 1) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j+1))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j+1))));
						for (Entry ent : current) {
							if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("+1")) {
								lemmas.add(new LemmaChoice(ent.lemma, ent.probability*0.97));
							}
						}
					}
					if (j < nodeList.getLength() - 2) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(getTextFromToken(nodeList.item(j+2))), Statistics.getSubstitution(getTextFromToken(nodeList.item(j+2))));
						for (Entry ent : current) {
							if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("+2")) {
								lemmas.add(new LemmaChoice(ent.lemma, ent.probability*0.88));
							}
						}
					}
					if (lemmas.size() == 0) {
						noStat++;
						processed.add(getTextFromToken(node) + " [No statistics/after]");
						continue;
					}
					else {
						lemmas = Analysis.countLemmas(lemmas);
						LemmaChoice rightLemma = chooseLemma(lemmas);
						if (Statistics.subToWord(getTextFromToken(nodeList.item(j)), rightLemma.lemma).equals(lemma)) {
							right++;
							processed.add(getTextFromToken(node) + " [" + lemma + "]");
						}
						else {
							ambig++;
							processed.add(getTextFromToken(node) + " [" + Statistics.subToWord(getTextFromToken(nodeList.item(j)), rightLemma.lemma) + "]; Right: " + lemma);
						}
					}
				}
			}
		}
		System.out.println("processed: " + proc);
		System.out.println("right: " + right);
		System.out.println("ambig: " + ambig);
		System.out.println("noStat: " + noStat);	
		writeInFile(processed, "D:/Лена/Statistics/Results.txt", false);
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
	
	public static String getTextFromToken(Node node) {
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			return element.getAttribute("text").toLowerCase();
		}
		return null;
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
	
	public static void writeInFile(List<String> data, String path, boolean b) throws IOException {
		try (FileWriter writer = new FileWriter(path, b)) {
			for (String s : data) {
				writer.write(s + "\n");
				writer.flush();
			}
		}
	}
	
	public static LemmaChoice chooseLemma(List<LemmaChoice> processed) {
		double cur = 0;
		for (LemmaChoice lemma : processed) {
			if (lemma.coefficient > cur) {
				cur = lemma.coefficient;
			}
		}
		for (LemmaChoice lemma : processed) {
			if (lemma.coefficient == cur) {
				return lemma;
			}
		}
		return null;
	}

}
