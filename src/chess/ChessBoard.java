//棋盘类
package chess;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import frame.PlayFrame;

public class ChessBoard extends JPanel {
	private static final long serialVersionUID = 6611726865381135927L;

	// 棋盘是否可用
	public boolean chessBoardCanUse = false;
	// 棋子数组
	public ChessPieces[][] chessPieces = new ChessPieces[15][15];
	// PlayFrame引用
	public PlayFrame playFrame;
	// 最新下的点的外框坐标数组
	// [0]:是否需要画边框,0:不需要画,1:需要画
	// [1]:横坐标[2]:纵坐标
	public int[] newChessPieceLocation = new int[3];
	// 如果有人赢了，会用这个来画连成五子的线
	public String[] winArray = null;

	// 构造器
	public ChessBoard(PlayFrame father) {
		// 初始化PlayFrame
		this.playFrame = father;
		// 添加鼠标监听器
		this.addMouseListener(new MouseAdapter() {
			// 当鼠标按下时
			@Override
			public void mousePressed(MouseEvent e) {
				if (chessBoardCanUse == true) {
					// 拿到横坐标和纵坐标
					int x = e.getX();
					int y = e.getY();
					// 最终要定数组位置的坐标
					int setX;
					int setY;
					// 如果已超出棋盘范围，直接return
					if (x < 45 || x > 645 || y < 35 || y > 635) {
						return;
					}
					// 如果x偏左
					if (((x - 65) % 40) <= 20) {
						setX = Math.round((x - 65) / 40);
						// 如果x偏右
					} else {
						setX = Math.round((x - 65) / 40) + 1;
					}
					// 如果y偏左
					if (((y - 55) % 40) <= 20) {
						setY = Math.round((y - 55) / 40);
						// 如果x偏右
					} else {
						setY = Math.round((y - 55) / 40) + 1;
					}
					// 如果该点已经有棋子了，直接return
					if (chessPieces[setX][setY] != null) {
						return;
					}
					// 添加新棋子
					chessPieces[setX][setY] = new ChessPieces(playFrame.myColor);
					// 调用sendMove通知服务器走棋
					playFrame.clientAgentThread.sendMove(setX, setY);
				}
			}
		});
	}

	// 重写paint方法
	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		// 画笔颜色：棋盘背景色
		g.setColor(new Color(249, 214, 91));
		// 画正方形棋盘
		g.fillRect(0, 0, 950, 700);
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		// 关闭抗锯齿
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		// 画竖线
		for (int i = 1; i <= 15; i++) {
			g.drawLine(25 + 40 * i, 55, 25 + 40 * i, 615);
		}
		// 画横线
		for (int i = 1; i <= 15; i++) {
			g.drawLine(65, 15 + 40 * i, 625, 15 + 40 * i);
		}
		// 打开抗锯齿
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// 画5个圆点
		g.fillOval(180, 170, 10, 10);
		g.fillOval(500, 170, 10, 10);
		g.fillOval(340, 330, 10, 10);
		g.fillOval(180, 490, 10, 10);
		g.fillOval(500, 490, 10, 10);
		// 画棋子
		for (int i = 0; i <= 14; i++) {
			for (int j = 0; j <= 14; j++) {
				if (chessPieces[i][j] != null) {
					g.setColor(chessPieces[i][j].getColor());
					g.fillOval(50 + 40 * i, 40 + 40 * j, 30, 30);
				}
			}
		}
		// 画最新点的方框
		if (newChessPieceLocation[0] == 1) {
			// 拿到坐标
			int x = newChessPieceLocation[1];
			int y = newChessPieceLocation[2];
			// 设置线的粗细
			g.setStroke(new BasicStroke(4));
			// 关闭抗锯齿
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
			// 设置画笔颜色
			g.setColor(Color.RED);
			// 画边框
			g.drawLine(47 + 40 * x, 37 + 40 * y, 55 + 40 * x, 37 + 40 * y);
			g.drawLine(75 + 40 * x, 37 + 40 * y, 83 + 40 * x, 37 + 40 * y);
			g.drawLine(47 + 40 * x, 37 + 40 * y, 47 + 40 * x, 45 + 40 * y);
			g.drawLine(47 + 40 * x, 65 + 40 * y, 47 + 40 * x, 73 + 40 * y);
			g.drawLine(47 + 40 * x, 73 + 40 * y, 55 + 40 * x, 73 + 40 * y);
			g.drawLine(75 + 40 * x, 73 + 40 * y, 83 + 40 * x, 73 + 40 * y);
			g.drawLine(83 + 40 * x, 37 + 40 * y, 83 + 40 * x, 45 + 40 * y);
			g.drawLine(83 + 40 * x, 65 + 40 * y, 83 + 40 * x, 73 + 40 * y);
		}

		// 如果有人赢了，画连成五子的线
		if (winArray != null) {
			// 拿到开始和结束的横纵坐标
			int startX = Integer.parseInt(winArray[1]);
			int startY = Integer.parseInt(winArray[2]);
			int endX = Integer.parseInt(winArray[3]);
			int endY = Integer.parseInt(winArray[4]);
			// 设置线的粗细
			g.setStroke(new BasicStroke(6));
			// 根据输赢画线
			if (winArray[0].equals("WIN")) {
				g.setColor(Color.GREEN);
				g.drawLine(65 + 40 * startX, 55 + 40 * startY, 65 + 40 * endX,
						55 + 40 * endY);
			} else if (winArray[0].equals("LOSE")) {
				g.setColor(Color.RED);
				g.drawLine(65 + 40 * startX, 55 + 40 * startY, 65 + 40 * endX,
						55 + 40 * endY);
			}
		}
	}
}
