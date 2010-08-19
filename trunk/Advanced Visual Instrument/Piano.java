import javax.swing.*;
import java.awt.*;

public class Piano{
  public static void main(String[] args) {
    Piano d = new Piano();
  }

  public Piano(){
    JFrame frame = new JFrame("Virtual Piano");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(new layout());
    frame.setSize(1043,200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);  
  }

  public class layout extends JComponent{
	  
    public void paint(Graphics g){
    	//draw piano
    	int width = getSize().width; 
        int height = getSize().height;
        
        g.setColor(Color.gray);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.black);
        g.fillRect(0, (int)(height*0.3), width, (int)(height*0.7));
        
        int whiteKey = width / 49 + 1;
        
        g.setColor(Color.white);
        for (int i = 0; i < 49; i++)
        	g.fillRect( i*whiteKey, (int)(height*0.3), whiteKey - 1, (int)(height*0.7));
        
        int blackKey = width / 147;
        int invalidKey = 1;
        
        g.setColor(Color.black);
        for (int i = 0; i < 49; i++)
        {
        	if ( invalidKey != 3 && invalidKey != 7)
        		g.fillRect( i*whiteKey + 2*blackKey, (int)(height*0.3), 2*blackKey, (int)(height*0.38));
        	else if (invalidKey ==7)
        		invalidKey = 0;
        	invalidKey++;
        }
        	
    }
  }
}