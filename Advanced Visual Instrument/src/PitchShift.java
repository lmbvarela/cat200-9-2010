

/****************************************************************************
*
* NAME: PitchShift
* VERSION: 1.2
* HOME URL: http://www.dspdimension.com
* KNOWN BUGS: none
*
* SYNOPSIS: Routine for doing pitch shifting while maintaining
* duration using the Short Time Fourier Transform.
*
* DESCRIPTION: The routine takes a pitchShift factor value which is between 0.5
* (one octave down) and 2. (one octave up). A value of exactly 1 does not change
* the pitch. numSampsToProcess tells the routine how many samples in indata[0...
* numSampsToProcess-1] should be pitch shifted and moved to outdata[0 ...
* numSampsToProcess-1]. The two buffers can be identical (ie. it can process the
* data in-place). fftFrameSize defines the FFT frame size used for the
* processing. Typical values are 1024, 2048 and 4096. It may be any value <=
* MAX_FRAME_LENGTH but it MUST be a power of 2. osamp is the STFT
* oversampling factor which also determines the overlap between adjacent STFT
* frames. It should at least be 4 for moderate scaling ratios. A value of 32 is
* recommended for best quality. sampleRate takes the sample rate for the signal 
* in unit Hz, ie. 44100 for 44.1 kHz audio. The data passed to the routine in 
* indata[] should be in the range [-1.0, 1.0), which is also the output range 
* for the data, make sure you scale the data accordingly (for 16bit signed integers
* you would have to divide (and multiply) by 32768). 
*
* COPYRIGHT 1999-2006 Stephan M. Bernsee <smb [AT] dspdimension [DOT] com>
*
* 						The Wide Open License (WOL)
*
* Permission to use, copy, modify, distribute and sell this software and its
* documentation for any purpose is hereby granted without fee, provided that
* the above copyright notice and this license appear in all source copies. 
* THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT EXPRESS OR IMPLIED WARRANTY OF
* ANY KIND. See http://www.dspguru.com/wol.htm for more information.
*
*****************************************************************************/
 
public class PitchShift
    {
 
        private static int MAX_FRAME_LENGTH = 16000;
        private static double[] gInFIFO = new double[MAX_FRAME_LENGTH];
        private static double[] gOutFIFO = new double[MAX_FRAME_LENGTH];
        private static double[] gFFTworksp = new double[2 * MAX_FRAME_LENGTH];
        private static double[] gLastPhase = new double[MAX_FRAME_LENGTH / 2 + 1];
        private static double[] gSumPhase = new double[MAX_FRAME_LENGTH / 2 + 1];
        private static double[] gOutputAccum = new double[2 * MAX_FRAME_LENGTH];
        private static double[] gAnaFreq = new double[MAX_FRAME_LENGTH];
        private static double[] gAnaMagn = new double[MAX_FRAME_LENGTH];
        private static double[] gSynFreq = new double[MAX_FRAME_LENGTH];
        private static double[] gSynMagn = new double[MAX_FRAME_LENGTH];
        private static int gRover, gInit;
        
//        public static void PitchShift(double pitchShift, int numSampsToProcess,
//           double sampleRate, double[] indata)
//        {
//            PitchShift(pitchShift, numSampsToProcess, (int)2048, (int)10, sampleRate, indata);
//        }
        public static void PitchShift(double pitchShift, int numSampsToProcess, int fftFrameSize,
            int osamp, double sampleRate, double[] indata, double[] outdata)
        {
            double magn, phase, tmp, window, real, imag;
            double freqPerBin, expct;
            int i, k, qpd, index, inFifoLatency, stepSize, fftFrameSize2;
 
 
            //outdata = indata;
            /* set up some handy variables */
            fftFrameSize2 = fftFrameSize / 2;
            stepSize = fftFrameSize / osamp;
            freqPerBin = sampleRate / (double)fftFrameSize;
            expct = 2.0 * Math.PI * (double)stepSize / (double)fftFrameSize;
            inFifoLatency = fftFrameSize - stepSize;
            if (gRover == 0) gRover = inFifoLatency;
 
 
            /* main processing loop */
            for (i = 0; i < numSampsToProcess; i++)
            {
 
                /* As int as we have not yet collected enough data just read in */
                gInFIFO[gRover] = indata[i];
                outdata[i] = gOutFIFO[gRover - inFifoLatency];
                gRover++;
 
                /* now we have enough data for processing */
                if (gRover >= fftFrameSize)
                {
                    gRover = inFifoLatency;
 
                    /* do windowing and re,im interleave */
                    for (k = 0; k < fftFrameSize; k++)
                    {
                        window = -.5 * Math.cos(2.0 * Math.PI * (double)k / (double)fftFrameSize) + .5;
                        gFFTworksp[2 * k] = (double)(gInFIFO[k] * window);
                        gFFTworksp[2 * k + 1] = 0.0F;
                    }
 
 
                    /* ***************** ANALYSIS ******************* */
                    /* do transform */
                    ShortTimeFourierTransform(gFFTworksp, fftFrameSize, -1);
 
                    /* this is the analysis step */
                    for (k = 0; k <= fftFrameSize2; k++)
                    {
 
                        /* de-interlace FFT buffer */
                        real = gFFTworksp[2 * k];
                        imag = gFFTworksp[2 * k + 1];
 
                        /* compute magnitude and phase */
                        magn = 2.0 * Math.sqrt(real * real + imag * imag);
                        phase = Math.atan2(imag, real);
 
                        /* compute phase difference */
                        tmp = phase - gLastPhase[k];
                        gLastPhase[k] = (double)phase;
 
                        /* subtract expected phase difference */
                        tmp -= (double)k * expct;
 
                        /* map delta phase into +/- Pi interval */
                        qpd = (int)(tmp / Math.PI);
                        if (qpd >= 0) qpd += qpd & 1;
                        else qpd -= qpd & 1;
                        tmp -= Math.PI * (double)qpd;
 
                        /* get deviation from bin frequency from the +/- Pi interval */
                        tmp = osamp * tmp / (2.0 * Math.PI);
 
                        /* compute the k-th partials' true frequency */
                        tmp = (double)k * freqPerBin + tmp * freqPerBin;
 
                        /* store magnitude and true frequency in analysis arrays */
                        gAnaMagn[k] = (double)magn;
                        gAnaFreq[k] = (double)tmp;
 
                    }
 
                    /* ***************** PROCESSING ******************* */
                    /* this does the actual pitch shifting */
                    for (int zero = 0; zero < fftFrameSize; zero++)
                    {
                        gSynMagn[zero] = 0;
                        gSynFreq[zero] = 0;
                    }
 
                    for (k = 0; k <= fftFrameSize2; k++)
                    {
                        index = (int)(k * pitchShift);
                        if (index <= fftFrameSize2)
                        {
                            gSynMagn[index] += gAnaMagn[k];
                            gSynFreq[index] = gAnaFreq[k] * pitchShift;
                        }
                    }
 
                    /* ***************** SYNTHESIS ******************* */
                    /* this is the synthesis step */
                    for (k = 0; k <= fftFrameSize2; k++)
                    {
 
                        /* get magnitude and true frequency from synthesis arrays */
                        magn = gSynMagn[k];
                        tmp = gSynFreq[k];
 
                        /* subtract bin mid frequency */
                        tmp -= (double)k * freqPerBin;
 
                        /* get bin deviation from freq deviation */
                        tmp /= freqPerBin;
 
                        /* take osamp into account */
                        tmp = 2.0 * Math.PI * tmp / osamp;
 
                        /* add the overlap phase advance back in */
                        tmp += (double)k * expct;
 
                        /* accumulate delta phase to get bin phase */
                        gSumPhase[k] += (double)tmp;
                        phase = gSumPhase[k];
 
                        /* get real and imag part and re-interleave */
                        gFFTworksp[2 * k] = (double)(magn * Math.cos(phase));
                        gFFTworksp[2 * k + 1] = (double)(magn * Math.sin(phase));
                    }
 
                    /* zero negative frequencies */
                    for (k = fftFrameSize + 2; k < 2 * fftFrameSize; k++) gFFTworksp[k] = 0.0F;
 
                    /* do inverse transform */
                    ShortTimeFourierTransform(gFFTworksp, fftFrameSize, 1);
 
                    /* do windowing and add to output accumulator */
                    for (k = 0; k < fftFrameSize; k++)
                    {
                        window = -.5 * Math.cos(2.0 * Math.PI * (double)k / (double)fftFrameSize) + .5;
                        gOutputAccum[k] += (double)(2.0 * window * gFFTworksp[2 * k] / (fftFrameSize2 * osamp));
                    }
                    for (k = 0; k < stepSize; k++) gOutFIFO[k] = gOutputAccum[k];
 
                    /* shift accumulator */
                    //memmove(gOutputAccum, gOutputAccum + stepSize, fftFrameSize * sizeof(double));
                    for (k = 0; k < fftFrameSize; k++)
                    {
                        gOutputAccum[k] = gOutputAccum[k + stepSize];
                    }
 
                    /* move input FIFO */
                    for (k = 0; k < inFifoLatency; k++) gInFIFO[k] = gInFIFO[k + stepSize];
                }
            }
        }
        
/*        public static void ShortTimeFourierTransform(double[] fftBuffer, int fftFrameSize, int sign)
        {
            double wr, wi, arg, temp;
            double tr, ti, ur, ui;
            int i, bitm, j, le, le2, k;
 
            for (i = 2; i < 2 * fftFrameSize - 2; i += 2)
            {
                for (bitm = 2, j = 0; bitm < 2 * fftFrameSize; bitm <<= 1)
                {
                    if ((i & bitm) != 0) j++;
                    j <<= 1;
                }
                if (i < j)
                {
                    temp = fftBuffer[i];
                    fftBuffer[i] = fftBuffer[j];
                    fftBuffer[j] = temp;
                    temp = fftBuffer[i + 1];
                    fftBuffer[i + 1] = fftBuffer[j + 1];
                    fftBuffer[j + 1] = temp;
                }
            }
            int max = (int)(Math.log(fftFrameSize) / Math.log(2.0) + .5);
            for (k = 0, le = 2; k < max; k++)
            {
                le <<= 1;
                le2 = le >> 1;
                ur = 1.0F;
                ui = 0.0F;
                arg = (double)Math.PI / (le2 >> 1);
                wr = (double)Math.cos(arg);
                wi = (double)(sign * Math.sin(arg));
                for (j = 0; j < le2; j += 2)
                {
 
                    for (i = j; i < 2 * fftFrameSize; i += le)
                    {
                        tr = fftBuffer[i + le2] * ur - fftBuffer[i + le2 + 1] * ui;
                        ti = fftBuffer[i + le2] * ui + fftBuffer[i + le2 + 1] * ur;
                        fftBuffer[i + le2] = fftBuffer[i] - tr;
                        fftBuffer[i + le2 + 1] = fftBuffer[i + 1] - ti;
                        fftBuffer[i] += tr;
                        fftBuffer[i + 1] += ti;
 
                    }
                    tr = ur * wr - ui * wi;
                    ui = ur * wi + ui * wr;
                    ur = tr;
                }
            }
        }*/
        
        public static void ShortTimeFourierTransform(double fftBuffer[], int fftFrameSize, int sign){
    		int N = fftFrameSize / 2;
    		Complex[] x = new Complex[N];
    		for (int i = 0; i < N; i++ ) {
    			x[i] = new Complex(fftBuffer[i*2], fftBuffer[i*2+1]);
    		}
    		Complex[] result;
    		if (sign < 0) {
    			result = FFT.fft(x);
    			for (int i = 0; i < N; i++ ) {
    				fftBuffer[i*2] = result[i].re();
    				fftBuffer[i*2+1] = result[i].im();
    			}
    		}
    		else if (sign > 0) {
    			result = FFT.ifft(x);
    			for (int i = 0; i < N; i++ ) {
    				fftBuffer[i*2] = result[i].re();
    				fftBuffer[i*2+1] = result[i].im();
    			}
    		}		
    	}
        
    
}

