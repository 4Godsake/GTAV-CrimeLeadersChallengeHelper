package com.demo.hotkey;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.demo.Client;
import com.demo.vo.MessageTemplate;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import java.security.MessageDigest;

public class F5 {


    public static void register() {
        JIntellitype.getInstance().registerHotKey(88, 0, 116);
        System.out.println("register...");
    }

    public static void unregister() {
        JIntellitype.getInstance().unregisterHotKey(88);
        System.out.println("unregister...");
    }

    public static void addListener() {
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int key) {
                if(key == 88) {         //你要做的事
                    System.out.println("you have pressed F5");
                    MessageTemplate quitMessage = new MessageTemplate();
                    quitMessage.setNickname("someone");
                    quitMessage.setTime(DateUtil.date());
                    quitMessage.setMessage("");
                    quitMessage.setCommand("QUIT");
                    String messageJson = JSON.toJSONString(quitMessage);
                    Client.printWriter.println(messageJson);

                }
            }
        });
    }

}

