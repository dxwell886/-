import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient extends JFrame{
    private JTextArea textUp = new JTextArea();
    private JTextField textDown = new JTextField();
    private String name;

    private Socket client = null;
    private int status = DISCONNECT;
    private static final int CONNECT = 0;
    private static final int DISCONNECT = 1;

    private DataOutputStream sendStream = null;



    public ChatClient(String name){
        init();
        this.name = name;
    }

    private void init(){
        this.setTitle("多人聊天室");
        this.add(textUp, BorderLayout.CENTER);
        this.add(textDown, BorderLayout.SOUTH);

        this.setBounds(300,300,300,400);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    System.out.println("客户端关闭");
                    client.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                System.exit(0);

            }
        });

        textUp.setEditable(false);
        textDown.requestFocusInWindow();

        textDown.addActionListener(e -> {
            if(textDown.getText().trim().equals("")){
                JOptionPane.showMessageDialog(textUp,"请输入内容后再发送");
                textDown.setText("");
                return;
            }
            String message = name + ": " + textDown.getText();
//            textUp.append(message + "\n");
            // 向服务器发送消息
            send(message);
            textDown.setText("");
        });

        // 连接服务器
        try {
            client = new Socket("127.0.0.1",8000);
            status = CONNECT;
            new Thread(new ReceiveMessageFromServer()).start();
            System.out.println("客户端：连接服务器成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
    }

    private void send(String message){
        if(status == CONNECT){
            try {
                sendStream = new DataOutputStream(client.getOutputStream());
                sendStream.writeUTF(message);
            } catch (IOException e) {
                if(client != null){
                    try {
                        client.close();
                    } catch (IOException ioException) {

                    }
                    status = DISCONNECT;
                }
                JOptionPane.showMessageDialog(ChatClient.this,"服务器出现故障，请过段时间再试！");
            }
        }
    }

    public class ReceiveMessageFromServer implements Runnable{

        @Override
        public void run() {
            while (status == CONNECT){
                try {
                    System.out.println("客户端接收消息");
                    DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                    String text = dataInputStream.readUTF();
                    System.out.println("接收");
                    textUp.append(text + "\n");
                } catch (IOException e) {
                    if(client != null){
                        try {
                            client.close();
                        } catch (IOException ioException) {

                        }
                        status = DISCONNECT;
                    }
                    JOptionPane.showMessageDialog(ChatClient.this,"服务器出现故障，请过段时间再试！");
                }
            }
        }
    }

}
