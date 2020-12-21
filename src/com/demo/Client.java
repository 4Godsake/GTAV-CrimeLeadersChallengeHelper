package com.demo;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.demo.hotkey.*;
import com.demo.vo.MessageTemplate;
import com.sun.xml.internal.ws.util.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class Client {

    //定时任务线程池
    private ExecutorService threadPool;

    public static PrintWriter printWriter = null;
    public static String nickName = null;
    public static Integer room = 1;
    private Socket socket = null;
    Boolean flag = true;


    public Client(){
        String IP_TENCENT_CLOUD = "120.53.227.170";
        String IP_LOCAL = "localhost";

        try {
            socket = new Socket(IP_TENCENT_CLOUD, 8088);
            socket.setSoTimeout(8000);
            threadPool = Executors.newScheduledThreadPool(5);
        } catch (Exception e) {
            System.out.println("服务连接失败，请检查服务状态");
        }

    }

    static class ConnectionChecker implements Runnable{

        //构造函数设置为public
        public ConnectionChecker(){

        }

        @Override
        public void run() {
            MessageTemplate heartbeat = new MessageTemplate();
            heartbeat.setCommand("HEARTBEAT");
            if (nickName==null){
                nickName = "someone";
            }

            while(true){
                try {
                    Thread.sleep(3000);
                    heartbeat.setTime(DateUtil.date());
                    heartbeat.setMessage(nickName+" is still alive");
                    heartbeat.setNickname(nickName);
                    printWriter.println(JSON.toJSONString(heartbeat));
                } catch (Exception e){
                    System.out.println("失去连接");
                    e.printStackTrace();
                }
            }
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
                    if ("SUCCESS".equals(message.getCommand())){
                        //donothing
                    }else if (flag && room.equals(message.getRoom()) && "QUIT_ALL".equals(message.getCommand())){
                        //quit后逻辑
                        AnsiConsole.out.println(DateUtil.format(message.getTime(),"HH:mm:ss")+"|"+ansi().fg(GREEN).a(message.getNickname()).reset()+": "+message.getMessage());
                        AnsiConsole.out.println(ansi().fg(RED).a("quiting now").reset());
                        String command = "taskkill /f /im GTA5.exe";
                        Runtime.getRuntime().exec(command);
                    }else if ("PING".equals(message.getCommand())){
                        Date date_now = DateUtil.date();
                        long delay = DateUtil.between(date_now, message.getTime(), DateUnit.MS);
                        AnsiConsole.out.println(ansi().fg(BLUE).a("ping：").reset()+""+delay);
                    }else if("ENTER_ALL".equals(message.getCommand())&& room.equals(message.getRoom())){
                        r.keyPress(KeyEvent.VK_ENTER);
                        r.delay(100);
                        r.keyRelease(KeyEvent.VK_ENTER);
                        AnsiConsole.out.println(DateUtil.format(message.getTime(),"HH:mm:ss")+"|"+ansi().fg(GREEN).a(message.getNickname()).reset()+": "+message.getMessage());
                        AnsiConsole.out.println(ansi().fg(BLUE).a("Force Pressed Enter").reset());
                    }else if("LEFT_CLICK_ALL".equals(message.getCommand())&& room.equals(message.getRoom())){
                        r.mousePress(KeyEvent.BUTTON1_MASK);
                        r.delay(20);
                        r.mouseRelease(KeyEvent.BUTTON1_MASK);
                        AnsiConsole.out.println(DateUtil.format(message.getTime(),"HH:mm:ss")+"|"+ansi().fg(GREEN).a(message.getNickname()).reset()+": "+message.getMessage());
                        AnsiConsole.out.println(ansi().fg(BLUE).a("Force left click").reset());
                    }
                    else {
                        AnsiConsole.out.println(DateUtil.format(message.getTime(),"HH:mm:ss")+"|"+ansi().fg(GREEN).a(message.getNickname()).reset()+": "+message.getMessage());
                    }
                }
            }catch(Exception e){
                System.out.println(DateUtil.date().toString()+"|连接异常");
                e.printStackTrace();
            }
        }
    }

    public void start(){
        try{
            //创建心跳线程
            ConnectionChecker connectionChecker = new ConnectionChecker();
            threadPool.execute(connectionChecker);

            ServerHandler handler = new ServerHandler();
//            threadPool.execute(handler);
            Thread t = new Thread(handler);
            t.setDaemon(true);
            t.start();

            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            PrintWriter pw = new PrintWriter(osw, true);
            printWriter = pw;
            //创建Scanner读取用户输入内容
            Scanner scanner = new Scanner(System.in);
//            Thread.sleep(1000);
//            AnsiConsole.out.println(ansi().fg(BLUE).a("请输入昵称：").reset());
//            nickName = scanner.nextLine();
            String absolutePath = FileUtil.getAbsolutePath("");
            System.out.println("cmd /c start "+absolutePath+"hack.exe");
            Runtime.getRuntime().exec("cmd /c start "+absolutePath+"hack.exe");
//            AnsiConsole.out.println(ansi().fg(BLUE).a("您将使用以下昵称："+nickName).reset());
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
                message.setRoom(room);
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
                }else if ("/?".equals(message.getMessage())){
                    AnsiConsole.out.println(ansi().fg(GREEN).a("/ping      --测试延迟").reset());
                    AnsiConsole.out.println(ansi().fg(GREEN).a("/list      --查看当前连接用户列表").reset());
                    AnsiConsole.out.println(ansi().fg(GREEN).a("/quit t    --启用远程闪退功能").reset());
                    AnsiConsole.out.println(ansi().fg(GREEN).a("/quit f    --禁用远程闪退功能").reset());
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
            System.out.println("++++++++++++++++ GTA-Agent +++++++++++++++");
            System.out.println("+            Author: 4Godsake            +");
            System.out.println("+   Github: https://github.com/4Godsake  +");
            System.out.println("++++++++++++++++++++++++++++++++++++++++++");
            GlobalHotkeyResourceManagement.initResources();
            GlobalHotkeyResourceManagement.addListener();
            GlobalHotkeyResourceManagement.allRegister();
            Thread.sleep(1000);
            AnsiConsole.systemInstall();
            System.out.println("success......");
        } catch (Exception exception){
            System.out.println("读写文件失败");
            exception.printStackTrace();
        }
        // TODO Auto-generated method stub
        Scanner scanner = new Scanner(System.in);
        AnsiConsole.out.println(ansi().fg(BLUE).a("请输入昵称：").reset());
        nickName = scanner.nextLine();

        Client client = new Client();
        client.start();
    }

}