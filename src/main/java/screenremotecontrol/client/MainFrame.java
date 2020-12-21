package screenremotecontrol.client;


import screenremotecontrol.client.bean.Const;

import javax.swing.*;
import java.awt.*;

/** 主窗体
 * @Author lrh 2020/9/22 14:01
 */
public class MainFrame {
    public static JFrame jFrame = new JFrame("screen");
    private JPanel jPanel = new JPanel();
    private JLabel jLabel = new JLabel("本机识别码");
    private JLabel jLabel2 = new JLabel("伙伴识别码");
    private JLabel myClientName = new JLabel("--- --- ---");
    public static JTextField friendName = new JTextField();
    private JButton connectBtn = new JButton("远程协助");
    public static JLabel tips = new JLabel(" 正在连接服务器...");

    public MainFrame() {
        try { // 使用当前系统的界面风格
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            System.out.println("系统界面风格设置失败");
        }
        init();
        addListener();
        connectServer();
    }

    /**   
     * 初始化
     * @Author lrh 2020/9/22 16:35
     */
    private void init(){

        jFrame.setSize(Const.FRAME_WIDTH,Const.FRAME_HEIGHT); //窗体大小
        jFrame.setResizable(false); //禁止调节窗体大小
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setLocation((int)screenSize.getWidth()/2-Const.FRAME_WIDTH/2,(int)screenSize.getHeight()/2-Const.FRAME_HEIGHT/2);

        jLabel.setBounds(20,50,100,20);
        myClientName.setBounds(25,90,140,40);
        jLabel2.setBounds(300,50,100,20);
        friendName.setBounds(300,90,290,50);
        friendName.setSize(280,40);
        connectBtn.setBounds(350,200,150,50);
        tips.setBounds(0,Const.FRAME_HEIGHT-70,Const.FRAME_WIDTH,35);
        tips.setOpaque(true);//设置成背景不透明
        tips.setBackground(Color.decode("#e5e3e3"));
        jPanel.setBackground(Color.WHITE);

        jLabel.setFont(Const.FONT(20));
        myClientName.setFont(Const.FONT(26));
        jLabel2.setFont(Const.FONT(20));
        friendName.setFont(Const.FONT(26));
        connectBtn.setFont(Const.FONT(20));
        tips.setFont(Const.FONT(18));

        jPanel.setLayout(null); //使用绝对定位
        jPanel.add(jLabel);
        jPanel.add(myClientName);
        jPanel.add(jLabel2);
        jPanel.add(friendName);
        jPanel.add(connectBtn);
        jPanel.add(tips);
        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }
    /**   
     * 事件监听
     * @Author lrh 2020/9/22 16:22
     */
    private void addListener(){
        ComponentListener.connectServerListener(connectBtn,friendName,myClientName);
        ComponentListener.closeMainFrameListener(jFrame,friendName);
    }
    /**   
     * 设置普通提示信息
     * @Author lrh 2020/9/22 16:18
     */
    public static void setMsg(String msg){
        tips.setText("");
        tips.setText(msg);
    }
    /**   
     * 设置错误提示信息
     * @Author lrh 2020/9/22 16:40
     */
    public static void setErrorMsg(String msg){
        tips.setForeground(Color.red);
        tips.setText("");
        tips.setText(msg);
    }
    /**
     * 设置成功提示信息
     * @Author lrh 2020/9/22 17:56
     */
    public static void setSuccessMsg(String msg){
        tips.setForeground(Color.decode("#2db60d"));
        tips.setText("");
        tips.setText(msg);
    }
    /**   
     * 连接服务器
     * @Author lrh 2020/9/22 17:27
     */
    public void connectServer(){
        try {
            glintChar();
            while(true){
                if(Const.myClientName != null && ScreenClient.serverChannel != null && ScreenClient.serverChannel.isActive()){
                    Const.CONNECT_SUCCESS = true;
                    Const.CONNECT_RETRY = false;
                    myClientName.setText(Const.myClientName);
                    setSuccessMsg(" 服务器连接成功");
                    break;
                }
                //连接服务器
                connect();
                System.out.println("等待连接服务器。。。");
                Thread.sleep(6000); //6秒重试连接一次，前提必须是已经设置连接超时时间
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void connect(){
        //连接服务器的放到单独的线程中去执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScreenClient.connectServer();
            }
        }).start();
    }
    /**
     * 连接服务器的时候提示信息闪烁
     * @Author lrh 2020/9/23 9:20
     */
    private static void glintChar(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Const.CONNECT_SUCCESS){
                    setMsg(" 正在连接服务器...");
                    try {
                        Thread.sleep(500);
                        setMsg("");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public static void main(String[] args) {
        new MainFrame();
    }
}
