package statistics;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.text.analisys.tests.util.TestingUtil;
import com.onpositive.text.analysis.CombinedMorphologicParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.AdditionalMetadataHandler;
import com.onpositive.text.analysis.utils.MorphologicUtils;

public class Methods {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String test = "шестьдесят минут прошло";
		List<IToken> disambig = new ArrayList<>();
		List<IToken> wordFormTokens = TestingUtil.getWordFormTokens(test);
		System.out.println("wordFormTokens: " + wordFormTokens);
		AdditionalMetadataHandler.setStoreMetadata(true);
		CombinedMorphologicParser cmp = new CombinedMorphologicParser();
		List<IToken> processed = cmp.process(wordFormTokens);
		for (IToken proc : processed) {
			if (!proc.hasCorrelation() || proc.getCorrelation() > 0.05) {
				disambig.add(proc);
				List<IToken> children = proc.getChildren();
				for (IToken child : children) {
					if (child instanceof SyntaxToken) {
						SyntaxToken ch = (SyntaxToken) child;
						ch.getPartOfSpeech();
					}
				}
				System.out.print(proc.getStableStringValue());
				String eur = AdditionalMetadataHandler.get(proc, AdditionalMetadataHandler.EURISTIC_KEY);
				if (eur != null) {
					System.out.println(" " + eur);
				} else {
					System.out.println();
				}
			}
		}
//		System.out.println(disambig);
		List<IToken> withoutConflicts = MorphologicUtils.getWithNoConflicts(processed);
		System.out.println(withoutConflicts);
	}

}
