package statistics;

import java.io.Serializable;

public class Substitution implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int letters;
	public String ending;
	
	public Substitution(int letters, String ending) {
		this.letters = letters;
		this.ending = ending;
	}
	
	public String toString() {
		return letters + ending;
	}
}
