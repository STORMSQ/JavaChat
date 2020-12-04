package tw.com.sydt.core;


import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerUI extends JFrame {

	private JPanel contentPane;
	private JTextField info;
	private DefaultListModel<String> listModel=null;
	private JList list;
	private JTextArea message;
	private Server server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerUI frame = new ServerUI();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public ServerUI() {
		setTitle("Sydt\u804A\u5929\u7A0B\u5F0F\u4F3A\u670D\u5668");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 530);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JLabel label = new JLabel("\u8A0A\u606F\u4E00\u89BD");
		contentPane.add(label, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel label_1 = new JLabel("--\u5728\u7DDA\u7528\u6236--");
		panel.add(label_1, BorderLayout.NORTH);
		
		JScrollPane userList = new JScrollPane();
		userList.setAutoscrolls(true);
		panel.add(userList, BorderLayout.CENTER);
		
		JList<String> list = new JList<String>();
		listModel = new DefaultListModel<String>();
		listModel.addElement("所有人");
		list.setModel(listModel);
		this.list = list;
		userList.setViewportView(list);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		info = new JTextField();
		panel_1.add(info, BorderLayout.CENTER);
		info.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u8F38\u5165");
		panel_1.add(lblNewLabel, BorderLayout.WEST);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JTextArea message = new JTextArea();
		
		this.message = message;
		
		scrollPane.setViewportView(message);
		
		JButton submit = new JButton("\u767C\u9001\u8A0A\u606F");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("".equals(info.getText())) {
					JOptionPane.showMessageDialog(ServerUI.this, "請輸入訊息");
				}else {
					Object select = ServerUI.this.list.getSelectedValue();
					if(select == null) {
						JOptionPane.showMessageDialog(ServerUI.this, "請選擇聊天對象");
						return ;
					}else {
						server.sendMessage(select,info.getText());
						message.setText(message.getText()+"您 說: "+info.getText()+"\n");
						info.setText(null);
					}
				}
			}
		});
		panel_1.add(submit, BorderLayout.EAST);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				server =new Server();
				server.start(listModel,message);
			}
		}).start();
	}

}
