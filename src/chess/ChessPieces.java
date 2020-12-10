//棋子类
package chess;

import java.awt.Color;

public class ChessPieces {
	// 棋子颜色
	private Color color;

	// 带参构造器
	public ChessPieces(Color color) {
		this.color = color;
	}

	// get和set方法
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
