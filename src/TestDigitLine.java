import static org.junit.Assert.*;
import org.junit.Test;

public class TestDigitLine {

	@Test
	public void emptyLineShouldBeInvalid() {
		assertFalse(new DigitLine().isValid());
	}
	
	@Test
	public void feedingFiveLinesShouldFail() {
		DigitLine line = new DigitLine();
		line.feedRow("    _  _     _  _  _  _  _ ");
		line.feedRow("  | _| _||_||_ |_   ||_||_|");
		line.feedRow("  ||_  _|  | _||_|  ||_| _|");
		line.feedRow("                           ");
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		assertFalse(line.isValid());
	}
	
	@Test
	public void feedALine() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_|| || || || || || || || |");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow("                           ");
		assertEquals(800000000L, line.getValue());
		assertFalse(line.isValid()); // Checksum invalid

		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("| || || || || || || || || |");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow("                           ");
		assertEquals(000000000L, line.getValue());
		assertTrue(line.isValid());

		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow(" _| _| _| _| _| _| _| _| _|");
		line.feedRow("|_ |_ |_ |_ |_ |_ |_ |_ |_ ");
		line.feedRow("                           ");
		assertEquals(222222222L, line.getValue());
		assertFalse(line.isValid()); // Checksum

		line.feedRow("    _  _     _  _  _  _  _ ");
		line.feedRow("  | _| _||_||_ |_   ||_||_|");
		line.feedRow("  ||_  _|  | _||_|  ||_| _|");
		line.feedRow("                           ");
		assertEquals(123456789L, line.getValue());
		assertTrue(line.isValid());

		line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow(" _| _| _| _| _| _| _| _| _|");
		line.feedRow("                           ");
		assertEquals(999999999L, line.getValue());
	}

	@Test
	public void invalidChecksum() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_|| || || || || || || || |");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow("                           ");
		assertEquals(800000000L, line.getValue());
		assertFalse(line.isValid());
		assertNotEquals(0, line.getChecksum());
	}
	
	@Test
	public void validChecksum() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow(" _|| || ||_|| || || || || |");
		line.feedRow("|_ |_||_||_||_||_||_||_||_|");
		line.feedRow("                           ");
		assertEquals(200800000L, line.getValue());
		assertTrue(line.isValid());
		assertEquals(0,line.getChecksum());
	}

	@Test
	public void testCloning() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_|| || || || || || || || |");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow("                           ");
		assertEquals(800000000L, line.getValue());
		assertFalse(line.isValid());
		DigitLine copy = new DigitLine(line);
		assertEquals(line.getValue(), copy.getValue());
		assertEquals(line.isValid(), copy.isValid());
	}
	
	@Test
	public void testErrorCorrection() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("  |  |  |  |  |  |  |  |  |");
		line.feedRow("  |  |  |  |  |  |  |  |  |");
		line.feedRow("                           ");
	
		assertTrue(line.errorCorrection());
		assertEquals(0, line.getChecksum());
		assertEquals(777777177, line.getValue());
	}
	
	@Test
	public void testAmbiguous() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow(" _| _| _| _| _| _| _| _| _|");
		line.feedRow("                           ");
		assertFalse(line.errorCorrection());
		assertNotEquals(0, line.getChecksum());
		assertEquals(999999999, line.getValue());
	}
	
	@Test
	public void testAmbToString() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_||_||_||_||_||_||_||_||_|");
		line.feedRow(" _| _| _| _| _| _| _| _| _|");
		line.feedRow("                           ");
		assertEquals("999999999 ERR", line.toString());
		line.errorCorrection();
		assertEquals("999999999 AMB ['899999999','993999999','999959999']",
				line.toString());
	}
	
	@Test
	public void testInvalidToString() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow("|_||_||_|| ||_||_||_||_||_|");
		line.feedRow(" _| _| _| _| _| _| _| _| _|");
		line.feedRow("                           ");
		assertEquals("999?99999 ILL", line.toString());
		line.errorCorrection();
	}
	
	@Test
	public void testInvalidToString2() {
		DigitLine line = new DigitLine();
		line.feedRow(" _  _  _  _  _  _  _  _  _ ");
		line.feedRow(" _|| || ||_|| || || || || |");
		line.feedRow("|_ |_||_||_||_|| ||_||_||_|");
		line.feedRow("                           ");
		assertEquals("20080?000 ILL", line.toString());
		line.errorCorrection();
		assertEquals("200800000 ", line.toString());
	}
	
	@Test
	public void testInvalidToString3() {
		DigitLine line = new DigitLine();
		line.feedRow("    _  _  _  _  _  _     _ ");
		line.feedRow("|_||_|| ||_||_   |  |  | _ ");
		line.feedRow("  | _||_||_||_|  |  |  | _|");
		line.feedRow("                           ");
		assertEquals("49086771? ILL", line.toString());
		line.errorCorrection();
		assertEquals("490867715 ", line.toString());
	}
}
