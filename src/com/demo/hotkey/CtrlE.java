package com.demo.hotkey;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.demo.Client;
import com.demo.vo.MessageTemplate;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

import java.security.MessageDigest;

public class CtrlE {


    public static void register() {
        JIntellitype.getInstance().registerHotKey(99, JIntellitypeConstants.MOD_CONTROL, (int)'E');
        System.out.println("register for CTRL+E...");
    }

    public static void unregister() {
        JIntellitype.getInstance().unregisterHotKey(99);
        System.out.println("unregister for CTRL+E...");
    }

    public static void addListener() {
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int key) {
                if(key == 99) {         //你要做的事
                    System.out.println("you have pressed Ctrl+E");
                    MessageTemplate quitMessage = new MessageTemplate();
                    quitMessage.setNickname(Client.nickName);
                    quitMessage.setTime(DateUtil.date());
                    quitMessage.setMessage("");
                    quitMessage.setCommand("ENTER");
                    String messageJson = JSON.toJSONString(quitMessage);
                    Client.printWriter.println(messageJson);
                }
            }
        });
    }

}

