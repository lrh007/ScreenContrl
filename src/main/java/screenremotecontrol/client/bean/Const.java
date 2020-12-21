package screenremotecontrol.client.bean;

import java.awt.*;

/**
 * 常量类
 *
 * @Author lrh 2020/9/22 15:59
 */
public class Const {

    /**
     * 客户端自己的名称
     * @Author lrh 2020/9/22 9:55
     */
    public static String myClientName = "";
    /**   
     * 其他客户端的名称
     * @Author lrh 2020/9/24 10:16
     */
    public static String friendClientName = "";

    /**
     * 服务器ip
     * @Author lrh 2020/9/22 17:30
     */
    public static final String SERVER_HOST = "127.0.0.1";
    /**
     * 服务器端口号
     * @Author lrh 2020/9/22 17:31
     */
    public static final int SERVER_PORT = 9527;
    /**   
     * 用来判断服务器是否连接成功
     * @Author lrh 2020/9/22 18:13
     */
    public static boolean CONNECT_SUCCESS = false;
    /**   
     * 服务器重新连接标识
     * @Author lrh 2020/9/23 10:34
     */
    public static boolean CONNECT_RETRY = false;
    /**   
     * 连接关闭标识
     * @Author lrh 2020/9/23 18:16
     */
    public static boolean CONNECT_CLOSE = false;
    /**   
     * 鼠标是否在ViewFrame上的标识
     * @Author lrh 2020/9/24 9:25
     */
    public static boolean MOUSE_ON = false;
    /**   
     * 窗体宽度
     * @Author lrh 2020/9/22 16:00
     */
    public static final int FRAME_WIDTH = 600;
    /**   
     * 窗体高度
     * @Author lrh 2020/9/22 16:00
     */
    public static final int FRAME_HEIGHT = 400;
    /**   
     * 视图窗体宽度
     * @Author lrh 2020/9/23 15:02
     */
    public static final int VIEW_FRAME_WIDTH = 1000;
    /**   
     * 视图窗体高度
     * @Author lrh 2020/9/23 15:02
     */
    public static final int VIEW_FRAME_HEIGHT = 900;
    /**   
     * 获取指定大小的字体
     * @Author lrh 2020/9/22 16:02
     */
    public static Font FONT(int fontSize){
        return new Font(Font.SERIF,Font.PLAIN,fontSize);
    }
    /**
     * 给客户端发送，询问是否接受连接请求
     * @Author lrh 2020/9/23 10:09
     */
    public static final int STATUS_RECEIVE = 1;
    /**
     * 客户端之间断开连接，不用再相互传输数据了
     * @Author lrh 2020/9/23 13:57
     */
    public static final int STATUS_CLOSE = 3;
    /**   
     * 拒绝连接
     * @Author lrh 2020/9/23 14:18
     */
    public static final int STATUS_REJECT = 4;
    /**   
     * 同意连接
     * @Author lrh 2020/9/23 14:19
     */
    public static final int STATUS_AGREE = 5;
    /***   
     * 找不到客户端
     * @Author lrh 2020/9/23 11:14
     */
    public static final int STATUS_NOT_FOUND = 404;
    /**   
     * 允许继续操作
     * @Author lrh 2020/10/20 15:26
     */
    public static final int STATUS_CONTINUE = 100;
    /**   
     * 发送数据的间隔，单位毫秒
     * @Author lrh 2020/9/24 14:36
     */
    public static final int SEND_DATA_INTERVAL = 30;

    /**   
     * 鼠标拖拽事件
     * @Author lrh 2020/9/24 15:10
     */
    public static final String mouseDragged = "mouseDragged";
    /**   
     * 鼠标移动事件
     * @Author lrh 2020/9/24 15:10
     */
    public static final String mouseMoved = "mouseMoved";
    /**   
     * 鼠标滚轮滑动事件
     * @Author lrh 2020/9/24 15:11
     */
    public static final String mouseWheelMoved = "mouseWheelMoved";
    /**   
     * 鼠标点击事件
     * @Author lrh 2020/9/24 15:12
     */
    public static final String mouseClicked = "mouseClicked";
    /**   
     * 鼠标按下事件
     * @Author lrh 2020/9/24 15:12
     */
    public static final String mousePressed = "mousePressed";
    /**   
     * 鼠标松开事件
     * @Author lrh 2020/9/24 15:18
     */
    public static final String mouseReleased = "mouseReleased";
    /**
     * 鼠标发送消息时，自己的客户端名称
     * @Author lrh 2020/9/24 17:14
     */
    public static String mouseSendClientName = "";
    /**
     * 鼠标发送消息时，接受数据的客户端名称
     * @Author lrh 2020/9/24 17:15
     */
    public static String mouseReceiveClientName = "";
    
    /**   
     * 键盘事件
     * @Author lrh 2020/9/24 14:48
     */
    public static String keyBoardAction = "";
    /**   
     * 缩放比例
     * @Author lrh 2020/10/19 18:03
     */
    public static final double scale = 0.9;

}
