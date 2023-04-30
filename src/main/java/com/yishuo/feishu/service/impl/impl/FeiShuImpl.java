package com.yishuo.feishu.service.impl.impl;

import com.alibaba.fastjson2.JSONObject;
import com.yishuo.feishu.utils.FeishuNotifyDataDecrypter;
import com.yishuo.feishu.utils.HttpRequests;
import com.yishuo.feishu.service.impl.FeiShu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * FeiShuImpl
 * 功能实现类
 *
 * @version 0.1
 * @date 2023年03月25日 15:26
 **/
@Service
public class FeiShuImpl implements FeiShu {

    @Value("${feiShuParam.encrypt-key:}")
    private String encryptKey;

    @Value("${feiShuParam.app-id}")
    private String app_id;
    @Value("${feiShuParam.app-secret}")
    private String app_secret;

    @Value("${feiShuParam.tenant-access-token-url}")
    private String tenant_access_token_url;


    @Value("${feiShuParam.timeoff-events-url}")
    private String timeoff_events_url;

    /**
     * 数值匹配正则表达式
     */
    private static final Pattern NUM_PATTERN = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}");

    /**
     * 获取token
     *
     * @return
     */
    @Override
    public String getToken() {
        try {
            String data = String.format("{\n" +
                    "    \"app_id\": \"%s\",\n" +
                    "    \"app_secret\": \"%s\"\n" +
                    "}", app_id, app_secret);
            String requests = HttpRequests.requests(tenant_access_token_url, data, "");
            JSONObject challengeObject = JSONObject.parseObject(requests);
            String token = challengeObject.getString("tenant_access_token");
            return token;
        } catch (Exception e) {
            System.out.println("获取token error: " + e);
        }
        return "获取token error";
    }

    /**
     * 监听飞书
     *
     * @param data
     * @return
     */

    @Override
    public String eventFeiShu(JSONObject data) {
        if (data.containsKey("encrypt")) {
            try {
                System.out.println("进来了");
                FeishuNotifyDataDecrypter feishuNotifyDataDecrypter = new FeishuNotifyDataDecrypter(encryptKey);
                String encrypt = data.getString("encrypt");
                String challenge = feishuNotifyDataDecrypter.decrypt(encrypt);
                JSONObject challengeObject = JSONObject.parseObject(challenge);
                System.out.println(challengeObject);
                if (challengeObject.containsKey("challenge")) {
                    String newChallenge = challengeObject.getString("challenge");
                    String result = String.format("{\"challenge\":\"%s\"}", newChallenge);
                    return result;
                } else {
                    String event = challengeObject.getString("event");
                    JSONObject eventObject = JSONObject.parseObject(event);
                    if (eventObject.containsKey("leave_type")) {
                        leaveSchedule(getToken(), challengeObject);
                    } else {
                        return event;
                    }
                }
            } catch (Exception e) {
                System.out.println("服务器验证 error: " + e);
            }
        }
        return "ok";
    }

    /**
     * 请假同步日历
     *
     * @param token
     * @param data
     */
    @Override
    public void leaveSchedule(String token, JSONObject data) {
        try {
            System.out.println(data);
            String employee_id = data.getJSONObject("event").getString("employee_id");
            String leave_type = data.getJSONObject("event").getString("leave_type");
            String start_time = data.getJSONObject("event").getString("start_time");
            String end_time = data.getJSONObject("event").getString("end_time");
            if (leave_type.equals("年假") || leave_type.equals("婚假")|| leave_type.equals("产假")|| leave_type.equals("陪产假")|| leave_type.equals("丧假")){
                start_time = formatDate(data.getJSONObject("event").getString("leave_start_time"));
                end_time = formatDate(data.getJSONObject("event").getString("leave_end_time"));
            }
            String jsonData = String.format("{\n" +
                    "    \"user_id\": \"%s\",\n" +
                    "    \"timezone\": \"Asia/Shanghai\",\n" +
                    "    \"start_time\": \"%s\",\n" +
                    "    \"end_time\": \"%s\",\n" +
                    "    \"title\": \"%s中\",\n" +
                    "    \"description\": \"若删除此日程，飞书中相应的“请假”标签将自动消失，而请假系统中的休假申请不会被撤销。\"\n" +
                    "}", employee_id, start_time, end_time, leave_type);
            System.out.println(jsonData);
            String requests = HttpRequests.requests(timeoff_events_url, jsonData, "Bearer " + token);
            System.out.println("请假同步日历：" + requests);
        } catch (Exception e) {
            System.out.println("请求创建请假日常接口: " + e);
        }
    }

    /**
     * 解析字符串中数值
     *
     * @param text 含有数值的字符串，例如,库存剩余200件
     * @return 数值
     */
    public static String formatDate(String text) {
        Matcher matcher = NUM_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
            return matcher.group();
        }
        return text;
    }

}