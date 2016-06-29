package statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;

public class TestEntry {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Entry test = getEntry("мы", "мен€ем", "мен€ю");
		System.out.println(test.toString());
	}
	public static Entry getEntry(String previousWord, String homonym, String lemma) {
		ContextElement prev = new ContextElement(new FullSubstitution(getLastLetters(previousWord), getSubstitution(previousWord)));
		FullSubstitution hom = new FullSubstitution(getLastLetters(homonym), getSubstitution(homonym));
		FullSubstitution lem = getLemma(homonym, lemma);		
		Entry entry = new Entry(hom, prev, lem.substitutions.get(0));
		return entry;		
	}
	
	public static FullSubstitution getLemma(String word, String lemma) {
		List<Substitution> substitutions = new ArrayList<>();
		int end = 0;
		String flex = null;
		outer:	for (int w = 0; w < word.length(); w++) {
			for (int b = 0; b < lemma.length(); b++) {
				if (word.charAt(w) != lemma.charAt(b)) {
					end = word.length() - w;
					flex = lemma.substring(b);
					substitutions.add(new Substitution(end, flex));
					break outer;
				}
				if (b == lemma.length()-1 && w < word.length()-1) {
					end = word.length() - b -1;
					flex = "";
					substitutions.add(new Substitution(end, flex));
					break outer;
				}
				if (w == word.length() - 1 && b < lemma.length()-1) {
					end = 0;
					flex = lemma.substring(b+1);
					substitutions.add(new Substitution(end, flex));
					break outer;
				}
				if (word.charAt(w) == lemma.charAt(b)) {
					if (w == word.length()-1 && b == lemma.length()-1) {
						substitutions.add(new Substitution(0, ""));
						break outer;
					}
					w++;
					continue;
				}

			}
		}
		return new FullSubstitution(getLastLetters(lemma), substitutions);
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
					if (b == baseForm.length()-1 && w < word.length()-1 && word.charAt(w) == baseForm.charAt(b)) {
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
					if (word.charAt(w) != baseForm.charAt(b)) {
						end = word.length() - w;
						flex = baseForm.substring(b);
						substitutions.add(new Substitution(end, flex));
						break outer;
					}
				}
			}
		}
		return substitutions;
	}
	
	public static String getLastLetters(String word) {
		if (word.length() >= 3) {
			return word.substring(word.length() - 3);
		}
		if (word.length() < 3) {
			return word;
		}
		return null;
	}
	
	public static boolean isPunctuationMark(String word) {
		String[] marks = {".", ",", "?", "!", "\"", ":", ";", "-", "(", ")"};
		for (String mark : marks) {
			if (mark.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<String> visualizeProbability(HashMap<Entry, Float> dictionary, HashMap<Bigram, Float> bigrams) {
		List<String> visualizing = new ArrayList<>();
		Set<Entry> entriesInDict = dictionary.keySet();
		Object[] homs = bigrams.keySet().toArray();
		for (Entry entryInDict : entriesInDict) {
			FullSubstitution homonym = entryInDict.homonym;
			for (int i = 0; i < homs.length; i++) {
				if (homs[i].toString().equals(homonym.toString())) {
					visualizing.add(entryInDict.toString() + " (" + dictionary.get(entryInDict) + " / " + bigrams.get(homs[i]) + ")");
				}
			}
		}
		return visualizing;
	}

}
