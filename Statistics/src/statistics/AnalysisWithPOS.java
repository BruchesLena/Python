package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.onpositive.text.analisys.tests.util.TestingUtil;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.utils.MorphologicUtils;

public class AnalysisWithPOS {

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated method stub
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
		
		List<String> processed = analyze(text);
		Testing.writeInFile(processed, "D:/Лена/Statistics/Results.txt", false);
	}
	
	public static List<String> analyze(String text) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		
		List<String> wordForms = new ArrayList<>();
		List<IToken> sentences = TestingUtil.getSentences(text);
		for (IToken sentence : sentences) {			
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
		}
		
		List<String> processed = new ArrayList<>();
		for (int i = 0; i < wordForms.size(); i++) {
			List<LemmaChoice> lemmas = new ArrayList<>();
			if (!Analysis.inDictionary(wordForms.get(i)) && !Statistics.isNumber(wordForms.get(i)) && !Statistics.isPunctuationMark(wordForms.get(i)) && !wordForms.get(i).equals("") && !wordForms.get(i).equals("\r")) {
				
				Set<Entry> entriesForUnknown = StatisticsReader.readStatistics().keySet();
				String letters = Statistics.getLastLetters(wordForms.get(i));
				List<Entry> current = new ArrayList<>();
				for (Entry entry : entriesForUnknown) {
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
						FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(wordForms.get(i+2)), Statistics.getSubstitution(wordForms.get(i+2)));
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
						HashMap<FullSubWithPOS, Integer> statForGuessing = ReadingStatForGuessing.readStatistics();
						Set<FullSubWithPOS> fullSubsWithPOS = statForGuessing.keySet();
						String pos = "";

						for (LemmaChoice rightLemma : rightLemmas) {
							for (FullSubWithPOS fs : fullSubsWithPOS) {
								if (fs.fullSubstitution.lastLetters.equals(Statistics.getLastLetters(Statistics.subToWord(wordForms.get(i), rightLemma.lemma)))) {
									pos = fs.partOfSpeech;
								}
							}
						processed.add(i + wordForms.get(i) + " [ ? " + Statistics.subToWord(wordForms.get(i), rightLemma.lemma) + " " + pos + " (" + rightLemma.coefficient + ")]");
						}
						continue;
					}
				}			
			if (Analysis.getBasicForms(wordForms.get(i)) == null) {
				continue;
			}
			if (Analysis.getBasicForms(wordForms.get(i)).size() == 1) {
				processed.add(wordForms.get(i) + " [" + Analysis.getBasicForms(wordForms.get(i)) + "]");
				continue;
			}
			if (Analysis.getBasicForms(wordForms.get(i)).size() > 1) {
				Set<Entry> entries = ReadingPOSStatistics.readStatistics().keySet();
				List<Substitution> substitutions = Statistics.getSubstitution(wordForms.get(i));
				List<Entry> current = new ArrayList<>();
				for (Entry entry : entries) {
					if (entry.homonym!=null && entry.homonym.substitutions.toString().equals(substitutions.toString())) {						
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
					lemmas = Analysis.countLemmas(lemmas);				
					List<LemmaChoice> rightLemmas = Analysis.chooseLemma(lemmas);
					for (LemmaChoice rightLemma : rightLemmas) {
					processed.add(i + wordForms.get(i) + " [" + Statistics.subToWord(wordForms.get(i), rightLemma.lemma) + " (" + rightLemma.coefficient + ")]");
					}
				}
			}			
		}
		return processed;
	}

}
