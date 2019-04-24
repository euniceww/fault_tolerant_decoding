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
		// 封装目录,需要修改文件格式的路径		
		File srcFolder = new File("E:\\FTD\\my_FTD\\resource\\train_language_model_text\\othercountrynovel"); 		
		// 递归功能实现		
		getAllJavaFilePaths(srcFolder);	} 	
	private static void getAllJavaFilePaths(File srcFolder) throws IOException { 		
		// 获取该目录下所有的文件或者文件夹的File数组		
		File[] fileArray = srcFolder.listFiles(); 		
		// 遍历该File数组，得到每一个File对象		
		for (File file : fileArray) { 			
			// 继续判断是否以.java结尾,不是的话继续调用getAllJavaFilePaths()方法		
			if (file.isDirectory()) { 				
				getAllJavaFilePaths(file); 			
				} else { 				
										
						// 以GBK格式,读取文件					
						FileInputStream fis = new FileInputStream(file);					
						InputStreamReader isr = new InputStreamReader(fis, "UTF-8");					
						BufferedReader br = new BufferedReader(isr);					
						String str = null; 					
						// 创建StringBuffer字符串缓存区					
						StringBuffer sb = new StringBuffer(); 					
						// 通过readLine()方法遍历读取文件					
						while ((str = br.readLine()) != null) {						
							// 使用readLine()方法无法进行换行,需要手动在原本输出的字符串后面加"\n"或"\r"						
							str += "\n";						
							sb.append(str);					
							}					
						String str2 = sb.toString(); 					
						// 以UTF-8格式写入文件,file.getAbsolutePath()即该文件的绝对路径,false代表不追加直接覆盖,true代表追加文件					
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
