package top.angelinaBot.model;

import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * 文本生成图片类
 */
public class TextLine {
    //文本内容
    private final List<List<Object>> text = new ArrayList<>();
    //单行内容
    private List<Object> line = new ArrayList<>();
    //列数
    private int width = 0;
    //行数
    private int height = 0;
    //画图指针
    private int pointers;
    //最长允许多少列
    private final int maxWidth;

    /**
     * 默认最长允许20个字符
     */
    public TextLine() {
        this.maxWidth = 20;
    }

    /**
     * @param maxWidth 最长允许maxWidth个字符
     */
    public TextLine(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * 为内容增加一个小型图标，图片可以超出最长字符限制
     *
     * @param image 图片
     */
    public void addImage(BufferedImage image) {
        pointers += 3;
        addSpace();
        line.add(image);
        addSpace();
    }

    /**
     * 为内容增加一个自定义图片，图片可以超出最长字符限制
     *
     * @param image 图片
     */
    public void addImage(Image image,int x, int y,int widthOfSize,int heightOfSize) {
        int a = pointers;
        pointers = 0;
        ImageInfo imageInfo =new ImageInfo();
        imageInfo.setImage(image);
        imageInfo.setX(x);
        imageInfo.setY(y);
        imageInfo.setWidthOfSize(widthOfSize);
        imageInfo.setHeightOfSize(heightOfSize);
        pointers += widthOfSize;
        line.add(imageInfo);
        pointers = a;

    }

    /**
     * 为内容增加一行字符串，字符串若超出最长限制，截断换行(并进行两个空格的缩进(未启用))
     *
     * @param s 字符串
     */
    public void addString(String s) {
        if (pointers > maxWidth) {
            nextLine();
            addString(s);
        } else if (s.length() + pointers > maxWidth) {
            int splitPointer = maxWidth - pointers;
            line.add(s.substring(0, splitPointer));
            nextLine();
            //addSpace(2);
            addString(s.substring(splitPointer));
            pointers = maxWidth;
        } else {
            pointers += s.length();
            line.add(s);
        }
    }

    /**
     * 为内容添加空格，若添加空格后超出最长限制，则仅添加一个
     *
     * @param spaceNum 空格数
     */
    public void addSpace(int spaceNum) {
        if (pointers + spaceNum > maxWidth) {
            spaceNum = 1;
        }
        line.add(spaceNum);
        pointers += spaceNum;
    }

    /**
     * 默认只增加一个空格
     */
    public void addSpace() {
        addSpace(1);
    }

    /**
     * 换行
     */
    public void nextLine() {
        if (pointers > width) {
            width = pointers;
        }
        pointers = 0;
        height++;
        text.add(line);
        line = new ArrayList<>();
    }

    /**
     * 添加单独一行的居中字符串，居中字符串必须单独一行
     *
     * @param s 字符串
     */
    public void addCenterStringLine(String s) {
        StringBuilder sb = new StringBuilder(s);
        if (pointers != 0) {
            nextLine();
        }
        pointers = s.length();
        line.add(sb);
        nextLine();
    }

    /**
     * 将TextLine生成一个图片
     *
     * @param size 单个字符大小
     * @param use  是否使用背景图
     * @return 生成图片
     */
    public BufferedImage drawImage(int size,boolean use){
        return this.drawImage(size, use,false);
    }

    public BufferedImage drawImage(int size, boolean use, boolean white) {
        if (!line.isEmpty()) {
            if (pointers > width) {
                width = pointers;
            }
            height++;
            text.add(line);
        }
        BufferedImage image = new BufferedImage((width + 4) * size, (height + 3) * size, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        Color wordColor;//字体颜色
        Font wordFont;//字体格式
        Color bottomColor;//阴影底色
        Color pink =new Color(245,181,182);//调色粉色
        Color skin = new Color(249,243,227);//调色肉色
        boolean shadowSwitch;//是否开启阴影
        if (use){
            int setWidth = (width + 2) * size;
            int setHeight = (height + 2) * size;
            if(setHeight<0.6*setWidth){
                graphics.setColor(pink);
                graphics.fillRect(0, 0, (width + 4) * size, (height + 3) * size);
                try {
                    InputStream pic = new ClassPathResource("/pic/new.jpg").getInputStream();
                    Image img = ImageIO.read(pic).getScaledInstance(setWidth, (int)(setWidth*0.56), Image.SCALE_DEFAULT);
                    graphics.drawImage(img, size, size, setWidth, (int)(setWidth*0.56), null);
                }catch (IOException e){
                    e.printStackTrace();
                }
                wordColor = Color.PINK;
                shadowSwitch = true;
                bottomColor =Color.black;
                wordFont = new Font("新宋体", Font.BOLD, size);
            }
            else if(setHeight<1.6*setWidth){
                graphics.setColor(pink);
                graphics.fillRect(0, 0, (width + 4) * size, (height + 3) * size);
                try {
                    InputStream pic = new ClassPathResource("/pic/new2.jpg").getInputStream();
                    Image img = ImageIO.read(pic).getScaledInstance(setWidth, (setWidth/10)*16, Image.SCALE_DEFAULT);
                    graphics.drawImage(img, size, size, setWidth, (setWidth/10)*16, null);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                wordColor = skin;
                shadowSwitch = true;
                bottomColor =Color.DARK_GRAY;
                wordFont = new Font("新宋体", Font.BOLD, size);
            }else {
                graphics.setColor(new Color(182, 203, 253));
                graphics.fillRect(0, 0, (width + 4) * size, (height + 3) * size);
                graphics.setColor(new Color(237, 247, 251));
                graphics.fillRect(size , size , (width + 2) * size, (height + 1) * size);
                wordColor = Color.PINK;
                shadowSwitch = true;
                bottomColor =Color.black;
                wordFont = new Font("新宋体", Font.BOLD, size);
            }
        }else {
            graphics.setColor(new Color(0, 129, 212));
            graphics.fillRect(0, 0, (width + 4) * size, (height + 3) * size);
            if (white){
                graphics.setColor(Color.lightGray);
                graphics.fillRect(size , size , (width + 2) * size, (height + 1) * size);
                wordColor = Color.black;
                shadowSwitch = false;
            }else {
                graphics.setColor(new Color(90, 150, 222));
                graphics.fillRect(size , size , (width + 2) * size, (height + 1) * size);
                wordColor = Color.white;
                shadowSwitch = true;
            }
            bottomColor =Color.black;
            wordFont = new Font("新宋体", Font.BOLD, size);
        }


        int x = size * 3/2;
        int y = size * 3/2;
        for (List<Object> line : text) {
            for (Object obj : line) {
                if (obj instanceof String) {
                    String str = (String) obj;
                    graphics.setFont(wordFont);//格式
                    if(shadowSwitch){
                        graphics.setColor(bottomColor);//底色
                        graphics.drawString(str, (x+4),y+4 + size);
                    }
                    graphics.setColor(wordColor);//字色
                    graphics.drawString(str, x,y + size);
                    x += str.length() * size ;
                }
                if (obj instanceof StringBuilder) {
                    String str = ((StringBuilder) obj).toString();
                    graphics.setFont(wordFont);//格式
                    if(shadowSwitch) {
                        graphics.setColor(bottomColor);//底色
                        graphics.drawString(str, ((width + 4 - str.length()) / 2 * size) + 4, y + size + 4);
                    }
                    graphics.setColor(wordColor);//字色
                    graphics.drawString(str, (width + 4 - str.length()) / 2 * size, y + size);
                    x = size / 2;
                }
                if (obj instanceof BufferedImage) {
                    BufferedImage bf = (BufferedImage) obj;
                    graphics.drawImage(bf, x, y, size, size, null);
                    x += size;
                }
                if (obj instanceof ImageInfo){
                    Image img = ((ImageInfo) obj).getImage();
                    int imgX = ((ImageInfo) obj).getX();
                    int imgY = ((ImageInfo) obj).getY();
                    int imgWidth = ((ImageInfo) obj).getWidthOfSize();
                    int imgHeight = ((ImageInfo) obj).getHeightOfSize();
                    graphics.drawImage(img,imgX,imgY,imgWidth * size * 2,imgHeight * size * 2,null);
                    x += imgWidth * size;
                }
                if (obj instanceof Integer) {
                    x += (int) obj * size;
                }
            }
            x = size * 3/2;
            y += size;
        }

        //右上角图标
        try {
            InputStream is = new ClassPathResource("/pic/logo.jpg").getInputStream();
            graphics.drawImage(ImageIO.read(is), image.getWidth() - size, 0, size, size, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
        return image;
    }

    /**
     * 默认字符大小50
     */
    public BufferedImage drawImage() {
        return drawImage(50,false);
    }
}
