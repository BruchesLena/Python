package statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

public class TestingWithStat {

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		// TODO Auto-generated method stub

		HashMap<Entry, Integer> statistics = ReadingPOSStatistics.readStatistics();
		Set<Entry> entries = statistics.keySet();
		List<String> processed = new ArrayList<>();
		int right = 0;
		int ambig = 0;
		int proc = 0;
		int noStat = 0;
		int rightUnknown = 0;
		int ambigUnknown = 0;
		int Unknown = 0;
		
		String text1 = "";
		String text2 = "";
		String text3 = "";
		String text4 = "";
		
		File folder = new File("D:/Лена/NoAmbig");
		List<File> folderFiles = getXmlFiles(folder);
		for (int i = 2500; i < 2550; i++) { //�������� �������� i �� �����, �� ������� ���������� ������������
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
				
				//��� ���������� ����
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
						if (j > 0) {
							text1 = Statistics.getTextFromToken(nodeList.item(j-1));
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j-1))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j-1))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-1")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*1.00));
								}
							}
						}
						if (j > 1) {
							text2 = Statistics.getTextFromToken(nodeList.item(j-2));
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j-2))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j-2))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("-2")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.93));
								}
							}
						}
						if (j < nodeList.getLength() - 1) {
							text3 = Statistics.getTextFromToken(nodeList.item(j+1));
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j+1))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j+1))));
							for (int e = 0; e < current.size(); e++) {
								if (current.get(e).contextElement.fullSubstition.toString().equals(context.toString()) && current.get(e).contextElement.position.equals("+1")) {
									lemmas.add(new LemmaChoice(current.get(e).lemma, current.get(e).probability*0.97));
								}
							}
						}
						if (j < nodeList.getLength() - 2) {
							text4 = Statistics.getTextFromToken(nodeList.item(j+2));
							FullSubstitution context = new FullSubstitution(Statistics.getLastLetters(Statistics.getTextFromToken(nodeList.item(j+2))), Statistics.getSubstitution(Statistics.getTextFromToken(nodeList.item(j+2))));
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
				
				if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node))!=null && Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node)).size()>1) {
					HashSet<PartOfSpeech> partsOfSpeech = Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node));
					if (partsOfSpeech.contains(PartOfSpeech.VERB) && partsOfSpeech.contains(PartOfSpeech.INFN)) {
						continue;
					}
					
					String rightPOS = POSStatistics.getPOSFromToken(node);
					List<Entry> current = new ArrayList<>();
					List<POSChoice> parts = new ArrayList<>();
					proc++;
					for (Entry e : entries) {
						if (e.getPartsOfSpeech().toString().equals(Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node)).toString())) {
							current.add(e);
						}
					}
					if (current.isEmpty()) {
						noStat++;
						processed.add(Statistics.getTextFromToken(node) + " [No statistics/before]");
						continue;
					}
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
					if (parts.size() == 0) {
						noStat++;
						processed.add(Statistics.getTextFromToken(node) + " [No statistics/after]");
						continue;
					}
					else {
						parts = countLemmas(parts);
						POSChoice rightpart = chooseLemma(parts);
						if (rightpart.partOfSpeech.equals(rightPOS)) {
							right++;
							if (j > 0 && j < nodeList.getLength()-1) {
							processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]" + " " + Statistics.getTextFromToken(nodeList.item(j+1)));
							}
							else {
								processed.add(Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]");
							}
						}
						else {
							ambig++;
							if (j > 0 && j < nodeList.getLength()-1) {
							processed.add(Statistics.getTextFromToken(nodeList.item(j-1)) + " " + Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]; Right: " + rightPOS + " " + Statistics.getTextFromToken(nodeList.item(j+1)));
							}
							else {
								processed.add(Statistics.getTextFromToken(node) + " [" + rightpart.partOfSpeech + " " + rightpart.coefficient + "]; Right: " + rightPOS);
							}
						}
					}
				}
			}
		}		
		System.out.println("processed: " + proc);
		System.out.println("right: " + right);
		System.out.println("ambig: " + ambig);
		System.out.println("unknown: " + Unknown);
		System.out.println("rightUnknown: " + rightUnknown);
		System.out.println("ambigUnknown: " + ambigUnknown);
		System.out.println("noStat: " + noStat);
		Analysis.writeInFile(processed, "D:/Лена/Statistics/Results.txt", false);
	}
	
	public static List<POSChoice> countLemmas(List<POSChoice> parts) {
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
//			else if (!processed.contains(part)) {
//				processed.add(part);
//			}
//			else {
//				for (POSChoice lpro : processed) {
//					if (lpro.partOfSpeech.equals(part.partOfSpeech)) {
//						lpro.coefficient = lpro.coefficient + part.coefficient;						
//					}
//				}
//			}
		}
		return processed;
	}
	
	public static POSChoice chooseLemma(List<POSChoice> processed) {
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

	
	}


