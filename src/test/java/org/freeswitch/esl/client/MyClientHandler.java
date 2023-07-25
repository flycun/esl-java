package org.freeswitch.esl.client;

import org.freeswitch.esl.client.dptools.Execute;
import org.freeswitch.esl.client.dptools.ExecuteException;
import org.freeswitch.esl.client.internal.Context;
import org.freeswitch.esl.client.outbound.IClientHandler;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.freeswitch.esl.client.transport.message.EslHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MyClientHandler implements IClientHandler {

    private static Logger logger = LoggerFactory.getLogger(MyClientHandler.class);

    @Override
    public void onConnect(Context context, EslEvent eslEvent) {


//        logger.warn(nameMapToString(eslEvent.getMessageHeaders(), eslEvent.getEventBodyLines()));

        String uuid = eslEvent.getEventHeaders().get("Unique-ID");

        logger.warn("Creating execute app for uuid1 {}", uuid);

        Execute exe = new Execute(context, uuid);

        try {
            doBridge(exe);
//            context.sendBackgroundApiCommand("status", "");
//            context.sendApiCommand("status", "");
//            exe.preAnswer();
//            context.sendBackgroundApiCommand("originate", "{ignore_early_media=true}user/601 &playback(/tmp/1.wav)");
//            String status = exe.ApiCommand("status", "");
//
//
//            logger.info("--->status:"+status);
//            exe.playback("/tmp/1.wav");
//            logger.info("end playback.");
//            exe.hangup();

//            String digits = exe.playAndGetDigits(3, 5, 10, 10 * 1000, "#", prompt,
//                    failed, "^\\d+", 10 * 1000);
//            logger.warn("Digits collected: {}", digits);

        } catch (Exception e) {
            logger.error("Could not prompt for digits", e);
        } finally {
            context.closeChannel();
//            try {
//                exe.hangup(null);
//            } catch (ExecuteException e) {
//                logger.error("Could not hangup", e);
//            }
        }
    }

    @Override
    public void onEslEvent(Context ctx, EslEvent event) {
        logger.info("--> OUTBOUND onEslEvent: {}", event.getEventName());
    }

    public static String nameMapToString(Map<EslHeaders.Name, String> map,
                                         List<String> lines) {
        StringBuilder sb = new StringBuilder("\nHeaders:\n");
        for (EslHeaders.Name key : map.keySet()) {
            if (key == null)
                continue;
            sb.append(key.toString());
            sb.append("\n\t\t\t\t = \t ");
            sb.append(map.get(key));
            sb.append("\n");
        }
        if (lines != null) {
            sb.append("Body Lines:\n");
            for (String line : lines) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void doBridge(Execute exe ) {
        try {

            exe.set("ringback","/tmp/1.wav");
//            exe.answer();
                exe.export("effective_caller_id_name", "jimmy", true);
                exe.export("effective_caller_id_number", "111111", true);
//            exe.export("origination_caller_id_name", "yjmyzz", true);
//            exe.export("origination_caller_id_number", "139****7777", true);
            exe.bridge("user/611");
        } catch (ExecuteException e) {
            e.printStackTrace();
        }
    }
}
