package com.intelligent.custom.util;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBar implements ActionListener {
	private static final String DEFAULT_STATUS = "Please Waiting";
	private JDialog dialog;
	private JProgressBar progressBar;
	private JLabel lbStatus;
	private JButton btnCancel;
	private Window parent;
	private Thread thread; // 处理业务的线程
	private String statusInfo;
	private String resultInfo;
	private String cancelInfo;

	public ProgressBar() {
	}

	public static void show(Window parent, Thread thread) {
		new ProgressBar(parent, thread, DEFAULT_STATUS, null, null);
	}

	public static void show(Window parent, Thread thread, String statusInfo) {
		new ProgressBar(parent, thread, statusInfo, null, null);
	}

	public static void show(Window parent, Thread thread, String statusInfo,
			String resultInfo, String cancelInfo) {
		new ProgressBar(parent, thread, statusInfo, resultInfo, cancelInfo);
	}

	private ProgressBar(Window parent, Thread thread, String statusInfo,
			String resultInfo, String cancelInfo) {
		this.parent = parent;
		this.thread = thread;
		this.statusInfo = statusInfo;
		this.resultInfo = resultInfo;
		this.cancelInfo = cancelInfo;
		initUI();
		startThread();
		dialog.setVisible(true);
	}

	private void initUI() {
		if (parent instanceof Dialog) {
			dialog = new JDialog((Dialog) parent, true);
		} else if (parent instanceof Frame) {
			dialog = new JDialog((Frame) parent, true);
		} else {
			dialog = new JDialog((Frame) null, true);
		}
		final JPanel mainPane = new JPanel(null);
		progressBar = new JProgressBar();
		lbStatus = new JLabel("" + statusInfo);
		btnCancel = new JButton("Cancel");
		progressBar.setIndeterminate(true);
		btnCancel.addActionListener(this);
		mainPane.add(progressBar);
		mainPane.add(lbStatus);
		dialog.getContentPane().add(mainPane);
		dialog.setUndecorated(true);// 除去title
		dialog.setResizable(true);
		dialog.setSize(390, 100);
		dialog.setLocationRelativeTo(parent); // 设置此窗口相对于指定组件的位置

		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 不允许关闭

		mainPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				layout(mainPane.getWidth(), mainPane.getHeight());
			}
		});
	}

	private void startThread() {
		new Thread() {
			public void run() {
				try {
					thread.start(); // 处理耗时任务
					// 等待事务处理线程结束
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					// 关闭进度提示框
					dialog.dispose();
					if (resultInfo != null && !resultInfo.trim().equals("")) {
						String title = "消息";
						JOptionPane.showMessageDialog(parent, resultInfo,
								title, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}.start();
	}

	private void layout(int width, int height) {
		progressBar.setBounds(20, 20, 350, 15);
		lbStatus.setBounds(20, 50, 350, 25);
		btnCancel.setBounds(width - 85, height - 31, 75, 21);
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		resultInfo = cancelInfo;
		thread.stop();
	}
}