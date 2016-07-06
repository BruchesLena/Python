package statistics;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

import junit.framework.TestCase;

public class CombinedTest extends TestCase {
	
	@Test
	public void test01() throws Exception { //"красная" не имеет конфликтов
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("красная поляна", "красная");
		assertTrue(result.contains(PartOfSpeech.ADJF));
	}
	
	@Test
	public void test02() throws Exception { //"по" не имеет конфликтов
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("вахта по северному", "по");
		//System.out.println(result);
	}
	
	@Test
	public void test03() throws Exception { //"на" [МЕЖД] имеет два коэффициента
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("катастрофе на сочинской", "на");
		System.out.println(result);
	}
	
	@Test
	public void test04() throws Exception { //"которую" не имеет конфликтов
											//"попали" конфликт между словами одной части речи (ГЛ)
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("в которую попали", "которую");
		System.out.println(result);
	}
	
	@Test
	public void test05() throws Exception { //сумма коэффициентов не равна 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("выяснения её причин", "её");
		System.out.println(result);
	}
	
	@Test
	public void test06() throws Exception { //"и" [МЕЖД] имеет три коэффициента
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("причин и последствий", "и");
		System.out.println(result);
	}
	
	@Test
	public void test07() throws Exception { //сумма коэффициентов не равна 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach(", что ещё", "что");
		System.out.println(result);
	}
	
	@Test
	public void test08() throws Exception { // коэффициенты: 0 и пустой ArrayList
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("что ещё в", "ещё");
		System.out.println(result);
	}
	
	@Test
	public void test09() throws Exception { //"после" [СУЩ] имеет три коэффициента
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach(". после начала", "после");
		System.out.println(result);
	}
	
	@Test
	public void test10() throws Exception { //коэффициенты: 0 и пустой ArrayList
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("ремонта все отвалы", "все");
		System.out.println(result);
	}
	
	@Test
	public void test11() throws Exception { //"на" [МЕЖД] имеет три коэффициента
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("поступать на незаконную", "на");
		System.out.println(result);
	}
	
	@Test
	public void test12() throws Exception { // сумма коэффициентов не равна 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("свалку в верховьях", "в");
		System.out.println(result);
	}
	
	@Test
	public void test13() throws Exception { //сумма коэффиицентов не равна 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("7 или 8", "или");
		System.out.println(result);
	}

}
