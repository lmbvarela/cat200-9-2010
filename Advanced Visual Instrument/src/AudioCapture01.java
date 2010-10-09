// CAT 200 Group Project
// Group 9 : Advanced Visual Instruments

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;
import java.io.*;
import javax.sound.sampled.*;
import java.awt.font.*;
import java.text.*;

/**
 *Based on CapturePlayback.java
 * @version 1.11
 * @author Brian Lichtenwalter  
 */
public class AudioCapture01 extends JPanel implements ActionListener,ControlContext {

    final int bufSize = 18000;
   
    JButton playBtn, captBtn, pauseBtn,saveBtn;
    JTextField textField;
    
    FormatControls formatControls = new FormatControls();
    Capture capture = new Capture();
    Playback playback = new Playback();
    AudioInputStream audioInputStream;
    SamplingGraph samplingGraph;

    String fileName = "untitled";
    String printErr;
    double duration, seconds;
    File file;
    Vector vectorLine = new Vector();

    public AudioCapture01() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5,5,5,5));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(new EmptyBorder(10,0,5,0));
        playBtn = addButton("Play", buttonsPanel, false);
        captBtn = addButton("Capture", buttonsPanel, true);
        pauseBtn = addButton("Pause", buttonsPanel, false);    
        p2.add(buttonsPanel);

        JPanel samplingPanel = new JPanel(new BorderLayout());
        samplingPanel.add(samplingGraph = new SamplingGraph());
        p2.add(samplingPanel);

        JPanel savePanel = new JPanel();
        JPanel saveFilePanel = new JPanel();
        saveFilePanel.add(new JLabel("File name:  "));
        saveFilePanel.add(textField = new JTextField(fileName));
        textField.setPreferredSize(new Dimension(140,25));
        savePanel.add(saveFilePanel);

        JPanel saveButtonPanel = new JPanel();
        saveBtn = addButton("Save", saveButtonPanel, false);
        savePanel.add(saveButtonPanel);

        p2.add(savePanel);
        p1.add(p2);
        add(p1);
    }

    public void open() { }
    public void close(){
    	if(playback.thread !=null)
    		playBtn.doClick(0);
    	if(capture.thread !=null)
    		captBtn.doClick(0);
    }
    private JButton addButton(String name, JPanel p, boolean state) {
        JButton button = new JButton(name);
        button.addActionListener(this);
        button.setEnabled(state);
        p.add(button);
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();   
         if (obj.equals(saveBtn)) 
            saveToFile(textField.getText().trim(), AudioFileFormat.Type.WAVE);
         else if (obj.equals(playBtn)) 
         {
            if (playBtn.getText().startsWith("Play")) 
            {
                playback.start();
                samplingGraph.start();
                captBtn.setEnabled(false);
                pauseBtn.setEnabled(true);
                playBtn.setText("Stop");
             }
            else {
                playback.stop();
                samplingGraph.stop();
                captBtn.setEnabled(false);
                pauseBtn.setEnabled(false);
                playBtn.setText("Play");
                  }     
         } 
         else if (obj.equals(captBtn))
         {
            if (captBtn.getText().startsWith("Capture")) 
              {
                capture.start();
                fileName = "Untitled";
                samplingGraph.start();         
                playBtn.setEnabled(false);
                pauseBtn.setEnabled(true);           
                saveBtn.setEnabled(false);
                captBtn.setText("Stop");
              } 
            else {
                vectorLine.removeAllElements();  
                capture.stop();
                samplingGraph.stop();
              
                playBtn.setEnabled(true);
                pauseBtn.setEnabled(false);     
                saveBtn.setEnabled(true);
                captBtn.setText("Capture");
                 }
           
        } 
         else if (obj.equals(pauseBtn))
           {
            if (pauseBtn.getText().startsWith("Pause")) 
               {
                if (capture.thread != null)
                    capture.line.stop();      
                else {
                    if (playback.thread != null) 
                        playback.line.stop();     
                      }
                pauseBtn.setText("Resume");
               } 
            else {
                if (capture.thread != null) {
                    capture.line.start();
                } 
                else {
                    if (playback.thread != null) {
                        playback.line.start();
                     }
                }
                pauseBtn.setText("Pause");
            }     
        }        
    }

    public void createAudioInputStream(File file,boolean updateComponents) {
        if (file != null && file.isFile()) {
            try {
                printErr = null;
                audioInputStream = AudioSystem.getAudioInputStream(file);
                playBtn.setEnabled(true);
                fileName = file.getName();
            long milliseconds = (long)((audioInputStream.getFrameLength() *1000) / audioInputStream.getFormat().getFrameRate());
                saveBtn.setEnabled(true);
                if (updateComponents) {
                	audioInputStream.getFormat();
                    samplingGraph.createWaveForm(null);
                }
            } catch (Exception ex) { 
                reportStatus(ex.toString());
            }
        } else {
            reportStatus("Audio file required.");
        }
    }

    public void saveToFile(String name, AudioFileFormat.Type fileType) {
        if (audioInputStream == null) {
            reportStatus("No audio to save");
            return;
        } else if (file != null) {
            createAudioInputStream(file, false);
        }
        // reset to the beginnning of the captured data
        try {
            audioInputStream.reset();    
        } catch (Exception e) { 
            reportStatus("Unable to reset stream " + e);
            return;
    }
 	
        File file = new File(fileName = name);
        try {
            if (AudioSystem.write(audioInputStream, fileType, file) == -1) {
                throw new IOException("Problems writing to file");
            }
        } catch (Exception ex) { reportStatus(ex.toString()); }
        samplingGraph.repaint();
    }
        
    private void reportStatus(String msg) {
        if ((printErr = msg) != null) {
            System.out.println(printErr);
            samplingGraph.repaint();
        }
    }
    /**
     * Write data to the OutputChannel.
     */
    public class Playback implements Runnable {

        SourceDataLine line;
        Thread thread;

        public void start() {
            printErr = null;
            thread = new Thread(this);
            thread.setName("Playback");
            thread.start();
        }

        public void stop() {
            thread = null;
        }
        
        private void shutDown(String message) {
            if ((printErr = message) != null) {
                System.err.println(printErr);
                samplingGraph.repaint();
            }
            if (thread != null) {
                thread = null;
                samplingGraph.stop();
                captBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                playBtn.setText("Play");
            } 
        }
        public void run() {
            // make sure we have something to play
            if (audioInputStream == null) {
                shutDown("No loaded audio to play back");
                return;
            }
            // reset to the beginnning of the stream
            try {
                audioInputStream.reset();
            } catch (Exception e) {
                shutDown("Unable to reset the stream\n" + e);
                return;
            }
            // get an AudioInputStream of the desired format for playback
            AudioFormat format = formatControls.getFormat();
            AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
                        
            if (playbackInputStream == null) {
                shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
                return;
            }
            // define the required attributes for our line, 
            // and make sure a compatible line is supported.
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
                format);
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }
            // get and open the source data line for playback.
            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format, bufSize);
            } catch (LineUnavailableException ex) { 
                shutDown("Unable to open the line: " + ex);
                return;
            }
            // play back the captured audio data
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;
            // start the source data line
            line.start();
            while (thread != null) {
                try {
                    if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                        break;
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0 ) {
                        numBytesRemaining -= line.write(data, 0, numBytesRemaining);
                    }
                } catch (Exception e) {
                    shutDown("Error during playback: " + e);
                    break;
                }
            }
            // we reached the end of the stream.  let the data play out, then
            // stop and close the line.
            if (thread != null) {
                line.drain();
            }
            line.stop();
            line.close();
            line = null;
            shutDown(null);
        }
    } // End class Playback
        
    /** 
     * Reads data from the input channel and writes to the output stream
     */
    class Capture implements Runnable {

        TargetDataLine line;
        Thread thread;

        public void start() {
            printErr = null;
            thread = new Thread(this);
            thread.setName("Capture");
            thread.start();
        }
        public void stop() {
            thread = null;
        }    
        private void shutDown(String message) {
            if ((printErr = message) != null && thread != null) {
                thread = null;
                samplingGraph.stop();            
                playBtn.setEnabled(true);
                pauseBtn.setEnabled(false);             
                saveBtn.setEnabled(true);
                captBtn.setText("Capture");
                System.err.println(printErr);
                samplingGraph.repaint();
            }
        }

        public void run() {
            duration = 0;
            audioInputStream = null;
            
            // define the required attributes for our line, 
            // and make sure a compatible line is supported.

            AudioFormat format = formatControls.getFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
                   
            if (!AudioSystem.isLineSupported(info)) {
                shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the target data line for capture.

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, line.getBufferSize());
            } catch (LineUnavailableException ex) { 
                shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) { 
                shutDown(ex.toString());
                return;
            } catch (Exception ex) { 
                shutDown(ex.toString());
                return;
            }

            // play back the captured audio data
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;
            
            line.start();

            while (thread != null) {
                if((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
            }
            // we reached the end of the stream.  stop and close the line.
            line.stop();
            line.close();
            line = null;

            // stop and close the output stream
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // load bytes into the audio input stream for playback
            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
            long milliseconds = (long)((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
            duration = milliseconds / 1000.0;
            try {
                audioInputStream.reset();
            } catch (Exception ex) { 
                ex.printStackTrace(); 
                return;
            }
            samplingGraph.createWaveForm(audioBytes);
        }
        }
    /**
     * Controls for the AudioFormat.
     */
    class FormatControls extends JPanel {   
        Vector groups = new Vector();

        public FormatControls() { }

        public AudioFormat getFormat() {
            Vector v = new Vector(groups.size());    
            float rate = 8000;
            int sampleSize = 16;
            boolean signedString = true;
            boolean bigEndian = true;
            int channels = 1;
            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            return new AudioFormat(encoding,rate, sampleSize, 
                    channels, (sampleSize/8)*channels, rate, bigEndian);    
        }

    } // End class FormatControls


    /**
     * Render a WaveForm.
     */
    class SamplingGraph extends JPanel implements Runnable {

        private Thread thread;
        private Font font10 = new Font("serif", Font.PLAIN, 10);
        private Font font12 = new Font("serif", Font.PLAIN, 12);
        Color jfcBlue = new Color(204, 204, 255);
        Color pink = new Color(255, 175, 175);
        public SamplingGraph() {
            setBackground(new Color(20, 20, 20));
        }

        public void createWaveForm(byte[] audioBytes) {
            vectorLine.removeAllElements();  // clear the old vector
            AudioFormat format = audioInputStream.getFormat();
            if (audioBytes == null) {
                try {
                audioBytes = new byte[(int) (audioInputStream.getFrameLength()* format.getFrameSize())];                                          
                    audioInputStream.read(audioBytes);
                } catch (Exception ex) { 
                    reportStatus(ex.toString());
                    return; 
                }
            }
            Dimension d = getSize();
            int w = d.width;
            int h = d.height-15;
            int[] audioData = null;
            if (format.getSampleSizeInBits() == 16) {
                 int nlengthInSamples = audioBytes.length / 2;
                 audioData = new int[nlengthInSamples];
                 if (format.isBigEndian()) {
                    for (int i = 0; i < nlengthInSamples; i++) {
                         /* First byte is MSB (high order) */
                         int MSB = (int) audioBytes[2*i];
                         /* Second byte is LSB (low order) */
                         int LSB = (int) audioBytes[2*i+1];
                         audioData[i] = MSB << 8 | (255 & LSB);
                     }
                 } 
             }      
            int frames_per_pixel = audioBytes.length / format.getFrameSize()/w;
            byte my_byte = 0;
            double y_last = 0;
            int numChannels = format.getChannels();
            for (double x = 0; x < w && audioData != null; x++) {
                int idx = (int) (frames_per_pixel * numChannels * x);
                if (format.getSampleSizeInBits() == 8) {
                     my_byte = (byte) audioData[idx];
                } else {
                     my_byte = (byte) (128 * audioData[idx] / 32768 );
                }
                double y_new = (double) (h * (128 - my_byte) / 256);
                vectorLine.add(new Line2D.Double(x, y_last, x, y_new));
                y_last = y_new;
            }
            repaint();
        }
        public void paint(Graphics g) {
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            int INFOPAD = 15;

            Graphics2D g2 = (Graphics2D) g;
            g2.setBackground(getBackground());
            g2.clearRect(0, 0, w, h);
            g2.setColor(Color.white);
            g2.fillRect(0, h-INFOPAD, w, INFOPAD);

            if (printErr != null) {
                g2.setColor(jfcBlue);
                g2.setFont(new Font("serif", Font.BOLD, 18));
                g2.drawString("ERROR", 5, 20);
                AttributedString as = new AttributedString(printErr);
                as.addAttribute(TextAttribute.FONT, font12, 0, printErr.length());
                AttributedCharacterIterator aci = as.getIterator();
                FontRenderContext frc = g2.getFontRenderContext();
                LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
                float x = 5, y = 25;
                lbm.setPosition(0);
                while (lbm.getPosition() < printErr.length()) {
                    TextLayout tl = lbm.nextLayout(w-x-5);
                    if (!tl.isLeftToRight()) {
                        x = w - tl.getAdvance();
                    }
                    tl.draw(g2, x, y += tl.getAscent());
                    y += tl.getDescent() + tl.getLeading();
                }
            } else if (capture.thread != null) {
                g2.setColor(Color.black);
                g2.setFont(font12);
                g2.drawString("Length: " + String.valueOf(seconds), 3, h-4);
            } else {
                g2.setColor(Color.black);
                g2.setFont(font12);
                g2.drawString("File: " + fileName + "  Length: " + String.valueOf(duration) + "  Position: " + String.valueOf(seconds), 3, h-4);
                if (audioInputStream != null) {
                    // .. render sampling graph ..
                    g2.setColor(jfcBlue);
                    for (int i = 1; i < vectorLine.size(); i++) {
                        g2.draw((Line2D) vectorLine.get(i));
                    }
                    // .. draw current position ..
                    if (seconds != 0) {
                        double loc = seconds/duration*w;
                        g2.setColor(pink);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(new Line2D.Double(loc, 0, loc, h-INFOPAD-2));
                    }
                }
            }
        }
    
        public void start() {
            thread = new Thread(this);
            thread.setName("SamplingGraph");
            thread.start();
            seconds = 0;
        }
        public void stop() {
            if (thread != null) {
                thread.interrupt();
            }
            thread = null;
        }
        public void run() {
            seconds = 0;
            while (thread != null) {
                if ((playback.line != null) && (playback.line.isOpen()) ) {
                   long milliseconds = (long)(playback.line.getMicrosecondPosition() / 1000);
                    seconds =  milliseconds / 1000.0;
                } else if ( (capture.line != null) && (capture.line.isActive()) ) {
                    long milliseconds = (long)(playback.line.getMicrosecondPosition() / 1000);
                    seconds =  milliseconds / 1000.0;
                }
                try { thread.sleep(100); } catch (Exception e) { break; }

                repaint();                               
                while ((capture.line != null && !capture.line.isActive()) ||
                       (playback.line != null && !playback.line.isOpen())) 
                {
                    try { thread.sleep(10); } catch (Exception e) { break; }
                }
            }
            seconds = 0;
            repaint();
        }
    } // End class SamplingGraph
    
    public static void main(String s[]) {
        AudioCapture01 audioCapture = new AudioCapture01();
        audioCapture.open();
        JFrame f = new JFrame("Advanced Visual Instrument Recorder ");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
        f.getContentPane().add("Center", audioCapture);
        f.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 720;
        int h = 340;
        f.setLocation(screenSize.width/2 - w/2, screenSize.height/2 - h/2);
        f.setSize(w, h);
        f.show();
    }
    }