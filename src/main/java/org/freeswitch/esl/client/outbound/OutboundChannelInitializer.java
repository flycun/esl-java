package org.freeswitch.esl.client.outbound;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.freeswitch.esl.client.transport.message.EslFrameDecoder;

import java.util.concurrent.*;

public class OutboundChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final IClientHandlerFactory clientHandlerFactory;
    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("outbound-pool-%d").build();

    public ExecutorService callbackExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10000), namedThreadFactory);
    private static ThreadFactory onConnectThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("outbound-onConnect-pool-%d").build();
    private static ExecutorService onConnectExecutor = new ThreadPoolExecutor(32, 512,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2048), onConnectThreadFactory);
    public OutboundChannelInitializer(IClientHandlerFactory clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public OutboundChannelInitializer setCallbackExecutor(ExecutorService callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
        return this;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // Add the text line codec combination first
        pipeline.addLast("encoder", new StringEncoder());
        // Note that outbound mode requires the decoder to treat many 'headers' as body lines
        pipeline.addLast("decoder", new EslFrameDecoder(8092, true));
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        // now the outbound client logic
        pipeline.addLast("clientHandler",
                new OutboundClientHandler(clientHandlerFactory.createClientHandler(), callbackExecutor, onConnectExecutor));
    }
}
