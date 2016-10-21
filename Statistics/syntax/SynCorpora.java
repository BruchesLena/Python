package syntax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class SynCorpora {
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<Sentence> corpora = loadCorpora("D:/syntagrus in UTF");
	}
	
	public static List<Sentence> loadCorpora(String path) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		List<Sentence> corpora = new ArrayList<>();
		File folder = new File(path);
		List<File> folderFiles = getXmlFiles(folder);
		int ww = 0;
		for (int i = 0; i < folderFiles.size(); i++) {
			DocumentBuilderFactory f  = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(folderFiles.get(i)),"UTF-8")));
			NodeList nodeList = doc.getElementsByTagName("S");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Sentence sentence = new Sentence();
				Node node = nodeList.item(j);
				sentence.id = getSentenceID(node);
				NodeList words = getWords(node);
				List<WordForm> wordForms = new ArrayList<>();
				for (int w = 0; w < words.getLength(); w++) {
					Node word = words.item(w);
					wordForms.add(setWordForm(word));
				}
				sentence.words = wordForms;
				ww += wordForms.size();
				corpora.add(sentence);
			}
		}
		System.out.println("Sentences " + corpora.size());
		System.out.println("Words " + ww);
		return corpora;
	}
	
	public static NodeList getWords(Node node) {
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			return element.getElementsByTagName("W");
		}
		return null;
	}
	
	public static int getSentenceID(Node node) {
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			return Integer.parseInt(element.getAttribute("ID"));
		}
		return 0;
	}
	
	public static WordForm setWordForm(Node node) {
		WordForm wordForm = new WordForm();
		wordForm.word = node.getTextContent();
		if (node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element) node;
			String dom = element.getAttribute("DOM");
			if (dom.equals("_root")) {
				wordForm.dom = 0;
			}
			else {
			wordForm.dom = Integer.parseInt(element.getAttribute("DOM"));
			}
			wordForm.features = element.getAttribute("FEAT");
			wordForm.id = Integer.parseInt(element.getAttribute("ID"));
			wordForm.lemma = element.getAttribute("LEMMA");
			wordForm.link = element.getAttribute("LINK");
		}
		return wordForm;
	}
	
	private static List<File> getXmlFiles(File folder) {
		List<File> folderFiles = new ArrayList<File>();
		File[] files = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				boolean isTxt = name.endsWith(".tgt");
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
