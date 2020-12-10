//北京时间线程类
package thread;

import java.util.Calendar;

import javax.swing.JLabel;

public class CurrentTimeThread extends Thread {
	// 引用当前时间TextField
	JLabel label_currentTime = new JLabel();

	// 带参构造器
	public CurrentTimeThread(JLabel label_currentTime) {
		// 初始化
		this.label_currentTime = label_currentTime;
	}

	@Override
	public void run() {
		while (true) {
			// 拿到当前时间
			Calendar c = Calendar.getInstance();
			// 拿到当前时间的小时
			int currentHour = c.get(Calendar.HOUR_OF_DAY);
			// 拿到当前时间的分钟
			int currentMinute = c.get(Calendar.MINUTE);
			// 用于显示的String
			String currentTimeString;
			// 上午还是下午
			if (currentHour <= 12) {
				currentTimeString = "上午" + " ";
			} else {
				currentTimeString = "下午" + " ";
				currentHour -= 12;
			}
			// 加上小时
			if (currentHour <= 9) {
				currentTimeString += 0;
			}
			currentTimeString += currentHour;
			// 加冒号
			currentTimeString += ":";
			// 加分钟
			if (currentMinute <= 9) {
				currentTimeString += 0;
			}
			currentTimeString += currentMinute;
			// 设置文本
			label_currentTime.setText(currentTimeString);
			try {
				// 每分钟刷新一次
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
