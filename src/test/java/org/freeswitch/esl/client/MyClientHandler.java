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
    public void onConnect(Context context, EslEvent event) {

        System.out.println("Received connect response :" + event);

        String uuid = event.getEventHeaders().get("Unique-ID");
        logger.warn("Creating execute app for uuid {}", uuid);
        Execute exe = new Execute(context, uuid);
        try {
//            doBridge(exe);
//        originate(exe);
            event(context);
        } catch (Exception e) {
            logger.error("Could not prompt for digits", e);
        } finally {
//            context.closeChannel();
//            try {
//                exe.hangup(null);
//            } catch (ExecuteException e) {
//                logger.error("Could not hangup", e);
//            }
        }
    }

    @Override
    public void onEslEvent(Context ctx, EslEvent event) {
        String eventName = event.getEventName();
        if (eventName.equalsIgnoreCase("CUSTOM") || eventName.contains("HANGUP_COMPLETE")) {
            String ani = event.getEventHeaders().get("Caller-ANI");
            String myvar = event.getEventHeaders().get("MY-VAR-1");
            logger.info("INBOUND===> eventName: " + event.getEventName() + ", ani = " + ani + ", myvar = " + myvar);
        }
    }


    private void doBridge(Execute exe) {
        try {

//            exe.set("ringback","/tmp/1.wav");
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

    private void originate(Execute exe) throws ExecuteException {
//        /            context.sendBackgroundApiCommand("originate", "{ignore_early_media=true}user/601 &playback(/tmp/1.wav)");
        //originate {ignore_early_media=true,
        // call_timeout=60,hangup_after_bridge=false,
        // origination_uuid=新的uuid,
        // origination_caller_id_number=主叫号码,
        // origination_caller_id_name=主叫号码显示名称}user/目标号码 &park()
        String uuid = "4a474304-e013-46f7-94e9-56a3055b3766";
        String origination_caller_id_number = "118100";
        String origination_caller_id_name = "imusic";

        exe.ApiCommand("originate", "{ignore_early_media=true,call_timeout=60,hangup_after_bridge=false,origination_uuid=111222,origination_caller_id_number=118100,origination_caller_id_name=music}user/611 &park()");

        exe.answer();
    }

    private void event(Context context) throws ExecuteException {
        Execute exe = new Execute(context, null);
        context.sendCommand("event plain CHANNEL_CREATE CHANNEL_HANGUP CHANNEL_HANGUP_COMPLETE CHANNEL_DESTROY CUSTOM callcenter::info");
        event2(context);
    }

    private void event2(Context context) throws ExecuteException {
        Execute exe = new Execute(context, null);
        StringBuilder sbEvent = new StringBuilder();
        sbEvent.append("Event-Name=").append("CUSTOM").append(",");
        sbEvent.append("Event-Subclass=").append("callcenter::info").append(",");
        //自定义事件中的变量（根据业务需求，可自行添加，注：系统变量并不能覆盖，比如下面的Caller-ANI）
        sbEvent.append("Caller-ANI=").append("999999").append(",");
        //只有业务新增的变量，赋值才有意义
        sbEvent.append("MY-VAR-1=").append("abcdefg").append(",");
        //触发自定义事件
        exe.event(sbEvent.toString());

        //其它处理（这里只是示例调用了echo）
        exe.echo();
    }


}
