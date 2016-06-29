package statistics;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

public class Entry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FullSubstitution homonym;
	public ContextElement contextElement;
	public Substitution lemma;
	public float probability;
	public String partOfSpeech;
	public Set<String> partsOfSpeech;
	
	public Entry(FullSubstitution homonym, ContextElement contextElement, Substitution lemma) {
		this.homonym = homonym;
		this.contextElement = contextElement;
		this.lemma = lemma;
	}
	
	public Entry(ContextElement prev, HashSet<PartOfSpeech> partsOfSpeech, String partOfSpeech2) {
		this.contextElement = prev;
		this.partOfSpeech = partOfSpeech2;
		this.partsOfSpeech = partsOfSpeech.stream().map(p -> p.id).collect(Collectors.toSet());
	}

	public String toString() {
		if (homonym!=null && lemma!=null) {
		return homonym.toString() + " | " + contextElement.toString() + " | " + lemma.toString() + " | " + partOfSpeech  + " | " + probability;
		}
		else {
			return contextElement.toString() + " | " + partsOfSpeech.toString() + " | " + partOfSpeech + " | " + probability;
		}
	}
	
	public Set<PartOfSpeech> getPartsOfSpeech() {
		return partsOfSpeech.stream().map(str -> (PartOfSpeech) Grammem.get(str)).collect(Collectors.toSet());
	}
}
