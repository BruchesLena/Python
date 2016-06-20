import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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


public class Vectorising {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException{
		// TODO Auto-generated method stub

		writeInFile(format(parseFile("D:/Machine Learning/SentAnalysis/bank_train_2016.xml")), "D:/Machine Learning/SentAnalysis/Vectors.txt", false);
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
}
