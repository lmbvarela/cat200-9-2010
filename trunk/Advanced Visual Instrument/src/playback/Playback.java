
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
    private int currentSoundDuration = 0;
    private int currentSoundFrameLength = 0;
    
    /**
     * Load and play a specified sound file. If the file is not in a
     * recognised file format, false is returned. Otherwise the sound
     * is started and true is returned. The method returns immediately
     * after the sound starts (not after the sound finishes).
     * 
     * @param soundFile  The file to be loaded.
     * @return  True, if the sound file could be loaded, false otherwise.
     */
    public boolean play(File soundFile)
    {
        if(loadSound(soundFile)) {
            currentSoundClip.start();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Stop the currently playing sound (if there is one playing). If no 
     * sound is playing, this method has no effect.
     */
    public void stop()
    {
        if(currentSoundClip != null) {
            currentSoundClip.stop();
            currentSoundClip = null;
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
     * Resume the currently paused sound. If no sound is currently paused,
     * this method has no effect.
     */
    public void resume()
    {
        if(currentSoundClip != null) {
            currentSoundClip.start();
        }
    }

    /**
     * Set the current playing position in the currently playing sound to 'value'.
     * 'value' is a percentage value (0 to 100). If there is no sound currently
     * playing, this method has no effect.
     * 
     * @param value  The target position in the sound file, as a percentage.
     */
  
    
    
   
   

    
    public int getDuration()
    {
        return currentSoundDuration;
    }

    /**
     * Load the sound file supplied by the parameter into this sound engine.
     * 
     * @return  True if successful, false if the file could not be decoded.
     */
    private boolean loadSound(File file) 
    {
        currentSoundDuration = 0;

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
            currentSoundFrameLength = (int) stream.getFrameLength();
            currentSoundDuration = (int) (currentSoundClip.getBufferSize() / 
                              (currentSoundClip.getFormat().getFrameSize() * 
                              currentSoundClip.getFormat().getFrameRate()));
            return true;
        } catch (Exception ex) {
            currentSoundClip = null;
            return false;
        }
    }
}
