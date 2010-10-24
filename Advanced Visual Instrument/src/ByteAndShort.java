

public class ByteAndShort {
	
	
	byte[] byteArray;
	short[] shortArray;
	
	boolean isBigEndian;
	
	public ByteAndShort(byte[] array, boolean bigEndian) {
		short[] unsignedByteArray;
		int[] unsignedShortArray;
		int N = array.length;
		isBigEndian = bigEndian;
		

		if (N%2 != 0) {
			shortArray = new short[(N+1)/2];
			byteArray = new byte[N+1];
			unsignedByteArray = new short[N+1];
			unsignedShortArray = new int[(N+1)/2];
			
			for (int i = 0; i < N; i++) {
				unsignedByteArray[i] = (short) (array[i] + 128);
				byteArray[i] = array[i];
			}
			unsignedByteArray[N] = 1;
			byteArray[N] = 0;
		}
		else {
			shortArray = new short[N/2];
			byteArray = new byte[N];
			unsignedByteArray = new short[N];
			unsignedShortArray = new int[(N)/2];
			
			for (int i = 0; i < N; i++) {
				unsignedByteArray[i] = (short) (array[i] + 128);
				byteArray[i] = array[i];
			}			
		}
		
		if (isBigEndian) {
			for (int i = 0; i < unsignedShortArray.length; i++) {
				unsignedShortArray[i] = (int) (256 * (unsignedByteArray[i*2]) + (unsignedByteArray[i*2+1]));
			}			
		}
		else {
			for (int i = 0; i < shortArray.length; i++) {
				unsignedShortArray[i] = (int) (256 * (unsignedByteArray[i*2+1]) + (unsignedByteArray[i*2]));
			}
		}
		
		for (int i = 0; i < shortArray.length; i++) {
			shortArray[i] = (short) (unsignedShortArray[i] - 128 * 256);
		}
	}
	
	public ByteAndShort(short[] array, boolean bigEndian) {
		short[] unsignedByteArray;
		int[] unsignedShortArray;
		shortArray = array;
		isBigEndian = bigEndian;
		int N = shortArray.length;
		
		unsignedShortArray = new int[N];
		for (int i = 0; i < N; i++) {
			unsignedShortArray[i] = shortArray[i] + 128 * 256;
		}
		byteArray = new byte[N*2];
		unsignedByteArray = new short[N*2];
		if (isBigEndian) {
			for (int i = 0; i < N; i++) {
				unsignedByteArray[i*2] = (short) (unsignedShortArray[i]/256);
				unsignedByteArray[i*2+1] = (short) (unsignedShortArray[i]%256);
			}
		}
		else {
			for (int i = 0; i < N; i++) {				
				unsignedByteArray[i*2+1] = (short) (unsignedShortArray[i]/256);
				unsignedByteArray[i*2] = (short) (unsignedShortArray[i]%256);
			}
		}
		for (int i = 0; i < N*2; i++) {
			byteArray[i] = (byte) (unsignedByteArray[i] - 128);
		}
	}
}
