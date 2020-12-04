package tw.com.sydt.core;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import tw.com.sydt.model.User;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginUI extends JFrame {

	private JPanel contentPane;
	private JTextField username;
	private JPasswordField password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginUI frame = new LoginUI();
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
	public LoginUI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 377, 194);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 1, 0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		
		JLabel label = new JLabel("\u4F7F\u7528\u8005\u5E33\u865F");
		panel.add(label);
		
		username = new JTextField();

		panel.add(username);
		username.setColumns(20);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		
		JLabel label_1 = new JLabel("\u4F7F\u7528\u8005\u5BC6\u78BC");
		panel_1.add(label_1);
		
		password = new JPasswordField();
		password.setColumns(20);
		panel_1.add(password);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);
		
		JButton login = new JButton("\u767B\u5165");
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Client client = new Client();
				User validate = client.login(username.getText());
				if(validate!=null && !validate.isLogin()) {

					new ClientUI(client,validate);
					LoginUI.this.dispose();
				}else if(validate==null){
					JOptionPane.showMessageDialog(LoginUI.this, "登入失敗");
				}else {
					JOptionPane.showMessageDialog(LoginUI.this, "發生錯誤");
				}
			}
		});
		panel_2.add(login);
	}

}
