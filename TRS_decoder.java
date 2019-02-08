import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
/**
 * 
 * @author Corentin
 * TRS files from Conquest of Elysium 4 (CoE4) contains sprites images.
 * The files are encoded in big endian notations.
 * The format is as follow, in sequence :
 * The header of the file : 
 * 4 bytes for the file signature : "TCSF" in UTF-8.
 * 16 bits word (2 bytes) gives the quantity of sprites in the TRS file.
 * 16 bits word (2 bytes) gives the version of the TRS encoding (3)
 * 16 bits word (2 bytes) gives the Xres value (X resolution)
 * 16 bits word (2 bytes) empty (0)
 * 
 * (1) From there, the file contains a sequence of Sprite locations, one for each of the sprites contained. The quantity is the one obtained above.
 * 1 byte for the Width of the sprite. It must be read as an unsigned byte.
 * 1 byte for the Height of the sprite.
 * 4 bytes for the unpacked offset in the file, starting at position 0 (beginning of the file).
 * this offset points to the starts of unpacked data for that sprite
 * 4 bytes for the packed offset in the file, starting at position 0 (beginnign of the file).
 * this offset points to the starts of packed data for that sprite.
 * 16 bits word (2 bytes) empty (0)
 * 
 * --the offsets value are either packed or unpacked : if there's an offset that isn't 0 (I.E. the packed offset is 456654) the other offset should be 0
 *	The next sprites location starts here (goto (1) until all sprites locations are decoded)
 *
 *if the offset points toward unpacked data, then the quantity of pixels is simply equals to width * height for that sprite.
 *note that each pixel is coded on 2 bytes.
 *
 *if the offset points toward packed data, then the unpacking is relatively straigthforward.
 *the first data at the pointed adress is
 *16 bits word (2bytes) represneting the quantity of "chunks" for that sprites.
 *then the chunks follows directly.
 *the packed sprites represent the pixels in sequences of "pointer jumps" and "meaningful pixels" starting from top left ending in bottom right. 
 *The idea is that there is a "screen" of width Xres (see X resolution above) that allow a 1D array of pixels to be represented in 2D
 *This "screen" has a pointer determining the current byte in that 1D array.
 *each pixels is 16 bits (=2 bytes).
 *so we write pixels in sequence : X=0,Y=0 is the first pixel of the first row represented by the two firsts byte (the first word).
 *X5, Y=0 is the 5th pixel of the first row, represented by bytes 10 and 11.
 *X=10, Y=50 is the 10th pixel of the 50th row, represented by bytes (10*Xres + X*2) and (10*Xres + X*2)+1
 *
 *since many pixels are "empty" pixels (that is, completely transparent pixels that allows  the sprites to be drawed on top of other images)
 *we can avoid storing those empty pixels.
 *
 *This is why the "packed offset" points to a sequence of "chunks" of data.
 *each "chunk" is 
 * 16bits word (2bytes) for the Screen offset. (this is the quantity of BYTES you from the current screen pointer that only contains "empty" pixels" 
 * 16bits word (2bytes) Nbr of data words -1. This is the quantity of PIXELS that are meaningful (NOT empty) minus 1.
 * N * 16 bits word (2bytes) of pixel values. Where N is equal to the "Nbr of data words" value that we just extracted (we extract the value -1, so we must add 1.)
 * followed by the next chunk if any.
 * 
 * If the quantity of pixels recorded through those "jumps" and "meaningful" data do not adds up to sprite_height * Xres, then we padd with more empty pixels until we have the right amount. this saves up a bit more space.
 *   
 * With the Xres of 800, For a picture that has height 16 and width 32, this will draw a sprite that is 800x16 in pixels
 * You can then convert those pixels to a 16x32 
 * I.E. the pixel X=5,Y=6 is located at Y*Xres+5, or 6*800+5.
 * You can also find the X and Y of each pixels by using integer division and remainder (/ and %)
 * IE. pixel at index 1954 has a X coordinate of
 * 1954 % Xres (800 in our case)=> this gives us 354
 * and Y is equal to 1954 / Xres (800) => this gives us 2
 * so the pixel 1954 is at X=354, Y=2  
 */
public class TRS_decoder {
	final int byteSize = 1;
	final int wordSize = 2;
	final int longSize = 4;
	FileOpener fo;
	String filename;
	String directory_name;
	String tcsf_signature = "TCSF";
	int quantity_sprites;
	int Xres;
	int file_version;
	boolean flag_monster_filename;
	boolean flag_packed;
	Color emptyPixel = new Color(0,0,0,0); //beware that sprites that are "unpacked" use "black" as a transparent.
	public TRS_decoder(String filename, boolean monster_filename, boolean flag_packed) throws IOException
	{
		this.filename = filename;
		this.directory_name = "dir_" + this.filename + "/";
		this.flag_monster_filename = monster_filename;
		this.flag_packed = flag_packed;
		this.fo = new FileOpener(filename);
		if ( CheckSignature() )
		{
			System.out.println("File has the correct signature.");
			System.out.println("Extracted files will be in directory named \"" + this.directory_name + "\"");
			new File(this.directory_name).mkdir();
			ExtractHeader();
			ExtractSprites();		
			
		}
		this.fo.Close();
	}
	
	boolean CheckSignature() throws IOException
	{
		ByteBuffer bb = fo.ReadNext(4);
			
		int signature_in_file = bb.getInt();
		String chars = new String(Utils.intToBytes(signature_in_file), "UTF-8"); //should be "TCSF"
		
		if ( chars.compareTo(tcsf_signature) == 0)
			return true;
		else
			return false;
	}
	void ExtractSprites() throws IOException
	{
		ByteBuffer bb;
		for(int i = 0; i < this.quantity_sprites; i++)
		{
			bb = fo.ReadNext(byteSize);
			int width = Byte.toUnsignedInt(bb.get()); 
			if ( width == 0 )
				width=256;

			bb = fo.ReadNext(byteSize);
			int height = Byte.toUnsignedInt(bb.get());
			if ( height == 0)
				height = 256;

			fo.ReadNext(wordSize); //empty word
			
			bb = fo.ReadNext(longSize);
			long offset_unpacked = Integer.toUnsignedLong(bb.getInt());
			bb = fo.ReadNext(longSize);
			long offset_packed = Integer.toUnsignedLong(bb.getInt());

			ArrayList<Color> pixels = null;
			if ( offset_unpacked != 0)
			{
				pixels = ReadSpriteUnpacked(width, height, offset_unpacked);
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				int index = 0;
				for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						img.setRGB(x, y, pixels.get(index).getRGB());
						index++;
					}
				}
				
				
			}
			else
			{
				ArrayList<Color> temporary_pixels = ReadSpritePacked(width, height, offset_packed);
				pixels = new ArrayList<Color>();
				for( int row = 0; row < height; row++)
				{
	                int row_offset = row * this.Xres;
	                for(int w = row_offset; w < row_offset + width; w++)
	                {
	                	pixels.add(temporary_pixels.get(w));
	                }
				}
			}
			
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int index = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					img.setRGB(x, y, pixels.get(index).getRGB());
					index++;
				}
			}
			String filename = this.directory_name;
			if ( this.flag_monster_filename)
				filename += coe4_utils.coe4_monster_nameFile(i);
			else
				filename += "" + i;
			if ( this.flag_packed)
			{
				if ( offset_unpacked != 0)
					filename += "_u";
				else
					filename += "_p";
			}
			filename += ".png";
			File outputfile = new File(filename);
			ImageIO.write(img, "png", outputfile);
		}	
	}
	void ExtractHeader() throws IOException
	{
		ByteBuffer bb = fo.ReadNext(wordSize);
		this.quantity_sprites = bb.getShort();
		bb = fo.ReadNext(wordSize);
		this.file_version = bb.getShort();
		bb = fo.ReadNext(wordSize);
		this.Xres = bb.getShort();		
		fo.ReadNext(wordSize); //skip empty word.
		System.out.println("File contains " + this.quantity_sprites + " Sprites.");
		System.out.println("Working...");
	}
	
	
	Color DecodeRGB(int pixel)
	{
	    int r = (pixel & 0xf800) >> 8;
		int g = (pixel & 0x07e0) >> 3;
		int b = (pixel & 0x001f) << 3;
	    return new Color(r,g,b);
	}
	/* We use "Read" method and not "ReadNext" to avoid disturbing the file pointer of the underlying channel.
	 */
	ArrayList<Color> ReadSpriteUnpacked(int width, int height, long offset_unpacked) throws IOException
	{
		ArrayList<Color> c = new ArrayList<Color>();
		ByteBuffer bb = fo.Read(2*width*height, offset_unpacked);
		for(int i = 0; i < width*height; i++)
		{
			Color col = this.DecodeRGB(Short.toUnsignedInt(bb.getShort()));
			c.add(col);
		}
		return c;
	}
	/* We use "Read" method and not "ReadNext" to avoid disturbing the file pointer of the underlying channel.
	 */
	ArrayList<Color> ReadSpritePacked(int width, int height, long offset_packed) throws IOException
	{
		long offset = offset_packed;
		ByteBuffer bb = fo.Read(wordSize, offset);
		offset += wordSize;
		int qtyChunks = Short.toUnsignedInt(bb.getShort());
		ArrayList<Color> pixels = new ArrayList<Color>();

		for(int chunk_num = 0; chunk_num < qtyChunks ; chunk_num++)
		{
			bb = fo.Read(wordSize, offset);
			offset += wordSize;
			int empty_pixels_in_bytes = Short.toUnsignedInt(bb.getShort());  
			int empty_pixels_count = empty_pixels_in_bytes / wordSize; //each pixel is 2 bytes

			for(int i = 0; i < empty_pixels_count; i++)
			{
				pixels.add(this.emptyPixel);
			}
			
			bb = fo.Read(wordSize,  offset);
			offset += wordSize;
			short pixel_count = (bb.getShort());
			while(pixel_count-- >= 0)
			{
				bb = fo.Read(wordSize, offset);
				offset += wordSize;
				Color c = this.DecodeRGB(Short.toUnsignedInt(bb.getShort()));
				pixels.add(c);
			}
		}
		
		int entire_size = this.Xres * height; //total pixels the image should have
		int current_size = pixels.size(); //current pixels we have
		int difference = entire_size - current_size;
		while(difference-- > 0)//we must pad if difference is > 0
			pixels.add(this.emptyPixel);		
		return pixels;
	}
}
