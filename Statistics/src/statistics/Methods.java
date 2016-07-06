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
import java.util.HashSet;
import java.util.List;

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
import com.onpositive.text.analysis.utils.MorphologicUtils;

public class Methods {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		
		int right = 0;
		int wrong = 0;
		int process = 0;
		int noAmbig = 0;
		
		File folder = new File("D:/Лена/NoAmbig");
		List<File> folderFiles = getXmlFiles(folder);
		for (int i = 2500; i < 2550; i++) { //изменить значения для тестирования
			System.out.println(i);
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new InputStreamReader(new FileInputStream(folderFiles.get(i)),"UTF-8")));
			NodeList nodeList = doc.getElementsByTagName("token");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Node node = nodeList.item(j);
				if (Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node))!=null && Statistics.getPartsOfSpeech(Statistics.getTextFromToken(node)).size() > 1) {
					process++;
					PartOfSpeech rightPOS = TestingWithPOS.toPartOfSpeech(Statistics.getPOSFromToken(node));
					HashSet<PartOfSpeech> chosenParts = TestingWithPOS.chooseWithCombinedApproach(nodeList, j);
					if (chosenParts.contains(rightPOS)) {
						right++;
						if (chosenParts.size() == 1) {
							noAmbig++;
						}
					}
					else {
						wrong++;
					}
				}
			}
		}
		System.out.println("Processed: " + process);
		System.out.println("Right: " + right);
		System.out.println("Wrong: " + wrong);
		System.out.println("noAmbig: " + noAmbig);
		

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
