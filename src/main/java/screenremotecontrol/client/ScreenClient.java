package screenremotecontrol.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import screenremotecontrol.client.bean.Const;


/**
 * 客户端
 *
 * @Author lrh 2020/9/21 15:10
 */
public class ScreenClient {

    /**   
     * 服务器通道
     * @Author lrh 2020/9/22 17:42
     */
    public static Channel serverChannel;

    /**   
     * 连接服务器
     * @Author lrh 2020/9/22 17:10
     */
    public static void connectServer(){
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ScreenClientInitializer())
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
        try {
            ChannelFuture future = bootstrap.connect(Const.SERVER_HOST, Const.SERVER_PORT).sync();
            serverChannel = future.channel();
            System.out.println("服务器连接成功。。。");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("服务器连接失败,错误信息： "+e.getMessage());
        }finally {
            group.shutdownGracefully();
        }
    }


    /*public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ScreenClientInitializer())
                .option(ChannelOption.SO_KEEPALIVE,true);
        try {
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            System.out.println("服务器连接成功。。。");
            Channel channel = future.channel();
            //输入数据的格式是，接收方名称:要发送的数据
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNext()){
                String s = scanner.nextLine();
                try{
                    String[] split = s.split(":");
                    ScreenData screenData = new ScreenData();
                    screenData.setSendName(ScreenClientHandler.myClientName);
                    screenData.setReceiveName(split[0]);
                    screenData.setContent(split[1]);
                    channel.writeAndFlush(screenData);
                }catch (Exception e){
                    System.out.println("输入不正确，请重新输入！");
                }

            }
//            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("服务器连接失败");
        }finally {
            group.shutdownGracefully();
        }
    }*/
}
