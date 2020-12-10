//总时长线程类
package thread;

import javax.swing.JLabel;

public class TotalTimeThread extends Thread {
	// 线程是否运行控制位
	boolean flag = true;
	// 总时长Label
	JLabel label_totalTime;
	// 总秒数
	int totalSeconds = 0;

	// 带参构造器
	public TotalTimeThread(JLabel label_totalTime) {
		// 初始化总时长Label
		this.label_totalTime = label_totalTime;
	}

	@Override
	public void run() {
		while (flag) {
			try {
				// 每隔1秒刷新一次
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			totalSeconds++;
			// 要设置的文本
			String setString;
			// 要设置的总分钟
			int setMinutes = totalSeconds / 60;
			// 要设置的总秒数
			int setSeconds = totalSeconds % 60;
			setString = "总时长" + " " + setMinutes + ":";
			// 设置setString
			if (setSeconds <= 9) {
				setString = setString + "0" + setSeconds;
			} else {
				setString = setString + setSeconds;
			}
			// 设置Label
			label_totalTime.setText(setString);
		}
	}
}
