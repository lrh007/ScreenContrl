package screenremotecontrol.client;


import screenremotecontrol.ProtoMsg;
import screenremotecontrol.client.bean.Const;
import screenremotecontrol.client.bean.ImageData;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.*;

import static java.awt.Frame.MAXIMIZED_BOTH;

/**
 * 视图窗口，用来展示图像
 *
 * @Author lrh 2020/9/23 14:35
 */
public class ViewFrame {
    private static ViewFrame INSTANCE;
    private JFrame jFrame = new JFrame("view");
    private JPanel jPanel = new JPanel();
    // 创建滚动面板, 指定滚动显示的视图组件(textArea), 垂直滚动条一直显示, 水平滚动条从不显示
    private JScrollPane jScrollPane = new JScrollPane();
    /**
     * 保存所有的JLabel组件
     * @Author lrh 2020/10/13 16:44
     */
    public static ConcurrentHashMap<Integer,JLabel> allJLables = new ConcurrentHashMap<>();
    /**
     * 创建线程池,总共10个线程
     * @Author lrh 2020/10/14 9:18
     */
    public static ExecutorService threadPool = Executors.newFixedThreadPool(30);
    /**   
     * 设置队列，最大容量为1000，超过这个容量就等待
     * @Author lrh 2020/10/16 10:45
     */
    public static BlockingQueue<ImageData> blockingQueue = new LinkedBlockingQueue<>(100);


    private ViewFrame() {
        try { // 使用当前系统的界面风格
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            System.out.println("系统界面风格设置失败");
        }
        init();
        addListener();
    }
    
    /**   
     * 初始化
     * @Author lrh 2020/9/23 15:48
     */
    private void init(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setLocation((int)screenSize.getWidth()/2- Const.VIEW_FRAME_WIDTH/2,(int)screenSize.getHeight()/2-Const.VIEW_FRAME_HEIGHT/2);
        jFrame.setSize(Const.VIEW_FRAME_WIDTH,Const.VIEW_FRAME_HEIGHT);
        jFrame.setExtendedState(MAXIMIZED_BOTH);//窗口启动之后最大化
        jPanel.setPreferredSize(new Dimension(screenSize.width,screenSize.height));
        jPanel.setLayout(null);
        jPanel.setBackground(Color.BLACK);
        //jscrollpane添加jpanel的时候不能使用add方法，否则会不显示，应该使用setViewportView
        jScrollPane.setViewportView(jPanel);
        jFrame.add(jScrollPane);
        jFrame.setAlwaysOnTop(true); //窗口总是在最前面
        jFrame.setVisible(true);
    }
    /**   
     * 获取单例的视图对象
     * @Author lrh 2020/9/23 15:50
     */
    public static ViewFrame INSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new ViewFrame();
        }
        return INSTANCE;
    }
    /**   
     * ViewFrame窗体销毁
     * @Author lrh 2020/9/25 15:57
     */
    public static void dispose(){
        INSTANCE().jFrame.dispose();
        INSTANCE = null;
    }
    /**
     * 展示图片
     * @Author lrh 2020/9/23 15:59
     */
    public void showView(ProtoMsg.Image imageData){
        //第四种方式，分块传输，动态创建JLabel，优点：是不用合并图片，缺点：需要创建多个JLabel
        if(allJLables.get(imageData.getNumber()) != null){
            JLabel jLabel = allJLables.get(imageData.getNumber());
            byte[] bytes = Util.unzipString2(imageData.getData().toByteArray());
            ImageIcon imageIcon = new ImageIcon(bytes);
            jLabel.setIcon(imageIcon);
        }else{
            JLabel jLabel = new JLabel();
            jLabel.setOpaque(true);
            jLabel.setBackground(Color.black);
            jLabel.setBounds(imageData.getX(),imageData.getY(),imageData.getWidth(),imageData.getHeight());
            jPanel.add(jLabel);
            allJLables.put(imageData.getNumber(),jLabel);
            System.out.println("allJLables="+allJLables.size());
            //第一次直接将图片显示出来
            byte[] bytes = Util.unzipString2(imageData.getData().toByteArray());
            ImageIcon imageIcon = new ImageIcon(bytes);
            jLabel.setIcon(imageIcon);
            ComponentListener.updateUI(jPanel);
        }
    }
    /**
     * 展示图片
     * @Author lrh 2020/9/23 15:59
     */
    public void showView2(ImageData imageData){
        //第四种方式，分块传输，动态创建JLabel，优点：是不用合并图片，缺点：需要创建多个JLabel
        if(allJLables.get(imageData.getNumber()) != null){
            JLabel jLabel = allJLables.get(imageData.getNumber());
            byte[] bytes = Util.unzipString(imageData.getData());
//            BufferedImage image = Util.webpDecode(bytes);
            ImageIcon imageIcon = new ImageIcon(bytes);
            jLabel.setIcon(imageIcon);

        }else{
            JLabel jLabel = new JLabel();
            jLabel.setOpaque(true);
//            jLabel.setBackground(Color.black);
            jLabel.setBounds(imageData.getX(),imageData.getY(),imageData.getWidth(),imageData.getHeight());
            jPanel.add(jLabel);
            allJLables.put(imageData.getNumber(),jLabel);
            System.out.println("allJLables="+allJLables.size());
            //第一次直接将图片显示出来
            byte[] bytes = Util.unzipString(imageData.getData());
//            BufferedImage image = Util.webpDecode(bytes);
            ImageIcon imageIcon = new ImageIcon(bytes);
            jLabel.setIcon(imageIcon);
            ComponentListener.updateUI(jPanel);
        }
    }
    /**
     * 事件监听
     * @Author lrh 2020/9/22 16:22
     */
    private void addListener(){
        ComponentListener.closeViewFrameListener(jFrame,MainFrame.friendName);
//        ComponentListener.viewFrameMouseListener(jLabel,MainFrame.friendName);
        ComponentListener.viewFrameKeyBoardListener(MainFrame.friendName);

    }

    public static void main(String[] args) throws Exception {
        showImage3();
    }




    public static void showImage3() throws Exception{
        ViewFrame instance = INSTANCE();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Rectangle rectangle = new Rectangle(screenSize);
//        Rectangle rectangle = new Rectangle(100,200,300,300);

        Robot robot = new Robot();
        ConcurrentHashMap<Integer, ImageData> beforeImageData = new ConcurrentHashMap<>(); //存放上一次的图片数据，用来和这次进行对比
        while (true){
            Thread.sleep(30);
            BufferedImage screenCapture = robot.createScreenCapture(rectangle);

//            Map<Integer, ImageData> imageDatas = Util.splitImageAndNum((int)(screenSize.width*0.9), (int)(screenSize.height*0.9), 0,screenCapture);
            Map<Integer, ImageData> imageDatas = Util.splitImageAndNum(screenSize.width, screenSize.height, 0,screenCapture);
//            Map<Integer, ImageData> imageDatas = Util.splitImageAndNum(300, 300, 0,screenCapture);
            for (int i=0;i<imageDatas.size();i++){
                final ImageData data = imageDatas.get(i);
                //如果是第一次，就将当前的数据保存
                if(beforeImageData.get(data.getNumber()) == null){
                    beforeImageData.put(data.getNumber(),data);
                }
                final int j = i;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
//                        Rectangle rectangle = new Rectangle(data.getX(),data.getY(),data.getWidth(),data.getHeight());
                        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();

                        long s1 = System.currentTimeMillis();
                        //用来保存之前的数据进行异或操作
                        BufferedImage beforeBufferedImage = beforeImageData.get(data.getNumber()).getBufferedImage();
                        boolean b = Util.compareImageData(j, data.getBufferedImage(), beforeBufferedImage);
                        System.out.println("图片是否相同= "+b+",耗时="+(System.currentTimeMillis()-s1));
                        if(!b){
                            //将原来的图片替换
                            beforeImageData.get(j).setBufferedImage(data.getBufferedImage());
                            //将图片进行异或操作
//                            BufferedImage xorImageData = Util.getXorImageData(data.getNumber(), data.getBufferedImage(), beforeBufferedImage);

                            //将图片进行还原操作
//                            BufferedImage bufferedImage = Util.restoreXorImageData(beforeBufferedImage, xorImageData);
                            byte[] bytes = Util.encodeImage(data.getBufferedImage());
//                            byte[] bytes2 = Util.encodeImage(xorImageData);
//                            byte[] bytes = Util.webpEncode2(data.getBufferedImage(), 0.7f);
//                            byte[] bs = Util.zipString2(bytes);
//                            System.out.println("bs压缩后图片大小："+bs.length/1024+"，之前大小="+bytes.length/1024);
                            String imageData = Util.zipString(bytes);
                            System.out.println("压缩之后图片大小="+imageData.getBytes().length/1024);
                            ImageData dataImage = new ImageData(imageData,data.getX(),data.getY(),data.getHeight(),data.getWidth(),null,j,screenSize.width,screenSize.height);
                            instance.showView2(dataImage);
//                            byte[] bytes2 = Util.decodeUnCompress(imageData);
//                            BufferedImage image = Util.webpDecode(bytes);
//                            ImageIO.write(image,"webp",new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\"+new Random().nextInt(Integer.MAX_VALUE)+".jpg"));
                        }
                        byteArrayStream.reset();
                        byteArrayStream = null;

                    }
                });

            }
        }
    }


}
