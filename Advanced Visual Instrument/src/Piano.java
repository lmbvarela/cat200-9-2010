// CAT 200 Group Project
// Group 9 : Advanced Visual Instruments

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Piano extends JInternalFrame{
	
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
    getContentPane().add(new layout());
    setVisible(true); 
  }

  public class layout extends JPanel implements MouseListener{
	  
	  public void layout(){
		  addMouseListener(this);
	  }
	  
    public void paint(Graphics g){
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
					break;
				}
			}// end for
		}// end keyFound == false
	}// end keyboard area
}// end mousePressed
  
  public void mouseReleased(MouseEvent evt){
	  repaint();
  }
  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {} 
  public void mouseClicked(MouseEvent evt){}
  private int[] whiteKeyStartX = new int[49];
  private int[] whiteKeyStopX = new int[49];
  private int[] blackKeyStartX = new int[35];
  private int[] blackKeyStopX = new int[35];
}// end layout()
}// end piano()