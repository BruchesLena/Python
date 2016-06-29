package statistics;

import java.io.Serializable;

public class ContextElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String position;
	public FullSubstitution fullSubstition;
	
	public ContextElement(FullSubstitution fullSubstitution) {
		this.fullSubstition = fullSubstitution;
	}
	
	public String toString() {
		return fullSubstition.toString() + " " + position;
	}
}
