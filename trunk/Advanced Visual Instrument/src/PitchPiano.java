// CAT 200 Group Project
// Group 9 : Advanced Visual Instruments

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import javax.sound.sampled.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;


public class PitchPiano extends JFrame implements ActionListener{
        
        private boolean opt;
        JRadioButton mouseOpt;
        JRadioButton keyboardOpt;
        ButtonGroup optGroup;
        JLabel instruction;
        JPanel optPanel;
        Box optBox = Box.createHorizontalBox();
        layout keyboard;
        JDialog message;
        JLabel fileLabel;
        private JButton addFileButton;
        private JFileChooser fileChooser;
        private FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Files", "wav");
        private static File soundFile;
        static byte[] dataSound;
        double[][] outdata;
        InputStream in;
        SourceDataLine sourceDataLine;
    	static AudioFormat audioFormat;
    	static int numberOfSample;
    	static double pitchShift;
    	static int osample;
    	static int fftFrameSize;
    	static PitchShift shifter = new PitchShift(16000);

  public PitchPiano(){
    setTitle("Pitch Piano");
    setSize(new Dimension(350,250));
    setLocationRelativeTo(null);
    setResizable(false);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
    
    mouseOpt = new JRadioButton("Mouse Mode");
    keyboardOpt = new JRadioButton("Keyboard Mode");
    
    addFileButton = new JButton("Add Audio File");
    addFileButton.setActionCommand("Add");
    addFileButton.addActionListener(this);
    
    fileChooser = new JFileChooser(new File("."));
    fileChooser.setFileFilter(filter);
    
    optGroup = new ButtonGroup();
    optGroup.add(mouseOpt);
    optGroup.add(keyboardOpt);
    mouseOpt.setSelected(true); //Default option is mouse
    opt = true;
     
    optBox.add(Box.createHorizontalStrut(3));
    optBox.add(addFileButton);
    optBox.add(Box.createHorizontalStrut(3));
    optBox.add(mouseOpt);
    optBox.add(keyboardOpt);
    
    fileLabel = new JLabel();
    fileLabel.setText("File Name  : " + "No File is Loaded!");
    fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    optPanel = new JPanel(); //Create a new JPanel to hold the instrument combo box and mode radio button
    optPanel.setLayout(new BoxLayout(optPanel,BoxLayout.Y_AXIS));
    optBox.setAlignmentX(LEFT_ALIGNMENT);
    optPanel.add(Box.createHorizontalStrut(2));
    optPanel.add(optBox);
    optPanel.add(fileLabel);
    getContentPane().add(optPanel, BorderLayout.NORTH);
    
    instruction = new JLabel("Press 'z' to switch to the left octave or 'x' to switch to the right octave for keyboard mode");
    getContentPane().add(instruction, BorderLayout.PAGE_END);
    
    keyboard = new layout();
    getContentPane().add(keyboard);
    
    message = new JDialog(this,"Processing Completed",true);
    message.setLayout(new BoxLayout(message.getContentPane(),BoxLayout.Y_AXIS));
    message.add(new JLabel("  Sound Processing Completed."));
    message.setLocationRelativeTo(null);
    message.setSize(250,100);
    message.setVisible(false);
    
    
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
  
  //load audio file into input stream
  public static void getSoundData() {
  	
  	AudioInputStream audioInputStream;
		
		try {		
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);			
			audioFormat = audioInputStream.getFormat();
			numberOfSample = (int) audioInputStream.getFrameLength();		
			dataSound = getData(audioInputStream);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
  }

  //convert audio into byte array
  private static byte[] getData(AudioInputStream audioStream) {
  	int length = (int) (audioStream.getFrameLength() * audioFormat
	        .getFrameSize());
	    
	    byte[] data = new byte[length];
	    DataInputStream is = new DataInputStream(audioStream);
	    try {
	      is.readFully(data);
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	    return data;	  
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
          int octaveNum = 1;
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
        
        whiteKey = (width / 14) + 1;

        g.setColor(Color.white);
        for (int i = 0; i < 49; i++)
        {
                g.fillRect( i*whiteKey, (int)(height*0.3), whiteKey - 1, (int)(height*0.7));
                whiteKeyStartX[i] = i*whiteKey;
                whiteKeyStopX[i] = i*whiteKey + (whiteKey - 1);
        }
        
        blackKey = width / 42;
        int invalidKey = 1;
        int blackKeyCount = 0;
        
        g.setColor(Color.black);
        for (int i = 0; i < 14; i++)
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
        
        keyBoardLayout(g);
    }// end paint
    
    //keyboard interface layout
    public void keyBoardLayout(Graphics gK){
    	if(opt == false){
            gK.setColor(Color.blue);
            gK.drawRect(whiteKeyStartX[(octaveNum * 7)],(int)(height*0.3), 7*whiteKey,(int)(height*0.7));
            gK.drawRect(whiteKeyStartX[(octaveNum * 7)]-1,(int)(height*0.3)-1, 7*whiteKey+2,(int)(height*0.7)+2);
            gK.drawString("S", whiteKeyStartX[(octaveNum * 7)] + 7, (int)(height*0.9));
            gK.drawString("D", whiteKeyStartX[(octaveNum * 7) + 1] + 7, (int)(height*0.9));
            gK.drawString("F", whiteKeyStartX[(octaveNum * 7) + 2] + 7, (int)(height*0.9));
            gK.drawString("J", whiteKeyStartX[(octaveNum * 7) + 3] + 7, (int)(height*0.9));
            gK.drawString("K", whiteKeyStartX[(octaveNum * 7) + 4] + 7, (int)(height*0.9));
            gK.drawString("L", whiteKeyStartX[(octaveNum * 7) + 5] + 7, (int)(height*0.9));
            gK.drawString(";", whiteKeyStartX[(octaveNum * 7) + 6] + 7, (int)(height*0.9));
            gK.drawString("E", blackKeyStartX[(octaveNum * 5)] + 3, (int)(height*0.6));
            gK.drawString("R", blackKeyStartX[(octaveNum * 5) + 1] + 3, (int)(height*0.6));
            gK.drawString("I", blackKeyStartX[(octaveNum * 5) + 2] + 3, (int)(height*0.6));
            gK.drawString("O", blackKeyStartX[(octaveNum * 5) + 3] + 3, (int)(height*0.6));
            gK.drawString("P", blackKeyStartX[(octaveNum * 5) + 4] + 3, (int)(height*0.6));
    }
    } 
    
    public void pressBlack(int keyNum){
        Graphics drawKey = getGraphics();
        drawKey.setColor(Color.getHSBColor(0.61F, 0.67F, 1.0F));
        drawKey.fillRect(blackKeyStartX[keyNum] + 1, (int)(height*0.3), 2*blackKey - 2, (int)(height*0.38));
        
        keyBoardLayout(drawKey);
        
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
        
        ShortAndDouble s = new ShortAndDouble(outdata[key]);
    	ByteAndShort b = new ByteAndShort(s.shortArray, false);
    	try {
			play(b.byteArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
               
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
                else    //draw black key on the centre
                {
                        drawKey.fillRect(whiteKeyStartX[keyNum], (int)(height*0.3), blackKey - 2 , (int)(height*0.38));
                        drawKey.fillRect(whiteKeyStartX[keyNum] + 2*blackKey, (int)(height*0.3), blackKey + 1, (int)(height*0.38));
                }
                
                keyBoardLayout(drawKey);
                
              //white key note assigning
                if (keyNum % 7 == 0)
                        key = 0 + 12 * (keyNum / 7);
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
                
                ShortAndDouble s = new ShortAndDouble(outdata[key]);
            	ByteAndShort b = new ByteAndShort(s.shortArray, false);
            	try {
					play(b.byteArray);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
    }
    
    //play byte array data
    private void play(byte[] s) throws IOException {
      	in = new ByteArrayInputStream(s);
      	DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
  		try {
  			sourceDataLine = (SourceDataLine)AudioSystem.getLine(
  			        dataLineInfo);
  			sourceDataLine.open(audioFormat);
  			sourceDataLine.start();		
  			int nBytesRead1 = 0;
  			byte[] abData1 = new byte[54200];
  			while (nBytesRead1 != -1) {
  	            try {
  					nBytesRead1 = in.read(abData1, 0, abData1.length);
  				} catch (InterruptedIOException e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  				
  	            if (nBytesRead1 >= 0) {
  	            	sourceDataLine.write(abData1, 0, nBytesRead1);
  	            }          
  	            
  			}
  		} catch (LineUnavailableException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		
      	
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
        if (sourceDataLine!=null)
		sourceDataLine.stop();
          repaint ();
          }  
  }
  
  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {
	  if(sourceDataLine!=null)
	  sourceDataLine.stop();
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
           if(octaveNum == 1)
           {
                   octaveNum -= 1;
                   repaint();
           }
           break;
       case 'X':
           if(octaveNum == 0)
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
        if (sourceDataLine!=null)
        sourceDataLine.stop();
        repaint();
    }
}//end TimerListener
}// end layout()



@Override
public void actionPerformed(ActionEvent e) {
	 if(e.getActionCommand() == "Add"){
         //choose audio file
         if(fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION){
                 soundFile = fileChooser.getSelectedFile();
                 getSoundData();
                 double[] indata;    	
             	
             	ByteAndShort b = new ByteAndShort(dataSound, false);
             	ShortAndDouble s = new ShortAndDouble(b.shortArray);
             	indata = s.doubleArray;
             	
             	outdata = new double[24][indata.length];
             	fileLabel.setText("File Name  : " + soundFile.getName());

             	//pitch shifting mechanism
             	for (int i = 0; i < 24; i++){
             		if(i<11)
             		shifter.setPitchShift(0.5 + (double)i * 0.5 / 12);
             		else
             		shifter.setPitchShift((double)i / 12);
                	shifter.smbPitchShift(indata, outdata[i], 0, numberOfSample);
             	}
             	message.setVisible(true);

         }
 }
}
  
}// end piano()
