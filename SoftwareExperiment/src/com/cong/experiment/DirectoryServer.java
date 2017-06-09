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
		fileAndIP.put("a.txt", "127.0.0.1"); // 将不同的文件放到不同的机器上,这里是在同一台机器上进行操作
		fileAndIP.put("b.txt", "127.0.0.1");
		//fileAndIP.put("c.txt", "127.0.0.1");
		fileAndIP.put("d.txt", "127.0.0.1");
	}
	
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				System.out.println(); // 换行
				Socket socketClient =  serverSocket.accept(); // 接受客户端连接请求
				DataInputStream din = new DataInputStream(socketClient.getInputStream());
				byte buffer[] = new byte[1024]; // 输入缓冲区
				
				int tmp = din.read(buffer); // 接收数据
				String key = new String(buffer,0,tmp); // 解析客户端请求的文件名
				System.out.println("DirectoryServer: received a request from:" + socketClient.getInetAddress()+":"+socketClient.getPort());
				if(fileAndIP.containsKey(key)){ // 如果目录服务器中存在这个文件
					DataOutputStream dout = new DataOutputStream(socketClient.getOutputStream());
					System.out.println("DirectoryServer: the client need file:"+key+"["+fileAndIP.get(key)+"]");
					dout.write(fileAndIP.get(key).getBytes()); // 将包含这个文件的IP地址返回给客户端
					dout.close();
				}
				else{ // 如果不存在这个文件
					DataOutputStream dout = new DataOutputStream(socketClient.getOutputStream());
					String str = "DirectoryServer: There is no this file here:" + key;
					System.out.println(str);
					dout.write("0".getBytes()); // 返回0，表示没有这个文件
					dout.close();
				}
			} catch (IOException e) {
			}
		}
	}
	public static void main(String[] args) throws IOException{
		DirectoryServer serverDirectory = new DirectoryServer(8889); // 目录服务器的端口号为8889
		ClientFirst client00 = new ClientFirst(9000); // 主机00的端口号为：9000
		ClientZero client01 = new ClientZero(9001); // 主机01的端口号为：9001
		new Thread(serverDirectory).start(); // 开启目录服务器
		System.out.println("DirectoryServer: started...");
		new Thread(client00).start(); // 开启主机00的文件服务
		new Thread(client01).start(); // 开启主机01的文件服务
	}
}
