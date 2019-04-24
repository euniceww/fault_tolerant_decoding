package my_FTD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Delete_english_char {
	static ArrayList<Character> text_word_vector=new ArrayList<>();	
	//static ArrayList<String> text_word_vector_string=new ArrayList<>();	
	static byte[] text_word_vector_byte;
	static String text_directory_absolute_address = "E:\\FTD\\my_FTD\\resource\\train_language_model_text\\deleteEG\\dir";
	static String output_absolute_address = "E:\\FTD\\my_FTD\\resource\\train_language_model_text\\deleteEG\\";


	private static void get_file_vector(File file_directory,File[] file_vector,int[] num)//将某文件夹中的文件放入一个file_vector，若文件夹中是文件则放入，不是则接着打开文件夹再放入
	{
		File[] file_list=file_directory.listFiles();
		for (int i=0;i<file_list.length;i++)
		{
			if (file_list[i].isFile())
			{
				file_vector[num[0]]=file_list[i];
				num[0]++;
			}
			else
			{
				get_file_vector(file_list[i],file_vector,num);
			}
		}
	}
	
	private static void get_text_file(File text_file)//get_text_byte_vector
	{
//		String text_absolute_address=System.getProperty("user.dir")+text_relative_address;
//		File text_file=new File(text_absolute_address);
		
		if (text_file.exists() && text_file.isFile())
		{
			try
			{
				InputStream text_inputstream=new FileInputStream(text_file);
				InputStreamReader text_inputstreamreader=new InputStreamReader(text_inputstream,"GBK");
//				int file_char_num=0;
//				int i;     
//	            while((i=text_inputstreamreader.read()) != -1){ 
//	            	 if(i>127)
//	                 file_char_num++; 
//              }
//	            ArrayList<Character> text_word_vector=new ArrayList<>();
				text_word_vector.clear();
	            int thechar;
	            int j=0;
	            String s = "";
	            while((thechar=text_inputstreamreader.read()) != -1){ 
	            	 if(thechar>127)
	            	 {
	            		 text_word_vector.add((char)thechar);
//	            		 System.out.print(text_word_vector.get(j));
	            		 s = s+String.valueOf(text_word_vector.get(j));
	            		 
	            		 j++;
	            	 }
	            }
	            text_word_vector_byte = s.getBytes("GBK");
	            text_inputstreamreader.close();
				text_inputstreamreader=null;
				text_inputstream.close();
				text_inputstream=null;
//				System.out.println(String.valueOf(text_word_vector.size()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("text_file cannot be opened!");
			System.err.println("Text file's address is: "+text_file.getAbsolutePath());
			System.exit(1);
		}
	}
	
	public static void saveFile(String filename,byte [] data)throws Exception{ 
		String chinese_filepath =output_absolute_address + filename;
		File file  = new File(chinese_filepath);
		FileOutputStream fos = new FileOutputStream(file);   
	      fos.write(data,0,data.length);   
	      fos.flush();   
	      fos.close();
	}
	
	public static void main(String[] args) 
	{
		File text_directory=new File(text_directory_absolute_address);
		File[] file_vector=new File[999999];
		
		int[] num=new int[1];//int[] num是否可改为 int num?
		num[0]=0;
		if (text_directory.exists() && text_directory.isDirectory())
		{
			get_file_vector(text_directory,file_vector,num);
		}
		else
		{
			System.err.println("Directory isn't existed!");
			System.err.println("Directory'address is: "+text_directory_absolute_address);
			System.exit(1);
		}
		
		for (int i=0;i<num[0];i++)
		{
			System.out.println("read"+file_vector[i].getAbsolutePath());
			get_text_file(file_vector[i]);
			try {
				saveFile(String.valueOf(i), text_word_vector_byte);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			text_word_vector_byte = null;
		}
		
		
	
		//String[] a = Arrays.toString(text_byte_vector);
		
		//System.out.println(Integer.valueOf());
		
		
		
		
		
	}

	
}
