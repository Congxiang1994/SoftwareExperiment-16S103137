package com.cong.experiment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientFirst implements Runnable {
	private ServerSocket ss;

	public ClientFirst(int port) throws IOException {
		ss = new ServerSocket(port);
		ss.setSoTimeout(10000);
	}

	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Socket sock = ss.accept(); // ������������
				DataInputStream din = new DataInputStream(sock.getInputStream());
				byte buffer[] = new byte[1024]; // ������
				int len = din.read(buffer);
				String filename = new String(buffer, 0, len); // ��ȡ����Ҫ��ȡ���ļ���
				System.out.println("Client01: received a file request from" + sock.getInetAddress()+":"+sock.getPort()+",filename:"+ filename);
				DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
				FileInputStream fis = new FileInputStream(new File("D:/networkExperiment/experiment04/host01/" + filename));
				byte b[] = new byte[1024];
				fis.read(b); // ���ļ��н�����д�뻺����
				dout.write(b); // ���������е����ݷ���
				System.out.println("Client01: return file successfully!");
				dout.close();
				sock.close();
			} catch (IOException e) {
			}
		}
	}

	public static void request() throws Exception, IOException {
		Socket socket = new Socket("127.0.0.1", 8889); // ��������Ŀ¼���������׽���
		DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
		String key = "c.txt";
		dout.write(key.getBytes()); // ������Ҫ��ȡ���ļ���
		System.out.println("Client01: requset file:"+key);
		// dout.close();

		InputStream input = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String strFromDirectoryServer;
		strFromDirectoryServer = reader.readLine(); // ��Ŀ¼�������������ݣ�������������ļ���������IP
		input.close();
		// �������������ļ�
		if(strFromDirectoryServer.equals("0")){
			System.out.println("Client01: not exist file:"+ key);
			System.exit(0); // ˵����ǰ�����в���������ļ�
		}
		
		System.out.println("Client01: the file in "+strFromDirectoryServer);
		socket.close();
		
		// �Ӱ������ļ���������ȡ�ļ�
		socket = new Socket(strFromDirectoryServer, 9001); // ��Ϊ������ͬһ̨�������Ͻ���ʵ�飬����ͨ����ͬ�Ķ˿ںŽ�������
		OutputStream outstream = socket.getOutputStream();
		outstream.write(key.getBytes()); // ����Ҫ��ȡ���ļ������͸�����
		input = socket.getInputStream();
		FileOutputStream fos = new FileOutputStream(new File("D:/networkExperiment/experiment04/host01/recv-" + key));
		int tmp = 0;
		while ((tmp = input.read()) != -1) { // �����ܵ�����д���Լ���Ŀ¼��
			fos.write(tmp);
		}
		System.out.println("Client01: get file:"+key +" successfully!");
		fos.close();
		outstream.close();
		socket.close();
	}

	public static void main(String[] args) throws IOException, Exception {
		ClientFirst.request();
	}

}
