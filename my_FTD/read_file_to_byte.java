package my_FTD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;



public class read_file_to_byte {
	
	public static byte[] read_file(String file_absolute_address)
	{
		byte[] file_byte_vector=null;
		File file=new File(file_absolute_address);
		if (file.exists() && file.isFile())
		{
			file_byte_vector=new byte[(int)file.length()];
//			System.out.println("file length is:"+file.length());
//			System.out.println("file_byte_vector's length is:"+file_byte_vector.length);
			try
			{
				InputStream file_outputstream=new FileInputStream(file);
				file_outputstream.read(file_byte_vector);
				file_outputstream.close();
				file_outputstream=null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("File cannot be open!    File'address is:"+file_absolute_address);
			System.exit(1);
		}
		file=null;
		return file_byte_vector;
	}
	public static void saveFile(String filename,byte [] data)throws Exception{ 
		String byte_filepath ="D:\\" + filename;
		File file  = new File(byte_filepath);
		FileOutputStream fos = new FileOutputStream(file);   
	      fos.write(data,0,data.length);   
	      fos.flush();   
	      fos.close();
	}

	public static void main(String[] args) 
	{
		String filename="C3-Art0002.txt";
		String file_absolute_address = System.getProperty("user.dir")+"\\"+filename;
		byte[] text_byte_vector=null;
		text_byte_vector=read_file(file_absolute_address);
		int[]a=new int[text_byte_vector.length];
		for(int i=0;i<text_byte_vector.length;i++)
		{
			a[i]=Integer.valueOf(text_byte_vector[i]);
			System.out.printf("%x",a[i]);System.out.println();
		}
		
		//String[] a = Arrays.toString(text_byte_vector);
		
		//System.out.println(Integer.valueOf());
		
		try {
			saveFile("C3-Art0002", text_byte_vector);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
