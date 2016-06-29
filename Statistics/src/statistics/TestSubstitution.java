package statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;

public class TestSubstitution {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String test = "их";		
		System.out.println(new FullSubstitution(getLastLetters(test), getSubstitution(test)).toString());
		String test1 = "двигаем";
		Substitution testSub = new Substitution(0, "");
		System.out.println("subToWord " + subToWord(test1, testSub));
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
	
	public static String getLastLetters(String word) {
		if (word.length() >= 3) {
			return word.substring(word.length() - 3);
		}
		if (word.length() < 3) {
			return word;
		}
		return null;
	}
	
	public static String subToWord(String word, Substitution sub) {
		String newWord = word.substring(0, word.length() - sub.letters);
		newWord = newWord + sub.ending;
		return newWord;
	}

}
