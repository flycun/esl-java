package org.freeswitch.esl.client;

import org.freeswitch.esl.client.outbound.IClientHandler;
import org.freeswitch.esl.client.outbound.SocketClient;
import org.freeswitch.esl.client.transport.message.EslHeaders.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class OutboundTest {
    private static Logger logger = LoggerFactory.getLogger(OutboundTest.class);


    public static void main(String[] args) {
        new OutboundTest();
    }

    public OutboundTest() {
        try {
            final SocketClient outboundServer = new SocketClient(
                    new InetSocketAddress("192.168.2.230", 8084),
                    MyClientHandler::new);
            outboundServer.startAsync();

        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
    }


}
