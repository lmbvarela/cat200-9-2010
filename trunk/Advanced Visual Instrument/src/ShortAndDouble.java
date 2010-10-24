

public class ShortAndDouble {
	double[] doubleArray;
	short[] shortArray;
	
	public ShortAndDouble(short[] array) {
		shortArray = array;
		doubleArray = new double[shortArray.length];
		for (int i = 0; i < shortArray.length; i++) {
			doubleArray[i] = (double)(shortArray[i]) / 32768;
		}
	}
	
	public ShortAndDouble(double[] array) {
		doubleArray = array;
		shortArray = new short[doubleArray.length];
		for (int i = 0; i < shortArray.length; i++) {
			shortArray[i] = (short)(doubleArray[i] * 32768);
		}
	}
}