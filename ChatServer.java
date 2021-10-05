import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends JFrame {
    private JTextArea serverText = new JTextArea();
    private JPanel serverPanel = new JPanel();
    private JButton start = new JButton("启动");
    private JButton stop = new JButton("停止");

    private ServerSocket server = null;
    private boolean isStart = false;

    private DataInputStream serverInput = null;

    private List<ConnectClient> clientsList = new ArrayList<>();

    public ChatServer() {
        init();
    }
    private void init() {
        this.setTitle("服务器");
        this.add(serverText, BorderLayout.CENTER);
        serverText.setEditable(false);
        serverPanel.add(start);
        serverPanel.add(stop);
        this.add(serverPanel, BorderLayout.SOUTH);
        this.setBounds(200, 200, 400, 400);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        eventHandler();
    }

    public void eventHandler() {
        start.addActionListener(e -> {
            if(!isStart){
                try {
                    server = new ServerSocket(8000);
                    System.out.println("服务器：启动");
                    serverText.append("服务器：启动\n");
                    isStart = true;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                new Thread(new ReceiveMessageFromClient()).start();
            }
            else  JOptionPane.showMessageDialog(ChatServer.this, "服务器已经启动，无需再启动");
        });

        stop.addActionListener(e -> {
            if(server != null){
                isStart = false;
                try {
                    server.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            serverText.append("服务器：关闭\n");
        });
    }

    public class ReceiveMessageFromClient implements Runnable {
        @Override
        public void run() {
            try {
                while (isStart){
                    Socket accept = server.accept();
                    serverText.append("服务器" + accept.getInetAddress() + ":" + accept.getPort() + "连接进来\n");
                    clientsList.add(new ConnectClient(accept));
                }
            } catch (IOException e) {

            }
        }
    }

    private class ConnectClient implements Runnable{
        private Socket socket;

        public ConnectClient(Socket socket) {
            this.socket = socket;
            new Thread(this).start();
        }

        @Override
        public void run() {
            while (isStart){
                try {
                    serverInput = new DataInputStream(socket.getInputStream());
                    String message = serverInput.readUTF();
//                    serverText.append(message + "\n");
                    System.out.println("群发消息");
                    for(ConnectClient client : clientsList){
                        DataOutputStream dataOutputStream = new DataOutputStream(client.socket.getOutputStream());
                        dataOutputStream.writeUTF(message);
                    }

                } catch (IOException e) {
                    serverText.append("服务器" + socket.getInetAddress() + ":" + socket.getPort() + "断开\n");
                    int i = 0;
                    for(ConnectClient cli : clientsList){
                        if(cli.socket == this.socket){
                            clientsList.remove(i);
                            return;
                        }
                        i++;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}
