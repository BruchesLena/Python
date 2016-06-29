package statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

public class Bigram {

	public FullSubstitution homonym;
	public ContextElement contextElement;
	public Set<PartOfSpeech> partsOfSpeech;
	
	public Bigram(FullSubstitution homonym, ContextElement contextElement) {
		this.homonym = homonym;
		this.contextElement = contextElement;
	}
	
	public Bigram(Set<PartOfSpeech> partsOfSpeech, ContextElement contextElement2) {
		// TODO Auto-generated constructor stub
		this.partsOfSpeech = partsOfSpeech;
		this.contextElement = contextElement2;
	}

	public String toString() {
		if (homonym!=null) {
		return homonym.toString() + " " + contextElement.toString();
		}
		else {
			return contextElement.toString() + " " + partsOfSpeech.toString();
		}
	}
	
	public String POSInEngToString() {
		List<String> pos = new ArrayList<>();
		for (PartOfSpeech p : partsOfSpeech) {
			pos.add(p.id);
		}
		return pos.toString();
	}
}
