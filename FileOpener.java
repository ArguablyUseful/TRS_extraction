

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;


public class FileOpener {

	RandomAccessFile file;
	FileChannel channel;
	int readResult;
	long size;
	public FileOpener(String pathname) throws IOException
	{
		this.file		= new RandomAccessFile(pathname, "r");
		this.channel	= file.getChannel();
		this.size = channel.size();
	}
	public long GetSize()
	{
		return size;
	}
	public int GetLastBytesCount()
	{
		return this.readResult;
	}
	/*
	 * readnext create a bytebuffer of size "bytecount"
	 * it read that many bytes using the file channel
	 * it then flips the buffer and returns it
	 * this.readResult contains the quantity of bytes read or -1 if EOF was reached before that quantity of bytes could been read.
	 * after using readNext, the channel is "bytecount" further in the file.
	 * I.E. ReadNext(45), check with GetLastBytesCount is != from -1, use the ByteBuffer.
	 * call Close() when finished with that file.
	 */
	public ByteBuffer ReadNext(int byteCount) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(byteCount);
		this.readResult = this.channel.read(buffer);
		buffer.flip();
		return buffer;
	}
	/*
	 * this methods allow to read bytecount bytes from a specified position
	 * note that it doesn't change the channel current position.
	 */
	public ByteBuffer Read(int byteCount, long position) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.allocate(byteCount);
		buffer.order(ByteOrder.BIG_ENDIAN);
		this.readResult = this.channel.read(buffer, position);
		buffer.flip();
		return buffer;
	}
	
	public void OffsetPosition(long newPosition) throws IOException
	{
		this.channel.position(newPosition);
	}
	public long GetCurrentPosition() throws IOException
	{
		return this.channel.position();
	}
	public void Close() throws IOException
	{
		this.channel.close();
	}


}
