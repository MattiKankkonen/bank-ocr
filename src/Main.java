import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


public class Main {

	private void readLines(String fileName) {
		try {
			List<DigitLine> lines = new ArrayList<DigitLine>();
			DigitLine line = null;
			int fourRows = 1;
			Scanner scanner = new Scanner(new File(fileName));
			scanner.useDelimiter("\n");
			
			while(scanner.hasNext()) {
				if(fourRows == 1) {
					line = new DigitLine();
					lines.add(line);
				}
				String key = scanner.next();
				line.feedRow(key);
				
				fourRows++;
				if(fourRows > 4) {
					fourRows = 1;
				}
			}
			scanner.close();
			
			Iterator<DigitLine> i = lines.iterator();
			while(i.hasNext()) {
				line = i.next();
				if(line.isValid() == false) {
					line.errorCorrection();
				}
				System.out.println(line.toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	public static void main(String[] args) {
		if(args.length > 0) {
			new Main().readLines(args[0]);
		}
	}
}
