package com.miteke.mt4.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class SocketHandler {
	private static Logger logger = Logger.getLogger(SocketHandler.class.getName());
	
	public static String sendData(String host, int port, String data)
			throws UnknownHostException, IOException {
		Socket client = new Socket(host, port);
		PrintWriter os = new PrintWriter(client.getOutputStream());
		BufferedReader is = new BufferedReader(new InputStreamReader(
				client.getInputStream()));

		os.print(data);
		os.flush();
		
		logger.info(host+":"+data);

		char[] buffer = new char[1024];

		StringBuffer sb = new StringBuffer();
		int len = 0;

		while ((len = is.read(buffer, 0, buffer.length)) > 0) {
			sb.append(buffer, 0, len);
		}
		os.close();
		is.close();
		client.close(); // close Socket
		return sb.toString();
	}
	
//	public static void main(String argv[]) {
//		try {
//			String content = SocketHandler.sendData("54.250.124.129", 444,
//					"WWAPUSER-1013|a123456\r\nQUIT\r\n");
//			System.out.println(content);
//			
//			content = SocketHandler.sendData("54.250.124.129", 444,
//					"Wver=123457&login=1013&webTrade=160&cmd=0&volume=0.1&price=1&symbol=EURUSD&type=66\r\nQUIT\r\n");
//			System.out.println(content);
//			
//			String order=content.substring(content.indexOf("order")).substring(
//					content.substring(content.indexOf("order")).indexOf('=')+1,
//					content.substring(content.indexOf("order")).indexOf('&'));
//			content = SocketHandler.sendData("54.250.124.129", 444,
//					"Wver=123457&login=1013&webTrade=160&order=" + order +
//					"&cmd=1&volume=0.1&price=1&symbol=EURUSD&type=70\r\nQUIT\r\n");
//			System.out.println(content);
//			
//			String quoteRes = SocketHandler.sendData("54.250.124.129", 444,
//					"WQUOTES-EURUSD,GBPUSD,USDJPY,AUDUSD,USDCAD,\nQUIT\n");
//			System.out.println(quoteRes);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

}
