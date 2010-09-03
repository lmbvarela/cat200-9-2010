package samplecode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.sound.sampled.*;

  public class AudioCapture01  extends JFrame{

  boolean stopCapture = false;
  ByteArrayOutputStream  byteArrayOutputStream;              
  AudioFormat audioFormat;
  TargetDataLine targetDataLine;
  AudioInputStream audioInputStream;
  SourceDataLine sourceDataLine;
  File file=new File("Advanced Visual Instrument Recorder.wav");
  FileOutputStream fout;
  AudioFileFormat.Type fileType;
  public static void main(
                        String args[]){
    new AudioCapture01();
  }//end main
  
  public AudioCapture01(){//constructor
	  try {
		fout=new FileOutputStream(file);
	} catch (FileNotFoundException e1) {
		
		e1.printStackTrace();
	}
    final JButton captureBtn =  new JButton("Capture");           
    final JButton stopBtn = new JButton("Stop");              
    final JButton playBtn= new JButton("Play");
    final JButton saveBtn = new JButton("Save");
              
    captureBtn.setEnabled(true);
    stopBtn.setEnabled(false);
    playBtn.setEnabled(false);
    saveBtn.setEnabled(false);
    
  ////////////////// press capture button //////////////////// 
    
    captureBtn.addActionListener(
    new ActionListener()
    {
       public void actionPerformed(ActionEvent e)
        {
          captureBtn.setEnabled(false);
          stopBtn.setEnabled(true);
          playBtn.setEnabled(false);
          saveBtn.setEnabled(false);
         
          captureAudio();
        }
      }
    );
    getContentPane().add(captureBtn);
    
////////////////////press stop button /////////
    
    stopBtn.addActionListener(
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
         {
          captureBtn.setEnabled(true);
          stopBtn.setEnabled(false);
          playBtn.setEnabled(true);
          saveBtn.setEnabled(true);
          
          //Terminate the capturing of input data from the microphone.
          stopCapture = true;
        }//end actionPerformed
      }//end ActionListener
    );//end addActionListener()
    getContentPane().add(stopBtn);
    
  ///////////////  press play button  /////////////////////
    
    playBtn.addActionListener(new ActionListener()
        {
    	   public void actionPerformed(ActionEvent e)
    	    {
             //Play back all of the data that was saved during capture.
    	      playAudio();
    	        }//end actionPerformed
    	      }//end ActionListener
    	    );//end addActionListener()
    	    getContentPane().add(playBtn);
    	    
  ///////////////////   press save button  ///////////////
    	    
    saveBtn.addActionListener(
      new ActionListener(){
        public void actionPerformed( ActionEvent e)
        {
          //save all the data after play back
          saveAudio();
        }//end actionPerformed
      }//end ActionListener
    );//end addActionListener()
    getContentPane().add(saveBtn);

    ////////////////   set  recorder layout     //////////////
    
    getContentPane().setLayout(new FlowLayout());                  
    setTitle("Advanced Visual Instrument Recorder");
    setDefaultCloseOperation( EXIT_ON_CLOSE);                   
    setSize(250,70);
    setVisible(true);
  }//end constructor
  
       //////////////     CaptureAudio function     ///////////////
  //This method captures audio input from a microphone and saves it in a ByteArrayOutputStream object.
  private void captureAudio(){
    try{
      //Get everything set up for capture 
      audioFormat = getAudioFormat();
      DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);              
      targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);                              
      targetDataLine.open(audioFormat);
      targetDataLine.start();

      //Create a thread to capture the microphone data and start it running.It will run until
      // the Stop button is clicked.
      Thread captureThread = new Thread(new CaptureThread());                       
      captureThread.start();
      }//end try 
      catch (Exception e) 
      {
      System.out.println(e);
      System.exit(0);
      }//end catch
     }//end captureAudio method
  
     ///////////////    playAudio function   ////////////////////////////////
  
   private void playAudio() {
	 try{
	  //Get everything set up for playback.
	  //Get the previously-saved data into a byte array object.
	  byte audioData[] =byteArrayOutputStream.toByteArray();                                      
	  //Get an input stream on the byte array containing the data
	  InputStream byteArrayInputStream= new ByteArrayInputStream(audioData);                         
	  AudioFormat audioFormat = getAudioFormat();           
	  audioInputStream =new AudioInputStream(byteArrayInputStream,audioFormat,
			            audioData.length/audioFormat. getFrameSize());                         
	  DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,audioFormat);                
	  sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);                     
	  sourceDataLine.open(audioFormat);
	  sourceDataLine.start();

	//Create a thread to play back the data and start it running.  It will run until
    // all the data has been playedback.
	   Thread playThread =new Thread(new PlayThread());     
	   playThread.start();
	    } //end try
	   catch (Exception e) {
	   System.out.println(e);
	   System.exit(0);
	    }//end catch
	  }//end playAudio
   
  ////////////////      saveAudio function    ///////////////////////////

  //This method save the audio data in the ByteArrayOutputStream
  private void saveAudio() {
    try{
    //Get everything set up for save the audio data
    //Get the previously-saved data into a byte array object. 
     byte audioData[] = byteArrayOutputStream.toByteArray();                   
    //Get an input stream on the byte array containing the data
     InputStream byteArrayInputStream= new ByteArrayInputStream(audioData);                      
     AudioFormat audioFormat = getAudioFormat();                
     audioInputStream =new AudioInputStream(byteArrayInputStream,audioFormat,
    		            audioData.length/audioFormat.getFrameSize());                 
     DataLine.Info dataLineInfo =new DataLine.Info( SourceDataLine.class,audioFormat);        
      sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
      sourceDataLine.open(audioFormat);
      sourceDataLine.start();
  
      //Create a thread to savethe data and start it running.  It will run until
      // all the data has been played back.   
      Thread saveThread = new Thread(new SaveThread());   
      saveThread.start();
     } //end try
      catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }//end catch
  }//end saveAudio

  //This method creates and returns an AudioFormat object for a given set of format parameters.
   private AudioFormat getAudioFormat(){
    float sampleRate = 8000.0F;
    int sampleSizeInBits = 16;
    int channels = 1;
    boolean signed = true;
    boolean bigEndian = false;
    return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);               
  }//end getAudioFormat

   //////////   class CaptureThread   /////////////////
 //Inner class to capture data from microphone
  class CaptureThread extends Thread{
  //An arbitrary-size temporary holding buffer 
  byte tempBuffer[] = new byte[10000];
  public void run(){
    byteArrayOutputStream =new ByteArrayOutputStream();     
    stopCapture = false;
    try{
    	//Loop until stopCapture is set by another thread that services the Stop button.
    while(!stopCapture){
        //Read data from the internal buffer of the data line.
        int cnt = targetDataLine.read( tempBuffer,0,tempBuffer.length);   
        if(cnt > 0){
          //Save data in output stream object.
          byteArrayOutputStream.write(tempBuffer, 0, cnt);
        }//end if
      }//end while
      byteArrayOutputStream.close();
    }//end try
    catch (Exception e) {
    System.out.println(e);
    System.exit(0);
    }//end catch
  }//end run
}//end inner class CaptureThread

    ///////////////  class PlayThread    //////////////////////
    //Inner class to play back the data
   class PlayThread extends Thread{
	  byte tempBuffer[] = new byte[10000];
	  public void run(){
	  try{
	     int cnt;
	      //Keep looping until the input read method returns -1 for empty stream.
	    while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1)   
	       {   
	        if(cnt > 0){
	          //Write data to the internal buffer of the data line where it will be delivered
	          // to the speaker.
	       sourceDataLine.write(tempBuffer, 0, cnt);           
	        }//end if
	      }//end while
	      //Block and wait for internal buffer of the data line to empty.
	      sourceDataLine.drain();
	      sourceDataLine.close();
	    }//end try
	     catch (Exception e) {
	     System.out.println(e);
	     System.exit(0);
	    }//end catch
	  }//end run
	}//end inner class PlayThread
   
   /////////////////////      class SaveThread    /////////////////
   //Inner class to play back the data
     class SaveThread extends Thread{
        byte tempBuffer[] = new byte[10000];
         public void run(){
       try{
            int cnt;
      //Keep looping until the input read method returns -1 for empty stream.
       if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE,audioInputStream))
       {     
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
       }//end if   
      }//end try
      catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }//end catch
  }//end run
}//end inner class SaveThread
     
}//end outer class AudioCapture01.java
 