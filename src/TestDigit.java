import static org.junit.Assert.*;
import org.junit.Test;

public class TestDigit {

	@Test
	public void emptyDigitShouldNotBeValid() {
		assertFalse(new Digit().isValid());
	}
	@Test
	public void emptyDigitShouldHaveInvalidValue() {
		assertEquals(-1, new Digit().getValue());
	}
	@Test
	public void digitWithValueIsValid() {
		Digit d = new Digit(8);
		assertTrue(d.isValid());
		assertEquals(8, d.getValue());
	}
	
	/*
	 * This test case is ALWAYS VALID and requires
	 * visual inspection on console
	 * So yes, it's not really a proper automated 
	 * test case
	 */
	@Test
	public void digitToString() {
		for(int i=0; i<10; i++) {
			System.out.println(new Digit(i).toString());
		}
	}
	
	@Test
	public void feedLinesPartiallyShouldFail() {
		Digit d1 = new Digit();
		Digit d2 = new Digit();
		d1.feedUpRow(" _ ");
		assertFalse(d1.isValid());
		d1.feedMidRow(" _ ");
		assertFalse(d1.isValid());
		d2.feedMidRow(" _ ");
		assertFalse(d2.isValid());
		d2.feedBottomRow(" _ ");
		assertFalse(d2.isValid());
	}
	
	@Test
	public void invalidFeedShouldReturnZero() {
		Digit d = new Digit();
		assertEquals(0,d.feedUpRow("| |"));
		assertEquals(0,d.feedUpRow(" _  "));
		assertEquals(0,d.feedUpRow(""));
		assertEquals(0,d.feedMidRow(" _ "));
		assertEquals(0,d.feedBottomRow("| |"));
	}
	
	@Test
	public void checkForValidBitMask() {
		Digit d = new Digit();
		int empty = d.feedUpRow("   ");
		int notEmpty = d.feedUpRow(" _ ");
		
		/* Combining top row empty and
		 * not empty bitmasks should hit all 
		 * digits and create a mask of
		 * 1111111111
		 * which is 1023 
		 */
		assertEquals(1023, (empty | notEmpty));
		// AND operation should negate the masks
		assertEquals(0, (empty & notEmpty));
	}
	
	@Test
	public void evaluateValidDigit() {
		Digit d = new Digit();
		String tst = " _  _ ";
		d.feedUpRow(tst.substring(0, 3));
		d.feedMidRow   ("|_|");
		d.feedBottomRow("|_|");
		assertEquals(1, d.evaluate());
		assertTrue(d.isValid());
		assertEquals(8, d.getValue());
	}
		
	@Test
	public void evaluateInvalidDigit() {
		Digit d = new Digit();
		d.feedUpRow    (" _ ");
		d.feedMidRow   ("|_|");
		d.feedBottomRow("|_ ");
	
		assertEquals(0, d.evaluate());
		assertFalse(d.isValid());
		assertEquals(-1, d.getValue());
	}
}
