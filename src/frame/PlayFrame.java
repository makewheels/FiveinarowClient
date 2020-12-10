//客户端棋盘窗体类
package frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import thread.ClientAgentThread;
import thread.CurrentTimeThread;
import chess.ChessBoard;

public class PlayFrame extends JFrame {
	private static final long serialVersionUID = 9007397968169427929L;

	// 我的昵称
	public String myNickName;
	// 申请游戏Button
	public JButton button_applyGame = new JButton("申请游戏");
	// 认输Button
	public JButton button_giveUp = new JButton("认输");
	// 断开Button
	public JButton button_disconnect = new JButton("断开连接");
	// 关闭程序Button
	public JButton button_closeProgram = new JButton("关闭程序");
	// 倒计时Label
	public JLabel label_countDown = new JLabel();
	// 总时长Label
	public JLabel label_totalTime = new JLabel();
	// 在线用户列表Label
	public JLabel label_userOnline = new JLabel("在线用户列表：");
	// 聊天Label
	public JLabel label_chat = new JLabel("聊天：");
	// 聊天TextArea
	public JTextArea textarea_chat = new JTextArea();
	// 给聊天TextArea加滚动条
	public JScrollPane scrollpane_chat = new JScrollPane(textarea_chat);
	// 得到TextArea竖直方向滚动条的滑块
	public JScrollBar scrollbar_chat = scrollpane_chat.getVerticalScrollBar();
	// 聊天输入TextField
	public JTextField textfield_chatInput = new JTextField();
	// 清空聊天记录按钮
	public JButton button_clearTextArea = new JButton("清空记录");
	// 发送聊天信息给对手按钮
	public JButton button_sendToOpponent = new JButton("发送给对手");
	// 发送给列表中的指定人
	public JButton button_sendToSomeone = new JButton("发送给列表中的指定人");
	// 左Panel：棋盘
	public ChessBoard panel_chessBoard = new ChessBoard(this);
	// 右Panel：各种操作的面板
	public JPanel panel_right = new JPanel();
	// 在线用户列表
	public JList<String> list_userOnline = new JList<String>();
	// 给列表加滚动条
	public JScrollPane scrollpane_userOnline = new JScrollPane(list_userOnline);
	// 当前时间Label
	public JLabel label_currentTime = new JLabel();
	// 分割Panel
	public JSplitPane splitpane_split = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT, panel_chessBoard, panel_right);
	// 什么颜色，黑色是true，白色是false
	public Color myColor;
	// ClientAgentThread
	public ClientAgentThread clientAgentThread;

	// 空参构造器
	public PlayFrame(String myNickName) {
		// 初始化我的昵称
		this.myNickName = myNickName;

		// 创建当前时间线程
		CurrentTimeThread currentTimeThread = new CurrentTimeThread(
				label_currentTime);
		// 启动当前时间线程
		currentTimeThread.start();

		// 初始化组件
		initComponents();

		// 添加监听器
		addListeners();

		// 初始化窗体
		initFrame();
	}

	// 初始化组件
	public void initComponents() {
		// 右Panel采用null布局
		panel_right.setLayout(null);

		// 设置位置和大小
		button_applyGame.setBounds(15, 15, 100, 30);
		button_giveUp.setBounds(125, 15, 100, 30);
		button_disconnect.setBounds(15, 60, 100, 30);
		button_closeProgram.setBounds(125, 60, 100, 30);
		label_countDown.setBounds(30, 120, 200, 30);
		label_totalTime.setBounds(40, 180, 150, 25);
		label_userOnline.setBounds(30, 220, 100, 30);
		scrollpane_userOnline.setBounds(15, 250, 210, 120);
		label_chat.setBounds(30, 370, 100, 30);
		scrollpane_chat.setBounds(15, 400, 210, 120);
		textfield_chatInput.setBounds(15, 525, 210, 30);
		button_clearTextArea.setBounds(15, 560, 100, 30);
		button_sendToOpponent.setBounds(125, 560, 100, 30);
		button_sendToSomeone.setBounds(15, 595, 210, 30);
		label_currentTime.setBounds(45, 633, 200, 30);

		// 倒计时字体
		Font font_countDown = new Font("宋体", Font.BOLD, 30);
		label_countDown.setFont(font_countDown);
		label_countDown.setForeground(Color.RED);
		// 总时长字体
		Font font_totalTime = new Font("黑体", Font.PLAIN, 20);
		label_totalTime.setFont(font_totalTime);
		// 当前时间字体
		Font font_currentTime = new Font("黑体", Font.PLAIN, 30);
		label_currentTime.setFont(font_currentTime);

		// 设置聊天输入框的背景色
		textfield_chatInput.setBackground(Color.YELLOW);

		// 设置可用性
		button_giveUp.setEnabled(false);
		button_sendToOpponent.setEnabled(false);
		textarea_chat.setEditable(false);

		// 设置List为单选
		list_userOnline.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// 各种组件加入右Panel
		panel_right.add(button_applyGame);
		panel_right.add(button_giveUp);
		panel_right.add(button_disconnect);
		panel_right.add(button_closeProgram);
		panel_right.add(label_countDown);
		panel_right.add(label_totalTime);
		panel_right.add(label_userOnline);
		panel_right.add(scrollpane_userOnline);
		panel_right.add(label_chat);
		panel_right.add(scrollpane_chat);
		panel_right.add(textfield_chatInput);
		panel_right.add(button_clearTextArea);
		panel_right.add(button_sendToOpponent);
		panel_right.add(button_sendToSomeone);
		panel_right.add(label_currentTime);
	}

	// 添加监听器
	public void addListeners() {
		// 申请游戏Button
		button_applyGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientAgentThread.sendApplyGame();
			}
		});

		// 认输Button
		button_giveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientAgentThread.sendGiveUp();
			}
		});

		// 断开连接Button
		button_disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientAgentThread.clientLeave();
			}
		});

		// 关闭程序Button
		button_closeProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientAgentThread.closeProgram();
			}
		});

		// 点窗口右上角红叉时关闭程序
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				clientAgentThread.closeProgram();
			}
		});

		// 聊天输入框监听回车键
		textfield_chatInput.addKeyListener(new KeyAdapter() {
			// 当键盘有按键按下时
			public void keyPressed(KeyEvent e) {
				// 如果是回车键
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// 回车键是个列表中的人发消息，不是对手
					clientAgentThread.sendChatToSomeone();
				}
			}
		});

		// 清空聊天记录按钮监听
		button_clearTextArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 清空聊天记录
				textarea_chat.setText("");
				// 清空输入框
				textfield_chatInput.setText("");
			}
		});

		// 发送聊天信息给对手按钮监听
		button_sendToOpponent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientAgentThread.sendChatToOpponent();
			}
		});

		// 发送聊天信息给指定人按钮监听
		button_sendToSomeone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientAgentThread.sendChatToSomeone();
			}
		});
	}

	// 初始化窗体
	public void initFrame() {
		// 设置窗体标题
		this.setTitle("欢迎您，" + myNickName);
		// 把分割面板加进来
		this.add(splitpane_split);
		// 设置分割面板分割线的位置
		splitpane_split.setDividerLocation(695);
		// 设置分割面板分割线的宽度
		splitpane_split.setDividerSize(5);
		// 拿到屏幕分辨率
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		// 显示在屏幕中央
		this.setBounds(screenWidth / 2 - 475, screenHeight / 2 - 350, 950, 700);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
