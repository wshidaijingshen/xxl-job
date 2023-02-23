package com.xxl.job.core.util;

import cn.hutool.http.HttpUtil;

import java.util.HashMap;

/**
 * 发送消息
 *<p/>
 * 1. tg api params: https://core.telegram.org/bots/api#sendmessage
 * @link https://api.telegram.org/bot5398463677:AAF69yqSha-tKBa39Y3tKpcNILhqfKOF3a4/sendMessage?chat_id=1570338227&text=111
 *
 * @author stormfeng
 * @date 2023-02-23  23:50
 */
public class MsgUtil {
    public static final String tgApiPrefix = "https://api.telegram.org/bot5398463677:AAF69yqSha-tKBa39Y3tKpcNILhqfKOF3a4/";

    public static void send(Integer userId, Object content) {
        send(null, userId, content);
    }

    public static void send(Sender type, Integer userId, Object content) {
        type = type == null ? Sender.TELEGRAM : type;
        if (type== Sender.TELEGRAM) {
            sendTG(userId, content);
        }
    }

    public static void sendTG(Integer userId, Object content) {
        final String s = HttpUtil.get(tgApiPrefix+"sendMessage", new HashMap() {{
            put("chat_id", userId);
            put("disable_notification", true);
            put("parse_mode", "HTML");
            put("protect_content", true);
            put("allow_sending_without_reply", true);
            put("text", content);
        }});
    }
}

enum Sender{
    TELEGRAM
}