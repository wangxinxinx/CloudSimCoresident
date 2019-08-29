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
     * ���filePath������ļ������ڣ��򴴽��ļ�
     * @return  �Ƿ񴴽��ɹ����ɹ��򷵻�true
     */
    private boolean createFile(){
        Boolean bool = false;
        File file = new File(getFilePath());
        
        try {
            //����ļ��Ѵ��ڣ�����ɾ���Ѵ��ڵ��ļ����ٴ����µ��ļ�
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
     * һ���������������ݴ���һ��CSV�ļ�
     * @param dataLists
     * @return
     */
 
    public static File createCSVFile( List<List<Object>> dataLists, List<Object>   headList,String csvFilePath) throws IOException {
 
        BufferedWriter csvWrite = null;
        File csvFile = null;
        
        try {
 
            //�����ļ�
            csvFile = new File(csvFilePath);
            //�����ļ�Ŀ¼
            File parent = csvFile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
 
            }
            
            //�����ļ�
            csvFile.createNewFile();
            csvWrite = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 32768);
 
            //д���ͷ
            write(headList, csvWrite);
            //д������
 
            for ( List<Object> dataList:dataLists){
            	write(dataList, csvWrite);
            }
            csvWrite.flush();
        } catch (IOException e) {
            throw  new IOException("�ļ�����ʧ��");
        } finally {
 
            try {
                csvWrite.close();
 
            } catch (IOException e) {
 
                throw  new IOException("�ر��ļ���ʧ��");
            }
        }
 
        return csvFile;
    }
 

    /**
     * ��csv�ļ�������һ����¼
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
     * �����ݰ���д������
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

