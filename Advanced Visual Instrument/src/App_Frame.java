// CAT 200 Group Project
// Main Application frame

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;

public class App_Frame extends JFrame implements ActionListener{

	private JMenuBar menuBar;
	private JMenu fileMenu, playMenu, toolMenu, saveSubMenu;
	private JMenuItem newItem, openItem, exitItem,
					  saveWavItem, saveNoteItem,
					  playAllItem, pauseItem, stopItem,
					  pianoItem1, pianoItem2, recordItem;
	
	private JFileChooser fileChooser;
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Files", "wav");
	private File soundFile;
	
	private JPanel quickAccessPanel;
	private JButton addButton, pianoButton1, pianoButton2;
	private RoundButton playAllButton, stopAllButton;
	URL playPic = getClass().getResource("play.gif");
	URL pausePic = getClass().getResource("pause.gif");
	URL stopPic = getClass().getResource("stop.gif");
	ImageIcon playIcon = new ImageIcon(playPic);
	ImageIcon pauseIcon = new ImageIcon(pausePic);
	ImageIcon stopIcon = new ImageIcon(stopPic);
	
	JDesktopPane mainFrame;
	private SongTable fileTable;
	private JScrollPane fileTablePane;
	SongPanel songPanel;
	Piano virtualPiano;
	
	public static void main (String [] args){
		  /*try {
		        UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		    } 
		    catch (Exception e) {
		       System.out.println("ERROR : Unable to set to current windows look and feel");
		    }*/
		new App_Frame();
	}
	
	//Constructor
	public App_Frame(){
		
		//JFrame properties
		super("Advanced Visual Instrument");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setSize(new Dimension(1200,500));
	    setLocationRelativeTo(null); 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
		
	    //File menu
	    fileMenu = new JMenu("File");
	    newItem = new JMenuItem("New");
	    openItem = new JMenuItem("Open");
	    saveSubMenu = new JMenu("Save");
	    saveWavItem = new JMenuItem("Save As WAV");
	    saveNoteItem = new JMenuItem("Save As Music Note");
	    exitItem = new JMenuItem("Exit");
	    
	    fileMenu.add(newItem);
	    fileMenu.add(openItem);
	    fileMenu.add(saveSubMenu);
	    fileMenu.add(exitItem);
	    saveSubMenu.add(saveWavItem);
	    saveSubMenu.add(saveNoteItem);
	    
	    newItem.addActionListener(this);
	    openItem.addActionListener(this);
	    saveWavItem.addActionListener(this);
	    saveNoteItem.addActionListener(this);
	    exitItem.addActionListener(this);
	    
	    //Play Menu
	    playMenu = new JMenu("Play");
	    playAllItem = new JMenuItem("Play All");
	    pauseItem = new JMenuItem("Pause");
	    stopItem = new JMenuItem("Stop");
	    
	    playMenu.add(playAllItem);
	    playMenu.add(pauseItem);
	    playMenu.add(stopItem);
	    
	    playAllItem.addActionListener(this);
	    pauseItem.addActionListener(this);
	    stopItem.addActionListener(this);
	    
	    //Tool Menu
	    toolMenu = new JMenu("Tool(s)");
	    pianoItem1 = new JMenuItem("Virtual Piano v1");
	    pianoItem2 = new JMenuItem("Virtual Piano v2");
	    recordItem = new JMenuItem("Record from Microphone");
	    
	    toolMenu.add(pianoItem1);
	    toolMenu.add(pianoItem2);
	    toolMenu.add(recordItem);
	    
	    pianoItem1.addActionListener(this);
	    pianoItem2.addActionListener(this);
	    recordItem.addActionListener(this);
	    
	    //Menu Bar properties
	    menuBar = new JMenuBar();
	    setJMenuBar(menuBar);
	    menuBar.add(fileMenu);
	    menuBar.add(playMenu);
	    menuBar.add(toolMenu);
	    
	    
	    //Quick Access Panel
	    addButton = new JButton("Add Audio File");
	    addButton.setActionCommand("Add");
	    addButton.addActionListener(this);
	    
	    pianoButton1 = new JButton("Virtual Piano v1");
	    pianoButton1.addActionListener(this);
	    
	    pianoButton2 = new JButton("Virtual Piano v2");
	    pianoButton2.addActionListener(this);
	    
	    playAllButton = new RoundButton(playIcon);
	    playAllButton.setActionCommand("Play All");
	    playAllButton.addActionListener(this);
	    
	    stopAllButton = new RoundButton(stopIcon);
	    stopAllButton.setActionCommand("Stop");
	    stopAllButton.addActionListener(this);
	    
	    quickAccessPanel = new JPanel();
	    quickAccessPanel.add(addButton);
	    quickAccessPanel.add(pianoButton1);
	    quickAccessPanel.add(pianoButton2);
	    quickAccessPanel.add(playAllButton);
	    quickAccessPanel.add(stopAllButton);
	    quickAccessPanel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    
	    //File Panel
	    fileTable = new SongTable();
	    fileTable.addColumn("Files Opened");
	    fileTable.getColumnModel().getColumn(0).setCellRenderer(new CustomTableCellRenderer());
	    fileTable.setRowHeight(150);
	    fileTable.setFillsViewportHeight(true);

	    fileTablePane = new JScrollPane(fileTable);
	    
	    
	    //Adding components to App_Frame
	    getContentPane().add(quickAccessPanel);
	    getContentPane().add(Box.createHorizontalStrut(5));
	    getContentPane().add(fileTablePane);
	    

	    //Initialize other components that are invoked by user
	    //File Chooser
	    fileChooser = new JFileChooser(new File("."));
	    fileChooser.setFileFilter(filter);
	    
	    //Virtual Piano v1
	    virtualPiano = new Piano();
	    virtualPiano.setVisible(false);
	    
	    //tableUpdate task
	    setVisible(true); 
	}
	
	public void actionPerformed(ActionEvent e){
		
		if(e.getActionCommand() == "New")
		  System.out.println("Not implemented yet");

		
		if(e.getActionCommand() == "Open" || e.getActionCommand() == "Add"){
		
			if(fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION){
				soundFile = fileChooser.getSelectedFile();
				songPanel = new SongPanel(soundFile);
				fileTable.addRow(songPanel);
			}
		}
		
		
		if(e.getActionCommand() == "Save As WAV")
			System.out.println("Not implemented yet");

		if(e.getActionCommand() == "Save As Music Note")
			System.out.println("Not implemented yet");
		
		if(e.getActionCommand() == "Exit"){	
			System.out.println("Here");
			int opt = JOptionPane.showConfirmDialog(getContentPane(), 
					     "Exit Advanced Visual Instruments?","Exit", JOptionPane.YES_NO_CANCEL_OPTION);
			if(opt == JOptionPane.YES_OPTION)
				System.exit(0);
		}
		
		if(e.getActionCommand() == "Play All")
			System.out.println("Not implemented yet");

		
		if(e.getActionCommand() == "Pause")
			System.out.println("Not implemented yet");
		
		
		if(e.getActionCommand() == "Stop")
			System.out.println("Not implemented yet");	
		
		
		if (e.getActionCommand() == "Virtual Piano v1")
			 virtualPiano.setVisible(true);
		
		
		if (e.getActionCommand() == "Virtual Piano v2")
			System.out.println("Not implemented yet");
		
		if(e.getActionCommand() == "Record From Microphone")
			System.out.println("Not implemented yet");
	
	}
	
	public class CustomTableCellRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
    
			SongPanel cell = (SongPanel) value;
			return cell;
		}
	}
	
}
