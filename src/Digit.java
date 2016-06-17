/**
 * A class for holding and handling one LCD digit
 * which can have one of following values:
 *  _    _  _     _  _  _  _  _
 * | | | _| _||_||_ |_   ||_||_|
 * |_| ||_  _|  | _||_|  ||_| _|
 * 
 */
public class Digit {
	private static final String[] upRow     = {" _ ","   "," _ "," _ ","   "," _ "," _ "," _ "," _ "," _ "};
	private static final String[] midRow    = {"| |","  |"," _|"," _|","|_|","|_ ","|_ ","  |","|_|","|_|"};
	private static final String[] bottomRow = {"|_|","  |","|_ "," _|","  |"," _|","|_|","  |","|_|"," _|"};
		
	private boolean isValid = false;
	private int value = -1;
	
	private int upRowBitmask = 0;
	private int midRowBitmask = 0;
	private int bottomRowBitmask = 0;
		
	Digit() {
		isValid = false;
	}
	
	/**
	 * Create a digit with the given initial value
	 * @param value
	 */
	Digit(int value) {
		this.value = value;
		isValid = true;
	}
	
	public boolean isValid() { return isValid; }
	public int getValue() { return value; }
	public void setValue(int value) { 
		this.value = value;
		this.isValid = true; 
	}	
	
	/**
	 * Return the string representation of the
	 * value of this digit
	 */
	public String toString() {
		return upRow[value] +"\n"+ 
		midRow[value] + "\n" + 
		bottomRow[value] + "\n";
	}
	
	private int loopThroughDigits(String line, String row[]) {
		
		int bitmask = 0;
		for (int i=0; i<10; i++) {
			if(row[i].equals(line)) {
				bitmask = bitmask | 1<<i;
			}
		}
		return bitmask;
	}
	
	/**
	 * The top row of this LCD digit
	 * @param input row string
	 * @return the mask matching LCD digit's rows, 0 means fail
	 */
	public int feedUpRow(String input) {
		upRowBitmask = loopThroughDigits(input, upRow);
		return upRowBitmask;
	}
	
	/**
	 * The middle row of this LCD digit
	 * @param input row string
	 * @return the mask matching LCD digit's rows, 0 means fail
	 */
	public int feedMidRow(String input) {
		midRowBitmask = loopThroughDigits(input, midRow);
		return midRowBitmask;
	}
	
	/**
	 * The bottom row of this LCD digit
	 * @param input row string
	 * @return the mask matching LCD digit's rows, 0 means fail
	 */
	public int feedBottomRow(String input) {
		bottomRowBitmask = loopThroughDigits(input, bottomRow);
		return bottomRowBitmask;
	}
	
	/**
	 * Evaluate the value of this digit based on the
	 * given rows.
	 * Before calling this you need to have fed
	 * all three rows so that we can compose the value
	 * for the digit
	 */
	public int evaluate() {
		int mask = 0;
		int found = 0;
		int theNumberIs = -1;
		for(int i=0; i < 10; i++) {
			mask = upRowBitmask & midRowBitmask & bottomRowBitmask & (1<<i);
			if(mask != 0) {
				found++;
				theNumberIs = i;
			}
		}
		if(found != 1) {
			// Either nothing found or more than one match
			this.isValid = false;
		} else {
			// Woohoo! we have a winner!
			this.isValid = true;
			this.value = theNumberIs;
		}
		return found;
	}
}
