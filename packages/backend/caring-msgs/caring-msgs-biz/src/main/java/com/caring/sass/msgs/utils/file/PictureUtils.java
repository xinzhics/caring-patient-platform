//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.caring.sass.msgs.utils.file;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class PictureUtils {
    private Font font = new Font("宋体", 0, 14);
    private Graphics2D g = null;

    public PictureUtils() {
    }

    public BufferedImage loadImageUrl(String imgName) {
        try {
            URL url = new URL(imgName);
            return ImageIO.read(url);
        } catch (IOException var3) {
            System.out.println(var3.getMessage());
            return null;
        }
    }

    public BufferedImage modifyImageYe(BufferedImage img, String text) {
        try {
            int w = img.getWidth();
            int h = img.getHeight();
            this.g = img.createGraphics();
            this.g.setBackground(Color.WHITE);
            this.g.setColor(Color.red);
            if (this.font != null) {
                this.g.setFont(this.font);
            }

            this.g.drawString(text, w - 320, h - 50);
            this.g.dispose();
        } catch (Exception var5) {
            System.out.println(var5.getMessage());
        }

        return img;
    }

    public BufferedImage modifyImagetogeter(BufferedImage b, BufferedImage d) {
        try {
            int w = b.getWidth();
            int h = b.getHeight();
            this.g = d.createGraphics();
            this.g.drawImage(b, 125, 150, w - 200, h - 200, (ImageObserver)null);
            this.g.dispose();
        } catch (Exception var5) {
            System.out.println(var5.getMessage());
        }

        return d;
    }
}
