package tester;

import vora.utils.JavaConsole;

public class ConsoleTester {

	public static void main(String[] args) {
		
		JavaConsole cons = new JavaConsole();
		cons.out.println("IS THIS MADNESS?");
		
		cons.out.print("This is INSANITY!");
		cons.out.setCaretVisible(true);

	}

}
