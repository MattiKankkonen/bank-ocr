import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class holds a line of LCD digits
 * representing 9 digit account numbers
 */
public class DigitLine {
	private final static int DIGITS = 9;
	private final static int MAX_ROW = DIGITS*3;
	
	private boolean valid = false;
	private String status = "";
	private List<Digit> digits = null;
	private List<DigitLine> alternativeLines = null;
	private int rowNumber = 0;
	
	public DigitLine() {
		this.valid = false;
		digits = new ArrayList<Digit>();
		for(int i=0; i< DIGITS +1; i++) {
			digits.add(new Digit());
		}
	}
	
	/**
	 * Create a new instance of DigitLine
	 * with the values of the given parameter
	 * 
	 * @param source
	 */
	public DigitLine(DigitLine source) {
		this.valid = source.valid;
		this.status = source.status;
		this.digits = new ArrayList<Digit>();
		Iterator<Digit> i = source.digits.iterator();
		while(i.hasNext()) {
			Digit d = i.next();
			this.digits.add(new Digit(d.getValue()));
		}
	}
	
	/**
	 * Does this line have a valid account number
	 * 
	 * @return boolean
	 */
	public boolean isValid() { return valid; }

	/**
	 * Feed the digits to "line" row by row
	 * one line consists of four rows
	 * 
	 * @param row
	 */
	public void feedRow(String row) {
		int digit = 0;
		this.valid = false; 
		
		for(int i=0; i < MAX_ROW; i += 3) {
			Digit d = digits.get(digit);
			String subRow = row.substring(i, i+3);
			int result = 0;
			switch(rowNumber) {
				case 0:
					result = d.feedUpRow(subRow);
					break;
				case 1:
					result = d.feedMidRow(subRow);
					break;
				case 2:
					result = d.feedBottomRow(subRow);
					break;
				case 3:
					d.evaluate();
					result = 1;
					break;
			}
			if(result == 0) {
				System.out.println("Feeding row line:" + row + "\n" +
						"part:" + subRow +
						" at digit:" + (digit+1) + 
						" from left at row:" + rowNumber + 
						" doesn't belong to any valid LCD number\n");
			}
			digit++;
		}
		rowNumber++;
		if(rowNumber > 3){
			// Final row
			this.valid = true;
			for(int i=0;i<DIGITS;i++) {
				if(digits.get(i).isValid() == false) {
					this.valid = false;
					status = "ILL";
				} else if( !status.equals("ILL") && this.getChecksum() != 0) {
					this.valid = false;
					status = "ERR";
				}
			}
			// Yes we do prepare for the wrap around and you can overwrite
			// old values simply by inserting new lines
			rowNumber = 0;
		}
			
	}
	
	/**
	 * Get number representation of this line's value
	 * 
	 * @return long
	 */
	public long getValue() {
		long value = 0;
		for(int i=0; i< DIGITS; i++) {
			value = (long) (value + (digits.get(i).getValue() * Math.pow(10, (DIGITS-i-1))));			
		}
		return value;
	}
	
	/**
	 * Calculate checksum over the line's digits
	 * 
	 * @return 0 if checksum is valid
	 */
	public long getChecksum() {
		long checksum = 0;
		for(int i=0; i< DIGITS; i++) {
			checksum = checksum + digits.get(i).getValue() * (DIGITS-i);
		}
		return checksum%11;
	}
	
	/**
	 * Try to correct a line with wrong or corrupted digits
	 * trying several alternatives to make the checksum correct
	 * 
	 * @return true is the line was corrected
	 */
	public boolean errorCorrection() {
		/* 
		 * From 0 to 9, what can we get by adding or removing 
		 * one part of digit. 
		 * 10th "bonus" line is for the corrupted digit
		 * */
		final int[][] digitAlternatives = {
				{8}, // 0
				{7},
				{},  // 2
				{9},
				{},
				{6,9}, // 5
				{5,8},
				{1},
				{0},
				{8,3,5}, // 9
				{0,1,2,3,4,5,6,7,8,9} // Anything goes with the corrupt number
		};
		this.alternativeLines = new ArrayList<DigitLine>();
		DigitLine tmpCopy = new DigitLine(this);
		int selector = -1;
		
		for(int i=0; i< DIGITS; i++) {
			
			int originalValue = tmpCopy.digits.get(i).getValue();
			if(originalValue == -1) {
				// we have clearly corrupted digit
				selector = 10;
			} else {
				if(this.status.equals("ILL")) {
				// The line has corrupted digits so we're not
			    // trying to fix digits that appear to be OK
				// so move on to neeext
					continue;
				} else {
				// No seemingly corrupted digits so we try each
				// one by one with suitable alternatives
					selector = originalValue;
				}
			}
			// Loop through alternatives and check if checksum is corrected
			for(int x=0; x < digitAlternatives[selector].length; x++) {				
				tmpCopy.digits.get(i).setValue(digitAlternatives[selector][x]);
				if(tmpCopy.getChecksum() == 0) {
					// We found a working alternative
					alternativeLines.add(new DigitLine(tmpCopy));
				}				
				tmpCopy.digits.get(i).setValue(originalValue);				
			}
		}
		/*
		 * All right let's check what we found
		 */
		if(this.alternativeLines.size() == 1) {
			// Only one alternative so we can actually do the
			// error correction
			DigitLine corrected = alternativeLines.get(0);
			for(int i=0; i< DIGITS; i++) {
				this.digits.get(i).setValue(
						corrected.digits.get(i).getValue());
				this.alternativeLines = null;
				this.valid = true;
				this.status ="";
			}
			return true;
		}
		this.status ="AMB";
		Iterator<DigitLine> i = alternativeLines.iterator();
		this.status = this.status + " [";
		int counter = 0;
		while(i.hasNext()) {
			DigitLine line = i.next();
			if(counter>0) {
				this.status = this.status +",";
			}
			this.status = this.status + "'" + line.digitsAsString() +"'";			
			counter++;
		}
		this.status = this.status + "]";
		return false;
	}
	
	private String digitsAsString() {
		String line = "";
		for(int i = 0; i< DIGITS; i++) {
			if(digits.get(i).isValid()) {
				line = line + digits.get(i).getValue();
			} else {
				line = line + "?";
			}
		}
		return line;
	}
	
	/**
	 * Return the value of line and the status
	 * as string
	 */
	public String toString() {
		String line = "";
		line = digitsAsString();
		line = line + " " + status;
		return line;
	}
}
