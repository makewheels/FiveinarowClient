//倒计时线程类
package thread;

import javax.swing.JLabel;

public class CountDownThread extends Thread {
	// 线程是否运行控制位
	boolean flag = true;
	// 引用客户端代理线程
	ClientAgentThread clientAgentThread;
	// 引用倒计时Label
	JLabel label_countDown;
	// 剩余秒数
	int remainingTime = 59;

	// 带参构造器
	public CountDownThread(ClientAgentThread clientAgentThread,
			JLabel label_countDown) {
		// 初始化客户端代理线程
		this.clientAgentThread = clientAgentThread;
		// 初始化倒计时Label
		this.label_countDown = label_countDown;
	}

	@Override
	public void run() {
		while (flag) {
			// 设置Label，如果是一位数，前面补0
			if (remainingTime <= 9) {
				label_countDown.setText("倒计时:" + "0" + remainingTime + "秒");
			} else {
				label_countDown.setText("倒计时:" + remainingTime + "秒");
			}
			// 如果时间到了
			if (remainingTime == 0) {
				// 调用timeUp()方法
				clientAgentThread.sendTimeUp();
				// 设置Label
				label_countDown.setText("时间到！");
			}
			// 剩余时间减1秒
			remainingTime--;
			try {
				// 每秒刷新一次
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
