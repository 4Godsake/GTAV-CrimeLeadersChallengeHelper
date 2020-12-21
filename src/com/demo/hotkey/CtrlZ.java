package com.demo.hotkey;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.demo.Client;
import com.demo.vo.MessageTemplate;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

public class CtrlZ {


    public static void register() {
        JIntellitype.getInstance().registerHotKey(77, JIntellitypeConstants.MOD_CONTROL, (int)'Z');
//        System.out.println("register for CTRL+E...");
    }

    public static void unregister() {
        JIntellitype.getInstance().unregisterHotKey(77);
        System.out.println("unregister for CTRL+E...");
    }

    public static void addListener() {
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int key) {
                if(key == 77) {         //你要做的事
                    System.out.println("you have pressed Ctrl+Z");
                    MessageTemplate quitMessage = new MessageTemplate();
                    quitMessage.setNickname(Client.nickName);
                    quitMessage.setTime(DateUtil.date());
                    quitMessage.setMessage("");
                    quitMessage.setCommand("LEFT_CLICK");
                    quitMessage.setRoom(Client.room);
                    String messageJson = JSON.toJSONString(quitMessage);
                    Client.printWriter.println(messageJson);
                }
            }
        });
    }

}

