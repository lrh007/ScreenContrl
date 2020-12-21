package screenremotecontrol.client;


import screenremotecontrol.ProtoMsg;
import screenremotecontrol.client.bean.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 组件的事件监听
 *
 * @Author lrh 2020/9/22 16:21
 */
public class ComponentListener {
    /**
     * 连接服务器事件监听（点击远程协助按钮）
     * @Author lrh 2020/9/22 16:34
     */
    public static void connectServerListener(JButton jButton,JTextField friendName,JLabel myClientName){
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clientName = friendName.getText().trim();
                if(!Const.CONNECT_SUCCESS){
                    JOptionPane.showMessageDialog(null,"请等待连接服务器");
                }else if("".equals(clientName)){
                    JOptionPane.showMessageDialog(null,"请输入伙伴识别码");
                }else if(clientName.length() != 9){
                    JOptionPane.showMessageDialog(null,"伙伴识别码错误");
                } else if(clientName.equals(Const.myClientName)){
                    JOptionPane.showMessageDialog(null,"伙伴识别码不能和自己相同");
                }else if(!ScreenClient.serverChannel.isActive() && !Const.CONNECT_RETRY){
                    JOptionPane.showMessageDialog(null,"服务器连接异常，请重启软件");
                    Const.CONNECT_SUCCESS = false;
                    Const.CONNECT_RETRY = true;
                    myClientName.setText("--- --- ---");
                    MainFrame.setErrorMsg(" 服务器连接异常，请重启软件");
                }else{
                    //发送消息给其他客户端，请求接受连接
                    ProtoMsg.Screen screen = ProtoMsg.Screen.newBuilder().setSendName(Const.myClientName).setReceiveName(clientName).setStatus(Const.STATUS_RECEIVE).build();
                    ScreenClient.serverChannel.writeAndFlush(screen);
                    System.out.println("发送消息给其他客户端，请求接受连接，channel="+ScreenClient.serverChannel.isActive());
                }
            }
        });
    }
    /**
     * 关闭MainFrame窗口时调用
     * @Author lrh 2020/9/23 18:08
     */
    public static void closeMainFrameListener(JFrame jFrame,JTextField friendName) {
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(ScreenClient.serverChannel != null && ScreenClient.serverChannel.isActive()){
                    ProtoMsg.Screen screen;
                    if(!"".equals(friendName.getText().trim())){
                        screen = ProtoMsg.Screen.newBuilder().setSendName(Const.myClientName).setReceiveName(friendName.getText().trim()).setStatus(Const.STATUS_CLOSE).build();
                    }else{
                        screen = ProtoMsg.Screen.newBuilder().setSendName(Const.myClientName).setReceiveName(Const.friendClientName).setStatus(Const.STATUS_CLOSE).build();
                    }
                    ScreenClient.serverChannel.writeAndFlush(screen);
                }
                System.out.println("关闭MainFrame,myClientName="+Const.myClientName+",friend="+Const.friendClientName+",client="+friendName.getText().trim());
                System.exit(0);
            }
        });
    }
    /**
     * 关闭ViewFrame窗口时调用
     * @Author lrh 2020/9/23 18:08
     */
    public static void closeViewFrameListener(JFrame jFrame,JTextField friendName) {
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(ScreenClient.serverChannel != null && ScreenClient.serverChannel.isActive()){
                    ProtoMsg.Screen screen;
                    if(!"".equals(friendName.getText().trim())){
                        screen = ProtoMsg.Screen.newBuilder().setSendName(Const.myClientName).setReceiveName(friendName.getText().trim()).setStatus(Const.STATUS_CLOSE).build();
                    }else{
                        screen = ProtoMsg.Screen.newBuilder().setSendName(Const.myClientName).setReceiveName(Const.friendClientName).setStatus(Const.STATUS_CLOSE).build();
                    }
                    ScreenClient.serverChannel.writeAndFlush(screen);
                }
                System.exit(0);
            }
        });
    }
    /**   
     * viewFrame 鼠标事件监听
     * @Author lrh 2020/9/24 9:33
     */
    public static void viewFrameMouseListener(JLabel jLabel, JTextField friendName) {
        jLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("鼠标拖拽。。。");
                setMouseInfo(e.getX(),e.getY(), Const.mouseDragged,e.getButton(),0);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("鼠标移动。。。");
                setMouseInfo(e.getX(),e.getY(),Const.mouseMoved,e.getButton(),0);
            }
        });
        jLabel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                System.out.println("鼠标滚轮滑动。。。"+e.getScrollAmount()+","+e.getScrollType()+","+e.getWheelRotation()+","+e.getUnitsToScroll());
                setMouseInfo(e.getX(),e.getY(),Const.mouseWheelMoved,e.getButton(),e.getUnitsToScroll());
            }
        });
        jLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("鼠标点击:"+e.getButton());
                setMouseInfo(e.getX(),e.getY(),Const.mouseClicked,e.getButton(),0);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("鼠标按下");
                setMouseInfo(e.getX(),e.getY(),Const.mousePressed,e.getButton(),0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("鼠标松开");
                setMouseInfo(e.getX(),e.getY(),Const.mouseReleased,e.getButton(),0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("鼠标进入组件");
                Const.MOUSE_ON = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                System.out.println("鼠标退出组件");
                Const.MOUSE_ON = false;
            }

        });
    }
    /**
     * 设置鼠标信息
     * @Author lrh 2020/9/24 15:08
     */
    public static void setMouseInfo(int x,int y,String action,int mouseType,int mouseWhileAmt){
        //向其他客户端发送鼠标消息
        if(ScreenClient.serverChannel != null && ScreenClient.serverChannel.isActive()){
            //使用protobuf序列化
            ProtoMsg.Mouse mouse = ProtoMsg.Mouse.newBuilder().setMouseX(x).setMouseY(y).setMouseAction(action).setMouseType(mouseType).setMouseWhileAmt(mouseWhileAmt).build();
            ProtoMsg.Screen screen = ProtoMsg.Screen.newBuilder()
                    .setSendName(Const.mouseSendClientName)
                    .setReceiveName(Const.mouseReceiveClientName)
                    .setStatus(Const.STATUS_AGREE)
                    .setMouse(mouse).build();

            ScreenClient.serverChannel.writeAndFlush(screen);
        }
    }

    /**   
     * ViewFrame 键盘事件监听
     * @Author lrh 2020/9/24 14:49
     */
    public static void viewFrameKeyBoardListener(JTextField friendName) {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                //只有鼠标在视图上面的时候才记录键盘信息
                if(Const.MOUSE_ON){
                    //向其他客户端发送键盘消息
                    if(ScreenClient.serverChannel != null && ScreenClient.serverChannel.isActive()){
                        KeyEvent keyEvent = (KeyEvent) event;
                        //使用protobuf序列化
                        ProtoMsg.KeyBoard keyBoard = ProtoMsg.KeyBoard.newBuilder().setKeyCode(keyEvent.getKeyCode()).setKeyAction(keyEvent.getID()).build();
                        ProtoMsg.Screen screen = ProtoMsg.Screen.newBuilder()
                                .setSendName(Const.mouseSendClientName)
                                .setReceiveName(Const.mouseReceiveClientName)
                                .setStatus(Const.STATUS_AGREE)
                                .setKeyBoard(keyBoard).build();

                        ScreenClient.serverChannel.writeAndFlush(screen);
                    }
                }
//                if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED) {
//                    //放入自己的键盘监听事件
//                    //((KeyEvent) event).getKeyCode();// 获取按键的code
//                    //((KeyEvent) event).getKeyChar();// 获取按键的字符
//                    System.out.println("键盘按键类型"+((KeyEvent) event).getKeyCode());
//                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }


    /**
     * 动态添加组件后刷新界面
     * @Author lrh 2020/10/13 16:36
     */
    public static void updateUI(JPanel component) {
        SwingUtilities.updateComponentTreeUI(component);//添加或删除组件后,更新窗口
    }
}
