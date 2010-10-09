// CAT 200 Group Project
// Group 9 : Advanced Visual Instruments

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Piano extends JFrame{
	
	private MidiChannel channel;
	private Synthesizer synthesizer;
	private boolean opt;
	JComboBox instruments;
	JRadioButton mouseOpt;
	JRadioButton keyboardOpt;
	ButtonGroup optGroup;
	JLabel instruction;
	JPanel optPanel;
	Box optBox = Box.createHorizontalBox();
	layout keyboard;

  public Piano(){
    setTitle("Virtual Piano");
    setSize(new Dimension(1083,250));
    setResizable(false);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
    insInit();
    instruments = new JComboBox (getInstruments());
    
    mouseOpt = new JRadioButton("Mouse Mode");
    keyboardOpt = new JRadioButton("Keyboard Mode");
    optGroup = new ButtonGroup();
    optGroup.add(mouseOpt);
    optGroup.add(keyboardOpt);
    mouseOpt.setSelected(true); //Default option is mouse
    opt = true;
     
    optBox.add(mouseOpt);
    optBox.add(Box.createHorizontalStrut(10));
    optBox.add(keyboardOpt);
    
    optPanel = new JPanel(); //Create a new JPanel to hold the instrument combo box and mode radio button
    optPanel.setLayout(new BoxLayout(optPanel,BoxLayout.Y_AXIS));
    instruments.setAlignmentX(LEFT_ALIGNMENT);
    optBox.setAlignmentX(LEFT_ALIGNMENT);
    optPanel.add(instruments);
    optPanel.add(Box.createHorizontalStrut(2));
    optPanel.add(optBox);
    getContentPane().add(optPanel, BorderLayout.NORTH);
    
    instruction = new JLabel("Press 'z' to switch to the left octave or 'x' to switch to the right octave for keyboard mode");
    getContentPane().add(instruction, BorderLayout.PAGE_END);
    
    keyboard = new layout();
    getContentPane().add(keyboard);
    
    ActionListener al;
    al = new ActionListener (){
             public void actionPerformed (ActionEvent e)
             {
                JComboBox cb = (JComboBox) e.getSource ();
                chooseInstrument (cb.getSelectedIndex ());
                if(opt == false) //Make sure focus to keyboard is not disturbed
                keyboard.requestFocus();
             }
         };
    instruments.addActionListener (al);
    
    mouseOpt.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		opt = true;
    		keyboard.repaint();
    	}
    });
    
    keyboardOpt.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		opt = false;
    		keyboard.requestFocus();
    		keyboard.repaint();
    	}
    });
  }
  
  public boolean chooseInstrument (int instrumentID)
  {
     if (channel == null)
         return false;
     // choose instrument
     channel.programChange (instrumentID);
     return true;
  }
  
  public String [] getInstruments ()
  {
     if (synthesizer == null)
         return null;

     Instrument [] instruments = synthesizer.getLoadedInstruments ();
     String [] ins = new String [instruments.length];

     for (int i = 0; i < instruments.length; i++)
          ins [i] = instruments [i].toString ();

     return ins;
  }

  
  public boolean insInit ()
  {
     try         // load synthesizer
     {
         if (synthesizer == null) 
         {
             if ((synthesizer = MidiSystem.getSynthesizer ()) == null)
                 return false;
             
             synthesizer.open ();
         }
     }
     catch (Exception e)
     {
         e.printStackTrace();
         return false;
     }


     // get instruments from default sound bank
     synthesizer.loadAllInstruments (synthesizer.getDefaultSoundbank ());

     // Obtain the set of MIDI channels
     MidiChannel [] midiChannel = synthesizer.getChannels ();
    
     if (midiChannel.length == 0 || midiChannel [0] == null)
     {
         synthesizer.close ();
         synthesizer = null;
         return false;
     }

     channel = midiChannel [0];

     return true;
  }


  public class layout extends JPanel implements MouseListener, KeyListener{
	  
	  private int width;
	  private int height;
	  private int whiteKey;
	  private int blackKey;
	  private int[] whiteKeyStartX = new int[49];
	  private int[] whiteKeyStopX = new int[49];
	  private int[] blackKeyStartX = new int[35];
	  private int[] blackKeyStopX = new int[35];
	  int key;
	  int octaveNum = 3;
	  boolean keyFound = false;
	  long prevKey = 0;
	  private Timer timer = new Timer(1, new TimerListener());
	  boolean released = false;
	  
	  public void layout(){
		  addMouseListener(this);
		  addKeyListener(this);
	  }
	  
    public void paint(Graphics g){
    	key = 0;
    	
    	//draw piano
    	width = getSize().width; 
        height = getSize().height;

        g.setColor(Color.gray);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.black);
        g.fillRect(0, (int)(height*0.3), width, (int)(height*0.7));
        
        whiteKey = (width / 49) + 1;

        g.setColor(Color.white);
        for (int i = 0; i < 49; i++)
        {
        	g.fillRect( i*whiteKey, (int)(height*0.3), whiteKey - 1, (int)(height*0.7));
        	whiteKeyStartX[i] = i*whiteKey;
        	whiteKeyStopX[i] = i*whiteKey + (whiteKey - 1);
        }
        
        blackKey = width / 147;
        int invalidKey = 1;
        int blackKeyCount = 0;
        
        g.setColor(Color.black);
        for (int i = 0; i < 49; i++)
        {
        	if ( invalidKey != 3 && invalidKey != 7)
        	{
        		g.fillRect( i*whiteKey + 2*blackKey, (int)(height*0.3), 2*blackKey, (int)(height*0.38));
        		blackKeyStartX[blackKeyCount] = i*whiteKey + 2*blackKey;
        		blackKeyStopX[blackKeyCount] = i*whiteKey + 4*blackKey;
        		blackKeyCount ++;
        	}
        	else if (invalidKey ==7)
        		invalidKey = 0;
        	invalidKey++;
        }
        
        if(opt == false){
        	g.setColor(Color.blue);
        	g.drawRect(whiteKeyStartX[(octaveNum * 7)],(int)(height*0.3), 7*whiteKey,(int)(height*0.7));
        	g.drawRect(whiteKeyStartX[(octaveNum * 7)]-1,(int)(height*0.3)-1, 7*whiteKey+2,(int)(height*0.7)+2);
        	g.drawString("S", whiteKeyStartX[(octaveNum * 7)] + 7, (int)(height*0.9));
        	g.drawString("D", whiteKeyStartX[(octaveNum * 7) + 1] + 7, (int)(height*0.9));
        	g.drawString("F", whiteKeyStartX[(octaveNum * 7) + 2] + 7, (int)(height*0.9));
        	g.drawString("J", whiteKeyStartX[(octaveNum * 7) + 3] + 7, (int)(height*0.9));
        	g.drawString("K", whiteKeyStartX[(octaveNum * 7) + 4] + 7, (int)(height*0.9));
        	g.drawString("L", whiteKeyStartX[(octaveNum * 7) + 5] + 7, (int)(height*0.9));
        	g.drawString(";", whiteKeyStartX[(octaveNum * 7) + 6] + 7, (int)(height*0.9));
        	g.drawString("E", blackKeyStartX[(octaveNum * 5)] + 3, (int)(height*0.6));
        	g.drawString("R", blackKeyStartX[(octaveNum * 5) + 1] + 3, (int)(height*0.6));
        	g.drawString("I", blackKeyStartX[(octaveNum * 5) + 2] + 3, (int)(height*0.6));
        	g.drawString("O", blackKeyStartX[(octaveNum * 5) + 3] + 3, (int)(height*0.6));
        	g.drawString("P", blackKeyStartX[(octaveNum * 5) + 4] + 3, (int)(height*0.6));
        }
    }// end paint
 
    public void pressBlack(int keyNum){
    	Graphics drawKey = getGraphics();
    	drawKey.setColor(Color.getHSBColor(0.61F, 0.67F, 1.0F));
    	drawKey.fillRect(blackKeyStartX[keyNum] + 1, (int)(height*0.3), 2*blackKey - 2, (int)(height*0.38));
		
		  //black key note assigning
		  if (keyNum % 5 == 0)
			  key = 1 + 12 * (keyNum / 5);
		  else if (keyNum % 5 == 1)
			  key = 3 + 12 * (keyNum / 5);
		  else if (keyNum % 5 == 2)
			  key = 6 + 12 * (keyNum / 5);
		  else if (keyNum % 5 == 3)
			  key = 8 + 12 * (keyNum / 5);
		  else if (keyNum % 5 == 4)
			  key = 10 + 12 * (keyNum / 5);
		  
		  drawKey.dispose();
		  channel.noteOn (key + 24, 100);     //play notes
    }
    
    public void pressWhite(int keyNum){
    	Graphics drawKey = getGraphics();
    	drawKey.setColor(Color.getHSBColor(0.61F, 0.67F, 1.0F));
    	drawKey.fillRect(whiteKeyStartX[keyNum], (int)(height*0.3), whiteKey - 1, (int)(height*0.7)); 
		
		drawKey.setColor(Color.black);
			
		if((keyNum + 1) % 7 == 1 || (keyNum + 1) % 7 == 4)           //Draw black key on the right
			drawKey.fillRect(whiteKeyStartX[keyNum] + 2*blackKey, (int)(height*0.3),  2*blackKey, (int)(height*0.38));
		else if ((keyNum + 1) % 7 == 0 || (keyNum + 1) % 7 == 3)     //Draw black key on the left
			drawKey.fillRect(whiteKeyStartX[keyNum], (int)(height*0.3), blackKey - 2, (int)(height*0.38));
		else 	//draw black key on the centre
		{
			drawKey.fillRect(whiteKeyStartX[keyNum], (int)(height*0.3), blackKey - 2 , (int)(height*0.38));
			drawKey.fillRect(whiteKeyStartX[keyNum] + 2*blackKey, (int)(height*0.3), blackKey + 1, (int)(height*0.38));
		}
		//white key note assigning
		if (keyNum % 7 == 0)
			key = 12 * (keyNum / 7);
		else if (keyNum % 7 == 1)
			key = 2 + 12 * (keyNum / 7);
		else if (keyNum % 7 == 2)
			key = 4 + 12 * (keyNum / 7);
		else if (keyNum % 7 == 3)
			key = 5 + 12 * (keyNum / 7);
		else if (keyNum % 7 == 4)
			key = 7 + 12 * (keyNum / 7);
		else if (keyNum % 7 == 5)
			key = 9 + 12 * (keyNum / 7);
		else if (keyNum % 7 == 6)
			key = 11 + 12 * (keyNum / 7);
		
		drawKey.dispose();
		channel.noteOn (key + 24, 100);     //play notes
    }
    
    public void mousePressed (MouseEvent evt){
	 
    //If is in mouse mode
    if(opt == true)
    {
	  int x = evt.getX();
	  int y = evt.getY();	  
	  
	  //enter keyboard area
	  if(y > height*0.30)
	  {
		  if (y < height*0.68) // assume black key is pressed
		  {
			  for(int j = 0; j < 35; j++)
			  {
				  if (x >= blackKeyStartX[j] && x <= blackKeyStopX[j])
				  {	
					  keyFound = true;
					  pressBlack(j);
					  break;
				  }
			  }
		  } //end black key case
		  
		  if((y < height*0.68 && keyFound == false) || y >= height*0.68)
		  {
			  for(int i = 0; i < 49; i++)
			  {
				  if (x >= whiteKeyStartX[i] && x <= whiteKeyStopX[i])
				  {
					pressWhite(i);
					break;
				}
			}
		}
	}// end keyboard area
  }//end if
}// end mousePressed
  
  public void mouseReleased(MouseEvent evt){
	  if (opt == true){
          channel.noteOff(key + 24, 100);
          repaint ();
	  }  
  }
  
  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {
	      channel.noteOff(key + 24, 100);
  } 
  public void mouseClicked(MouseEvent evt){}
  
  
  public void keyPressed(KeyEvent evt) {
	
	released = false; //Prevent auto-repeat key for keyReleased
	timer.stop();
	
	//Check whether it is an auto-repeat key for keyPressed before play note
	if(evt.getWhen() - prevKey != 0)
	{
       int keyValue = evt.getKeyCode();	
	
       switch (keyValue){
       case 'S':
    	   pressWhite(octaveNum * 7);
    	   break;
       case 'D':
    	   pressWhite((octaveNum * 7) + 1);
    	   break;
       case 'F':
    	   pressWhite((octaveNum * 7) + 2);
    	   break;
       case 'J':
    	   pressWhite((octaveNum * 7) + 3);
    	   break;
       case 'K':
    	   pressWhite((octaveNum * 7) + 4);
    	   break;
       case 'L':
    	   pressWhite((octaveNum * 7) + 5);
    	   break;
       case ';':
    	   pressWhite((octaveNum * 7) + 6);
    	   break;
       case 'E':
    	   pressBlack(octaveNum *5);
    	   break;
       case 'R':
    	   pressBlack((octaveNum *5) + 1);
    	   break;
       case 'I':
    	   pressBlack((octaveNum *5) + 2);
    	   break;
       case 'O':
    	   pressBlack((octaveNum *5) + 3);
    	   break;
       case 'P':
    	   pressBlack((octaveNum *5) + 4);
    	   break;
       case 'Z':
    	   if(octaveNum > 0)
    	   {
    		   octaveNum -= 1;
    		   repaint();
    	   }
    	   break;
       case 'X':
    	   if(octaveNum < 6)
    	   {
    		   octaveNum +=1;
    		   repaint();
    	   }
    	   break;
	}//end switch
   } //end if
}

public void keyReleased(KeyEvent evt) {
	prevKey = evt.getWhen();  //Prevent auto-repeat key for keyPressed
	
	if(released == false)  //Prevent auto-repeat key for keyReleased
	timer.restart();
}

public void keyTyped(KeyEvent evt) { }

private class TimerListener implements ActionListener{
	//Prevent auto-repeat key for keyReleased
    public void actionPerformed(ActionEvent e){
    	released = true;
    	timer.stop();
    	channel.noteOff(key + 24, 100);
    	repaint();
    }
}//end TimerListener
}// end layout()
  
}// end piano()