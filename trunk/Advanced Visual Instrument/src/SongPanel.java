import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.PanelUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

public class SongPanel extends JPanel implements ActionListener, ChangeListener{

	File soundFile;
	Playback player;
	private Timer timer = new Timer(100, new TimerListener());
	
	URL playPic = getClass().getResource("play.gif");
	URL pausePic = getClass().getResource("pause.gif");
	URL stopPic = getClass().getResource("stop.gif");
	URL volumePic = getClass().getResource("volume.gif");
	ImageIcon playIcon = new ImageIcon(playPic);
	ImageIcon pauseIcon = new ImageIcon(pausePic);
	ImageIcon stopIcon = new ImageIcon(stopPic);
	ImageIcon volumeIcon = new ImageIcon(volumePic);
	RoundButton playButton;
	RoundButton pauseButton;
	RoundButton stopButton;
	
	String soundFileName;
	int durationMin = 0;
	int durationSec = 0;
	int currentMin = 0;
	int currentSec = 0;
	int currentPosition = 0;
	JSlider volumeBar;
	JSlider durationBar;
	JLabel fileLabel;
	JLabel volumeLabel;
	JLabel durationLabel;
	JPanel toolPanel;
	JPanel durationPanel;

	
	/**
	 * Construct a new SongPanel based on the sound file chosen by user
	 * 
	 */
	public SongPanel(File inSoundFile){
		
		initPlayback(inSoundFile); // Load sound file
		setSize(1200,150);
		setBorder(new EtchedBorder(EtchedBorder.RAISED));
		setLayout(new BorderLayout(5,5));
		
		
		//Play, Pause and Stop Button Properties and Initialization
		playButton = new RoundButton(playIcon);
		playButton.setPreferredSize(new Dimension(35,35));
		playButton.setActionCommand("Play");
		playButton.addActionListener(this);
		playButton.setEnabled(true);
		
		pauseButton = new RoundButton(pauseIcon);
		pauseButton.setPreferredSize(new Dimension(35,35));
		pauseButton.setActionCommand("Pause");
		pauseButton.addActionListener(this);
		pauseButton.setEnabled(false);
		
		stopButton = new RoundButton(stopIcon);
		stopButton.setPreferredSize(new Dimension(35,35));
		stopButton.setActionCommand("Stop");
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		
		
		//Volume properties
		volumeLabel = new JLabel();
		volumeLabel.setIcon(volumeIcon);
		volumeBar = new JSlider(JSlider.HORIZONTAL,0,100,80);
		volumeBar.setMaximumSize(new Dimension(50,35));
		volumeBar.addChangeListener(this);	
		
		
		//Create a toolPanel to contain Play, Pause, Stop buttons and volume
		toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.X_AXIS));
		toolPanel.add(playButton);
		toolPanel.add(pauseButton);
		toolPanel.add(stopButton);
		toolPanel.add(Box.createVerticalStrut(10));
		toolPanel.add(volumeLabel);
		toolPanel.add(volumeBar);
		toolPanel.setAlignmentX(RIGHT_ALIGNMENT);
		
		
		//Create a JLabel to display sound file name
		fileLabel = new JLabel();
		fileLabel.setText("File Name  : " + soundFile.getName());
		fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		
		//Sound progress bar implemented as JSlider to enable user to skip to a desired position
		durationLabel = new JLabel();	
		durationLabel.setText(currentMin + ":" + String.format("%02d" , currentSec) + " of " + durationMin + ":" + String.format("%02d" , durationSec));
		durationPanel = new JPanel();
		durationPanel.setLayout(new BoxLayout(durationPanel, BoxLayout.Y_AXIS));
		durationPanel.add(durationBar);
		durationPanel.add(Box.createHorizontalStrut(3));
		durationPanel.add(durationLabel);
		durationPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		
		//Add components into the SongPanel
		add(fileLabel, BorderLayout.NORTH);
		add(durationPanel, BorderLayout.CENTER);
		add(toolPanel, BorderLayout.SOUTH);
		
	    setVisible(true); 

	}
	
	public void actionPerformed (ActionEvent evt){
		if(evt.getActionCommand() == "Play"){		  
			player.play();
			timer.start();
			playButton.setEnabled(false);		  
			pauseButton.setEnabled(true);		  
			stopButton.setEnabled(true);
		}
		
		if(evt.getActionCommand() == "Pause"){
			player.pause();
			timer.stop();
			playButton.setEnabled(true);
			pauseButton.setEnabled(false);
			stopButton.setEnabled(true);
		}
		
		if(evt.getActionCommand() == "Stop"){
			player.stop();
			timer.restart();
			playButton.setEnabled(true);
			pauseButton.setEnabled(false);
			stopButton.setEnabled(false);
		}
	}
	
	//TimerListener to fire update event every second
	private class TimerListener implements ActionListener{
	    public void actionPerformed(ActionEvent e){
	    	currentPosition = player.getCurrentPosition();
	    	currentMin = currentPosition / 60;
	    	currentSec = currentPosition % 60;
	    	durationBar.setValue(currentPosition);
	    	durationLabel.setText(currentMin + ":" + String.format("%02d" , currentSec) + " of " + durationMin + ":" + String.format("%02d" , durationSec));
	    	}
	}//end TimerListener
	
	
	//To load sound file and set up the sound progress bar
	public void initPlayback(File inSoundFile){
		boolean loaded;
		
		soundFile = inSoundFile;
		player = new Playback();
		loaded = player.load(soundFile);
		
		if(!loaded)
			System.out.println("ERROR : Could not load the file. FILE format not supported.");
		else{
			int duration = player.getDuration();
			durationMin = duration / 60;
			durationSec = duration % 60;
			durationBar = new JSlider(0, duration, currentPosition);
			durationBar.addChangeListener(this);
			player.setVolume(80.0f);
		}
	}
	
	//To update volume and sound progress
	
	public void stateChanged(ChangeEvent e) {
        
		if (e.getSource() == volumeBar){
        float volume = (float)volumeBar.getValue();
        player.setVolume(volume);
        }
        
        
		if(e.getSource() == durationBar){
        	int desiredPosition = durationBar.getValue();
        	
        	if(desiredPosition != currentPosition){
        	currentPosition = desiredPosition;
        	 player.setCurrentPostion(currentPosition);
        	 durationBar.setValue(currentPosition);
        	}
        }
    }

}
