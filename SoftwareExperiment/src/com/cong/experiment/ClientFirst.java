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
				Socket sock = ss.accept(); // 接受连接请求
				DataInputStream din = new DataInputStream(sock.getInputStream());
				byte buffer[] = new byte[1024]; // 缓冲区
				int len = din.read(buffer);
				String filename = new String(buffer, 0, len); // 提取出想要获取的文件名
				System.out.println("Client01: received a file request from" + sock.getInetAddress()+":"+sock.getPort()+",filename:"+ filename);
				DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
				FileInputStream fis = new FileInputStream(new File("D:/networkExperiment/experiment04/host01/" + filename));
				byte b[] = new byte[1024];
				fis.read(b); // 从文件中将数据写入缓冲区
				dout.write(b); // 将缓冲区中的数据返回
				System.out.println("Client01: return file successfully!");
				dout.close();
				sock.close();
			} catch (IOException e) {
			}
		}
	}

	public static void request() throws Exception, IOException {
		Socket socket = new Socket("127.0.0.1", 8889); // 创建连接目录服务器的套接字
		DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
		String key = "c.txt";
		dout.write(key.getBytes()); // 发送需要获取的文件名
		System.out.println("Client01: requset file:"+key);
		// dout.close();

		InputStream input = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String strFromDirectoryServer;
		strFromDirectoryServer = reader.readLine(); // 从目录服务器接受数据，即：包含这个文件的主机的IP
		input.close();
		// 如果不存在这个文件
		if(strFromDirectoryServer.equals("0")){
			System.out.println("Client01: not exist file:"+ key);
			System.exit(0); // 说明当前网络中不存在这个文件
		}
		
		System.out.println("Client01: the file in "+strFromDirectoryServer);
		socket.close();
		
		// 从包含该文件的主机获取文件
		socket = new Socket(strFromDirectoryServer, 9001); // 因为这是在同一台的主机上进行实验，所以通过不同的端口号进行区分
		OutputStream outstream = socket.getOutputStream();
		outstream.write(key.getBytes()); // 将需要获取的文件名发送给主机
		input = socket.getInputStream();
		FileOutputStream fos = new FileOutputStream(new File("D:/networkExperiment/experiment04/host01/recv-" + key));
		int tmp = 0;
		while ((tmp = input.read()) != -1) { // 将接受的数据写入自己的目录中
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
