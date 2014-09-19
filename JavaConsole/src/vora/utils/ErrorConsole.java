package vora.utils;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorConsole {
	
	
	private Frame mFrame;
	private TextArea mTextArea;
	private SimpleDateFormat mLogTimeFormat;
	
	public ErrorConsole()
	{
		mTextArea = new TextArea();
		mTextArea.setEditable(false);
		
		mFrame = new Frame();
		mFrame.setResizable(false);
		mFrame.add(mTextArea);
		
		mFrame.setBounds(50, 50, 450, 350);
		
		mFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {			
				super.windowClosing(e);
				mFrame.setVisible(false);
			}
		});
		
		mLogTimeFormat = new SimpleDateFormat("[hh:mm:ss] '>' ");
	}
	
	public void print(String text)
	{
		Date now = new Date();
		mTextArea.append(mLogTimeFormat.format(now) + text + "\n");
		mTextArea.setCaretPosition(mTextArea.getText().length());
		if(!mFrame.isVisible()) mFrame.setVisible(true);
		
	}
	
	public void println(String text)
	{
		print(text);
	}
	
	public void dispose()
	{
		mFrame.dispose();
	}
	
}
