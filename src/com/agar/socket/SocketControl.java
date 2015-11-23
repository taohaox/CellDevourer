package com.agar.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.agar.activity.BattlegroundView2SurfaceView;
import com.alibaba.fastjson.JSONObject;

public class SocketControl implements Runnable{

	public static final String SERVER_IP = "192.168.1.2";
	public static final int PORT = 12345;
	private DatagramSocket socket;
	private InetAddress serverAddress;
	private static SocketControl mSocketControl = new SocketControl();
	/**
	 * 处理服务器发来的消息
	 * @author Gongyb 下午3:27:04
	 *
	 */
	public interface GetMsgHandler{
		public void handlerMsg(String data);
	} 
	private GetMsgHandler handler;
	
	public GetMsgHandler getHandler() {
		return handler;
	}

	public void setHandler(GetMsgHandler handler) {
		this.handler = handler;
	}

	private SocketControl() {
		try {
			socket = new DatagramSocket(PORT);
			serverAddress = InetAddress.getByName(SERVER_IP);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}

	public static SocketControl getInstance() {
		return mSocketControl;
	}

	/**
	 * 发送信息到服务器
	 * 
	 * @param msg
	 */
	public void sendMsgToServer(final String msg,final int code) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					JSONObject json = new JSONObject();
					json.put("status", code);
					json.put("data",msg);
					byte data[] = json.toJSONString().getBytes("UTF-8");
					// 将数据打包
					DatagramPacket packet = new DatagramPacket(data, data.length,serverAddress, PORT);
					// 利用socket发送数据到服务器
					socket.send(packet);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}

	/**
	 * 从服务器读取信息
	 * 
	 * @return
	 */
	public void getMsgByServer() {
		try {
			socket.setBroadcast(true);
			byte data[] = new byte[2048];
			DatagramPacket packet = new DatagramPacket(data,data.length,serverAddress,PORT);
			while(BattlegroundView2SurfaceView.isLife){
				// 使用receive方法接收服务端所发送的数据，
				// 如果服务端没有发送数据，该进程就停滞在这里
				Log.d("UDP Demo", "准备接受");
				socket.receive(packet);
				String result = new	String(packet.getData(), packet.getOffset(),packet.getLength());
				Log.d("UDP Demo", packet.getAddress().getHostAddress().toString()+ ":" +result );
				if(handler!=null){
					handler.handlerMsg(result);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(BattlegroundView2SurfaceView.isLife){
			getMsgByServer();
		}
	}
}
