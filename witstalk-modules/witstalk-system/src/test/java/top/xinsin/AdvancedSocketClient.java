package top.xinsin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Java Socket 客户端（增强版，多线程）
 */
public class AdvancedSocketClient {
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public AdvancedSocketClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * 连接服务器
     */
    public boolean connect() {
        try {
            socket = new Socket(serverHost, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("成功连接到服务器：" + serverHost + ":" + serverPort);

            // 启动独立线程接收服务器消息
            new Thread(this::receiveServerMessages).start();

            return true;
        } catch (IOException e) {
            System.err.println("连接服务器失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 接收服务器消息的线程方法
     */
    private void receiveServerMessages() {
        String serverMessage;
        try {
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("\n服务器消息：" + serverMessage);
                System.out.print("请输入消息："); // 提示用户输入
            }
        } catch (IOException e) {
            System.err.println("接收服务器消息异常：" + e.getMessage());
        }
    }

    /**
     * 发送消息到服务器
     */
    public void sendMessage(String message) {
        if (out != null && !socket.isClosed()) {
            out.println(message);
        } else {
            System.err.println("客户端未连接或已关闭，无法发送消息");
        }
    }

    /**
     * 关闭客户端连接
     */
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("客户端已关闭连接");
        } catch (IOException e) {
            System.err.println("关闭客户端连接异常：" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AdvancedSocketClient client = new AdvancedSocketClient("192.168.9.195", 8888);

        // 连接服务器
        if (!client.connect()) {
            return;
        }

        // 读取用户输入并发送
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            String userMessage;
            System.out.print("请输入消息（输入 'quit' 退出）：");

            while ((userMessage = userInput.readLine()) != null) {
                client.sendMessage(userMessage);

                if ("quit".equalsIgnoreCase(userMessage)) {
                    client.close();
                    break;
                }

                System.out.print("请输入消息：");
            }
        } catch (IOException e) {
            System.err.println("读取用户输入异常：" + e.getMessage());
        }
    }
}
