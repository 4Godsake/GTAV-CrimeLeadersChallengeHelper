package com.demo.hotkey;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.demo.Client;
import com.demo.vo.MessageTemplate;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

public class Ctrl1 {


    public static void register() {
        JIntellitype.getInstance().registerHotKey(101, JIntellitypeConstants.MOD_CONTROL, 49);
//        System.out.println("register for CTRL+1...");
    }

    public static void unregister() {
        JIntellitype.getInstance().unregisterHotKey(101);
        System.out.println("unregister for CTRL+1...");
    }

    public static void addListener() {
        JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
            @Override
            public void onHotKey(int key) {
                if(key == 101) {         //你要做的事
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    System.out.println("you joined room1");
                    Client.room = 1;
                    MessageTemplate roomMessage = new MessageTemplate();
                    roomMessage.setNickname(Client.nickName);
                    roomMessage.setTime(DateUtil.date());
                    roomMessage.setMessage("我加入了1号房间");
                    roomMessage.setCommand("JOIN_ROOM_1");
                    roomMessage.setRoom(Client.room);
                    String messageJson = JSON.toJSONString(roomMessage);
                    Client.printWriter.println(messageJson);
                }
            }
        });
    }

}

