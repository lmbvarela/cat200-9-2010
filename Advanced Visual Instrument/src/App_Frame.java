// CAT 200 Group Project
// Group 9 : Advanced Visual Instrument
// Main application frame

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App_Frame extends JFrame{

	private JMenuBar menuBar;
	private JMenu fileMenu, playMenu, instrumentMenu, saveSubMenu;
	private JMenuItem newItem, openItem, exitItem,
					  playAllItem, pauseItem, stopItem,
					  selectItem, readNoteItem, recordItem;
	JDesktopPane mainFrame = new JDesktopPane();
	Piano visualPiano;
	
	public static void main (String [] args){
		new App_Frame();
	}
	
	public App_Frame(){
		
		super("Advanced Visual Instrument");
		setMinimumSize(new Dimension(1200,500));
	    setLocationRelativeTo(null); 

	    setContentPane(mainFrame);

	    menuBar = new JMenuBar();
	    
	    fileMenu = new JMenu("File");
	    saveSubMenu = new JMenu("Save");
	    newItem = new JMenuItem("New");
	    openItem = new JMenuItem("Open");
	    exitItem = new JMenuItem("Exit");
	    
	    fileMenu.add(newItem);
	    fileMenu.add(openItem);
	    fileMenu.add(saveSubMenu);
	    fileMenu.add(exitItem);
	    saveSubMenu.add(new JMenuItem("Save As WAV"));
	    saveSubMenu.add(new JMenuItem("Save As Music Note"));
	    
	    playMenu = new JMenu("Play");
	    playAllItem = new JMenuItem("Play All");
	    pauseItem = new JMenuItem("Pause");
	    stopItem = new JMenuItem("Stop");
	    
	    playMenu.add(playAllItem);
	    playMenu.add(pauseItem);
	    playMenu.add(stopItem);
	    
	    instrumentMenu = new JMenu ("Instrument(s)");
	    selectItem = new JMenuItem("Select");
	    readNoteItem = new JMenuItem("Read Note");
	    recordItem = new JMenuItem("Record from Microphone");
	    
	    instrumentMenu.add(selectItem);
	    instrumentMenu.add(readNoteItem);
	    instrumentMenu.add(recordItem);
	    
	    setJMenuBar(menuBar);
	    menuBar.add(fileMenu);
	    menuBar.add(playMenu);
	    menuBar.add(instrumentMenu);
	    
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
	    visualPiano = new Piano();

	    mainFrame.add(visualPiano);
	    setVisible(true); 
	}
}
