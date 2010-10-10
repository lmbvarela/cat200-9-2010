
import java.io.*;
import javax.sound.sampled.*;

/**
 * Based on SoundEngine.java
 * @author Michael Kolling and David J Barnes 
 * @version 1.0
 */
public class Playback
{
    // the following three fields hold information about the sound clip
    // currently loaded in this engine
    private Clip currentSoundClip = null;
    private FloatControl volctrl;
    private float minVol;
    private float maxVol;
    private boolean loaded;
    
    /**
     * Load and play a specified sound file. If the file is not in a
     * recognised file format, false is returned. Otherwise the sound
     * is started and true is returned. The method returns immediately
     * after the sound starts (not after the sound finishes).
     * 
     * @param soundFile  The file to be loaded.
     * @return  True, if the sound file could be loaded, false otherwise.
     */
    public boolean load(File soundFile)
    {
    	loaded = loadSound (soundFile);
    	if (loaded)
    		return true;
    	else
    		return false;
    }
    
    public void play()
    {
        if(loaded)
            currentSoundClip.start();
    }

    /**
     * Stop the currently playing sound (if there is one playing). If no 
     * sound is playing, this method has no effect.
     */
    public void stop()
    {
        if(currentSoundClip != null) {
        	currentSoundClip.setFramePosition(0);
        	currentSoundClip.stop();
        }
    }            

    /**
     * Pause the currently playing sound. If no sound is currently playing,
     * calling this method has no effect.
     */
    public void pause()
    {
        if(currentSoundClip != null) {
            currentSoundClip.stop();
        }
    }


    /**
     * Get the total duration of the sound
     * 
     */
    public int getDuration()
    {
        return (int) (currentSoundClip.getMicrosecondLength() / 1000000);
    }

    
    /**
     * Get the current playing position of the sound
     * 
     */
    public int getCurrentPosition()
    {
    	return (int)(currentSoundClip.getFramePosition() / currentSoundClip.getFormat().getFrameRate());
    }
    
    
    /**
     * Skip to playing position set by user
     * @param currentPosition
     */
    public void setCurrentPostion(int currentPosition)
    {
        currentSoundClip.setMicrosecondPosition(currentPosition * 1000000);
    }
    
    
    /**
     * Set the volume
     * @param newVal
     */
    public void setVolume(float newVal){
    	newVal = (newVal/100.0f) * (maxVol - minVol) + minVol;
        volctrl.setValue(newVal);
    }
    
    /**
     * Load the sound file supplied by the parameter into this sound engine.
     * 
     * @return  True if successful, false if the file could not be decoded.
     */
    private boolean loadSound(File file) 
    {

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();

            // we cannot play ALAW/ULAW, so we convert them to PCM
            //
            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                (format.getEncoding() == AudioFormat.Encoding.ALAW)) 
            {
                AudioFormat tmp = new AudioFormat(
                                          AudioFormat.Encoding.PCM_SIGNED, 
                                          format.getSampleRate(),
                                          format.getSampleSizeInBits() * 2,
                                          format.getChannels(),
                                          format.getFrameSize() * 2,
                                          format.getFrameRate(),
                                          true);
                stream = AudioSystem.getAudioInputStream(tmp, stream);
                format = tmp;
            }
            DataLine.Info info = new DataLine.Info(Clip.class, 
                                           stream.getFormat(),
                                           ((int) stream.getFrameLength() *
                                           format.getFrameSize()));

            currentSoundClip = (Clip) AudioSystem.getLine(info);
            currentSoundClip.open(stream);
            volctrl= (FloatControl)currentSoundClip.getControl(FloatControl.Type.MASTER_GAIN); 
            minVol = volctrl.getMinimum();
            maxVol = volctrl.getMaximum();
            
            return true;
        } catch (Exception ex) {
            currentSoundClip = null;
            return false;
        }
    }
}
