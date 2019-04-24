package grammar_tester;


import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.TreeMap;

public class Dictionary_2 
{
	final int max_order_number=3;
	Map<String,Double>[] gram_model_information=new Map[max_order_number];
	
	
	public Dictionary_2(String gram_absolute_address)
	{
		for (int i=0;i<gram_model_information.length;i++)
		{
			gram_model_information[i]=new TreeMap<String,Double>();
		}
		get_gram_model_information(gram_absolute_address);
	}
	
	private void get_gram_model_information(String gram_absolute_address)
	{
		for (int i=0;i<3;i++)
		{
			get_a_gram_model_information(gram_absolute_address,i+1);
		}
	}
	
	private void get_a_gram_model_information(String gram_absolute_address,int order_number)
	{
		String one_word=null;
		String gram_absolute_address_single=gram_absolute_address+"_"+String.valueOf(order_number);
		File gram_file=new File(gram_absolute_address_single);
		if (gram_file.exists() && gram_file.isFile())
		{
			try
			{
				InputStream gram_inputstream=new FileInputStream(gram_file);
				InputStreamReader gram_inputstreamreader=new InputStreamReader(gram_inputstream);
				BufferedReader gram_bufferreader=new BufferedReader(gram_inputstreamreader);
				while ((one_word=gram_bufferreader.readLine())!=null)
				{
					int offset=-1;
					for (int i=0;i<one_word.length();i++)
					{
						if (one_word.charAt(i)==(byte)9)
						{
							offset=i;
							break;
						}
					}
					if (offset==-1)
					{
						System.err.println("Tab cannot be found!");
						System.exit(1);
					}
					else
					{
						String word_string=one_word.substring(0,offset);
						String information_string=one_word.substring(offset+1,one_word.length());
						Double information=Double.valueOf(information_string);
						gram_model_information[order_number-1].put(word_string, information);
					}
				}
				gram_inputstream.close();
				gram_inputstream=null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("File cannot be opened!");
			System.err.println("File address is:"+gram_absolute_address);
			System.exit(1);
		}
	}
	
	public double calculate_gram_information(String word_string)
	{
		for (int i=max_order_number;i>0;i--)
		{
//			System.out.println("word_string="+word_string);
//			System.out.println("value="+gram_model_information[i-1].get(word_string));
			if (gram_model_information[i-1].get(word_string)!=null)
			{
				double information=gram_model_information[i-1].get(word_string);
				return information;
			}
			else
			{
				if (i>1)//如果i阶gram_model中无想获取的word_string则 娶妻i-1阶（后i-1个词）
				{
					word_string=cut_one_word(word_string);
				}
			}
		}
		double information=gram_model_information[0].get("<unknown>");
		return information;
	}
	
	private String cut_one_word(String word_string)
	{
		int offset=-1;
		for (int i=0;i<word_string.length();i++)
		{
			if (word_string.charAt(i)==(byte)32)
			{
				offset=i;
				break;
			}
		}
		if (offset==-1)
		{
			System.err.println("Space cannot be found!");
			System.out.println("word_string="+word_string);
			System.exit(1);
		}
		else
		{
//			System.out.println("offset="+offset);
//			System.out.println("1.word_string="+word_string);
			word_string=word_string.substring(offset+1,word_string.length());
//			System.out.println("2.word_string="+word_string);
		}
		return word_string;
	}
	
	
}
