//客户端登录窗体类
package frame;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import thread.ClientAgentThread;

public class LoginFrame extends JFrame {
	private static final long serialVersionUID = 33284405318158218L;
	// 昵称是否重复
	public boolean haveTheName = false;
	// 是否已经连接成功
	public boolean isConnected = false;
	// 服务器IP Label
	public JLabel label_serverIP = new JLabel("服务器IP：");
	// 服务器IP TextField
	public JTextField textfield_serverIP = new JTextField("127.0.0.1");
	// 端口号Label
	public JLabel label_portNumber = new JLabel("端  口  号：");
	// 端口号TextField
	public JTextField textfield_portNumber = new JTextField("6788");
	// 昵称Label
	public JLabel label_nickName = new JLabel("昵        称：");
	// 昵称TextField
	public JTextField textfield_nickName = new JTextField("");
	// 连接服务器Button
	public JButton button_connectServer = new JButton("连接服务器");
	// 关闭程序Button
	public JButton button_closeProgram = new JButton("关闭程序");

	// 空参构造器
	public LoginFrame(String myNickName) {
		// 初始化组件
		initCompontents(myNickName);
		// 添加监听器
		addListeners();
		// 初始化窗体
		initFrame();
	}

	// 初始化组件
	public void initCompontents(String myNickName) {

		textfield_nickName.setText(myNickName);

		// 设置位置和大小
		label_serverIP.setBounds(30, 20, 70, 30);
		textfield_serverIP.setBounds(100, 20, 110, 30);
		label_portNumber.setBounds(30, 60, 70, 30);
		textfield_portNumber.setBounds(100, 60, 110, 30);
		label_nickName.setBounds(30, 100, 70, 30);
		textfield_nickName.setBounds(100, 100, 110, 30);
		button_connectServer.setBounds(45, 150, 150, 30);
		button_closeProgram.setBounds(45, 190, 150, 30);

		// 加入窗体
		this.add(label_serverIP);
		this.add(textfield_serverIP);
		this.add(label_portNumber);
		this.add(textfield_portNumber);
		this.add(label_nickName);
		this.add(textfield_nickName);
		this.add(button_connectServer);
		this.add(button_closeProgram);
	}

	// 添加监听器
	public void addListeners() {
		// 添加键盘监听：回车登录
		// ESC键退出
		textfield_serverIP.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
		textfield_portNumber.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
		textfield_nickName.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
		button_connectServer.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
		button_closeProgram.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});

		// 连接服务器按钮
		button_connectServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});

		// 关闭程序按钮
		button_closeProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	// 初始化窗体
	public void initFrame() {
		// 设置Title
		this.setTitle("请登录");
		// 设置登录Frame为null布局
		this.setLayout(null);
		// 拿到屏幕分辨率
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		// 显示在屏幕中央
		this.setBounds(screenWidth / 2 - 120, screenHeight / 2 - 134, 240, 268);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		// 默认选中昵称TextField
		textfield_nickName.requestFocus();
		textfield_nickName.selectAll();
	}

	// 登录方法
	public void login() {
		// 如果有为空的情况，警告并return
		if (textfield_serverIP.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请填写服务器IP！", "服务器IP错误",
					JOptionPane.ERROR_MESSAGE);
			// 选中服务器IP的TextField
			textfield_serverIP.requestFocus();
			textfield_serverIP.selectAll();
			return;
		} else if (textfield_portNumber.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请填写端口号！", "端口号错误",
					JOptionPane.ERROR_MESSAGE);
			// 选中端口号TextField
			textfield_portNumber.requestFocus();
			textfield_portNumber.selectAll();
			return;
		} else if (textfield_nickName.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请填写昵称！", "昵称错误",
					JOptionPane.ERROR_MESSAGE);
			// 选中昵称TextField
			textfield_nickName.requestFocus();
			textfield_nickName.selectAll();
			return;
		} else if (textfield_nickName.getText().trim().contains("|")) {
			JOptionPane.showMessageDialog(this, "昵称不能含有\"|\"", "昵称错误",
					JOptionPane.ERROR_MESSAGE);
			// 选中昵称TextField
			textfield_nickName.requestFocus();
			textfield_nickName.selectAll();
			return;
		} else if (textfield_nickName.getText().trim().equals("OPPONENT")) {
			JOptionPane.showMessageDialog(this, "昵称不能为\"OPPONENT\"", "昵称错误",
					JOptionPane.ERROR_MESSAGE);
			// 选中昵称TextField
			textfield_nickName.requestFocus();
			textfield_nickName.selectAll();
			return;
		}
		// 现在能说，三个TextField都不为空，那就连服务器试试
		ClientAgentThread clientAgentThread = new ClientAgentThread(this,
				textfield_nickName.getText().trim());
		// 先看是不是连接上了
		if (isConnected == true) {
			// 如果连接上了，并且有重名
			if (haveTheName == true) {
				JOptionPane.showMessageDialog(this, "已连接到服务器！\n昵称重复，重填再试！",
						"错误", JOptionPane.ERROR_MESSAGE);
				haveTheName = false;
				return;
			}
			// 客户端代理线程开始
			clientAgentThread.start();
			// 把登录Frame干掉
			LoginFrame.this.dispose();
			// 如果没连接上
		} else {
			JOptionPane.showMessageDialog(this, "连接服务器失败！", "服务器连接错误",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// 主方法
	public static void main(String[] args) {
		new LoginFrame("player_01");
	}
}
