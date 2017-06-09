package com.cong.experiment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DirectoryServer implements Runnable{
	private ServerSocket serverSocket;
	private HashMap<String,String> fileAndIP;
	public DirectoryServer(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
		fileAndIP = new HashMap<String, String>();
		fileAndIP.put("a.txt", "127.0.0.1"); // ����ͬ���ļ��ŵ���ͬ�Ļ�����,��������ͬһ̨�����Ͻ��в���
		fileAndIP.put("b.txt", "127.0.0.1");
		//fileAndIP.put("c.txt", "127.0.0.1");
		fileAndIP.put("d.txt", "127.0.0.1");
	}
	
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				System.out.println(); // ����
				Socket socketClient =  serverSocket.accept(); // ���ܿͻ�����������
				DataInputStream din = new DataInputStream(socketClient.getInputStream());
				byte buffer[] = new byte[1024]; // ���뻺����
				
				int tmp = din.read(buffer); // ��������
				String key = new String(buffer,0,tmp); // �����ͻ���������ļ���
				System.out.println("DirectoryServer: received a request from:" + socketClient.getInetAddress()+":"+socketClient.getPort());
				if(fileAndIP.containsKey(key)){ // ���Ŀ¼�������д�������ļ�
					DataOutputStream dout = new DataOutputStream(socketClient.getOutputStream());
					System.out.println("DirectoryServer: the client need file:"+key+"["+fileAndIP.get(key)+"]");
					dout.write(fileAndIP.get(key).getBytes()); // ����������ļ���IP��ַ���ظ��ͻ���
					dout.close();
				}
				else{ // �������������ļ�
					DataOutputStream dout = new DataOutputStream(socketClient.getOutputStream());
					String str = "DirectoryServer: There is no this file here:" + key;
					System.out.println(str);
					dout.write("0".getBytes()); // ����0����ʾû������ļ�
					dout.close();
				}
			} catch (IOException e) {
			}
		}
	}
	public static void main(String[] args) throws IOException{
		DirectoryServer serverDirectory = new DirectoryServer(8889); // Ŀ¼�������Ķ˿ں�Ϊ8889
		ClientFirst client00 = new ClientFirst(9000); // ����00�Ķ˿ں�Ϊ��9000
		ClientZero client01 = new ClientZero(9001); // ����01�Ķ˿ں�Ϊ��9001
		new Thread(serverDirectory).start(); // ����Ŀ¼������
		System.out.println("DirectoryServer: started...");
		new Thread(client00).start(); // ��������00���ļ�����
		new Thread(client01).start(); // ��������01���ļ�����
	}
}
