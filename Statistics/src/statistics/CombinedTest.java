package statistics;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;

import junit.framework.TestCase;

public class CombinedTest extends TestCase {
	
	@Test
	public void test01() throws Exception { //"�������" �� ����� ����������
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("������� ������", "�������");
		assertTrue(result.contains(PartOfSpeech.ADJF));
	}
	
	@Test
	public void test02() throws Exception { //"��" �� ����� ����������
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("����� �� ���������", "��");
		//System.out.println(result);
	}
	
	@Test
	public void test03() throws Exception { //"��" [����] ����� ��� ������������
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("���������� �� ���������", "��");
		System.out.println(result);
	}
	
	@Test
	public void test04() throws Exception { //"�������" �� ����� ����������
											//"������" �������� ����� ������� ����� ����� ���� (��)
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("� ������� ������", "�������");
		System.out.println(result);
	}
	
	@Test
	public void test05() throws Exception { //����� ������������� �� ����� 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("��������� � ������", "�");
		System.out.println(result);
	}
	
	@Test
	public void test06() throws Exception { //"�" [����] ����� ��� ������������
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("������ � �����������", "�");
		System.out.println(result);
	}
	
	@Test
	public void test07() throws Exception { //����� ������������� �� ����� 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach(", ��� ���", "���");
		System.out.println(result);
	}
	
	@Test
	public void test08() throws Exception { // ������������: 0 � ������ ArrayList
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("��� ��� �", "���");
		System.out.println(result);
	}
	
	@Test
	public void test09() throws Exception { //"�����" [���] ����� ��� ������������
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach(". ����� ������", "�����");
		System.out.println(result);
	}
	
	@Test
	public void test10() throws Exception { //������������: 0 � ������ ArrayList
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("������� ��� ������", "���");
		System.out.println(result);
	}
	
	@Test
	public void test11() throws Exception { //"��" [����] ����� ��� ������������
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("��������� �� ����������", "��");
		System.out.println(result);
	}
	
	@Test
	public void test12() throws Exception { // ����� ������������� �� ����� 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("������ � ���������", "�");
		System.out.println(result);
	}
	
	@Test
	public void test13() throws Exception { //����� ������������� �� ����� 1
		HashSet<PartOfSpeech> result = TestingWithPOS.chooseWithCombinedApproach("7 ��� 8", "���");
		System.out.println(result);
	}

}
