package my_FTD;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class utf82gbk {
	public static void main(String[] args) throws IOException { 		
		// ��װĿ¼,��Ҫ�޸��ļ���ʽ��·��		
		File srcFolder = new File("E:\\FTD\\my_FTD\\resource\\train_language_model_text\\othercountrynovel"); 		
		// �ݹ鹦��ʵ��		
		getAllJavaFilePaths(srcFolder);	} 	
	private static void getAllJavaFilePaths(File srcFolder) throws IOException { 		
		// ��ȡ��Ŀ¼�����е��ļ������ļ��е�File����		
		File[] fileArray = srcFolder.listFiles(); 		
		// ������File���飬�õ�ÿһ��File����		
		for (File file : fileArray) { 			
			// �����ж��Ƿ���.java��β,���ǵĻ���������getAllJavaFilePaths()����		
			if (file.isDirectory()) { 				
				getAllJavaFilePaths(file); 			
				} else { 				
										
						// ��GBK��ʽ,��ȡ�ļ�					
						FileInputStream fis = new FileInputStream(file);					
						InputStreamReader isr = new InputStreamReader(fis, "UTF-8");					
						BufferedReader br = new BufferedReader(isr);					
						String str = null; 					
						// ����StringBuffer�ַ���������					
						StringBuffer sb = new StringBuffer(); 					
						// ͨ��readLine()����������ȡ�ļ�					
						while ((str = br.readLine()) != null) {						
							// ʹ��readLine()�����޷����л���,��Ҫ�ֶ���ԭ��������ַ��������"\n"��"\r"						
							str += "\n";						
							sb.append(str);					
							}					
						String str2 = sb.toString(); 					
						// ��UTF-8��ʽд���ļ�,file.getAbsolutePath()�����ļ��ľ���·��,false����׷��ֱ�Ӹ���,true����׷���ļ�					
						FileOutputStream fos = new FileOutputStream(file.getAbsolutePath(), false);					
						OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");					
						osw.write(str2);					
						osw.flush();					
						osw.close();					
						fos.close();					
						br.close();					
						isr.close();					
						fis.close();				
									
					}		
			}	
		}
						
}
