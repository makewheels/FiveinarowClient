//客户端代理线程类
package thread;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JOptionPane;

import chess.ChessPieces;
import frame.LoginFrame;
import frame.PlayFrame;

public class ClientAgentThread extends Thread {
	// 线程是否运行控制位
	boolean flag = true;
	// 引用登录窗体
	LoginFrame loginFrame;
	// 引用游戏窗体
	PlayFrame playFrame;
	// 引用倒计时线程
	CountDownThread countDownThread;
	// 引用总时长线程
	TotalTimeThread totalTimeThread;
	// Socket
	Socket socket;
	// 输入输出流
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;

	// 带参构造器
	public ClientAgentThread(LoginFrame loginFrame, String myNickName) {
		this.loginFrame = loginFrame;
		// 设置线程名字
		this.setName(myNickName);
		String messageFromServer;
		try {
			socket = new Socket(loginFrame.textfield_serverIP.getText(),
					Integer.parseInt(loginFrame.textfield_portNumber.getText()));
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF("<#NEW_CLIENT#>" + myNickName);
			loginFrame.isConnected = true;
			// 检测重名
			messageFromServer = dataInputStream.readUTF().trim();
			if (messageFromServer.startsWith("<#HAVE_THE_NAME#>")) {
				// 昵称重复
				loginFrame.haveTheName = true;
				// 该关的关
				socket.close();
				dataInputStream.close();
				dataOutputStream.close();
				flag = false;
				return;
			}
		} catch (Exception e) {
			loginFrame.isConnected = false;
			e.printStackTrace();
			return;
		}
		// 打开棋盘Frame
		playFrame = new PlayFrame(myNickName);
		// 把本代理线程给playFrame用来引用
		playFrame.clientAgentThread = this;
		// 更新客户端列表
		if (messageFromServer.startsWith("<#NICK_LIST#>")) {
			String usefulMessageFromServer = messageFromServer.substring(13);
			refreshClientList(usefulMessageFromServer);
		}
	}

	// run方法
	@Override
	public void run() {
		while (flag) {
			try {
				// 接收来自服务器的消息
				String messageFromServer = dataInputStream.readUTF().trim();
				// 刷新列表
				if (messageFromServer.startsWith("<#NICK_LIST#>")) {
					refreshClientList(messageFromServer.substring(13));
					continue;
					// 服务器关闭
				} else if (messageFromServer.startsWith("<#SERVER_SHUT_DOWN#>")) {
					serverShutDown();
					continue;
					// 收到“申请游戏”申请
				} else if (messageFromServer.startsWith("<#APPLY_GAME#>")) {
					receiveApplyGame(messageFromServer.substring(14));
					continue;
					// 用户正忙
				} else if (messageFromServer.startsWith("<#BUSY#>")) {
					busy(messageFromServer.substring(8));
					continue;
					// 同意申请
				} else if (messageFromServer.startsWith("<#AGREE#>")) {
					agree(messageFromServer.substring(9));
					continue;
					// 拒绝申请
				} else if (messageFromServer.startsWith("<#REFUSE#>")) {
					refuse(messageFromServer.substring(10));
					continue;
					// 时间到
				} else if (messageFromServer.startsWith("<#TIME_UP#>")) {
					receiveTimeUp();
					continue;
					// 接到走棋命令
				} else if (messageFromServer.startsWith("<#MOVE#>")) {
					receiveMove(messageFromServer.substring(8));
					continue;
					// 收到聊天
				} else if (messageFromServer.startsWith("<#CHAT#>")) {
					receiveChat(messageFromServer.substring(8));
					continue;
				} else if (messageFromServer.startsWith("<#RUN_AWAY#>")) {
					// 对方逃跑
					runAway();
					continue;
					// 认输
				} else if (messageFromServer.startsWith("<#GIVE_UP#>")) {
					receiveGiveUp();
					continue;
					// 游戏正常结束
				} else if (messageFromServer.startsWith("<#GAME_OVER#>")) {
					gameOver(messageFromServer.substring(13));
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 用户正忙
	public void busy(String usefulMessageFromServer) {
		String[] message = usefulMessageFromServer.split("\\|");
		// 通知申请者
		JOptionPane.showMessageDialog(playFrame, "您申请游戏的用户" + message[0]
				+ "正在和" + message[1] + "下棋，请换人再试！", "通知",
				JOptionPane.ERROR_MESSAGE);
	}

	// 提出游戏申请
	public void sendApplyGame() {
		// 要申请的玩家昵称
		String applyPlayer = playFrame.list_userOnline.getSelectedValue();
		// 如果applyPlayer没有，提示
		if (applyPlayer == null || applyPlayer.equals("")) {
			JOptionPane.showMessageDialog(playFrame, "请先选择玩家，再申请游戏！", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			// 向服务器提交“申请游戏”申请
			dataOutputStream.writeUTF("<#APPLY_GAME#>" + applyPlayer);
			JOptionPane.showMessageDialog(playFrame, "已提交对" + applyPlayer
					+ "的游戏申请，等待对方回复！", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 收到“申请游戏”申请
	public void receiveApplyGame(String usefulMessageFromServer) {
		// 询问是否接受游戏申请
		String[] options = { "接受", "拒绝" };
		int yesOrNo = JOptionPane.showOptionDialog(playFrame,
				usefulMessageFromServer + "向您提出游戏申请，请处理：", "收到游戏申请",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);
		// 如果接受邀请
		if (yesOrNo == 0) {
			// 设置myColor
			playFrame.myColor = Color.WHITE;
			// 开始游戏
			startGame();
			// 设置棋盘为不可用
			playFrame.panel_chessBoard.chessBoardCanUse = false;
			try {
				// 通知服务器同意游戏申请
				dataOutputStream
						.writeUTF("<#AGREE#>" + usefulMessageFromServer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 通知游戏开始
			JOptionPane.showMessageDialog(playFrame, "游戏开始，请等待对方先下！", "通知",
					JOptionPane.INFORMATION_MESSAGE);
			// 如果拒绝邀请
		} else {
			try {
				// 通知服务器拒绝游戏申请
				dataOutputStream.writeUTF("<#REFUSE#>"
						+ usefulMessageFromServer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 收到同意申请
	public void agree(String usefulMessageFromServer) {
		// 设置myColor
		playFrame.myColor = Color.BLACK;
		// 开始游戏
		startGame();
		// 设置棋盘为可用
		playFrame.panel_chessBoard.chessBoardCanUse = true;
		// 通知游戏开始
		JOptionPane.showMessageDialog(playFrame, usefulMessageFromServer
				+ "已同意您的申请，游戏开始，请您先下！", "通知", JOptionPane.INFORMATION_MESSAGE);
		// 启动倒计时线程
		this.countDownThread = new CountDownThread(this,
				playFrame.label_countDown);
		// 启动该倒计时线程
		countDownThread.start();
	}

	// 拒绝申请
	public void refuse(String usefulMessageFromServer) {
		// 通知客户端
		JOptionPane.showMessageDialog(playFrame, usefulMessageFromServer
				+ "已拒绝您的申请！", "通知", JOptionPane.ERROR_MESSAGE);
	}

	// 发出走棋
	public void sendMove(int x, int y) {
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 结束倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		// 设置棋盘为不可用
		playFrame.panel_chessBoard.chessBoardCanUse = false;
		// 把最新点的边框设置为不要画
		playFrame.panel_chessBoard.newChessPieceLocation[0] = 0;
		// repaint棋盘
		playFrame.panel_chessBoard.repaint();
		try {
			// 通知服务器
			dataOutputStream.writeUTF("<#MOVE#>" + x + "|" + y);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 接到走棋命令
	public void receiveMove(String usefulMessageFromServer) {
		// 拿到要添加棋子的位置
		String[] message = usefulMessageFromServer.split("\\|");
		int x = Integer.parseInt(message[0]);
		int y = Integer.parseInt(message[1]);
		// 设置要添加棋子的颜色为对方的颜色
		Color tempColor;
		if (playFrame.myColor == Color.BLACK) {
			tempColor = Color.WHITE;
		} else {
			tempColor = Color.BLACK;
		}
		// 添加棋子
		playFrame.panel_chessBoard.chessPieces[x][y] = new ChessPieces(
				tempColor);
		// 画最新点的边框
		// 设置为要画
		playFrame.panel_chessBoard.newChessPieceLocation[0] = 1;
		// 设置坐标
		playFrame.panel_chessBoard.newChessPieceLocation[1] = x;
		playFrame.panel_chessBoard.newChessPieceLocation[2] = y;
		// repaint棋盘
		playFrame.panel_chessBoard.repaint();
		// 设置棋盘为可用
		playFrame.panel_chessBoard.chessBoardCanUse = true;
		// 启动倒计时线程
		this.countDownThread = new CountDownThread(this,
				playFrame.label_countDown);
		// 启动该倒计时线程
		countDownThread.start();
	}

	// 发送聊天信息给对手
	public void sendChatToOpponent() {
		// 要发送的信息
		String sendMessage = playFrame.textfield_chatInput.getText().trim();
		// 如果要发送的内容为空，提示并返回
		if (sendMessage.equals("")) {
			JOptionPane.showMessageDialog(playFrame, "请输入聊天内容后再发送！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 更新TextArea
		playFrame.textarea_chat.append(getCurrentTimeString() + " " + "我向对手说："
				+ "\n" + sendMessage + "\n");
		try {
			// 通知服务器
			dataOutputStream.writeUTF("<#CHAT#>" + "OPPONENT" + "|"
					+ sendMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 清空聊天输入框
		playFrame.textfield_chatInput.setText("");
		// 设置ScrollBar的位置到最后
		playFrame.scrollbar_chat
				.setValue(playFrame.scrollbar_chat.getMaximum());
	}

	// 发送聊天信息给列表中指定的人
	public void sendChatToSomeone() {
		// 要接受信息玩家的昵称
		String receiverNickName = playFrame.list_userOnline.getSelectedValue();
		// 如果没有选中列表中的玩家，提示并返回
		if (receiverNickName == null || receiverNickName.equals("")) {
			JOptionPane.showMessageDialog(playFrame, "请先选择列表中的玩家！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 要发送的信息
		String sendMessage = playFrame.textfield_chatInput.getText().trim();
		// 如果要发送的内容为空，提示并返回
		if (sendMessage.equals("")) {
			JOptionPane.showMessageDialog(playFrame, "请输入聊天内容后再发送！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 更新TextArea
		playFrame.textarea_chat.append(getCurrentTimeString() + " " + "我向"
				+ receiverNickName + "说：" + "\n" + sendMessage + "\n");
		try {
			// 通知服务器
			dataOutputStream.writeUTF("<#CHAT#>" + receiverNickName + "|"
					+ sendMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 清空聊天输入框
		playFrame.textfield_chatInput.setText("");
		// 设置ScrollBar的位置到最后
		playFrame.scrollbar_chat
				.setValue(playFrame.scrollbar_chat.getMaximum());
	}

	// 接到聊天信息
	public void receiveChat(String usefulMessageFromServer) {
		// 解析数据得到stringArray，[0]:发送者的昵称，[1]:聊天信息
		String[] stringArray = usefulMessageFromServer.split("\\|");
		// 如果是对手发来的
		if (stringArray[0].equals("OPPONENT")) {
			playFrame.textarea_chat.append(getCurrentTimeString() + " "
					+ "您的对手" + "向您说：" + "\n" + stringArray[1] + "\n");
			// 如果不是从对手发来的
		} else {
			playFrame.textarea_chat.append(getCurrentTimeString() + " "
					+ stringArray[0] + "向您说：" + "\n" + stringArray[1] + "\n");
		}
		// 设置ScrollBar的位置到最后
		playFrame.scrollbar_chat
				.setValue(playFrame.scrollbar_chat.getMaximum());
	}

	// 发出认输
	public void sendGiveUp() {
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止总时长线程
		if (totalTimeThread != null) {
			totalTimeThread.flag = false;
		}
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		try {
			// 通知服务器
			dataOutputStream.writeUTF("<#GIVE_UP#>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 设置棋盘可用性
		playFrame.panel_chessBoard.chessBoardCanUse = false;
		// 设置按钮的可用性
		playFrame.button_applyGame.setEnabled(true);
		playFrame.button_giveUp.setEnabled(false);
		playFrame.button_disconnect.setEnabled(true);
		playFrame.button_closeProgram.setEnabled(true);
		playFrame.button_sendToOpponent.setEnabled(false);
		// 通知
		JOptionPane.showMessageDialog(playFrame, "您已认输，游戏结束！", "游戏结束",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// 收到认输
	public void receiveGiveUp() {
		// 终止总时长线程
		if (totalTimeThread != null) {
			totalTimeThread.flag = false;
		}
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		// 设置棋盘可用性
		playFrame.panel_chessBoard.chessBoardCanUse = false;
		// 设置按钮的可用性
		playFrame.button_applyGame.setEnabled(true);
		playFrame.button_giveUp.setEnabled(false);
		playFrame.button_disconnect.setEnabled(true);
		playFrame.button_closeProgram.setEnabled(true);
		playFrame.button_sendToOpponent.setEnabled(false);
		// 通知
		JOptionPane.showMessageDialog(playFrame, "对方已认输，游戏结束！", "游戏结束",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// 刷新客户端列表
	public void refreshClientList(String usefulMessageFromServer) {
		// 分割出列表，放到Sting数组里
		String[] listString = usefulMessageFromServer.split("\\|");
		// 真正要用的Vector
		Vector<String> list = new Vector<String>();
		// 如果遍历listString，如果不是自己的昵称，或者不为空，加到Vector里面
		for (int i = 0; i < listString.length; i++) {
			if ((!listString[i].equals(playFrame.myNickName))
					&& (!listString[i].equals(""))) {
				list.add(listString[i]);
			}
		}
		// 设置List的内容
		playFrame.list_userOnline.setListData(list);
	}

	// 初始化棋盘
	public void initChessBoard() {
		// 清空所有棋子
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				playFrame.panel_chessBoard.chessPieces[i][j] = null;
			}
		}
		// 把最新点的边框设置为不要画
		playFrame.panel_chessBoard.newChessPieceLocation[0] = 0;
		// 清空用来画连成五子的数组
		playFrame.panel_chessBoard.winArray = null;
	}

	// 发出时间到
	public void sendTimeUp() {
		// 设置棋盘为不可用
		playFrame.panel_chessBoard.chessBoardCanUse = false;
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		try {
			// 通知服务器时间到
			dataOutputStream.writeUTF("<#TIME_UP#>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 收到时间到
	public void receiveTimeUp() {
		// 设置棋盘为不可用
		playFrame.panel_chessBoard.chessBoardCanUse = true;
		// 通知
		JOptionPane.showMessageDialog(playFrame, "对方时间到，现在轮到您下了!", "请下棋",
				JOptionPane.INFORMATION_MESSAGE);
		// 启动倒计时线程
		this.countDownThread = new CountDownThread(this,
				playFrame.label_countDown);
		// 启动该倒计时线程
		countDownThread.start();
	}

	// 对方逃跑
	public void runAway() {
		// 终止总时长线程
		if (totalTimeThread != null) {
			totalTimeThread.flag = false;
		}
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		// 设置按钮的可用性
		playFrame.button_applyGame.setEnabled(true);
		playFrame.button_giveUp.setEnabled(false);
		playFrame.button_disconnect.setEnabled(true);
		playFrame.button_closeProgram.setEnabled(true);
		playFrame.button_sendToOpponent.setEnabled(false);
		// 设置棋盘为不可用
		playFrame.panel_chessBoard.chessBoardCanUse = false;
		// 通知
		JOptionPane.showMessageDialog(playFrame, "对方已逃跑，游戏结束！", "游戏结束",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// 开始游戏：为游戏的开始做准备
	public void startGame() {
		// 初始化棋盘
		initChessBoard();
		// repaint棋盘
		playFrame.panel_chessBoard.repaint();
		// 设置按钮的可用性
		playFrame.button_applyGame.setEnabled(false);
		playFrame.button_giveUp.setEnabled(true);
		playFrame.button_disconnect.setEnabled(false);
		playFrame.button_closeProgram.setEnabled(false);
		playFrame.button_sendToOpponent.setEnabled(true);
		// 创建总时长线程
		this.totalTimeThread = new TotalTimeThread(playFrame.label_totalTime);
		// 启动总时长线程
		totalTimeThread.start();
	}

	// 断开连接按钮执行方法
	public void clientLeave() {
		// 终止总时长线程
		if (totalTimeThread != null) {
			totalTimeThread.flag = false;
		}
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		try {
			// 通知服务器
			dataOutputStream.writeUTF("<#CLIENT_LEAVE#>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 干掉该客户端代理线程
		flag = false;
		// 通知
		JOptionPane.showMessageDialog(playFrame, "已与服务器断开连接，需重新登录", "重连通知",
				JOptionPane.ERROR_MESSAGE);
		// 打开登录窗口
		new LoginFrame(playFrame.myNickName);
		// 把游戏窗口干掉
		playFrame.dispose();
	}

	// 游戏正常结束
	public void gameOver(String usefulMessageFromServer) {
		// 终止总时长线程
		if (totalTimeThread != null) {
			totalTimeThread.flag = false;
		}
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		// 设置按钮的可用性
		playFrame.button_applyGame.setEnabled(true);
		playFrame.button_giveUp.setEnabled(false);
		playFrame.button_disconnect.setEnabled(true);
		playFrame.button_closeProgram.setEnabled(true);
		playFrame.button_sendToOpponent.setEnabled(false);
		// 设置棋盘为不可用
		playFrame.panel_chessBoard.chessBoardCanUse = false;
		// 分解从服务器得到的String
		String[] messageString = usefulMessageFromServer.split("\\|");
		// 设置棋盘的winArray
		playFrame.panel_chessBoard.winArray = messageString;
		// repaint棋盘
		playFrame.panel_chessBoard.repaint();
		// 通知游戏结果
		if (messageString[0].equals("WIN")) {
			JOptionPane.showMessageDialog(playFrame, "恭喜，您获胜！", "游戏结束",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (messageString[0].equals("LOSE")) {
			JOptionPane.showMessageDialog(playFrame, "很遗憾，您输了！", "游戏结束",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// 关闭程序按钮执行方法
	public void closeProgram() {
		// 终止总时长线程
		if (totalTimeThread != null) {
			totalTimeThread.flag = false;
		}
		// 设置倒计时Label
		playFrame.label_countDown.setText("");
		// 终止倒计时线程
		if (countDownThread != null) {
			countDownThread.flag = false;
		}
		try {
			// 通知服务器
			dataOutputStream.writeUTF("<#CLIENT_LEAVE#>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 关闭程序
		System.exit(0);
	}

	// 服务器关闭
	public void serverShutDown() {
		this.flag = false;
		playFrame.clientAgentThread = null;
		// 干掉该客户端代理线程
		flag = false;
		JOptionPane.showMessageDialog(playFrame, "很抱歉，服务器已关闭！\n您可以尝试重连。", "通知",
				JOptionPane.ERROR_MESSAGE);
		// 打开登录窗口
		new LoginFrame(playFrame.myNickName);
		// 把游戏窗口干掉
		playFrame.dispose();
	}

	// 返回当前时间的String：主要用来更新聊天TextArea中的时间
	public String getCurrentTimeString() {
		// 拿到当前系统时间
		Calendar calendar = Calendar.getInstance();
		// 拿到当前时间的小时
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		// 拿到当前时间的分钟
		int currentMinute = calendar.get(Calendar.MINUTE);
		// 拿到当前时间的秒
		int currentSecond = calendar.get(Calendar.SECOND);
		// 要返回的String
		String returnString = "";
		// 小时
		if (currentHour <= 9) {
			returnString = "0" + currentHour + ":";
		} else {
			returnString = currentHour + ":";
		}
		// 分钟
		if (currentMinute <= 9) {
			returnString = returnString + "0" + currentMinute + ":";
		} else {
			returnString = returnString + currentMinute + ":";
		}
		// 秒
		if (currentSecond <= 9) {
			returnString = returnString + "0" + currentSecond;
		} else {
			returnString = returnString + currentSecond + "";
		}
		// 返回当前时间的String
		return returnString;
	}
}
