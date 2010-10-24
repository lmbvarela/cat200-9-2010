/** CAT 200 Group Project
 *  Main Application frame
 *  Prerequisite : 
 *  In src file:
 *    microphone.GIF, play.gif, pause.gif, stop.gif and volume.gif
 *    Playback.java, AudioCapture01.java, SongPanel.java, SongTable.java, RoundButton.java,
 *    Piano.java, PitchPiano.java, PitchShift.java, ByteAndShort.java, ShortAndDouble.java
 */


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

public class App_Frame extends JFrame implements ActionListener{

        private JMenuBar menuBar;
        private JMenu fileMenu, playMenu, toolMenu;
        private JMenuItem openItem, exitItem,
                                          playAllItem, pauseItem, stopItem,
                                          pianoItem1, pianoItem2, recordItem;
        
        private JFileChooser fileChooser;
        private FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV Files", "wav");
        private File soundFile;
        
        private JPanel quickAccessPanel;
        private JButton addButton, deleteButton, pianoButton1, pianoButton2;
        private RoundButton micButton,playAllButton, pauseAllButton, stopAllButton;
        private URL playPic = getClass().getResource("play.gif");
        private URL pausePic = getClass().getResource("pause.gif");
        private URL stopPic = getClass().getResource("stop.gif");
        private URL microphonePic = getClass().getResource("microphone.GIF");
        private ImageIcon playIcon = new ImageIcon(playPic);
        private ImageIcon pauseIcon = new ImageIcon(pausePic);
        private ImageIcon stopIcon = new ImageIcon(stopPic);
        private ImageIcon microphoneIcon = new ImageIcon (microphonePic);
        
        private SongTable fileTable;
        private JScrollPane fileTablePane;
        private SongPanel songPanel;
        private Piano virtualPiano;
        private PitchPiano pitchpiano;
        private AudioCapture01 audioCapture;
        
        private int songCount = 0;
        private Timer timer = new Timer(500, new TimerListener());
        
        public static void main (String [] args){
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
            openItem = new JMenuItem("Open");
            exitItem = new JMenuItem("Exit");
            
            fileMenu.add(openItem);
            fileMenu.add(exitItem);
            
            openItem.addActionListener(this);
            exitItem.addActionListener(this);
            
            //Play Menu
            playMenu = new JMenu("Play");
            playAllItem = new JMenuItem("Play All");
            pauseItem = new JMenuItem("Pause All");
            stopItem = new JMenuItem("Stop All");
            
            playMenu.add(playAllItem);
            playMenu.add(pauseItem);
            playMenu.add(stopItem);
            
            playAllItem.addActionListener(this);
            pauseItem.addActionListener(this);
            stopItem.addActionListener(this);
            
            //Tool Menu
            toolMenu = new JMenu("Tool(s)");
            pianoItem1 = new JMenuItem("Virtual Piano");
            pianoItem2 = new JMenuItem("Pitch Piano");
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
            
            deleteButton = new JButton("Delete Audio File");
            deleteButton.setActionCommand("Delete");
            deleteButton.addActionListener(this);
            
            pianoButton1 = new JButton("Virtual Piano");
            pianoButton1.addActionListener(this);
            
            pianoButton2 = new JButton("Pitch Piano");
            pianoButton2.addActionListener(this);
            
            micButton = new RoundButton(microphoneIcon);
            micButton.setActionCommand("Record from Microphone");
            micButton.addActionListener(this);
            
            playAllButton = new RoundButton(playIcon);
            playAllButton.setActionCommand("Play All");
            playAllButton.addActionListener(this);
            
            pauseAllButton = new RoundButton(pauseIcon);
            pauseAllButton.setActionCommand("Pause All");
            pauseAllButton.addActionListener(this);
            
            stopAllButton = new RoundButton(stopIcon);
            stopAllButton.setActionCommand("Stop All");
            stopAllButton.addActionListener(this);
           
            quickAccessPanel = new JPanel();
            quickAccessPanel.add(addButton);
            quickAccessPanel.add(deleteButton);
            quickAccessPanel.add(pianoButton1);
            quickAccessPanel.add(pianoButton2);
            quickAccessPanel.add(micButton);
            quickAccessPanel.add(playAllButton);
            quickAccessPanel.add(pauseAllButton);
            quickAccessPanel.add(stopAllButton);
            quickAccessPanel.setAlignmentX(LEFT_ALIGNMENT);
            
            
            //File Panel
            fileTable = new SongTable();
            fileTablePane = new JScrollPane(fileTable);
            
            
            //Adding components to App_Frame
            getContentPane().add(quickAccessPanel);
            getContentPane().add(Box.createHorizontalStrut(5));
            getContentPane().add(fileTablePane);
            

            //Initialize other components that are invoked by user
            //File Chooser
            fileChooser = new JFileChooser(new File("."));
            fileChooser.setFileFilter(filter);
            
            //Virtual Piano
            virtualPiano = new Piano();
            virtualPiano.setVisible(false);
            
            //Pitch Piano
            pitchpiano = new PitchPiano();
            pitchpiano.setVisible(false);
            
            //AudioCapture01
            audioCapture = new AudioCapture01();
            audioCapture.setVisible(false);
            
            
            setVisible(true); 
        }
        
        public void actionPerformed(ActionEvent e){
                
                if(e.getActionCommand() == "Open" || e.getActionCommand() == "Add"){
                
                        if(fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION){
                                soundFile = fileChooser.getSelectedFile();
                                songPanel = new SongPanel(soundFile);
                                fileTable.addRow(songPanel);
                                timer.start(); //Assume the user play the songs, start the timer to update all the song panels.
                        }
                }
                
                if(e.getActionCommand() == "Exit"){     
                        int opt = JOptionPane.showConfirmDialog(getContentPane(), 
                                             "Exit Advanced Visual Instruments?","Exit", JOptionPane.YES_NO_CANCEL_OPTION);
                        if(opt == JOptionPane.YES_OPTION){
                                timer.stop();
                                System.exit(0);
                        }
                }
                
                if(e.getActionCommand() == "Delete"){
                        int rowIndex = fileTable.getSelectedRow();
                        fileTable.deleteRow(rowIndex);
                }
                
                if(e.getActionCommand() == "Play All"){
                        songCount = fileTable.getRowCount();
                        
                        if(songCount > 0){
                        for(int i = 0; i < songCount; i++){
                                songPanel = (SongPanel) fileTable.getModel().getValueAt(i, 0);
                                songPanel.playSound();
                        }
                }// end if
                }
                        
                
                if(e.getActionCommand() == "Pause All"){
                        songCount = fileTable.getRowCount();
                        
                        if(songCount > 0){
                        for(int i = 0; i < songCount; i++){
                                songPanel = (SongPanel) fileTable.getModel().getValueAt(i, 0);
                                songPanel.pauseSound();
                        }
                }// end if
                }
                
                
                if(e.getActionCommand() == "Stop All"){
                        songCount = fileTable.getRowCount();
                        
                        if(songCount > 0){
                        for(int i = 0; i < songCount; i++){
                                songPanel = (SongPanel) fileTable.getModel().getValueAt(i, 0);
                                songPanel.stopSound();
                        }
                }// end if
                }
                
                
                if (e.getActionCommand() == "Virtual Piano")
                         virtualPiano.setVisible(true);
                
                
                if (e.getActionCommand() == "Pitch Piano")
                        pitchpiano.setVisible(true);
                
                if(e.getActionCommand() == "Record from Microphone"){
                        audioCapture.setVisible(true);
                }
        
        }
        
        /**
         * A timer to update all the rows in fileTable
         *
         */
        private class TimerListener implements ActionListener{
            public void actionPerformed(ActionEvent e){
                fileTable.update();
            }       
         }

        
}
