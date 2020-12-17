package com.demo;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.demo.hotkey.CtrlE;
import com.demo.hotkey.F5;
import com.demo.hotkey.GlobalHotkeyResourceManagement;
import com.demo.vo.MessageTemplate;
import org.fusesource.jansi.AnsiConsole;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class Client {
    public static PrintWriter printWriter;
    public static String nickName;
    private Socket socket;
    Boolean flag = true;


    public Client(){
        String IP_TENCENT_CLOUD = "120.53.227.170";
        String IP_LOCAL = "localhost";

        try {
            socket = new Socket(IP_TENCENT_CLOUD, 8088);
        } catch (Exception e) {
            System.out.println("服务连接失败，请检查服务状态");
        }


    }

    private class ServerHandler implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String messageJson = null;
                MessageTemplate message = new MessageTemplate();
                Robot r=new Robot();//创建自动化工具对象
                while(true){
                    messageJson = br.readLine();
                    message = JSON.parseObject(messageJson,MessageTemplate.class);
                    //输出消息内容
                    AnsiConsole.out.println(DateUtil.format(message.getTime(),"HH:mm:ss")+"|"+ansi().fg(GREEN).a(message.getNickname()).reset()+": "+message.getMessage());
                    if (flag&&message.getCommand().equals("QUIT_ALL")){
                        //quit后逻辑
                        AnsiConsole.out.println(ansi().fg(RED).a("quiting now").reset());
                        String command = "taskkill /f /im GTA5.exe";
                        Runtime.getRuntime().exec(command);
                    }else if (message.getCommand().equals("PING")){
                        Date date_now = DateUtil.date();
                        long delay = DateUtil.between(date_now, message.getTime(), DateUnit.MS);
                        AnsiConsole.out.println(ansi().fg(BLUE).a("ping：").reset()+""+delay);
                    }else if(message.getCommand().equals("ENTER_ALL")){
                        r.keyPress(KeyEvent.VK_ENTER);
                        r.delay(100);
                        r.keyRelease(KeyEvent.VK_ENTER);
                        AnsiConsole.out.println(ansi().fg(BLUE).a("Force Pressed Enter").reset());
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void start(){
        AnsiConsole.systemInstall();
        try{
            ServerHandler handler = new ServerHandler();
            Thread t = new Thread(handler);
            t.setDaemon(true);
            t.start();

            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            PrintWriter pw = new PrintWriter(osw, true);
            printWriter = pw;
            //创建Scanner读取用户输入内容
            Scanner scanner = new Scanner(System.in);
            Thread.sleep(1000);
            AnsiConsole.out.println(ansi().fg(BLUE).a("请输入昵称：").reset());
            nickName = scanner.nextLine();
            String absolutePath = FileUtil.getAbsolutePath("");
            System.out.println("cmd /c start "+absolutePath+"hack.exe");
            Runtime.getRuntime().exec("cmd /c start "+absolutePath+"hack.exe");
            AnsiConsole.out.println(ansi().fg(BLUE).a("您将使用以下昵称："+nickName).reset());
            MessageTemplate message = new MessageTemplate();
            //将昵称传到服务器放入列表里
            message.setNickname(nickName);
            message.setMessage("");
            message.setCommand("SET_NICKNAME");
            pw.println(JSON.toJSONString(message));
            while(true){
                //设置消息的昵称以及消息内容
                message.setMessage(scanner.nextLine());
                message.setTime(DateUtil.date());
                //如果消息内容是F5，设置消息指令信息为“quit”
                if ("/ping".equals(message.getMessage())){
                    message.setCommand("PING_GET");
                    pw.println(JSON.toJSONString(message));
                }else if ("/list".equals(message.getMessage())){
                    message.setCommand("LIST");
                    pw.println(JSON.toJSONString(message));
                }else if ("/quit t".equals(message.getMessage())){
                    flag = true;
                    AnsiConsole.out.println(ansi().fg(RED).a("启用远程闪退功能").reset());
                }else if ("/quit f".equals(message.getMessage())){
                    flag = false;
                    AnsiConsole.out.println(ansi().fg(RED).a("禁用远程闪退功能").reset());
                }
                else{
                    message.setCommand("nothing");
                    pw.println(JSON.toJSONString(message));
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(socket != null){
                try{
                    socket.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    static class BeforeEnd {//程序退出事件处理

        BeforeEnd() {
            //模拟处理时间
            Thread t = new Thread(() -> {
                try {
                    //模拟正常终止前任务
                    String command = "taskkill /f /im hack.exe";
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Runtime.getRuntime().addShutdownHook(t);
        }
    }

    public static void main(String[] args) {
        try{
            new BeforeEnd();
            System.out.println("+++++++++++++++++GTA-Agent++++++++++++++++");
            System.out.println("+            Author: 4Godsake            +");
            System.out.println("+  Github: https://github.com/4Godsake   +");
            System.out.println("++++++++++++++++++++++++++++++++++++++++++");
            GlobalHotkeyResourceManagement.initResources();
            GlobalHotkeyResourceManagement.addListener();
            F5.register();
            CtrlE.register();
            Thread.sleep(1000);
            System.out.println("success......");
        } catch (Exception exception){
            System.out.println("读写文件失败");
            exception.printStackTrace();
        }
        // TODO Auto-generated method stub
        Client client = new Client();
        client.start();
    }

}