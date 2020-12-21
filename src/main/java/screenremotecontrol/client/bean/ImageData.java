package screenremotecontrol.client.bean;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 图片实体类
 *
 * @Author lrh 2020/9/25 13:31
 */
public class ImageData implements Serializable {
    /**   
     * 图片数据
     * @Author lrh 2020/9/25 13:32
     */
    private String data;
    /**
     * 图片X坐标
     * @Author lrh 2020/9/28 12:04
     */
    private int x;
    /**
     * 图片y坐标
     * @Author lrh 2020/9/28 12:04
     */
    private int y;
    /**
     * 图片高度
     * @Author lrh 2020/9/28 12:04
     */
    private int height;
    /**
     * 图片宽度
     * @Author lrh 2020/9/28 12:04
     */
    private int width;
    /**
     * 图片分割缓冲区，仅用于分割图片时使用
     * @Author lrh 2020/9/28 12:12
     */
    private BufferedImage bufferedImage;
    /**
     * 图片编号
     * @Author lrh 2020/10/9 15:36
     */
    private int number;
    /**   
     * 屏幕总宽度
     * @Author lrh 2020/10/13 15:38
     */
    private int screenWidth;
    /**   
     * 屏幕总高度
     * @Author lrh 2020/10/13 15:38
     */
    private int screenHeight;
    /**   
     * 裁剪后图片X坐标
     * @Author lrh 2020/10/14 11:11
     */
    private int miniX;
    /**   
     * 裁剪后图片Y坐标
     * @Author lrh 2020/10/14 11:11
     */
    private int miniY;
    /**   
     * 裁剪后图片的宽度
     * @Author lrh 2020/10/14 11:12
     */
    private int miniWidth;
    /**   
     * 裁剪后图片的高度
     * @Author lrh 2020/10/14 11:12
     */
    private int miniHeight;


    public ImageData() {
    }

    public ImageData(String data, int x, int y, int height, int width, BufferedImage bufferedImage,int number,int screenWidth,int screenHeight) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.bufferedImage = bufferedImage;
        this.number = number;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public ImageData(String data, int x, int y, int height, int width,int number,int screenWidth,int screenHeight) {
        this(data,x,y,height,width,null,number,screenWidth,screenHeight);
    }

    public ImageData(String data) {
        this(data,0,0,0,0,0,0,0);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getMiniX() {
        return miniX;
    }

    public void setMiniX(int miniX) {
        this.miniX = miniX;
    }

    public int getMiniY() {
        return miniY;
    }

    public void setMiniY(int miniY) {
        this.miniY = miniY;
    }

    public int getMiniWidth() {
        return miniWidth;
    }

    public void setMiniWidth(int miniWidth) {
        this.miniWidth = miniWidth;
    }

    public int getMiniHeight() {
        return miniHeight;
    }

    public void setMiniHeight(int miniHeight) {
        this.miniHeight = miniHeight;
    }
}
