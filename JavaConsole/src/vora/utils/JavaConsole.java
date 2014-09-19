package vora.utils;

public class JavaConsole {
	public ConsoleOut out;
	public ConsoleIn in;
	public ErrorConsole err;
	
	public JavaConsole()
	{
		out = new ConsoleOut(this);
		in = new ConsoleIn(out);
		err = new ErrorConsole();
		
	}
}
