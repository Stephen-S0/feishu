package com.yishuo.feishu.controller;


import com.alibaba.fastjson2.JSONObject;
import com.yishuo.feishu.service.impl.FeiShu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * UserController
 *
 * @version 0.1
 * 2022/11/21 12:20
 **/

@RestController
@RequestMapping("/feishu")
@CrossOrigin
public class FeishuController {

    @Autowired
    private FeiShu feiShu;

    @PostMapping("/Ny7d4dH54z7yYvvve")
    public String loginController(@RequestBody String body) {
        System.out.println(body);
        JSONObject data = JSONObject.parseObject(body);
        String res = feiShu.eventFeiShu(data);
        return res;
    }

}
