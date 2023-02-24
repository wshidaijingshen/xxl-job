package com.xxl.job.core.util;

import cn.hutool.http.HttpUtil;

import java.time.LocalDateTime;
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

    public static String send(Object content,Integer... userId) {
       return send(null, content, userId);
    }

    public static String send(Sender type, Object content,Integer... userId) {
        type = type == null ? Sender.TELEGRAM : type;
        if (type== Sender.TELEGRAM) {
            return sendTG(content,userId);
        }
        return "";
    }

    public static String sendTG(Object content,Integer... userId) {
        // https://hutool.cn/docs/#/http/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");

        return HttpUtil.get(tgApiPrefix + "sendMessage", new HashMap() {{
            put("chat_id", userId);
            put("disable_notification", true);
            put("parse_mode", "MarkdownV2");
            put("protect_content", true);
            put("allow_sending_without_reply", true);
            put("text", "`"+LocalDateTime.now() + "`\n" + content);
        }}, 3000);
    }
}

enum Sender{
    TELEGRAM
}