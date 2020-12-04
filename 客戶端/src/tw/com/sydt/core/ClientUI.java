package tw.com.sydt.core;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tw.com.sydt.model.Info;
import tw.com.sydt.model.SendType;
import tw.com.sydt.model.User;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientUI extends JFrame {

	private JPanel contentPane;
	private JTextField inputField;
	private Client client;
	private DefaultListModel<String> listModel=null;
	private JList<String> list;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUI frame = new ClientUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	

	/**
	 * Create the frame.
	 */
	public ClientUI(Client client,User user) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.end();
				
				client.setFlag(false);
				//ClientUI.this.dispose();
			}
		});
		
		setTitle("Sydt\u7528\u6236\u7AEF");
		this.client = client;
		
		
		setVisible(true);
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
		panel.add(userList, BorderLayout.CENTER);
		
		JList list = new JList();
		this.list = list;
		listModel = new DefaultListModel<String>();
		listModel.addElement("所有人");
		this.list.setModel(listModel);
		userList.setViewportView(this.list);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		inputField = new JTextField();
		panel_1.add(inputField, BorderLayout.CENTER);
		inputField.setColumns(10);
		
		JButton submit = new JButton("\u767C\u9001\u8A0A\u606F");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("".equals(inputField.getText())) {
					JOptionPane.showMessageDialog(ClientUI.this, "請輸入訊息");
				}else {
					Object select =ClientUI.this.list.getSelectedValue();
					if(select == null) {
						JOptionPane.showMessageDialog(ClientUI.this, "請選擇聊天對象");
						return ;
					}else {
						client.sendMessage(select,inputField.getText());
						inputField.setText(null);
					}
				}

		
			}
		});
		panel_1.add(submit, BorderLayout.EAST);
		
		JLabel lblNewLabel = new JLabel("\u8F38\u5165");
		panel_1.add(lblNewLabel, BorderLayout.WEST);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JTextPane showMessage = new JTextPane();
		scrollPane.setViewportView(showMessage);
		
		client.start(user,listModel,showMessage);
	}

}
