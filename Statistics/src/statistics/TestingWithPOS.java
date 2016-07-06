package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analisys.tests.util.TestingUtil;
import com.onpositive.text.analysis.CombinedMorphologicParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.AdditionalMetadataHandler;

public class TestingWithPOS {

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated method stub

		HashMap<Entry, Integer> statistics = ReadingPOSStatistics.readStatistics();
		Set<Entry> entries = statistics.keySet();
		List<String> processed = new ArrayList<>();
		List<String> coefficients = new ArrayList<>();
		int right = 0;
		int ambig = 0;
		int proc = 0;
		int noStat = 0;
		int rightUnknown = 0;
		int ambigUnknown = 0;
		int Unknown = 0;
		int noAmbig = 0;
		
		double combined = 1;
		double stat = 1;
		
		File folder = new File("D:/Лена/NoAmbig");
		List<File> folderFiles = getXmlFiles(folder);
		for (int k = 0; k < 5; k++) {
			combined = combined - 0.05;
			stat = 1;
			for (int l = 0; l < 5; l++) {
				stat = stat - 0.05;
				right = 0;
				proc = 0;
//			}
//		}
		
		for (int i = 2502; i < 2503; i++) { //изменить значения i на файлы, на которых проводится тестирование
			System.out.println(i);
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(folderFiles.get(i)),"UTF-8")));
			NodeList nodeList = doc.getElementsByTagName("token");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Node node = nodeList.item(j);
				if (Statistics.getSubstitution(Statistics.getTextFromToken(node)) == null) {
					if (Statistics.isNumber(Statistics.getTextFromToken(node)) | Statistics.isPunctuationMark(Statistics.getTextFromToken(node))) {
						continue;
					}
					//else {
					//processed.add(getTextFromToken(node) + " [Unknown]");
					//continue;
					//}
				}
				
				//для незнакомых слов
				if (!Analysis.inDictionary(Statistics.getTextFromToken(node)) && !Statistics.isNumber(Statistics.getTextFromToken(node)) && !Statistics.isPunctuationMark(Statistics.getTextFromToken(node))) {
					proc++;
					Unknown++;
					HashMap<Entry, Integer> statForUnknown = StatisticsReader.readStatistics();
					Set<Entry> entriesForUnknown = statForUnknown.keySet();
					String rightPOS = POSStatistics.getPOSFromToken(node);
					String lemma = Statistics.getLemmaFromToken(node);
					String letters = Statistics.getLastLetters(Statistics.getTextFromToken(node));
					List<Entry> current = new ArrayList<>();
					List<LemmaChoice> lemmas = new ArrayList<>();					
					for (Entry entry : entriesForUnknown) {
						if (entry.homonym.lastLetters.equals(letters)) {
							current.add(entry);
						}
					}
					if (current.size() == 0) {
						continue;
					}
						getContextsForUnknownWords(nodeList, j, current, lemmas);
						if (lemmas.size() == 0) {						
							continue;
						}
						else {
							lemmas = Analysis.countLemmas(lemmas);				
							LemmaChoice rightLemma = Testing.chooseLemma(lemmas);
							HashMap<FullSubWithPOS, Integer> statForGuessing = ReadingStatForGuessing.readStatistics();
							Set<FullSubWithPOS> fullSubsWithPOS = statForGuessing.keySet();
							String pos = "";
							for (FullSubWithPOS fs : fullSubsWithPOS) {
								if (fs.fullSubstitution.lastLetters.equals(Statistics.getLastLetters(Statistics.subToWord(Statistics.getTextFromToken(nodeList.item(j)), rightLemma.lemma)))) {
									pos = fs.partOfSpeech;
								}
							}
							
							if (Statistics.subToWord(Statistics.getTextFromToken(nodeList.item(j)), rightLemma.lemma).equals(lemma) && rightPOS.equals(pos)) {
								rightUnknown++;
								if (j > 0 && j < nodeList.getLength()-1) {
								processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + Statistics.getTextFromToken(node) + " [ ? " + lemma + " " + pos + "]" + Statistics.getTextFromToken(nodeList.item(j+1)));
								}
								else {
									processed.add(Statistics.getTextFromToken(node) + " [ ? " + lemma + " " + pos + "]");
								}
							}
							else {
								ambigUnknown++;
								if (j > 0 && j < nodeList.getLength()-1) {
								processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + Statistics.getTextFromToken(node) + " [ ? " + Statistics.subToWord(Statistics.getTextFromToken(nodeList.item(j)), rightLemma.lemma) + " " + pos + "]; Right: " + lemma + " " + rightPOS + " " + Statistics.getTextFromToken(nodeList.item(j+1)));
								}
								else {
									processed.add(Statistics.getTextFromToken(node) + " [ ? " + Statistics.subToWord(Statistics.getTextFromToken(nodeList.item(j)), rightLemma.lemma) + " " + pos + "]; Right: " + lemma + " " + rightPOS);
								}
							}
						}
					}			
				
				// для слов с омонимией
				if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node))!=null && Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node)).size()>1) {
					HashSet<PartOfSpeech> partsOfSpeech = Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node));
					if (partsOfSpeech.contains(PartOfSpeech.VERB) && partsOfSpeech.contains(PartOfSpeech.INFN)) {
						continue;
					}
					
					HashSet<PartOfSpeech> posWithCombApproach = chooseWithCombinedApproach(nodeList, j);
					
					String rightPOS = POSStatistics.getPOSFromToken(node);
					List<Entry> current = new ArrayList<>();
					List<POSChoice> parts = new ArrayList<>();
					proc++;
					for (Entry e : entries) {
						if (e.getPartsOfSpeech().toString().equals(Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node)).toString())) {
							current.add(e);
						}
					}
//					if (current.isEmpty()) {
//						noStat++;
//						processed.add(Statistics.getTextFromToken(node) + " [No statistics/before]");
//						continue;
//					}
					getContextsForWordsInDict(nodeList, j, current, parts);
//					if (parts.size() == 0) {
//						noStat++;
//						processed.add(Statistics.getTextFromToken(node) + " [No statistics/after]");
//						continue;
//					}
//					else {
						parts = countLemmas(parts);
						POSChoice rightpart = chooseLemma(parts);
						HashSet<PartOfSpeech> result = new HashSet<>();
						if (rightpart == null) {
							result = getResultPOSWithCoef(posWithCombApproach, null, combined, stat);
						}
						else {
							result = getResultPOSWithCoef(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech), combined, stat);
						}
						if (result.contains(toPartOfSpeech(rightPOS))) {
							if (result.size() == 1) {
								right++;
								noAmbig++;
								if (j > 0 && j < nodeList.getLength()-1 && rightpart!=null) {
									processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + result.toString() + " (" + visualizeChoice(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech)) + ")] " + Statistics.getTextFromToken(nodeList.item(j+1)));
								}
								else if (rightpart!=null) {
									processed.add(Statistics.getTextFromToken(node) + " [" + result.toString() + " (" + visualizeChoice(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech)) + ")]");
								}
							}
							if (result.size() > 1) {
								right++;
								if (j > 0 && j < nodeList.getLength()-1 && rightpart!=null) {
									processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + result.toString() + " (" + visualizeChoice(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech)) + ")] " + Statistics.getTextFromToken(nodeList.item(j+1)));
								}
								else if (rightpart!=null) {
									processed.add(Statistics.getTextFromToken(node) + " [" + result.toString() + " (" + visualizeChoice(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech)) + ")]");
								}
							}
						}
						else {
							ambig++;
							if (j > 0 && j < nodeList.getLength()-1 && rightpart!=null) {
								processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + result.toString() + " (" + visualizeChoice(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech)) + ")]; Right: " + rightPOS + " " + Statistics.getTextFromToken(nodeList.item(j+1)));
							}
							else if (rightpart!=null) {
								processed.add(Statistics.getTextFromToken(node) + " [" + result.toString() + " (" + visualizeChoice(posWithCombApproach, toPartOfSpeech(rightpart.partOfSpeech)) + ")]; Right: " + rightPOS);
							}
						}
//						if (rightpart.partOfSpeech.equals(rightPOS)) {
//							right++;
//							if (j > 0 && j < nodeList.getLength()-1) {
//							processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]" + " " + Statistics.getTextFromToken(nodeList.item(j+1)));
//							}
//							else {
//								processed.add(Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]");
//							}
//						}
//						else {
//							ambig++;
//							if (j > 0 && j < nodeList.getLength()-1) {
//							processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]; Right: " + rightPOS + " " + Statistics.getTextFromToken(nodeList.item(j+1)));
//							}
//							else {
//								processed.add(Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]; Right: " + rightPOS);
//							}
//						}
					//}
				}
			}
			coefficients.add(addResults(combined, stat, right));
		}	
	}
}
//		System.out.println("processed: " + proc);
//		System.out.println("right: " + right);
//		System.out.println("wrong: " + ambig);
//		System.out.println("noAmbig: " + noAmbig);
//		System.out.println("unknown: " + Unknown);
//		System.out.println("rightUnknown: " + rightUnknown);
//		System.out.println("ambigUnknown: " + ambigUnknown);
//		System.out.println("noStat: " + noStat);
		Analysis.writeInFile(coefficients, "D:/Лена/Statistics/Coef.txt", false);
	}

	public static void getContextsForWordsInDict(NodeList nodeList, int j,
			List<Entry> current, List<POSChoice> parts) {
		if (j > 0) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j-1))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j-1))));
			for (Entry ent : current) {
				if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("-1")) {
					parts.add(new POSChoice(ent.partOfSpeech, ent.probability*1.00));
					break;
				}
			}
		}
		if (j > 1) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j-2))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j-2))));
			for (Entry ent : current) {
				if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("-2")) {
					parts.add(new POSChoice(ent.partOfSpeech, ent.probability*0.93));
					break;
				}
			}
		}
		if (j < nodeList.getLength() - 1) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j+1))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j+1))));
			for (Entry ent : current) {
				if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("+1")) {
					parts.add(new POSChoice(ent.partOfSpeech, ent.probability*0.97));
					break;
				}
			}
		}
		if (j < nodeList.getLength() - 2) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j+2))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j+2))));
			for (Entry ent : current) {
				if (ent.contextElement.fullSubstition.toString().equals(context.toString()) && ent.contextElement.position.equals("+2")) {
					parts.add(new POSChoice(ent.partOfSpeech, ent.probability*0.88));
					break;
				}
			}
		}
	}

	public static void getContextsForUnknownWords(NodeList nodeList, int j,
			List<Entry> current, List<LemmaChoice> lemmas) {
		if (j > 0) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j-1))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j-1))));
			for (int e = 0; e < current.size(); e++) {
				if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-1")) {
					lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*1.00));
				}
			}
		}
		if (j > 1) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j-2))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j-2))));
			for (int e = 0; e < current.size(); e++) {
				if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-2")) {
					lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.93));
				}
			}
		}
		if (j < nodeList.getLength() - 1) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j+1))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j+1))));
			for (int e = 0; e < current.size(); e++) {
				if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+1")) {
					lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.97));
				}
			}
		}
		if (j < nodeList.getLength() - 2) {
			FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j+2))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j+2))));
			for (int e = 0; e < current.size(); e++) {
				if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+2")) {
					lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.88));
				}
			}
		}
	}
	
	public static List<POSChoice> countLemmas(List<POSChoice> parts) {
		if (parts.size() == 0) {
			return null;
		}
		List<POSChoice> processed = new ArrayList<>();
		for (POSChoice part : parts) {
			if (processed.size() == 0) {
				processed.add(part);
			}
			else {
			for (int i = 0; i < processed.size(); i++) {
				if (i == processed.size()-1 && !processed.get(i).partOfSpeech.equals(part.partOfSpeech)) {
					processed.add(part);
					break;
				}
				if (processed.get(i).partOfSpeech.equals(part.partOfSpeech)) {
					processed.get(i).coefficient = processed.get(i).coefficient + part.coefficient;
					break;
				}
			}
			}
		}
		return processed;
	}
	
	public static POSChoice chooseLemma(List<POSChoice> processed) {
		if (processed == null) {
			return null;
		}
		double cur = 0;
		for (POSChoice part : processed) {
			if (part.coefficient > cur) {
				cur = part.coefficient;
			}
		}
		for (POSChoice part : processed) {
			if (part.coefficient == cur) {
				return part;
			}
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
	
	public static PartOfSpeech toPartOfSpeech(String pos) {
		if (pos.equals("ADJF")) {
			return PartOfSpeech.ADJF;
		}
		if (pos.equals("ADJS")) {
			return PartOfSpeech.ADJS;
		}
		if (pos.equals("ADVB")) {
			return PartOfSpeech.ADVB;
		}
		if (pos.equals("COMP")) {
			return PartOfSpeech.COMP;
		}
		if (pos.equals("CONJ")) {
			return PartOfSpeech.CONJ;
		}
		if (pos.equals("GRND")) {
			return PartOfSpeech.GRND;
		}
		if (pos.equals("INFN")) {
			return PartOfSpeech.INFN;
		}
		if (pos.equals("INTJ")) {
			return PartOfSpeech.INTJ;
		}
		if (pos.equals("NOUN")) {
			return PartOfSpeech.NOUN;
		}
		if (pos.equals("NPRO")) {
			return PartOfSpeech.NPRO;
		}
		if (pos.equals("NUMR")) {
			return PartOfSpeech.NUMR;
		}
		if (pos.equals("PRCL")) {
			return PartOfSpeech.PRCL;
		}
		if (pos.equals("PRED")) {
			return PartOfSpeech.PRED;
		}
		if (pos.equals("PREP")) {
			return PartOfSpeech.PREP;
		}
		if (pos.equals("PRTF")) {
			return PartOfSpeech.PRTF;
		}
		if (pos.equals("PRTS")) {
			return PartOfSpeech.PRTS;
		}
		if (pos.equals("VERB")) {
			return PartOfSpeech.VERB;
		}
		return null;
	}
	
	public static HashSet<PartOfSpeech> chooseWithCombinedApproach(NodeList nodeList, int j) {
		if (j > 0 && j < nodeList.getLength()-1) {
			String textFromToken = Statistics.getTextFromToken(nodeList.item(j));
			String str = Statistics.getTextFromToken(nodeList.item(j-1)) + " " + textFromToken + " " + Statistics.getTextFromToken(nodeList.item(j+1));
			return chooseWithCombinedApproach(
					str, textFromToken);
		}
		return new HashSet<PartOfSpeech>();
	}

	public static HashSet<PartOfSpeech> chooseWithCombinedApproach(
			String str, String textFromToken) {
		HashSet<PartOfSpeech> partsOfSpeech = new HashSet<>();
		List<IToken> wordFormTokens = TestingUtil.getWordFormTokens(str);
		AdditionalMetadataHandler.setStoreMetadata(true);
		CombinedMorphologicParser cmp = new CombinedMorphologicParser();
		List<IToken> processed = cmp.process(wordFormTokens);
		for (IToken proc : processed) {
			if (!proc.hasCorrelation() || proc.getCorrelation() > 0.05) {
				String shortStringValue = proc.getShortStringValue().trim();
				if (proc.getShortStringValue().trim().equals(textFromToken)) {
					if (proc instanceof SyntaxToken) {
						SyntaxToken ch = (SyntaxToken) proc;
						partsOfSpeech.add(ch.getPartOfSpeech());
					}
				
			}
			}
		}
		return partsOfSpeech;
	}
	
//	public static HashMap<PartOfSpeech, Double> normalizeCoefficients(List<IToken> processed) {
//		HashMap<PartOfSpeech, Double> probabilities = new HashMap<>();
//		double general = 0;
//		for (IToken token : processed) {
//			general += token.getCorrelation();
//		}
//	}
//	
	public static HashSet<PartOfSpeech> getResultPOS(HashSet<PartOfSpeech> posWithCombined, PartOfSpeech posWithStat) {
		HashSet<PartOfSpeech> result = new HashSet<>();
		if (posWithStat == null) {
			result.addAll(posWithCombined);
			return result;
		}
		if (posWithCombined.contains(posWithStat)) {
			result.add(posWithStat);
		}
		else {
			result.addAll(posWithCombined);
			result.add(posWithStat);
		}
		return result;
	}
	
	public static HashSet<PartOfSpeech> getResultPOSWithCoef(HashSet<PartOfSpeech> posWithCombined, PartOfSpeech posWithStat, double combined, double stat) {
		HashSet<PartOfSpeech> result = new HashSet<>();
		HashMap<PartOfSpeech, Double> counting = new HashMap<>();
		counting.put(posWithStat, stat);
		for (PartOfSpeech pos : posWithCombined) {
			if (counting.containsKey(pos)) {
				double value = counting.get(pos) + combined;
				counting.put(pos, value);
			}
			else {
				counting.put(pos, combined);
			}
		}
		double current = 0;
		for (PartOfSpeech pos : counting.keySet()) {
			if (counting.get(pos) > current) {
				current = counting.get(pos);
			}
		}
		for (PartOfSpeech pos : counting.keySet()) {
			if (counting.get(pos) == current) {
				result.add(pos);
			}
		}
		return result;
	}
	
	public static String addResults(double combined, double stat, int right) {
		return combined + " " + stat + " " + right;
	}
	
	public static String visualizeChoice(HashSet<PartOfSpeech> posWithCombined, PartOfSpeech posWithStat) {
		if (posWithStat == null) {
			return "Combined Approach: " + posWithCombined.toString() + " Statistics: No Statistics";
		}
		return "Combined Approach: " + posWithCombined.toString() + " Statistics: " + posWithStat;
	}

	
	}


