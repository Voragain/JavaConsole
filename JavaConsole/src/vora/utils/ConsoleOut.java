package vora.utils;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class ConsoleOut {
	
	final public static int StylePlain = Font.PLAIN;
	final public static int StyleBold = Font.BOLD;
	final public static int StyleItalic = Font.ITALIC;
	final public static int StyleUnderlined = 4;
	final public static int StyleInverted = 8;
	final public static int StyleFlipped = 16;

	private class ConsoleCanvas extends Canvas
	{
		private BufferedImage mBackBuffer;
		
		private ConsoleCanvas()
		{
			
			addComponentListener(new ComponentAdapter() {
				
				
				
				@Override
				public void componentResized(ComponentEvent e) {
					//super.componentResized(e);

					int w = e.getComponent().getWidth();
					int h = e.getComponent().getHeight();
					if(w < 1) w = 1;
					if(h < 1) h = 1;


					BufferedImage newImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					if(mBackBuffer != null)
					{
						Graphics g = newImg.getGraphics();
						
						if(w < mBackBuffer.getWidth()) w = mBackBuffer.getWidth();
						if(h < mBackBuffer.getHeight()) h = mBackBuffer.getHeight();
						
						g.drawImage(mBackBuffer, 0, 0, null);
					}
					mBackBuffer = newImg;	
					
				}
			});
		}
		
		public void update(Graphics g)
		{
			Graphics bbg = null;
			Rectangle bounds;
			if(mBackBuffer != null)
				bbg = mBackBuffer.getGraphics();
			if(bbg == null)
			{
				System.err.println("null");
				bounds = g.getClipBounds();
				setBackground(mConsoleBackgroundColor);
				g.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);				
				paint(g);
							
			}
			else
			{
				bounds = g.getClipBounds();
				setBackground(mConsoleBackgroundColor);
				bbg.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);
			
				paint(bbg);
		
				g.drawImage(mBackBuffer, 0, 0, this);
			}
		}
		
		
		
		public void paint(Graphics g)
		{
			if(mElementDimension == null)
			{
				mElementDimension = new Dimension();
				FontMetrics fm = g.getFontMetrics(mFonts[0]);
				mElementDimension.width = fm.charWidth('_')+1;
				mElementDimension.height = fm.getHeight()+1;
			}
			
			Stack<AffineTransform> transforms = new Stack<AffineTransform>();
			
			Graphics2D g2D = (Graphics2D) g;

			// Background elements drawing
			
			// Text drawing
			// Setup text model
			
			
			g2D.translate(0, mElementDimension.height);
			
			for(int x = 0; x < mElementCount.width; x++)
				for(int y = 0; y < mElementCount.height; y++)
				{
					if(mElements[y][x].mValue != 0)
					{
						transforms.push(g2D.getTransform());
						
						g2D.translate(x * mElementDimension.width, y * mElementDimension.height);
						
						g2D.setFont(mFonts[(mElements[y][x].mFlags & 3)]);
						g2D.setColor(mElements[y][x].mBackColor);
						g2D.fillRect(0, -mElementDimension.height+1, mElementDimension.width, mElementDimension.height);
						g2D.setColor(mElements[y][x].mColor);
						if((mElements[y][x].mFlags & StyleFlipped) > 0)
						{
							g2D.translate(mElementDimension.width, 0);
							g2D.scale(-1,  1);
						}
						if((mElements[y][x].mFlags & StyleInverted) > 0)
						{
							g2D.scale(1,  -1);
							g2D.translate(0,  mElementDimension.height-5);
						}
						if((mElements[y][x].mFlags & StyleUnderlined) > 0)
							g2D.drawLine(0, 0, mElementDimension.width, 0);
						g2D.drawString("" + mElements[y][x].mValue, 0, 0);
						
						g2D.setTransform(transforms.pop());
					}
				}
			if(mCaretState && mCaretVisible)
			{
				transforms.push(g2D.getTransform());
			
				g2D.translate(mElementCursorPosition.x * mElementDimension.width, mElementCursorPosition.y * mElementDimension.height);
				g2D.drawString("_", 0, 0);
			
				g2D.setTransform(transforms.pop());
			
			}
			// Overhead elements
			
		}
		
		
	}
	
	private class ConsoleElement
	{
		private char mValue;
		private int mFlags;
		private Color mColor;
		private Color mBackColor;
		
		private ConsoleElement()
		{
			mValue = 0;
			mFlags = 0;
			mColor = mForegroundColor;
			mBackColor = mBackgroundColor;
			
		}
	}

	private static Font mFonts[] = null;
	private static Dimension mElementDimension;
	
	private Dimension mConsoleDimension;
	private ConsoleElement mElements[][];
	private Dimension mElementCount;
	private Point mElementCursorPosition;
	
	private Color mForegroundColor;
	private Color mBackgroundColor;
	private Color mConsoleBackgroundColor;
	private Color mConsoleForegroundColor;
	private int mTextFlags;
	
	private boolean mCaretVisible;
	private boolean mCaretState;
	private Timer mCaretTimer;
	private TimerTask mCaretTimerT;
	
	
	private JavaConsole mOwner;
	private Frame mFrame;
	private ConsoleCanvas mCanvas;
	

	public ConsoleOut(JavaConsole console)
	{
		mOwner = console;
		if(mFonts == null)
		{
			mFonts = new Font[4];
			for(int i = 0; i < 4; i++)
			{
				mFonts[i] = new Font("Lucida Console", i, 14);
			}
		}
		
		mForegroundColor = Color.white;
		mBackgroundColor = Color.black;
		mConsoleForegroundColor = Color.white;
		mConsoleBackgroundColor = Color.black;

		mFrame = new Frame();
		mCanvas = new ConsoleCanvas();
		mElementCursorPosition = new Point(0,0);
		mElementCount = new Dimension(80, 25);
		mElements = new ConsoleElement[25][80];
		for(int i = 0; i < 25; i++)
			for(int j = 0; j < 80; j++)
				mElements[i][j] = new ConsoleElement();
		
		
		mFrame.setBounds(50, 50, 800, 600);
		mFrame.add(mCanvas);
		
		mFrame.addKeyListener(new KeyAdapter() {
		});
		
		mFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				
			}
		});
		
		mFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
	
		
		mCaretVisible = false;
		mCaretState = false;
		mCaretTimer = new Timer();
		mCaretTimerT = new TimerTask()
		{
			public void run()
			{
				if(!mCaretVisible)
				{
					mCaretState = false;
					cancel();
					mCanvas.repaint();
					return;
				}
				
				mCaretState = !mCaretState;
				mCanvas.repaint();
			}
		};
		
		mFrame.setVisible(true);
	}
	
	private void lineFeed()
	{
		mElementCursorPosition.x = 0;
		mElementCursorPosition.y++;
		if(mElementCursorPosition.y >= mElementCount.height)
		{
			mElementCursorPosition.y = mElementCount.height - 1;
			for(int i = 0; i < mElementCursorPosition.y; i++)
				mElements[i] = mElements[i+1];
			mElements[mElementCursorPosition.y] = new ConsoleElement[mElementCount.width];
			for(int i = 0; i < mElementCount.width; i++)
				mElements[mElementCursorPosition.y][i] = new ConsoleElement();
		}
	}
	
	private void checkLineFeed()
	{
		if(mElementCursorPosition.x >= mElementCount.width) 
		{
			lineFeed();
		}	}
	
	public void println(String text)
	{
		print(text + "\n");
	}
	
	public void print(String text)
	{
		String commandString = "";
		boolean commandMode = false;
		int commandState = 0;
		for(char c : text.toCharArray())
		{
			ConsoleElement e = mElements[mElementCursorPosition.y][mElementCursorPosition.x]; 
			if(!commandMode)
			{
			if(c > ' ')
				if(c == '~')
				{
					commandMode = true;
					commandState = 0;
					commandString = "";
					continue;
				}
				else
				{
					e.mColor = mForegroundColor;
					e.mBackColor = mBackgroundColor;
					e.mFlags = mTextFlags;					
					e.mValue = c;
					mElementCursorPosition.x++;
					checkLineFeed();
				}
			else
				switch(c)
				{
				case 10:
					lineFeed();
					break;
				default:
					e.mColor = mForegroundColor;
					e.mBackColor = mBackgroundColor;
					e.mFlags = mTextFlags;
					e.mValue = ' ';
					mElementCursorPosition.x++;
					checkLineFeed();
				}
			}
			else
			{
				switch(c)
				{
				case '~':
					e.mColor = mForegroundColor;
					e.mBackColor = mBackgroundColor;
					e.mFlags = mTextFlags;
					e.mValue = '~';
					mElementCursorPosition.x++;
					checkLineFeed();
					commandMode = false;
					break;
				case '-':
					commandState = -1;
					break;
				case '+':
					commandState = 1;
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					commandState = (commandState << 8) + (16 * (c - '0'));
					break;
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
					commandState = (commandState << 8) + (16 * (c - 'A' + 10));
					break;
				case 'c':
					if(commandState == -1)
						mForegroundColor = mConsoleForegroundColor;
					else
					{
						commandString = "0x" + Integer.toHexString(commandState);
						mForegroundColor = Color.decode(commandString);
					}
					commandMode = false;
					break;
				case 'd':
					if(commandState == -1)
						mBackgroundColor = mConsoleBackgroundColor;
					else
					{
						commandString = "0x" + Integer.toHexString(commandState);
						mBackgroundColor = Color.decode(commandString);
					}
					commandMode = false;
					break;
				case 'b':
					if(commandState == -1)
						mTextFlags = (mTextFlags & ~StyleBold);
					if(commandState == 0)
						mTextFlags = (mTextFlags ^ StyleBold);
					if(commandState == 1)
						mTextFlags = (mTextFlags | StyleBold);
					commandMode = false;
					break;
				case 'i':
					if(commandState == -1)
						mTextFlags = (mTextFlags & ~StyleItalic);
					if(commandState == 0)
						mTextFlags = (mTextFlags ^ StyleItalic);
					if(commandState == 1)
						mTextFlags = (mTextFlags | StyleItalic);
					commandMode = false;
					break;
				case 'u':
					if(commandState == -1)
						mTextFlags = (mTextFlags & ~StyleUnderlined);
					if(commandState == 0)
						mTextFlags = (mTextFlags ^ StyleUnderlined);
					if(commandState == 1)
						mTextFlags = (mTextFlags | StyleUnderlined);
					commandMode = false;
					break;
				case 'f':
					if(commandState == -1)
						mTextFlags = (mTextFlags & ~StyleFlipped);
					if(commandState == 0)
						mTextFlags = (mTextFlags ^ StyleFlipped);
					if(commandState == 1)
						mTextFlags = (mTextFlags | StyleFlipped);
					commandMode = false;
					break;
				case 'r':
					if(commandState == -1)
						mTextFlags = (mTextFlags & ~StyleInverted);
					if(commandState == 0)
						mTextFlags = (mTextFlags ^ StyleInverted);
					if(commandState == 1)
						mTextFlags = (mTextFlags | StyleInverted);
					commandMode = false;
					break;

				}
			}
			
		}
		mCanvas.repaint();
	}
	
	public void setCaretVisible(boolean state)
	{
		mCaretVisible = state;
		mCaretState = state;
		mCaretTimer.scheduleAtFixedRate(mCaretTimerT, 0, 700);
		mCanvas.repaint();
	}
	
}
