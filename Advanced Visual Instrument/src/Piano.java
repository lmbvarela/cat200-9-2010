// CAT 200 Group Project
// Group 9 : Advanced Visual Instruments

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.*;


import java.awt.*;
import java.awt.event.*;

public class Piano extends JInternalFrame{
	
	private MidiChannel channel;
	private Synthesizer synthesizer;
	JComboBox instruments;
	
  public static void main(String[] args) {
    new Piano();
  }

  public Piano(){
    setTitle("Virtual Piano");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(1083,200));
    setClosable(true);
    setResizable(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    insInit();
    instruments = new JComboBox (getInstruments());
    getContentPane ().add (instruments, BorderLayout.NORTH);
    getContentPane().add(new layout());
    setVisible(true); 
    
    ActionListener al;
    al = new ActionListener ()
         {
             public void actionPerformed (ActionEvent e)
             {
                JComboBox cb = (JComboBox) e.getSource ();
                chooseInstrument (cb.getSelectedIndex ());
             }
         };
    instruments.addActionListener (al);
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


  public class layout extends JPanel implements MouseListener{
	  
	  int key;
	  
	  public void layout(){
		  addMouseListener(this);
	  }
	  
    public void paint(Graphics g){
    	
    	key = 0;
    	
    	//draw piano
    	int width = getSize().width; 
        int height = getSize().height;

        g.setColor(Color.gray);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.black);
        g.fillRect(0, (int)(height*0.3), width, (int)(height*0.7));
        
        int whiteKey = (width / 49) + 1;

        g.setColor(Color.white);
        for (int i = 0; i < 49; i++)
        {
        	g.fillRect( i*whiteKey, (int)(height*0.3), whiteKey - 1, (int)(height*0.7));
        	whiteKeyStartX[i] = i*whiteKey;
        	whiteKeyStopX[i] = i*whiteKey + (whiteKey - 1);
        }
        
        int blackKey = width / 147;
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
    }// end paint
 
  public void mousePressed (MouseEvent evt){
	  
	  int width = getSize().width;
	  int height = getSize().height;

	  int x = evt.getX();
	  int y = evt.getY();
	  
	  int whiteKey = (width / 49) + 1;
	  int blackKey = width / 147;
	  
	  boolean keyFound = false;
	  
	  Graphics g = getGraphics();
	  g.setColor(Color.getHSBColor(0.61F, 0.67F, 1.0F));
	  
	  //enter keyboard area
	  if(y > height*0.30)
	  {
		  if (y < height*0.68) // assume black key is pressed
		  {
			  for(int j = 0; j < 35; j++)
			  {
				  if (x >= blackKeyStartX[j] && x <= blackKeyStopX[j])
				  {	
					  g.fillRect(blackKeyStartX[j] + 1, (int)(height*0.3), 2*blackKey - 2, (int)(height*0.38));
					  keyFound = true;
					  //black key note assigning
					  if (j % 5 == 0)
						  key += 1 + 12 * (j / 5);
					  else if (j % 5 == 1)
						  key += 3 + 12 * (j / 5);
					  else if (j % 5 == 2)
						  key += 6 + 12 * (j / 5);
					  else if (j % 5 == 3)
						  key += 8 + 12 * (j / 5);
					  else if (j % 5 == 4)
						  key += 10 + 12 * (j / 5);
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
					g.fillRect(whiteKeyStartX[i], (int)(height*0.3), whiteKey - 1, (int)(height*0.7)); 
					
					
	
					g.setColor(Color.black);
						
					if((i + 1) % 7 == 1 || (i + 1) % 7 == 4)           //Draw black key on the right
						g.fillRect(whiteKeyStartX[i] + 2*blackKey, (int)(height*0.3), 2*blackKey, (int)(height*0.38));
					else if ((i + 1) % 7 == 0 || (i + 1) % 7 == 3)     //Draw black key on the left
						g.fillRect(whiteKeyStartX[i], (int)(height*0.3), blackKey - 1, (int)(height*0.38));
					else 	//draw black key on the centre
					{
						g.fillRect(whiteKeyStartX[i], (int)(height*0.3), blackKey, (int)(height*0.38));
						g.fillRect(whiteKeyStartX[i] + 2*blackKey, (int)(height*0.3), blackKey, (int)(height*0.38));
					}
					//white key note assigning
					if (i % 7 == 0)
						key += 12 * (i / 7);
					else if ( i % 7 == 1)
						key += 2 + 12 * (i / 7);
					else if (i % 7 == 2)
						key += 4 + 12 * (i / 7);
					else if (i % 7 == 3)
						key += 5 + 12 * (i / 7);
					else if (i % 7 == 4)
						key += 7 + 12 * (i / 7);
					else if (i % 7 == 5)
						key += 9 + 12 * (i / 7);
					else if (i % 7 == 6)
						key += 11 + 12 * (i / 7);
					break;
				}
			}// end for
		}// end keyFound == false
	}// end keyboard area
	
	  channel.noteOn (key + 24, 100);     //play notes
	  
}// end mousePressed
  
  public void mouseReleased(MouseEvent evt){
	
          channel.noteOff(key + 24, 100);
          repaint ();
      
  }
  
  
  
  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {
	      channel.noteOff(key + 24, 100);
  } 
  public void mouseClicked(MouseEvent evt){}
  private int[] whiteKeyStartX = new int[49];
  private int[] whiteKeyStopX = new int[49];
  private int[] blackKeyStartX = new int[35];
  private int[] blackKeyStopX = new int[35];
}// end layout()
  
  
  
}// end piano()