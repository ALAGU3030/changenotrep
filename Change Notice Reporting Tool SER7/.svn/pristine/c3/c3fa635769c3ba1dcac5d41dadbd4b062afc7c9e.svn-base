package com.teamcenter.soa.utils;
public class ProgressBarRotating extends Thread {
	public boolean showProgress = true;
	private String output;

	public ProgressBarRotating(String output) {
		this.output = output;
	}

	public void run() {
		String anim = "|/-\\";
		int x = 0;
		while (showProgress) {
			System.out.print("\r" + output + " "
					+ anim.charAt(x++ % anim.length()));
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}
			;
		}
	}
}