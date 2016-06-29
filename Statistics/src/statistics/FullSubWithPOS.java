package statistics;

import java.io.Serializable;

public class FullSubWithPOS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FullSubstitution fullSubstitution;
	public String partOfSpeech;
	public double probability;
	
	public FullSubWithPOS(FullSubstitution fullSubstitution, String partOfSpeech) {
		this.fullSubstitution = fullSubstitution;
		this.partOfSpeech = partOfSpeech;
	}
	
	public String toString() {
		return fullSubstitution.toString() + " - " + partOfSpeech + " (" + probability + ")";
	}
}
