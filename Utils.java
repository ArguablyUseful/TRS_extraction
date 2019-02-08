

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.xml.bind.DatatypeConverter;

public class Utils {
	public static byte[] intToBytes( final int i ) {
	    ByteBuffer bb = ByteBuffer.allocate(4); 
	    bb.order(ByteOrder.BIG_ENDIAN);
	    bb.putInt(i); 
	    return bb.array();
	}
	public static String toHex_2bytes(int sign)
	{
		return "0x" + Integer.toHexString(sign & 0xffff);
	}
	public static String toHex_4bytes(int sign)
	{
		return "0x" + Integer.toHexString(sign);
	}
	public static String toHex_8bytes(long sign)
	{
		return "0x" + Long.toHexString(sign);
	}
	public static String ByteBufferToHex(ByteBuffer buffer)
	{
		int size = buffer.limit() - buffer.position();
		if ( size <= 0)
			return null;
		byte []array = new byte[size];
		buffer.get(array);
		String result = DatatypeConverter.printHexBinary(array);
		return result;
	}
	/*byte group is the quantity of byte per "group" 
	//I.E. byte group of 4 will produce 00F1F2E0 A1234543 ... 
	 * byte group of 2 will be 00F1 F2E0 A123 4543 ...
	 * group per line is the quantity of these group before a "\n" occurs.
	 * 
	 */
	public static String FormatedByteBufferToHex(int byteGroup, int groupPerLine, ByteBuffer buffer)
	{
		int size = buffer.limit() - buffer.position();
		if ( size <= 0)
			return null;
		byte []array = new byte[size];
		buffer.get(array);
		String result = "";
		
		for( int index = 0; index < size; index++ )
		{
			if ( index % (byteGroup*groupPerLine) == 0)
				result += "\n";
			else if ( index % byteGroup == 0)
				result += " ";
			result += String.format("%02x",array[index]);
		}
		return result;
	}
}
