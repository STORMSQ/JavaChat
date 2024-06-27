package tw.com.stormsq.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.DefaultListModel;
import javax.swing.JTextPane;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

import tw.com.stormsq.model.Info;
import tw.com.stormsq.model.SendType;
import tw.com.stormsq.model.User;
//import tw.com.sydt.server.Server;
import tw.com.stormsq.util.DateFormatUtil;

public class Client {

	private User currentUser;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private boolean flag=true;
	private XStream xst;
	private DefaultListModel<String> listModel;
	private JTextPane textField;
	public Client()
	{
		
	}
	public User getCurrentUser()
	{
		return currentUser;
	}
	public User login(String username)
	{
		xst = new XStream(new Xpp3DomDriver());		
		FutureTask<User> ft = new FutureTask<User>(new Callable<User>() {		

			@Override
			public User call() throws Exception {

				socket = new Socket("127.0.0.1",8081);
				
				new DataOutputStream(socket.getOutputStream()).writeUTF(xst.toXML(new User(username)));
				User userModel = (User)xst.fromXML(new DataInputStream(socket.getInputStream()).readUTF());
				return userModel;
		
			}
			
		}); 
		
		new Thread(ft).start();
			
		try {
			System.out.println(ft.get());
			return ft.get();
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	public void start(User user,final DefaultListModel<String> listModel,final JTextPane textField)
	{
		currentUser = user;
		this.listModel = listModel;
		this.textField = textField;
		new Thread(new Runnable() {
			public void run() {
			
			try {

				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				
				Info init = new Info();
				init.setType(SendType.LOGIN);
				init.setFrom(currentUser);
				//init.setTo(Server.getAdmin());
				init.setTimestamp(DateFormatUtil.getDateTime(new Date()));
				
				
				out.writeUTF(xst.toXML(init));
				out.flush();
				
				Thread t = new Thread(new userThread(in));
				t.setDaemon(true);
				t.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		}).start();
	}
	public boolean end()
	{
		Info info = new Info();
		info.setType(SendType.DELUSER);
		info.setContent(currentUser.getName()+"已離線");
		info.setFrom(currentUser);
		
		try {
			out.writeUTF(xst.toXML(info));
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	
	public void sendMessage(Object toUser,String message)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Info info = new Info();
				info.setType(SendType.SEND);
				if("所有人".equals(toUser.toString())) {
					info.setTo(null);
				}else {
					info.setTo(new User(toUser.toString()));
				}
				
				info.setTimestamp(DateFormatUtil.getDateTime(new Date()));
				info.setFrom(currentUser);
				info.setContent(currentUser.getName()+" 說: "+message+"\n");
				
				try {
					out.writeUTF(xst.toXML(info));
					out.flush();
					textField.setText(textField.getText()+"說: "+message+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
		}).start();
	}
	public void setFlag(boolean flag)
	{
		this.flag=flag;
	}
	
	class userThread implements Runnable{
		
		XStream xst = new XStream(new Xpp3Driver());
		
		private DataInputStream in;
		public userThread(DataInputStream in) {
			super();
			// TODO Auto-generated constructor stub
			this.in = in;
		}


		@SuppressWarnings("incomplete-switch")
		@Override
		public void run() {
			
			try {
				while(flag) {
					String message = this.in.readUTF();
					Info messageXML = (Info)xst.fromXML(message);
					switch(messageXML.getType()) {
					
						case WELCOME:
							textField.setText("歡迎您 "+messageXML.getTo().getName()+'\n');
							break;
						case ADDUSER:
							textField.setText(textField.getText()+messageXML.getContent()+'\n');
							listModel.addElement(messageXML.getTo().getName());
							break;
						case LOADUSER:
							
							String[] users = messageXML.getContent().split("%");
							for(String user:users) {
								listModel.addElement(user);
							}
							break;
						case MESSAGE:
							textField.setText(textField.getText()+messageXML.getContent());
							break;
						case EXIT:
							
							textField.setText(textField.getText()+messageXML.getContent());
							listModel.removeElement(messageXML.getFrom().getName());
							break;
						
							
					}
					
				}
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
