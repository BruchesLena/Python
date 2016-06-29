package statistics;

import java.io.Serializable;
import java.util.List;

public class FullSubstitution implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String lastLetters;
	public List<Substitution> substitutions;
	
	public FullSubstitution(String lastLetters, List<Substitution> substitutions) {
		this.lastLetters = lastLetters;
		this.substitutions = substitutions;
	}
	
	public String toString() {
		if (substitutions == null) {
			return null;
		}
		if (isPunctuationMark(lastLetters)) {
			return "Punct";
		}
		else if (isNumber(lastLetters)) {
			return "Number";
		}
		else {
			return lastLetters + substitutions.toString();
		}
	}
	
	public static boolean isPunctuationMark(String word) {
		String[] marks = {".", ",", "?", "!", "\"", ":", ";", "-", "(", ")"};
		for (String mark : marks) {
			if (word.contains(mark)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNumber(String word) {
		String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-ом"};
		for (String number : numbers) {
			if (word.contains(number)) {
				return true;
			}
		}
		return false;
	}
	

}
