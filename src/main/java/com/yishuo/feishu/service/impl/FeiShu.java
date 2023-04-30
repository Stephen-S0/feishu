package com.yishuo.feishu.service.impl;

import com.alibaba.fastjson2.JSONObject;


/**
 * feishu
 *
 * @version 0.1
 * @date 2023年03月25日 15:25
 **/
public interface FeiShu {

    String getToken();

    String eventFeiShu(JSONObject data);

    void leaveSchedule(String token, JSONObject data);
}
