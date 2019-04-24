package my_FTD;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.security.auth.kerberos.KerberosKey;

import java.util.Random;

import grammar_tester.Dictionary_2;
import my_FTD.read_file_to_byte;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class Print_probability_graph {
	
	private LinkedList<String> word_linkedlist = new LinkedList<String>();
	private final int markov_number = 3;
	
    public String[] get_one_word(byte[] text_byte_vector) {
		
    	String[] one_word = new String[text_byte_vector.length];
		int i = 0;
		int j = 0;
			while (i < text_byte_vector.length) {
				
				byte[] word_byte_vector = new byte[2];
				if ((text_byte_vector[i]& 0xff)>127&&i < text_byte_vector.length-1) {
					word_byte_vector[0] = text_byte_vector[i];
					word_byte_vector[1] = text_byte_vector[i+1];
					i=i+2;
					String test_one_word = null;
					try {
						test_one_word = new String(word_byte_vector, "GBK");
						one_word[j] = test_one_word;
//						System.out.println(test_one_word);System.out.println(one_word[j]);
						j=j+1;
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					i++;
//					System.out.println(1);
				}
			}
			return one_word;
			
	}
    
    public String get_word_list(String one_word) {
		if (word_linkedlist.size() >= markov_number) {
			word_linkedlist.remove(0);
		}
		word_linkedlist.add(one_word);
		
		byte[] byte_vector = new byte[1];
		byte_vector[0] = (byte) 32;
		String s1 = new String(byte_vector);
		String word_list = "";
		for (int i = 0; i < word_linkedlist.size()-1; i++) {
			word_list += word_linkedlist.get(i);
			word_list += s1;
		}
		word_list += word_linkedlist.get(word_linkedlist.size()-1);
		byte_vector = null;
		s1 = null;
		return word_list;
	}
    
    /** 
     * 统计序列的概率密度返回横纵坐标 
    * @param frames 序列 
     * @param framenum 序列长度，从外面计算一并传进来了 
     * @param intervalCount 区间个数        * @return 
     */  
    public static double[][] statisticPD(double[] frames, int framenum, int intervalCount) {  
        // 概率密度  
        double Pd;  
          
        // 区间上下界和中间值  
        double upInterval, downInterval, middleValue;  
        // 每个区间内的帧数量  
        int count = 0;  
  
        // 横纵坐标保存数组  
        double[][] frameArray = new double[intervalCount][2];  
  
        Arrays.sort(frames);  
  
        double minFrame = frames[0];  
        double maxFrame = frames[framenum - 1];  
        // 区间宽度  
        double interval = (maxFrame - minFrame) / intervalCount;  
  
        System.out.println("Min=" + minFrame + " " + "Max=" + maxFrame + " " + "interval=" + interval);  
  
        for (int k = 0; k < intervalCount; k++) {  
  
            upInterval = minFrame + (k + 1) * interval ;  
            downInterval = minFrame + k * interval;  
            middleValue = downInterval + interval / 2; // 中点值（每一个横坐标）  
  
            for (int i = 0; i < framenum; i++) {  
                if (frames[i] < upInterval && frames[i] >= downInterval) {  
                    count++;  
                }  
            }  
            Pd = (double) count / framenum / interval; // 纵坐标  
            frameArray[k][0] = middleValue;  
            frameArray[k][1] = Pd;  
            count = 0;  
        }  
  
        return frameArray;  
    }
    
  /**  
    public void testWriter() throws Exception {
        CsvWriter wr = new CsvWriter("F://info.csv", ',', Charset.forName("GBK"));
        // 写表头
        String[] headers = {"编号","姓名","性别","年龄"};
        wr.writeRecord(headers);
        // 写内容
        List<String[]> contents = new ArrayList<String[]>();
        String[] row1 = { "001", "小明", "男", "21" };
        String[] row2 = { "002", "小红", "女", "18" };
        contents.add(row1);
        contents.add(row2);
        for (String[] row : contents) {
            wr.writeRecord(row);
        }
        wr.close();
    }
 * @throws IOException 

  **/
    public void add_BER_error_bit(double ber,byte[] file_byte_vector) {//加入均匀分布的误码
		// correct_compressed_file_byte_vector=new
		// byte[compressed_file_byte_vector.length];
		// for (int i=0;i<compressed_file_byte_vector.length;i++)
		// {
		// correct_compressed_file_byte_vector[i]=compressed_file_byte_vector[i];
		// }
		int interval = (int) Math.pow(10, ber);//10的ber次方
		int[] byte_offset = new int[1];
		int[] byte_bit_offset = new int[1];
		Random random = new Random();
		int Floating_Range = (int) (0.05 * interval);
		int offset = 1000 + Math.abs(random.nextInt()) % Floating_Range;
		while (offset < 8 * file_byte_vector.length - Floating_Range) {
			//error_offset_arraylist.add(offset);
			bit_offset_to_byte_bit_offset(offset, byte_offset, byte_bit_offset);
			byte one_byte = (byte) (1 << byte_bit_offset[0]);
			file_byte_vector[byte_offset[0]] ^= one_byte;
			offset += interval + Math.abs(random.nextInt()) % Floating_Range;
		}
	}
    
    private void bit_offset_to_byte_bit_offset(int bit_offset, int[] byte_offset, int[] byte_bit_offset) {
		byte_offset[0] = bit_offset >> 3;
		byte_bit_offset[0] = bit_offset & 7;
	}
    
    public static void saveFile(String filename,byte [] data)throws Exception{ 
		String byte_filepath ="resource\\train_language_model_text\\test\\" + filename;
		File file  = new File(byte_filepath);
		FileOutputStream fos = new FileOutputStream(file);   
	      fos.write(data,0,data.length);   
	      fos.flush();   
	      fos.close();
	}

   
/*******************************************************************main***********************************************************************/	
	public static void main(String args[]) throws IOException{
		String dictionary_absolute_address = "resource\\dict_probability";
		Dictionary_2 dict = new Dictionary_2(dictionary_absolute_address);
		
/*		String print_graph_absolute_adress = "resource\\train_language_model_text\\test\\pddUse.txt";
		byte[] text_byte_vector = null;
		text_byte_vector = read_file_to_byte.read_file(print_graph_absolute_adress);
*/		
//		for(int k=0;k<text_byte_vector.length;k++)
//		{
//			System.out.printf("%d",text_byte_vector[k]& 0xff);System.out.println();
//		}
		
/******************************************************正确的pdd**********************************************/		
/*		Print_probability_graph print_graph = new Print_probability_graph();
		//调节one_word_list长度
		String[] one_word_list = new String[9999999];
		System.arraycopy(print_graph.get_one_word(text_byte_vector), 0, one_word_list, 0, (print_graph.get_one_word(text_byte_vector)).length);
			
		
		ArrayList<Double> information_list = new ArrayList<>(); 
		for(int i=0;i<one_word_list.length;i++)
		{
			if(one_word_list[i]!=null){
				String word = print_graph.get_word_list(one_word_list[i]);//System.out.println(i+word);
				if(i>=2){
					double information = dict.calculate_gram_information(word);
			        information_list.add(information);
			        //System.out.println(information);
				}
			}
		}
		double[] info_list = new double[information_list.size()];
		for(int i = 0;i<information_list.size();i++){
			info_list[i] = information_list.get(i);
		}
		
		//得到横纵坐标//
		int intervalCount = 18;
		double[][] frameArray = new double[intervalCount][2];
		frameArray = print_graph.statisticPD(info_list, information_list.size(), intervalCount);
		//System.out.println(frameArray[48][1]);
		
		CsvWriter wr = new CsvWriter("resource\\train_language_model_text\\test\\pddUse_true.csv", ',', Charset.forName("GBK"));
		String[] headers = new String[intervalCount];
		String[] content = new String[intervalCount];
		for(int i =0;i<intervalCount;i++)
		{
			headers[i] = String.valueOf(frameArray[i][0]);
			content[i] = String.valueOf(frameArray[i][1]);
		}
        wr.writeRecord(headers);
        wr.writeRecord(content);
       wr.close();
 */
/******************************************压缩后加误码再解压缩后统计数据**********************************************/
		Print_probability_graph print_graph = new Print_probability_graph();
		//读取压缩文件
/*		byte[] compress_text_byte_vector = read_file_to_byte.read_file("resource\\train_language_model_text\\test\\pddUse_true_compress");
		//加误码
		
		print_graph.add_BER_error_bit(4.5, compress_text_byte_vector);
		//存取后解压缩
		try {
			saveFile("pddUse_false_compress", compress_text_byte_vector);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

*/        
		byte[] text_byte_vector = null;
		text_byte_vector = read_file_to_byte.read_file("resource\\train_language_model_text\\test\\pddUse_false_decompress");
        
        String[] one_word_list = new String[9999999];
		System.arraycopy(print_graph.get_one_word(text_byte_vector), 0, one_word_list, 0, (print_graph.get_one_word(text_byte_vector)).length);
			
		
		ArrayList<Double> information_list = new ArrayList<>(); 
		for(int i=0;i<one_word_list.length;i++)
		{
			if(one_word_list[i]!=null){
				String word = print_graph.get_word_list(one_word_list[i]);//System.out.println(i+word);
				if(i>=2){
					double information = dict.calculate_gram_information(word);
			        information_list.add(information);
			        //System.out.println(information);
				}
			}
		}
		double[] info_list = new double[information_list.size()];
		for(int i = 0;i<information_list.size();i++){
			info_list[i] = information_list.get(i);
		}
		
		//得到横纵坐标
		int intervalCount = 18;
		double[][] frameArray = new double[intervalCount][2];
		frameArray = print_graph.statisticPD(info_list, information_list.size(), intervalCount);
		//System.out.println(frameArray[48][1]);
		
		CsvWriter wr = new CsvWriter("resource\\train_language_model_text\\test\\pddUse_false.csv", ',', Charset.forName("GBK"));
		String[] headers = new String[intervalCount];
		String[] content = new String[intervalCount];
		for(int i =0;i<intervalCount;i++)
		{
			headers[i] = String.valueOf(frameArray[i][0]);
			content[i] = String.valueOf(frameArray[i][1]);
		}
        wr.writeRecord(headers);
        wr.writeRecord(content);
        wr.close();
	}
	
}
	
	


