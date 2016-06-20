import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ClassForBanks {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		
		HashSet<String> positive = new HashSet<String>();
		HashSet<String> negative = new HashSet<String>();
		HashSet<String> neutral = new HashSet<String>();
		//HashSet<String> vector = new HashSet<String>();
		List<String> twitsWithEval = parseFile("D:/Machine Learning/SentAnalysis/bank_train_2016.xml");


		//writeInFile(twitsWithEval, "D:/Machine Learning/SentAnalysis/Corpora.txt", false);
		for (String twit : twitsWithEval) {
			if (twit.endsWith("= 0")) {
				neutral.addAll(getWords(twit));
			}
			else if (twit.endsWith("= 1")) {
				positive.addAll(getWords(twit));
			}
			else if (twit.endsWith("= -1")) {
				negative.addAll(getWords(twit));
			}
		}
//		vector.addAll(neutral);
//		vector.addAll(positive);
//		vector.addAll(negative);
		
//		List<String> list = new ArrayList<String>();
//		for (String twit : twitsWithEval) {
//			List<Integer> currentVector = new ArrayList<Integer>();
//			for (String feature : vector) {
//				if (getWords(twit).contains(feature)) {
//					currentVector.add(1);
//				}
//				else {
//					currentVector.add(0);
//				}
//			}
//			list.add(currentVector.toString());
//		}
//		writeInFile(list, "D:/Machine Learning/SentAnalysis/Vectors.txt", false);	
		writeInFile(format(test("D:/Machine Learning/SentAnalysis/banks_test_etalon.xml", positive, negative, neutral)), "D:/Machine Learning/SentAnalysis/Results.txt", false);
		//System.out.println(getResults(test("D:/Machine Learning/SentAnalysis/banks_test_etalon.xml", positive, negative, neutral)));
		//writeInFile(parseFile("D:/Machine Learning/SentAnalysis/banks_test_etalon.xml"), "D:/Machine Learning/SentAnalysis/Test.txt", false);
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
				String current = " " + twits.get(i-2) + "}" + twits.get(i-1) + " = " + twits.get(i);
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
	
	public static HashSet<String> getWords(String twitWithEval) {
		HashSet<String> wordforms = new HashSet<String>();
		String twit = twitWithEval.substring(0, twitWithEval.indexOf(" = "));
		String[] words = twit.split("[ ,.!-?]");
		for (String str : words) {
			if (!str.equals("")) {
			wordforms.add(str.toLowerCase());
			}
		}
		return wordforms;
	}
	
	public static Integer classify(HashSet<String> positive, HashSet<String> negative, HashSet<String> neutral, String twit) {
		String[] words = twit.split("[ .,!-?]");
		int pos = 0;
		int neg = 0;
		int neut = 0;
		for (String word : words) {
			if (positive.contains(word.toLowerCase())) {
				pos++;
			}
			if (negative.contains(word.toLowerCase())) {
				neg++;
			}
			if (neutral.contains(word.toLowerCase())) {
				neut++;
			}
		}
		return chooseEmotion(pos, neg, neut);
	}
	
	public static Integer chooseEmotion(int pos, int neg, int neut) {
		List<Integer> data = new ArrayList<Integer>();
		data.add(pos);
		data.add(neg);
		data.add(neut);
		int current = 0;
		for (Integer i : data) {			
			if (i > current) {
				current = i;
			}
		}
		for (Integer i : data) {
			if (i == current) {
				if (i == pos) {
					return 1;
				}
				if (i == neg) {
					return -1;
				}
				if (i == neut) {
					return 0;
				}
			}
		}
		return 0;
	}
	
	public static List<String> test(String path, HashSet<String> positive, HashSet<String> negative, HashSet<String> neutral) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<String> twitsWithEval = parseFile(path);
		List<String> processed = new ArrayList<String>();
		for (String str : twitsWithEval) {
			int choice = classify(positive, negative, neutral, str);
			if (str.endsWith("= 0") && choice == 0) {
				processed.add(str + " " + choice);
			}
			else if (str.endsWith("= -1") && choice == -1) {
				processed.add(str + " " + choice);
			}
			else if (str.endsWith("= 1") && choice == 1) {
				processed.add(str + " " + choice);
			}
			else {
				processed.add(str + " " + choice);
			}
		}
		return processed;
	}
	
	public static List<String> format (List<String> processed) {
		List<String> result = new ArrayList<String>();
		for (String str : processed) {
			if (str.length() > 1) {
			String current = str.substring(1, str.indexOf("}")) + " " + str.substring(str.length() - 2);
			if (current.startsWith("1") |
					current.startsWith("2") |
					current.startsWith("3") |
					current.startsWith("4") |
					current.startsWith("5") |
					current.startsWith("6") |
					current.startsWith("7") |
					current.startsWith("8") |
					current.startsWith("9"))
			result.add(current);
		}
		}
		return result;
	}
	
	public static String getResults(List<String> processed) {
		int right = 0;
		int wrong = 0;
		for (String str : processed) {
			if (str.startsWith("+")) {
				right++;
			}
			else if (str.startsWith("-")) {
				wrong++;
			}
		}
		return "Right: " + right + " Wrong: " + wrong;
	}
	

}
