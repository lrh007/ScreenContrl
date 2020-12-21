package screenremotecontrol.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import screenremotecontrol.ProtoMsg;

/** 初始化
 * @Author lrh 2020/9/21 14:54
 */
public class ScreenServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO)); //日志处理器
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
//        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null))); //对象解码器
//        pipeline.addLast(new ObjectEncoder()); //对象编码器
        pipeline.addLast(new ProtobufVarint32FrameDecoder());//主要用于Protobuf的半包处理
        pipeline.addLast(new ProtobufDecoder(ProtoMsg.Screen.getDefaultInstance())); //protobuf解码器
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());//主要用于Protobuf的半包处理
        pipeline.addLast(new ProtobufEncoder()); //protobuf编码器
        pipeline.addLast(new ScreenServerHandler()); //自定义处理器

    }
}
