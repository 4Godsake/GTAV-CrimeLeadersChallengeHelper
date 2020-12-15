package com.demo.hotkey;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;


public class GlobalHotkeyResourceManagement {

    public static void initResources() {
        JIntellitype.getInstance();
        System.out.println("init resources...");
    }

    public static void addListener() {
        F5.addListener();
        CtrlE.addListener();
    }

    public static void releaseResources() {
        JIntellitype.getInstance().cleanUp();
        System.out.println("release resources...");
    }
}

