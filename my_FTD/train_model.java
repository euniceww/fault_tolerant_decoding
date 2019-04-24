package my_FTD;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class train_model {
	final int gram_order=3;
	Map<String,Integer>[] language_model=(Map<String,Integer>[])new Map[gram_order];//map�б�3��map
	Map<String,Double>[] gram_probability=(Map<String,Double>[])new Map[gram_order];
	ArrayList<Character> text_word_vector=new ArrayList<>();	String text_directory_absolute_address;
	String count_file_absolute_address;
	
	public train_model(String count_file_relative_address,String text_directory_relative_address)
	{
		for (int i=0;i<language_model.length;i++)
		{
			language_model[i]=new TreeMap<String,Integer>();
		}
		for (int i=0;i<gram_probability.length;i++)
		{
			gram_probability[i]=new TreeMap<String,Double>();
		}
		String address=null;
		for (int i=0;i<language_model.length;i++)
		{
			address=count_file_relative_address+"_"+String.valueOf(i+1);
			get_count_number(address,language_model[i]);
		}
		count_file_absolute_address=System.getProperty("user.dir")+count_file_relative_address;//count_file���Ѿ�ͳ�ƺõģ��ʼ�����ִ���
		text_directory_absolute_address=System.getProperty("user.dir")+text_directory_relative_address;//text_directory�Ǵ�ͳ�Ƶ��ı�
	}
	
	
	private void get_count_number(String count_file_relative_address,Map<String,Integer> gram_model)
	{
		String count_file_absolute_address=System.getProperty("user.dir")+count_file_relative_address;
		File count_file=new File(count_file_absolute_address);
//		byte[] count_byte_vector=new byte[(int)count_file.length()];
		String one_word=null;
		if (count_file.exists() && count_file.isFile())
		{
			try
			{
				InputStream count_inputstream=new FileInputStream(count_file);
				InputStreamReader count_inputstreamreader=new InputStreamReader(count_inputstream,"GBK");
				BufferedReader count_bufferreader=new BufferedReader(count_inputstreamreader);
				while ((one_word=count_bufferreader.readLine())!=null)
				{
					int tab_offset=-1;
					for (int i=0;i<one_word.length();i++)//byte 9 ֮ǰ��word chain��֮����number
					{
						if (one_word.charAt(i)==(byte)9)
						{
							tab_offset=i;
						}
					}
					if (tab_offset==-1)
					{
						System.err.println("word chain may be wrong!");
					}
					else
					{
						String word_chain=one_word.substring(0,tab_offset);
						String number=one_word.substring(tab_offset+1,one_word.length());
//						System.out.println("one_word:"+one_word);
//						System.out.println("word_chain:"+word_chain);
//						System.out.println("number:"+number);
						int num=Integer.parseInt(number);
						gram_model.put(word_chain,(Integer)num);
					}
				}
				count_bufferreader.close();
				count_inputstreamreader.close();
				count_inputstream.close();
				count_inputstream=null;
				count_inputstreamreader=null;
				count_bufferreader=null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
//			System.err.println("File may not be opened!     File's address is: "+count_file_absolute_address);
//			System.exit(1);
			try
			{
				count_file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public void train_language_model()
	{
		File text_directory=new File(text_directory_absolute_address);
		File[] file_vector=new File[9999999];
		int[] num=new int[1];//int[] num�Ƿ�ɸ�Ϊ int num?
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
			System.out.println(file_vector[i].getAbsolutePath());
		}
		for (int i=0;i<num[0];i++)//num[0]���ܵ��ļ�������file_vector�ĳ���
		{
			System.out.println("read file"+file_vector[i].getAbsolutePath());
			get_text_file(file_vector[i]);
			for (int j=0;j<language_model.length;j++)
			{
				calculate_count_number(j+1);
			}
			text_word_vector.clear();
		}
		file_vector=null;
		num=null;
		text_directory=null;
		store_language_model();
	}

	private void get_file_vector(File file_directory,File[] file_vector,int[] num)//��ĳ�ļ����е��ļ�����һ��file_vector�����ļ��������ļ�����룬��������Ŵ��ļ����ٷ���
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
	
	private void get_text_file(File text_file)//get_text_byte_vector
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
	           
	            int thechar;
	            int j=0;
	           
	            while((thechar=text_inputstreamreader.read()) != -1){ 
	            	 if(thechar>127)
	            	 {
	            		 text_word_vector.add((char)thechar);
//	            		 System.out.print(text_word_vector.get(j));
	            		 j++;
	            	 }
	            }
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
	
	private void calculate_count_number(int order_number)
	{
		
		int char_offset;
		char_offset=0;
		LinkedList<String> word_linkedlist=new LinkedList<String>();
		while (char_offset<text_word_vector.size())
		{
//				System.out.print("wangyuyang"+text_word_vector.get(4));
			    String one_word = String.valueOf(text_word_vector.get(char_offset));
//				System.out.println(one_word);
				String[] word_list=new String[1];
				word_list[0]="";
				boolean whether_3=get_word_list(one_word,word_list,
						word_linkedlist,order_number);
				
//				System.out.println(word_list[0]);
				if (whether_3)
				{
					if (null==language_model[order_number-1].get(word_list[0]))//��language_model��x��map��word_list�����ֵ������ֵ��ӳ��Ϊ1
					{
						language_model[order_number-1].put(word_list[0], (Integer)1);
					}
					else
					{
						int n1=language_model[order_number-1].get(word_list[0]);//����word)_list��ֵ����ӳ��ֵ��һ
						n1++;
						language_model[order_number-1].put(word_list[0], (Integer)n1);
					}
				}
				char_offset++;
		}
	}

	private boolean get_word_list(String one_word,String[] word_list,
			LinkedList<String> word_linkedlist,int order_number)//word_linkedlist����ʼ���ǽ״Σ�order_number������ʼ��ordernumberС��������ӣ�����ordernumber�������ȡ��һ���һ
	{
		if (word_linkedlist.size()>=order_number)
		{
			word_linkedlist.remove(0);
		}
		word_linkedlist.add(one_word);
		if (word_linkedlist.size()<order_number)//��word_linkedlist����С��ordernumber��return false������
		{
			return false;
		}
		byte[] byte_vector=new byte[1];
		byte_vector[0]=(byte)32;//ASCII 32�ǿո�
		String s1=new String(byte_vector);
		for (int i=0;i<order_number;i++)
		{
			word_list[0]+=word_linkedlist.get(i);//word_listһ��string,������word_linkedlist�еļ������ʣ�������Ϊ����ʽ�ǣ����� �ո� ���� �ո� ����
			if (i<order_number-1)
			{
				word_list[0]+=s1;
			}
		}
		byte_vector=null;
		s1=null;
		return true;
	}
	
	private void store_language_model()//language_model�洢�ʳ��ִ���
	{
		for (int i=0;i<language_model.length;i++)
		{
			String absolute_address=count_file_absolute_address+"_"+String.valueOf(i+1);
			train_model.store_one_map(absolute_address,language_model[i]);
		}
	}
	
	public static void store_one_map(String absolute_address,Map one_map)
	{
		File gram_model_file=new File(absolute_address);
		try
		{
			OutputStream gram_model_outputstream=new FileOutputStream(gram_model_file);
			Iterator<Map.Entry> iter=one_map.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry<String,Object> entry=iter.next();
				byte[] byte_vector=entry.getKey().getBytes();
				gram_model_outputstream.write(byte_vector);
				byte_vector=null;
				byte_vector=new byte[1];
				byte_vector[0]=(byte)9;// 9: \t
				gram_model_outputstream.write(byte_vector);
				byte_vector=null;
				byte_vector=String.valueOf(entry.getValue()).getBytes();
				gram_model_outputstream.write(byte_vector);
				byte_vector=null;
				byte_vector=new byte[1];
				byte_vector[0]=(byte)10;// 10: \n
				gram_model_outputstream.write(byte_vector);
				byte_vector=null;
			}
			gram_model_outputstream.close();
			gram_model_outputstream=null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	private void store_language_probability()//language_probability�洢���ʣ���Ϣ��
	{
		for (int i=0;i<gram_probability.length;i++)
		{
			String absolute_address=count_file_absolute_address+"_probability_"+String.valueOf(i+1);
			train_model.store_one_map(absolute_address,gram_probability[i]);
		}
	}
	
	
	
	
	private void calculate_gram_information()
	{
		double[] relative=new double[3];
		relative[2]=0.9;relative[1]=0.099;relative[0]=0.00099;
		for (int i=0;i<gram_order;i++)
		{
			calculate_one_gram_information(i,relative[i]);
		}
		store_language_probability();
	}
	
	private void calculate_one_gram_information(int order_number,double relative)//relative���ϵ��
	{
		int total=0;
		Iterator<Map.Entry<String,Integer>> iter=language_model[order_number].entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<String, Integer> entry=iter.next();
			total+=entry.getValue();//language_model���ܵĸ���Ŀ���ִ�����һ����һ���ʣ������Ƕ�����һ����֡�����
			entry=null;
		}
		iter=language_model[order_number].entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<String, Integer> entry=iter.next();
			if (order_number>1)
			{
				int condition_number=calculate_condition_number(entry.getKey(),order_number);
				double prob=Math.log((double)condition_number)/Math.log((double)2)-
						Math.log((double)entry.getValue())/Math.log((double)2)-Math.log((double)relative)/Math.log((double)2);
				gram_probability[order_number].put(entry.getKey(),(Double)prob);
				entry=null;
			}
			else
			{
				double prob=Math.log((double)total)/Math.log((double)2)-
						Math.log((double)entry.getValue())/Math.log((double)2)-Math.log((double)relative)/Math.log((double)2);
				gram_probability[order_number].put(entry.getKey(),(Double)prob);
				entry=null;
			}
		}
		if (order_number==0)
		{
			double prob=11*Math.log((double)10)/Math.log((double)2);
			String unk_str="<unknown>";
			gram_probability[order_number].put(unk_str,(Double)prob);
		}
	}
	
	private int calculate_condition_number(String one_gram,int order_number)//one_gram��language_model�е�һ����ֵ
	{
		int offset=-1;
		for (int i=0;i<one_gram.length();i++)//��order numberΪ3���� 
		{
			if (one_gram.charAt(i)==(byte)32)//ȡ�������ʵļ�ֵ�ĵ�һ��
			{
				offset=i+1;
				break;
			}
		}
		String str=one_gram.substring(offset, one_gram.length());

		int num=language_model[order_number-1].get(str);//�ڶ���language_model���Һ�������Ϊ����Ӧ�ļ�ֵ
		return num;
	}

	public static void main(String[] argv)
	{
        System.out.println("begin");
		String count_file_relative_address=argv[0];
		String text_directory_relative_address=argv[1];
		train_model count_number_object=new train_model(count_file_relative_address,
				text_directory_relative_address);
		count_number_object.train_language_model();
		count_number_object.calculate_gram_information();
	}
	

}

