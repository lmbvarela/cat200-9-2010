//Choo Seah Ling (104009)

import java.awt.*;
import javax.swing.*;

public class Lab01 extends JApplet{
	Display drawingSurface;
	
public void init(){
	
	drawingSurface = new Display();
	drawingSurface.setBackground(Color.white);
	setContentPane(drawingSurface);
	
}//end init()

class Display extends JPanel{

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		float hue = (float)Math.random();
		g.setColor(Color.getHSBColor(hue, 1.0F, 1.0F));
		
		int coorX = getSize().width;
		int coorY = getSize().height;
		
		//Draw Ship Body
		g.drawLine(100,100,coorX-100,coorY-100);
		g.drawArc(100,100,coorX-100,coorY-100,0,-180);
		
		g.dispose();
	}//end paintComponent
	
}//end JPanel
}//end JApplet
