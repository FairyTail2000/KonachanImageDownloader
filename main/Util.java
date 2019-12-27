package main;

import static main.KeyCode.*;

import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sun.awt.datatransfer.DataTransferer;

public class Util {

	private static List<Integer> values = new ArrayList<>();
	
	
	/**
	 * Switches the window (hopefully) to Firefox and waits a second
	 * @param r {@link Robot} to use
	 */
	public static void switch_toFirefox(Robot r) {
		PressKeyKombi(r, ALT, TAB);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
	}

	/**
	 * Closes the current Browser (Firefox) Tab
	 * @param r {@link Robot} to use
	 */
	public static void close_tab(Robot r) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		PressKeyKombi(r, CONTROL, W);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
	}
	/**
	 * Downloads every image until times using the specified downloadThread
	 * @param r The {@link Robot} to use
	 * @param times how many Images
	 * @param downloadThread the {@link Thread} used to download the images
	 */
	public static void download_via_url(Robot r, int times, Thread downloadThread) {
		r.mouseMove(Main.middle_width, 80);
		downloadThread.start();
		for (int i = 0; i < times; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
			}
			// mark url
			r.mousePress(Main.left_click);
			r.mouseRelease(Main.left_click);
			PressKeyKombi(r, CONTROL, A);

			// copy to here
			PressKeyKombi(r, CONTROL, C);
			String url = null;
			try {
				//Get the url from the clipboard we just copied
				//I need to do this because some times there is a unicode String and then Java freaks out
				DataTransferer.ReencodingInputStream transferer = (DataTransferer.ReencodingInputStream) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
						.getTransferData(DataFlavor.getTextPlainUnicodeFlavor());
				BufferedReader reader = new BufferedReader(new InputStreamReader(transferer));
				//So it looks like, the above method works for nearly every String but places a 0 character every second char, wtf? Who programmed that???
				url = reader.readLine().replaceAll("\0", "");
			} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
				//Self explanatory?
				System.err.println("Something unexpected happened:");
				e.printStackTrace();
				Main.errorLog.add(e.toString());
			}
			//Well someone messed really up
			//Now what do I do now?
			if (url == null) {
				//TODO: Handle this case
			} else {
				//Check if this really an konachan image site, I know that not every kombination is possible but I check, just because I don't want things to break
				if (url.startsWith("http://konachan.com/post/show/") || url.startsWith("https://konachan.com/post/show/") || url.startsWith("https://www.konachan.com/post/show/") || url.startsWith("http://www.konachan.com/post/show/")) {
					//Add it to the Queue, since this is an unlimited queue, I don't care to use offer()
					Main.toDownload.offer(url);
					close_tab(r);
				} else {
					//Self explanatory?
					System.out.println(url);
					System.err.println("Somebody messed things up and put a wrong tab in the way, stopping copying the urls");
					return;
				}
			}
		}
	}
	
	/**
	 * Presses a key combination for 10 milliseconds and then releases it
	 * @param r {@link Robot} to use
	 * @param keys {@link KeyCode} to press
	 */
	public static void PressKeyKombi (Robot r, KeyCode... keys) {
		for (KeyCode key : keys) {
			r.keyPress(key.getCode());
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		for (KeyCode key : keys) {
			r.keyRelease(key.getCode());
		}
	}
	/**
	 * Presses a key for 10 milliseconds and then releases it
	 * @param r {@link Robot} to use
	 * @param key {@link KeyCode} to press
	 */
	public static void PressKey (Robot r, KeyCode key) {
		r.keyPress(key.getCode());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		r.keyRelease(key.getCode());
	}
	/**
	 * Checks if the system we are running on is windows
	 * @return whether the system is windows or not
	 */
	public static boolean isWindows () {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
}