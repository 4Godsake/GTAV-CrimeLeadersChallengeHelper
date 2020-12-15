package com.demo.hotkey;

public class f5test {
    public f5test() {
        GlobalHotkeyResourceManagement.initResources();
        GlobalHotkeyResourceManagement.addListener();
        F5.register();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace(); }      //B.unregister();  //这里的两行可以要也可以不要，下面会说      //C.unregister();
//        GlobalHotkeyResourceManagement.releaseResources();
    }

    public static void main(String[] args) {
        new f5test();
    }

}
