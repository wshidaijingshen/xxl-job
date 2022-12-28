package com.xxl.job.admin.core.alarm.impl;

import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * job alarm by telegram
 * <p>
 * TG API : https://core.telegram.org/bots/api#sendmessage
 *
 * how to send emoji? https://stackoverflow.com/a/34610263
 *  - 在页面上，直接复制这里的表情 https://apps.timwhitlock.info/emoji/tables/unicode
 *
 * @author stormfeng
 */
@Component
@Slf4j
public class TelegramJobAlarm implements JobAlarm {
    @Value("${server.servlet.context-path}")
    String contextPath;

    @Value("${server.port}")
    Integer port;

    @Value("${tg.bot.id}")
    String stormFengBotId;

    @Value("${tg.user.id}")
    String userId;

    @Value("${tg.bot.api-prefix}")
    String apiPrefix;

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        if (null == info) {
            return true;
        }

        boolean alarmResult = true;

        // alarmContent
        String alarmContent = "\n<code>Alarm Job LogId=" + jobLog.getId();
        if (ReturnT.SUCCESS_CODE != jobLog.getTriggerCode()) {
            alarmContent += "\nTriggerMsg=\n" + jobLog.getTriggerMsg()+"</code>";
        }
        if (jobLog.getHandleCode() > 0 && ReturnT.SUCCESS_CODE != jobLog.getHandleCode()) {
            alarmContent += "\nHandleCode=" + jobLog.getHandleMsg();
        }

        alarmContent = removeUnknownHtmlTag(alarmContent);

        // email info
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
        String content = String.format(alarmTemplate(contextPath, port), group != null ? group.getTitle()+"\uD83D\uDCA9" : "\uD83D\uDCA9", info.getId(), info.getJobDesc(), alarmContent);
        try {
            // api params: https://core.telegram.org/bots/api#sendmessage
            // 测试 https://api.telegram.org/bot5398463677:AAF69yqSha-tKBa39Y3tKpcNILhqfKOF3a4/sendMessage?chat_id=1570338227&text=111
            final String s = HttpUtil.get(apiPrefix+"sendMessage", new HashMap() {{
                put("chat_id", userId);
                put("disable_notification", true);
                put("parse_mode", "HTML");
                put("protect_content", true);
                put("allow_sending_without_reply", true);
                put("text", content);
            }});
            if (true != (Boolean) JSONUtil.parseObj(s).get("ok")) {
                throw new RuntimeException(s);
            }
        } catch (Exception e) {
            log.error(">>>>>>>>>>> xxl-job, job fail alarm telegram send error, JobLogId:{}", jobLog.getId(), e);
            alarmResult = false;
        }
        return alarmResult;
    }

    /**
     * tg 智能识别有限的几个 html 标签，其他的会导致报错
     */
    private String removeUnknownHtmlTag(String htmlStr) {
        htmlStr = htmlStr.replaceAll("<br>", "\n");
        htmlStr = htmlStr.replaceAll(" >>>>>>>>>>>", "==========");
        htmlStr = htmlStr.replaceAll("<<<<<<<<<<<", "==========");
        htmlStr = HtmlUtil.unwrapHtmlTag(htmlStr, "span");
        htmlStr = HtmlUtil.removeHtmlTag(htmlStr, false,"span");
        return htmlStr;
    }

    /**
     * 1) MarkdownV2: https://core.telegram.org/bots/api#markdownv2-style
     *
     * *bold \*text*
     * _italic \*text_
     * __underline__
     * ~strikethrough~
     * ||spoiler||
     * *bold _italic bold ~italic bold strikethrough ||italic bold strikethrough spoiler||~ __underline italic bold___ bold*
     * [inline URL](http://www.example.com/)
     * [inline mention of a user](tg://user?id=123456789)
     * `inline fixed-width code`
     * ```
     * pre-formatted fixed-width code block
     * ```
     * ```python
     * pre-formatted fixed-width code block written in the Python programming language
     * ```
     * 2) HTML :
     * <b>bold</b>, <strong>bold</strong>
     * <i>italic</i>, <em>italic</em>
     * <u>underline</u>, <ins>underline</ins>
     * <s>strikethrough</s>, <strike>strikethrough</strike>, <del>strikethrough</del>
     * <span class="tg-spoiler">spoiler</span>, <tg-spoiler>spoiler</tg-spoiler>
     * <b>bold <i>italic bold <s>italic bold strikethrough <span class="tg-spoiler">italic bold strikethrough spoiler</span></s> <u>underline italic bold</u></i> bold</b>
     * <a href="http://www.example.com/">inline URL</a>
     * <a href="tg://user?id=123456789">inline mention of a user</a>
     * <code>inline fixed-width code</code>
     * <pre>pre-formatted fixed-width code block</pre>
     * <pre><code class="language-python">pre-formatted fixed-width code block written in the Python programming language</code></pre>
     *
     */
    @SneakyThrows
    private static final String alarmTemplate(String contextPath,Integer port) {

        final String title = I18nUtil.getString("jobconf_monitor"),
                subTitle = String.format("<a href=\"http://%s:%s%s/joblog\">%s</a>",
                        InetAddress.getLocalHost().getHostAddress(),contextPath,port,I18nUtil.getString("jobconf_monitor_detail")),
                alarmTitle = I18nUtil.getString("jobconf_monitor_alarm_title"),
                alarmType = I18nUtil.getString("jobconf_monitor_alarm_type");

        String format = "<b>" + title + "</b>,"
                + "<i>" + subTitle + "</i>\n\uD83D\uDC15 \uD83D\uDCA9 \uD83D\uDCA9 ⁉️\n"
                + I18nUtil.getString("jobinfo_field_jobgroup") + " ：\t%s\n"
                + I18nUtil.getString("jobinfo_field_id") + " ：\t%s\n"
                + I18nUtil.getString("jobinfo_field_jobdesc") + " ：\t%s\n"
                + alarmTitle + " ：\t" + alarmType + "\n"
                + I18nUtil.getString("jobconf_monitor_alarm_content") + " ：\t%s\n";

        return format + testHtmlFormat();
    }

    private static String testHtmlFormat() {
        final String testHtmlFormat = "<b>bold</b>, <strong>bold</strong>\n" +
                "<i>italic</i>, <em>italic</em>\n" +
                "<u>underline</u>, <ins>underline</ins>\n" +
                "<s>strikethrough</s>, <strike>strikethrough</strike>, <del>strikethrough</del>\n" +
                "<span class=\"tg-spoiler\">spoiler</span>, <tg-spoiler>spoiler</tg-spoiler>\n" +
                "<b>bold <i>italic bold <s>italic bold strikethrough <span class=\"tg-spoiler\">italic bold strikethrough spoiler</span></s> <u>underline italic bold</u></i> bold</b>\n" +
                "<a href=\"http://www.example.com/\">inline URL</a>\n" +
                "<a href=\"tg://user?id=123456789\">inline mention of a user</a>\n" +
                "<code>inline fixed-width code</code>\n" +
                "<pre>pre-formatted fixed-width code block</pre>\n" +
                "<pre><code class=\"language-python\">pre-formatted fixed-width code block written in the Python programming language</code></pre>";
        return testHtmlFormat;
    }

}