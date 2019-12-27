package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The DownloadRunner is a Runnable which downloads the things in the queue
 */
public class DownloadRunner implements Runnable {
	
	@Override
	public void run() {
		while (true) {
			//The link to site where image is
			String element = Main.toDownload.poll();
			//Check if the queue is empty and the download thread should exit
			if (element == null && Main.stop) {
				break;
			}
			//If the key is empty, wait 100 ms and then try again, over and over again
			if (element == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				continue;
			}
			System.out.println("Trying to download: " + element);
			String extractedImageUrl, fileName = null;
			//Shouldn't be necessary but the variables above won't get resolved if I don't do it
			{
				//Try to download the html of the page
				Document doc = null;
				try {
					doc = Jsoup.connect(element).get();
				} catch (IOException e) {
					e.printStackTrace();
					Main.errorLog.add(e.getMessage());
					continue;
				}

				//Extract the link to the image
				Elements viewLarge = doc.getElementsByClass("highres-show");
				if (viewLarge.size() != 0) {
					extractedImageUrl = viewLarge.get(0).attr("href");
				} else {
					Element image = doc.getElementById("image");
					extractedImageUrl = image.attr("src");
				}

				//Extract the description of the file which consist of the tags of the image, thanks konachan team!
				//This will be used for the file name
				Elements metaDesc = doc.getElementsByAttributeValue("property", "og:description");
				if (metaDesc.size() != 0) {
					fileName = metaDesc.get(0).attr("content");
				}
				//If the filename is null for some reason, this will choose a name for the file
				if (fileName == null) {
					File f = new File("1");
					while (f.exists()) {
						int name = Integer.parseInt(f.getName()) + 1;
						f = new File(String.valueOf(name));
					}
					fileName = f.getName() + ".png";
				}

			}
			try {
				//ImageIO is pretty useful just like JSoup, I should consider to switch to maven...
				BufferedImage image = ImageIO.read(new URL(extractedImageUrl));
				if (!ImageIO.write(image, "png", new File(Main.folder, fileName + ".png"))) {
					JOptionPane.showMessageDialog(null, "Error while writing Image to disk", "Error: ImageIO.write() returned false, because no appropriate driver to write the image to the disk could be found", JOptionPane.ERROR_MESSAGE);
					Main.errorLog.add("Unable to write image to disk");
				} else {
					System.out.println("Finished to download the image");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
