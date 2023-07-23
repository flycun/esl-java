package org.freeswitch.esl.client;

import com.google.common.base.Throwables;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.internal.IModEslApi;
import org.freeswitch.esl.client.internal.IModEslApi.EventFormat;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ClientExample {
    private static final Logger logger = LoggerFactory.getLogger(ClientExample.class);

    public static void main(String[] args) {
        try {
//            if (args.length < 1) {
//                System.out.println("Usage: java ClientExample PASSWORD");
//                return;
//            }

            String password = "ClueCon";

            Client client = new Client();

            client.addEventListener((ctx, event) -> logger.info("接收到事件: {}", event.getEventName()));

            client.connect(new InetSocketAddress("192.168.2.178", 8021), password, 10);
            client.setEventSubscriptions(EventFormat.PLAIN, "CHANNEL_ANSWER");
            test(client);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void test(Client client) {
        // 呼叫1002-播放语音
//        client.sendApiCommand("originate", "{ignore_early_media=true}user/601 &playback(/tmp/1.wav)");
//        client.sendBackgroundApiCommand("originate", "{ignore_early_media=true}user/601 &playback(/tmp/1.wav)");

          client.sendBackgroundApiCommand("status", "");
//        logger.info("api status return :{}",message.getBodyLines());
        // 呼叫手机-执行lua脚本
        // client.sendSyncApiCommand("originate", "{ignore_early_media=true}sofia/gateway/fs_sg/18621730742 &lua(welcome.lua)");
        // 建立1002和1000的通话
//             client.sendBackgroundApiCommand("originate", "user/601 &echo");

//            client.close();
    }
}
