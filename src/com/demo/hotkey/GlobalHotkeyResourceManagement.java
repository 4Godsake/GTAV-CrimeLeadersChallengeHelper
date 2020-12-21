package com.demo.hotkey;

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
            Ctrl1.addListener();
            Ctrl2.addListener();
            Ctrl3.addListener();
            Ctrl4.addListener();
            CtrlZ.addListener();
            }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void allRegister(){
        F5.register();
        CtrlE.register();
        Ctrl1.register();
        Ctrl2.register();
        Ctrl3.register();
        Ctrl4.register();
        CtrlZ.register();
    }

    public static void releaseResources() {
        JIntellitype.getInstance().cleanUp();
        System.out.println("release resources...");
    }
}

