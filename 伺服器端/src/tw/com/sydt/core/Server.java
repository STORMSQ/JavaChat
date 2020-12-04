package tw.com.sydt.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

import tw.com.sydt.model.Info;
import tw.com.sydt.model.SendType;
import tw.com.sydt.model.User;
import tw.com.sydt.util.DateFormatUtil;

public class Server {

	private boolean outFlag= true;
	private static Vector<User> users = new UserList().getList(); //可以登入的帳號清單
	private Vector<UserThread> userThreadList = new Vector<UserThread>(); 
	private ExecutorService es = Executors.newFixedThreadPool(100);
	private DefaultListModel<String> listModel;
	private JTextArea JTextArea;
	
	
	public static User getAdmin()
	{
		return users.get(0);
	}
	
	public void start(final DefaultListModel<String> listModel,final JTextArea JTextArea)
	{
		ServerSocket socket;
		try {
			socket = new ServerSocket(8081);
			XStream xst = new XStream(new Xpp3Driver());
			this.listModel = listModel;
			this.JTextArea = JTextArea;
			while(outFlag) {
				Socket client= socket.accept();
				
				DataInputStream validateInput = new DataInputStream(client.getInputStream());
				DataOutputStream validateOutput = new DataOutputStream(client.getOutputStream());
				
				User getLoginInfo = (User)xst.fromXML(validateInput.readUTF());
				
				User userModel=null;
				for (User user : Server.users) {
					
					if(user.getName().equals(getLoginInfo.getName())) {
						
						userModel=user;
						for (UserThread userThread : userThreadList) {
							if(userThread.currentUser.getUserId()==userModel.getUserId()) {
								userModel=new User();
								userModel.setLogin(true);
							}
						}
					}
				}
				if(userModel!=null && !userModel.isLogin()) {
					validateOutput.writeUTF(xst.toXML(userModel));
					UserThread thread = new UserThread(client,userModel);
					userThreadList.add(thread);
					es.execute(thread);
				}else {
					validateOutput.writeUTF(xst.toXML(userModel));
				}
				
				
			}

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void sendMessage(Object toUser,String message)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Info info = new Info();
				info.setType(SendType.MESSAGE);
				if("所有人".equals(toUser.toString())) {
					info.setTo(null);
				}else {
					for (User user:users) {
						if(toUser.toString().equals(user.getName())) {
							info.setTo(user);
						}
					}
					
				}
				
				info.setTimestamp(DateFormatUtil.getDateTime(new Date()));
				info.setFrom(Server.getAdmin());
				info.setContent(Server.getAdmin().getName()+" Say: "+message+"\n");
				
				
				for (UserThread userThread : userThreadList) {
					if(info.getTo()==null) {
						userThread.send(info);
					}else {
						if(userThread.getCurrentUser().equals(info.getTo())) {
							userThread.send(info);
						}
					}
				}
				
			}
		}).start();
	}
	class UserThread implements Runnable{

		public User currentUser;
		private Socket client;
		private DataInputStream in =null;
		private DataOutputStream out = null;
		private XStream xst = new XStream(new Xpp3DomDriver());
		private boolean flag = true;
		public UserThread(Socket client,User currentUser)
		{
			this.currentUser = currentUser;
			this.client = client;
			try {
				in = new DataInputStream(client.getInputStream());
				out = new DataOutputStream(client.getOutputStream());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public User getCurrentUser() {
			return currentUser;
		}

		public void send(Info info)
		{
			try {
				out.writeUTF(xst.toXML(info));
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			
			while(flag) {
				
				try {
					String messageXML = in.readUTF();
					Info fromMessage = (Info)xst.fromXML(messageXML);
					
					
					switch(fromMessage.getType()) {
					
						case LOGIN: 
							Info info = new Info();
							info.setFrom(Server.getAdmin());
							info.setTo(currentUser);
							info.setContent("歡迎你 "+currentUser.getName());
							info.setType(SendType.WELCOME);
							info.setTimestamp(DateFormatUtil.getDateTime(new Date()));
							
							String output = xst.toXML(info);
							out.writeUTF(output);
							out.flush();
							JTextArea.setText(JTextArea.getText()+currentUser.getName()+" 已上線 \n");
							listModel.addElement(currentUser.getName());
							StringBuilder sb = new StringBuilder();
							for (UserThread userThread : userThreadList) {
								if(userThread.currentUser.getUserId()!=currentUser.getUserId()) {
									Info sendInfo = new Info();
									
									sendInfo.setFrom(Server.getAdmin());
									sendInfo.setType(SendType.ADDUSER);
									sendInfo.setTo(currentUser);
									sendInfo.setTimestamp(DateFormatUtil.getDateTime(new Date()));
									sendInfo.setContent(currentUser.getName()+"已上線");
									sb.append(userThread.currentUser.getName()+"%");
									userThread.out.writeUTF(xst.toXML(sendInfo));
									userThread.out.flush();
								}
								
							}
							Info loadInfo = new Info();
							loadInfo.setType(SendType.LOADUSER);
							loadInfo.setContent(sb.toString());
							
							out.writeUTF(xst.toXML(loadInfo));
							out.flush();
							
						break;
						case SEND:
							for (UserThread userThread : userThreadList) {
								Info sendInfo = new Info();
								sendInfo.setType(SendType.MESSAGE);
								sendInfo.setTo(userThread.currentUser);
								sendInfo.setTimestamp(fromMessage.getTimestamp());
								sendInfo.setContent(fromMessage.getContent());
								if(fromMessage.getTo()==null) {
									if(userThread.currentUser.getUserId()!=currentUser.getUserId()) {
														
										sendInfo.setFrom(currentUser);
										userThread.out.writeUTF(xst.toXML(sendInfo));
										userThread.out.flush();
									}
								}else {
									if(userThread.currentUser.getName().equals(fromMessage.getTo().getName())) {
										
										sendInfo.setFrom(fromMessage.getFrom());
										userThread.out.writeUTF(xst.toXML(sendInfo));
										userThread.out.flush();
									}
								}
								
								
							}
							
						break;
						case DELUSER:

							UserThread exitUser = null;
							for (UserThread userThread : userThreadList) {
								if(currentUser.equals(userThread.currentUser)) {
									exitUser = userThread;
								}else {
									Info sendInfo = new Info();
									sendInfo.setType(SendType.EXIT);
									sendInfo.setContent(fromMessage.getFrom().getName()+"已離線\n");
									sendInfo.setFrom(currentUser);
									
									userThread.out.writeUTF(xst.toXML(sendInfo));
									userThread.out.flush();
									
								}
								
							}
							
							JTextArea.setText(JTextArea.getText()+fromMessage.getFrom().getName()+" ���u�F\n");
							listModel.removeElement(exitUser.currentUser.getName());
							flag=false;
							userThreadList.remove(exitUser);
							break;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
}


