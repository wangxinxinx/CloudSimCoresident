package org.iie.cloudsim.util;
 
import java.io.*;
import java.util.List;

import org.iie.clodsim.drlallocation.util.DrlConstants;
 
public class CsvUtil {

	private String filePath;
	
	public CsvUtil() {
		this("default.csv",null);
	}
	
	public CsvUtil(String fileName) {
		this(fileName,null);
	}
	
	public CsvUtil(String fileName,List<Object> headList) {
		filePath = DrlConstants.CSV_PATH +fileName;
		setFilePath(filePath);
		createFile();
		if(headList!=null) {
			writeOneLine(headList);
		}
	}	
			
	/**
     * 如果filePath代表的文件不存在，则创建文件
     * @return  是否创建成功，成功则返回true
     */
    private boolean createFile(){
        Boolean bool = false;
        File file = new File(getFilePath());
        
        try {
            //如果文件已存在，则先删除已存在的文件，再创建新的文件
        	if(file.exists()) {
        		file.delete();
        	}
        	file.createNewFile();
            bool = true;
            // System.out.println("success create file,the file is "+getFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return bool;
    }
			
    /**
     * 一次性输入所有数据创建一个CSV文件
     * @param dataLists
     * @return
     */
 
    public static File createCSVFile( List<List<Object>> dataLists, List<Object>   headList,String csvFilePath) throws IOException {
 
        BufferedWriter csvWrite = null;
        File csvFile = null;
        
        try {
 
            //创建文件
            csvFile = new File(csvFilePath);
            //创建文件目录
            File parent = csvFile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
 
            }
            
            //创建文件
            csvFile.createNewFile();
            csvWrite = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 32768);
 
            //写入表头
            write(headList, csvWrite);
            //写入数据
 
            for ( List<Object> dataList:dataLists){
            	write(dataList, csvWrite);
            }
            csvWrite.flush();
        } catch (IOException e) {
            throw  new IOException("文件生成失败");
        } finally {
 
            try {
                csvWrite.close();
 
            } catch (IOException e) {
 
                throw  new IOException("关闭文件流失败");
            }
        }
 
        return csvFile;
    }
 

    /**
     * 在csv文件中增加一条记录
     * @param data
     * @return
     */
    public boolean writeOneLine(List<Object> data) {
    	File csvFile = null;
    	BufferedWriter csvWrite = null;
    	boolean retFlag = false;
    	
    	try {
    		csvFile = new File(getFilePath());
    		csvWrite = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile,true), "GB2312"));
    		write(data, csvWrite);
            csvWrite.flush();
            retFlag = true;
			} catch (Exception e) {
				retFlag = false;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					csvWrite.close();
				} catch (IOException e) {
					retFlag = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
  
		return retFlag;
	}
 
    /**
     * 将数据按行写入数据
     *
     * @param dataList
     * @param csvWreite
     * @throws IOException
     */
    private static void write(List<Object> dataList,BufferedWriter csvWreite) throws IOException {
    	StringBuffer buffer=new StringBuffer();
    	String rowStr="";
 
        for (Object data: dataList) {
            buffer.append("\"").append(data).append("\",");
        }
        rowStr = buffer.toString();
        csvWreite.write(rowStr);
        csvWreite.newLine();
    }

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
 
}

