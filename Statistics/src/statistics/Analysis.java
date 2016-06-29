package statistics;

import java.io.BufferedReader;
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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analisys.tests.util.TestingUtil;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.utils.MorphologicUtils;

public class Analysis {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		HashMap<Entry, Integer> dictionary = StatisticsReader.readStatistics();
		Set<Entry> entries = dictionary.keySet();
		List<String> results = new ArrayList<>();
		File f = new File("D:/Лена/Statistics/Text.txt");
		String text = "";
		final int length = (int) f.length();
		if (length != 0) {
			char[] cbuf = new char[length];
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "UTF-8");
			final int read = isr.read(cbuf);
			text = new String (cbuf, 0, read);
			isr.close();
		}

			List<IToken> sentences = TestingUtil.getSentences(text);
			for (IToken sentence : sentences) {
				List<String> wordForms = new ArrayList<>();
				List<IToken> words = sentence.getChildren();
				List<IToken> chain = MorphologicUtils.getWithNoConflicts(words);
				for (IToken token : chain) {
					if (token.getShortStringValue().endsWith(" ")) {
						wordForms.add(token.getShortStringValue().substring(0, token.getShortStringValue().length()-1));
					}
					else {
						wordForms.add(token.getShortStringValue());
					}
				}
				List<String> processed = analyze(wordForms, entries);
				results.addAll(processed);
			}
			
		writeInFile(results, "D:/Лена/Statistics/Results.txt", false);		
		
	}
	
	public static List<IToken> tokens(String line) {
		final List<IToken> tokenize = new PrimitiveTokenizer().tokenize(line);
		final WordFormParser wordFormParser = new WordFormParser(
				WordNetProvider.getInstance());
		wordFormParser.setIgnoreCombinations(true);
		final List<IToken> process = wordFormParser.process(tokenize);
		return process;
	}
	
	public static HashSet<String> getBasicForms(String wordForm) {
		HashSet<String> basicForms = new HashSet<>();
		AbstractWordNet instance = WordNetProvider.getInstance();
		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms(wordForm);
		if (possibleGrammarForms == null) {
			return null;
		}
		for (GrammarRelation g : possibleGrammarForms) {
			TextElement textElement = g.getWord();
			String basicForm = textElement.getBasicForm();
			basicForms.add(basicForm);
		}
		return basicForms;
	}
	
	public static List<String> analyze(List<String> wordForms, Set<Entry> entries) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
				
		List<String> processed = new ArrayList<>();
		for (int i = 0; i < wordForms.size(); i++) {
			List<LemmaChoice> lemmas = new ArrayList<>();
			if (!inDictionary(wordForms.get(i)) && !Statistics.isNumber(wordForms.get(i)) && !Statistics.isPunctuationMark(wordForms.get(i)) && !wordForms.get(i).equals("") && !wordForms.get(i).equals("\r")) {
				
				String letters = Statistics.getLastLetters(wordForms.get(i));
				List<Entry> current = new ArrayList<>();
				for (Entry entry : entries) {
					if (entry.homonym.lastLetters.equals(letters)) {
						current.add(entry);
					}
				}
				if (current.size() == 0) {
					processed.add(wordForms.get(i) + " [No statictics before]");
					continue;
				}
					if (i > 0) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i-1)), Statistics.getSubstitution(wordForms.get(i-1)));
						for (int e = 0; e < current.size(); e++) {
							if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-1")) {
								lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*1.00));
							}
						}
					}
					if (i > 1) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i-2)), Statistics.getSubstitution(wordForms.get(i-2)));
						for (int e = 0; e < current.size(); e++) {
							if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-2")) {
								lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.93));
							}
						}
					}
					if (i < wordForms.size() - 1) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i+1)), Statistics.getSubstitution(wordForms.get(i+1)));
						for (int e = 0; e < current.size(); e++) {
							if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+1")) {
								lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.97));
							}
						}
					}
					if (i < wordForms.size() - 2) {
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i+2)), Statistics.getSubstitution(wordForms.get(i+2)));;
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
						List<LemmaChoice> rightLemmas = Analysis.chooseLemma(lemmas);
						for (LemmaChoice rightLemma : rightLemmas) {
						processed.add(i + wordForms.get(i) + " [ ? " + Statistics.subToWord(wordForms.get(i), rightLemma.lemma) + " (" + rightLemma.coefficient + ")]");
						}
						continue;
					}
				}			
			if (getBasicForms(wordForms.get(i)) == null) {
				continue;
			}
			if (getBasicForms(wordForms.get(i)).size() == 1) {
				processed.add(wordForms.get(i) + " [" + getBasicForms(wordForms.get(i)) + "]");
				continue;
			}
			if (getBasicForms(wordForms.get(i)).size() > 1) {
				List<Substitution> substitutions = Statistics.getSubstitution(wordForms.get(i));
				List<Entry> current = new ArrayList<>();
				for (Entry entry : entries) {
					if (entry.homonym.substitutions.toString().equals(substitutions.toString())) {						
						current.add(entry);
					}
				}
				if (current.size() == 0) {
					processed.add(wordForms.get(i) + " [No statictics before]");
					continue;
				}
				if (i > 0) {
					FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i-1)), Statistics.getSubstitution(wordForms.get(i-1)));
					for (int e = 0; e < current.size(); e++) {
						if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-1")) {
							lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*1.00));
						}
					}
				}
				if (i > 1) {
					FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i-2)), Statistics.getSubstitution(wordForms.get(i-2)));
					for (int e = 0; e < current.size(); e++) {
						if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-2")) {
							lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.93));
						}
					}
				}
				if (i < wordForms.size() - 1) {
					FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i+1)), Statistics.getSubstitution(wordForms.get(i+1)));
					for (int e = 0; e < current.size(); e++) {
						if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+1")) {
							lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.97));
						}
					}
				}
				if (i < wordForms.size() - 2) {
					FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i+2)), Statistics.getSubstitution(wordForms.get(i+2)));;
					for (int e = 0; e < current.size(); e++) {
						if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+2")) {
							lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.88));
						}
					}
				}
				if (lemmas.size() == 0) {
					processed.add(wordForms.get(i) + " [No statictics after]");
					continue;
				}
				else {
					lemmas = countLemmas(lemmas);				
					List<LemmaChoice> rightLemmas = chooseLemma(lemmas);
					for (LemmaChoice rightLemma : rightLemmas) {
					processed.add(i + wordForms.get(i) + " [" + Statistics.subToWord(wordForms.get(i), rightLemma.lemma) + " (" + rightLemma.coefficient + ")]");
					}
				}
			}			
		}
		return processed;
	}

	
	public static List<LemmaChoice> countLemmas(List<LemmaChoice> lemmas) {
		List<LemmaChoice> processed = new ArrayList<>();
		for (LemmaChoice lemma : lemmas) {
			if (processed.size() == 0) {
				processed.add(lemma);
			}
			else {
				for (LemmaChoice lpro : processed) {
					if (lpro.lemma.toString().equals(lemma.lemma.toString())) {
						lpro.coefficient = lpro.coefficient + lemma.coefficient;
					}
				}
			}
		}
		return processed;
	}
	
	public static List<LemmaChoice> chooseLemma(List<LemmaChoice> processed) {
		List<LemmaChoice> rightLemmas = new ArrayList<>();
		double cur = 0;
		for (LemmaChoice lemma : processed) {
			if (lemma.coefficient > cur) {
				cur = lemma.coefficient;
			}
		}
		for (LemmaChoice lemma : processed) {
			if (lemma.coefficient == cur) {
				rightLemmas.add(lemma);
			}
		}
		return rightLemmas;
	}
	
	public static boolean inDictionary(String word) {
		AbstractWordNet instance = WordNetProvider.getInstance();
		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms(word);
		if (possibleGrammarForms == null) {
			return false;
		}
		return true;
	}
	
	public static void writeInFile(List<String> data, String path, boolean b) throws IOException {
		try (FileWriter writer = new FileWriter(path, b)) {
			for (String s : data) {
				writer.write(s + "\n");
				writer.flush();
			}
		}
	}
	


}
