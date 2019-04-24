package my_FTD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import grammar_tester.Dictionary_2;


public class detectDelay {
		
	static byte[] compressed_file_byte_vector ;
	
	private final int THRESHOLD = 2;
	private final int N = 4096;
	private final int F = 18;
	private byte[] buffer_window = new byte[N + F - 1];
	private final int markov_number = 3;
	private final double information_threhold = 17;
	
	private int flags = 0;
	private int compressed_offset = 0;// ��⿪ʼ�ĵ㣬��ȡ�ļ���ָ�룬LZSSǰ14��ֵ�ļ�ͷ
	private int compressed_data_testing_offset = -1;
	private int buffer_window_offset;
	private ArrayList<Byte> decompressed_arraylist = new ArrayList<Byte>();
	byte[] import_error_byte_vector;
	private int test_word_offset = 0;
	private LinkedList<String> word_linkedlist = new LinkedList<String>();
	Dictionary_2 dict = new Dictionary_2("E:\\FTD\\my_FTD\\resource\\dict_probability");
	
	public detectDelay() {};
	
	private static void get_file_vector(File file_directory,File[] file_vector,int[] num)//��ĳ�ļ����е��ļ�����һ��file_vector�����ļ��������ļ�����룬��������Ŵ��ļ����ٷ���
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
		if (text_file.exists() && text_file.isFile())
		{
			compressed_file_byte_vector = new byte[(int) text_file.length()];
			try
			{
				FileInputStream text_inputstream=new FileInputStream(text_file);

				
					int x = text_inputstream.read(compressed_file_byte_vector);
					//System.out.println(x);
				
				
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
	
	public void add_special_one_error_bit(int offset) {

		int[] byte_offset = new int[1];
		int[] byte_bit_offset = new int[1];
		bit_offset_to_byte_bit_offset(offset, byte_offset, byte_bit_offset);
		byte one_byte = (byte) (1 << byte_bit_offset[0]);
		compressed_file_byte_vector[byte_offset[0]] ^= one_byte;
	}
	private void bit_offset_to_byte_bit_offset(int bit_offset, int[] byte_offset, int[] byte_bit_offset) {
		byte_offset[0] = bit_offset >> 3;
		byte_bit_offset[0] = bit_offset & 7;
	}
	
	private int detect_position(){
		boolean decoding_going = true;

		while (true) {
			decoding_going = decompressing_one_compressed_section();//ÿ�ν�ѹflagһλ��Ӧ������
			if (decoding_going) {
				boolean whether = true;
				if ((8 * compressed_offset - 1) > compressed_data_testing_offset) {
				whether = absolute_judging_grammar_correction();
				}
				if (whether) {
					boolean whether_1 = true;
					while (whether_1) {
						whether_1 = judging_word_exist();//�Ƿ�����֣�2�ֽڣ�
						if (whether_1) {
							String[] one_word = new String[1];
							boolean whether_2 = get_one_word(one_word);
							if (whether_2) {
								String[] word_list = new String[1];
								word_list[0] = "";
								boolean whether_3 = get_word_list(one_word[0], word_list);
								if (whether_3) {
									if ((8 * compressed_offset - 1) > compressed_data_testing_offset) {
										whether &= judging_grammar_correction(word_list[0]);//�ж��Ƿ�����﷨����
										 if (!whether)
										 {
										 print_decompressed_arraylist();
										 System.out.println("�﷨����Ŷ��");
										 return compressed_offset;
										 
										 }
									} else {
										whether = true;
									}
								}
								word_list = null;
							}
							one_word = null;
						} else {//�����������֣�������Ƿ��ѽ�ѹ��ѹ���ļ�β�ˡ�û���ͼ�����ѹ���������ѹ����
							if (compressed_offset == compressed_file_byte_vector.length) 
							{
								whether = false;
								return -1;
							}
						}
					}
				}
			}
		}
	}
	
	private void print_decompressed_arraylist() {
		byte[] decompressed_byte_vector = new byte[decompressed_arraylist.size()];
		
		for (int i = 0; i < decompressed_byte_vector.length; i++) {
			decompressed_byte_vector[i] = decompressed_arraylist.get(i);
			System.out.println(decompressed_byte_vector[i]&0xff);
		}
		String decompressed_string = new String(decompressed_byte_vector);
		String decompressed_string_1 = new String();
		int len = 500;
		for (int i = 0; i < len; i++) {
			if (decompressed_string.length() - len + i >= 0) {
				decompressed_string_1 += decompressed_string.charAt(decompressed_string.length() - len + i);
			}
		}
		System.out.println(decompressed_string_1);
		// System.out.println(decompressed_string);
		System.out.println("decompressed file's size=" + decompressed_arraylist.size());
	}
	
	private boolean decompressing_one_compressed_section() {
		// false: no byte true: some bytes
		int i[] = new int[1];
		int j[] = new int[1];
		
		if ((flags & 0x100) == 0) {//0x100:0x��ʾ16���ƣ�����ʮ����16^2*1=256,��������100000000
			if (compressed_offset < compressed_file_byte_vector.length) {
				flags = ((int) (compressed_file_byte_vector[compressed_offset]
						) & 0xFF) | 0xFF00;//^��� 0xFF:11111111
				compressed_offset++;
			} else {
				System.err.println("compressed_offset<compressed_file_byte_vector.length when flags is read!");
				System.exit(1);
			}
		}
		if ((flags & 1) == 1) {//flags����1����ʾ�ַ�δ������ֱ�������һ���ֽڣ�
			if (compressed_offset < compressed_file_byte_vector.length) {
				// Distance=-1;
				// Length=1;
				byte one_byte = (byte) (compressed_file_byte_vector[compressed_offset]);
				//////////////////////////////////////////////////////////////////////////////////////////
				// character error
				//////////////////////////////////////////////////////////////////////////////////////////

				buffer_window[buffer_window_offset] = one_byte;
				buffer_window_offset = (buffer_window_offset + 1) % N;//N���崰�ڴ�С4096��buffer_window_offset��һ������Nȡ����offsetʼ����0��N֮��
				decompressed_arraylist.add(one_byte);
				compressed_offset++;

			} else {
				// System.err.println("character error");
				return false;
			}
		} else {//flags=0,��ʾ�����˴������������˵�ġ�ƥ��λ�ã�ƥ�䳤�ȡ���Ԫ�飨�����ֽڣ�
			//��Ԫ��������ֽ����������ŵģ���һ���ֽڱ�ʾƥ��λ�õĵͰ�λ���ڶ����ֽڵĸ���λ��ʾƥ��λ�õĸ���λ���ڶ����ֽڵĵ���λ��ʾƥ�䳤��
			//�������ж���NΪ4096�����λ��ֵռ��12λ��Fֵ����Ϊ18����ȥƥ�䳤��Ϊ1��2�������������16�����ռ4λ����
			if (compressed_offset < compressed_file_byte_vector.length) {
				/*
				 * length=5, distance=19;
				 */
				/////////////////////////////////////////////////////////////////////
				i[0] = (int) ((compressed_file_byte_vector[compressed_offset]
						) & 0xFF);
				compressed_offset++;
				if (compressed_offset < compressed_file_byte_vector.length) {
					j[0] = (int) ((compressed_file_byte_vector[compressed_offset]
							) & 0xFF);
					compressed_offset++;

					//////////////////////////////////////////////////////////////////////////////////////////
					i[0] |= ((j[0] & 0xF0) << 4);//j[0] & 0xF0ǰ4λ�������λ��0��������4λ���ٻ�i�����õ�12λ������λ��j�ĸ���λ���Ͱ�λ��i����������ʾƥ��λ��
					j[0] = ((j[0] & 0x0F) + THRESHOLD);//j�ĵ���λ��ʾƥ�䳤�ȣ�����threshold
					/////////////////////////////////////////////////////////////////////

					for (int k = 0; k <= j[0]; k++) {
						byte one_byte = buffer_window[(i[0] + k) % N];
						buffer_window[buffer_window_offset] = one_byte;
						buffer_window_offset = (buffer_window_offset + 1) % N;
						decompressed_arraylist.add(one_byte);	


					}

				} else {
					// System.err.println("Distance-length error 2");
					return false;
				}
			} else {

				return false;
			}
		}
		flags >>= 1;

		i = null;
		j = null;
		return true;
	}
	
	private boolean absolute_judging_grammar_correction() {//�ж��Ƿ�gbk�еĺ���
		//System.out.println("test_word_offset"+test_word_offset);
		if (decompressed_arraylist.size() > 0) {
			if (decompressed_arraylist.size() - test_word_offset > 50) {
				return false;
			} else {
				for (int i = test_word_offset; i < decompressed_arraylist.size(); i=i+2) {					
					if ((decompressed_arraylist.get(i)& 0xFF) <= 127) {
						
						// System.err.println((int)decompressed_arraylist.get(i));
						 //System.out.println("i"+i);System.out.println("decompressed_arraylist_size"+decompressed_arraylist.size());
						// System.exit(1);
						return false;
					}
				}
				return true;
			}

		} else {
			return true;
		}
	}
	
	private boolean judging_word_exist() {//�ж�test_word_offset��decompressed_arraylist.size()֮���Ƿ�����֣�2�ֽڣ���test_word_offset��0��ʼ������Ҫ>2
		if (decompressed_arraylist.size() > 0) {
			if (decompressed_arraylist.size()-test_word_offset > 2) {
					return true;
				
			}
			return false;
		} else {
			return false;
		}
	}
	
	private boolean get_word_list(String one_word, String[] word_list) {
		if (word_linkedlist.size() >= markov_number) {
			word_linkedlist.remove(0);
		}
		word_linkedlist.add(one_word);
		if (word_linkedlist.size() < markov_number) {
			return false;
		}
		byte[] byte_vector = new byte[1];
		byte_vector[0] = (byte) 32;
		String s1 = new String(byte_vector);
		for (int i = 0; i < markov_number - 1; i++) {
			word_list[0] += word_linkedlist.get(i);
			word_list[0] += s1;
		}
		word_list[0] += word_linkedlist.get(markov_number - 1);
		byte_vector = null;
		s1 = null;
		return true;
	}
	
	private boolean get_one_word(String[] one_word) {
		
		int i = test_word_offset; 
			while (i < decompressed_arraylist.size()) {
				
				byte[] word_byte_vector = new byte[2];
//				if (decompressed_arraylist.get(i)>127) {
					word_byte_vector[0] = decompressed_arraylist.get(i);
					word_byte_vector[1] = decompressed_arraylist.get(i+1);
					test_word_offset = test_word_offset+2;
					i=i+2;
					one_word[0] = null;
					try {
						one_word[0] = new String(word_byte_vector, "GBK");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
//				} else {
//					test_word_offset ++;
//					i++;
//				}
			}
			
			return false;
	}
	
	private boolean judging_grammar_correction(String one_word) {
		if (word_linkedlist.size() < markov_number) {
			System.err.println("word_linkedlist.size()=" + word_linkedlist.size());
			System.exit(1);
		}
		double information = dict.calculate_gram_information(one_word);
		if (information < information_threhold) {
			return true;
		} else {
			return false;
		}
	}
	
	private void initial()
	{
		flags = 0;
		compressed_offset = 0;// ��⿪ʼ�ĵ㣬��ȡ�ļ���ָ�룬LZSSǰ14��ֵ�ļ�ͷ
		compressed_data_testing_offset = -1;
		buffer_window_offset=0;
		decompressed_arraylist.clear();
		for(int i=0;i<compressed_file_byte_vector.length;i++)
		{
			compressed_file_byte_vector[i]=0;
		}
		test_word_offset = 0;
		word_linkedlist.clear();
	}
	
	public static void main(String[] args) {
		File text_directory=new File("E:\\FTD\\my_FTD\\resource\\detectDelay");
		File[] file_vector=new File[100];
		ArrayList<Integer> delay_res = new ArrayList<>();
		int error_offset = 1000;
		
		detectDelay det = new detectDelay();
		
		int[] num=new int[1];//int[] num�Ƿ�ɸ�Ϊ int num?
		num[0]=0;
		if (text_directory.exists() && text_directory.isDirectory())
		{
			get_file_vector(text_directory,file_vector,num);
		}
		else
		{
			System.err.println("Directory isn't existed!");
			System.err.println("Directory'address is: ");
			System.exit(1);
		}
		for (int i=0;i<num[0];i++)
		{
			System.out.println("read"+file_vector[i].getAbsolutePath());
			get_text_file(file_vector[i]);
			det.add_special_one_error_bit(error_offset);
			int detect_pos = det.detect_position();
			int detect_delay = detect_pos-error_offset;
			delay_res.add(detect_delay);
			System.out.println(detect_pos);
			det.initial();
		}
		
	}


	
}
