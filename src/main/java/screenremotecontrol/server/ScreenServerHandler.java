package screenremotecontrol.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import screenremotecontrol.ProtoMsg;
import screenremotecontrol.client.bean.Const;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 业务处理
 * @Author lrh 2020/9/21 14:58
 */
public class ScreenServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 保存所有的客户端信息<br>
     * key = 客户端名称，value = Channel
     * @Author lrh 2020/9/21 15:42
     */
    public static ConcurrentHashMap<String, Channel> CLIENTMAP = new ConcurrentHashMap<>();


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String randomName = Utils.getRandomName();
        while (CLIENTMAP.containsKey(randomName)) {
            randomName = Utils.getRandomName();
        }
        System.out.println("客户端上线，地址："+ctx.channel().remoteAddress()+" ，名称： "+randomName);
        CLIENTMAP.put(randomName,ctx.channel());
        //向客户端发送注册成功的名称
        ProtoMsg.Screen screen = ProtoMsg.Screen.newBuilder().setContent(randomName).setStatus(Const.STATUS_CONTINUE).build();
        ctx.channel().writeAndFlush(screen);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtoMsg.Screen screenData = (ProtoMsg.Screen) msg;
        Channel otherChannel = CLIENTMAP.get(screenData.getReceiveName());
        //数据转发指定的客户端
        if(otherChannel != null){
            otherChannel.writeAndFlush(screenData);
        }else{
            //如果接收方是ALL,那么就将数据发送给全部的客户端，除了自己
            if("ALL".equalsIgnoreCase(screenData.getReceiveName())){
                for (Map.Entry<String,Channel> entry:CLIENTMAP.entrySet()){
                    if(entry.getValue() != ctx.channel()){
                        entry.getValue().writeAndFlush(screenData);
                    }
                }
            }else{
                //接收方不是ALL,并且找不到的情况下，直接返回提示信息
                ProtoMsg.Screen screen = ProtoMsg.Screen.newBuilder()
                        .setSendName(screenData.getSendName())
                        .setReceiveName(screenData.getReceiveName())
                        .setContent("【" + screenData.getReceiveName() + " 客户端不在线】")
                        .setStatus(Const.STATUS_NOT_FOUND).build();
                ctx.channel().writeAndFlush(screen);
            }
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String clientName = null;
        for (Map.Entry<String, Channel> entry : CLIENTMAP.entrySet()){
            if(entry.getValue() == ctx.channel()){
                clientName = entry.getKey();
                break;
            }
        }
        System.out.println("客户端离线，地址："+ctx.channel().remoteAddress()+" ，名称： "+clientName);
        if(clientName != null){
            CLIENTMAP.remove(clientName);
        }else{
            System.out.println("删除客户端channel失败，客户端地址： "+ctx.channel().remoteAddress());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(ctx.channel().isActive()){
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        cause.printStackTrace();
        ctx.close();
    }
}
