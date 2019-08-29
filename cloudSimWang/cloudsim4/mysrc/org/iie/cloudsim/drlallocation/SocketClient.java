package org.iie.cloudsim.drlallocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class SocketClient {

    public static void main(String args[])throws Exception {

        try {
            Socket socket = new Socket("localhost",8001);

            //��ȡ���������������˷�����Ϣ
            OutputStream os=socket.getOutputStream();//�ֽ������
            PrintWriter pw=new PrintWriter(os);//���������װΪ��ӡ��
            pw.write("����Java������");
            pw.flush();
            socket.shutdownOutput();//�ر������

            InputStream is=socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String info=null;
            while((info=in.readLine())!=null){
                System.out.println("���ǿͻ��ˣ�Python������˵��"+info);
            }
            is.close();
            in.close();
            socket.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}