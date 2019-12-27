package main;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JOptionPane;

/**
 * What do say?
 * Its the Main class...
 * @author rafael
 *
 */
public class Main {
	/**
	 * In the middle of the screen should be the address bar
	 */
	protected static final int middle_width = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
	
	/**
	 * A constant which saves the keycode for the left mouse button
	 */
	protected static final int left_click = InputEvent.BUTTON1_DOWN_MASK;
	
	/**
	 * The queue where the urls get inserted
	 * If the queue is full, the RAM is also, so no need to do additional steps
	 */
	protected static transient ArrayBlockingQueue<String> toDownload = new ArrayBlockingQueue<>(9999999, true);
	
	/**
	 * Here I signal the download thread to stop if and only if the queue is empty
	 */
	protected static transient boolean stop = false;
	
	/**
	 * The folder where the downloaded Images will be saved
	 */
	protected static File folder = new File(".Hentai");
	
	/**
	 * The Log to log errors
	 */
	protected static transient Error errorLog = new Error();

	/**
	 * Well its the Main method...
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new DownloadRunner());
		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		if (folder.isFile()) {
			folder.delete();
		}
		
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		if (Util.isWindows()) {
			try {
				Files.setAttribute(folder.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int times = Integer.valueOf(JOptionPane.showInputDialog("How many images do you have?"));
		if (times == 0) {
			times = Integer.valueOf(JOptionPane.showInputDialog("And how many do you really have?"));
			if (times == 0) {
				JOptionPane.showMessageDialog(null, "Then not", "0 images are not possible", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		}
		Util.switch_toFirefox(r);
		Util.download_via_url(r, times, t);

		System.out.println("Waiting for the download thread to exit...");
		while (toDownload.peek() != null) {
			Thread.sleep(500);
		}
		stop = true;
		t.join();
		errorLog.writeToDisk(new File("error.log"));
		System.out.println("Done, Goodbye");
	}
}