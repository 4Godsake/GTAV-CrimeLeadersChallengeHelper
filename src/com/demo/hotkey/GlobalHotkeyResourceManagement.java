package com.demo.hotkey;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;


public class GlobalHotkeyResourceManagement {

    public static void initResources() {
        try{
            JIntellitype.getInstance();
            System.out.println("init resources...");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void addListener() {
        try{
            F5.addListener();
            CtrlE.addListener();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void releaseResources() {
        JIntellitype.getInstance().cleanUp();
        System.out.println("release resources...");
    }
}

