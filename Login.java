import java.awt.*;
import javax.swing.*;


public class Login extends JFrame {
    JTextField txtName = new JTextField();
    JPasswordField txtPwd = new JPasswordField();
    JButton bl = new JButton("登录");
    JButton bg = new JButton("重置");

    //构造无参构造器把主要的方法放在构造器里,然后在main方法里面调
    public Login() {
        setBounds(25, 25, 400, 400);
        Container c = getContentPane();
        c.setLayout(new GridLayout(3, 2, 10, 10));
        c.add(new JLabel("用户名"), BorderLayout.EAST);
        c.add(txtName);
        c.add(new JLabel("密码"));
        c.add(txtPwd);
        c.add(bl);
        c.add(bg);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        bg.addActionListener(e -> txtPwd.setText(""));

        bl.addActionListener(e -> {
            String name = txtName.getText();
            String pass = txtPwd.getText();
            if (pass.equals("123")) {
                // TODO
                System.out.println("登录成功");
                Login.this.dispose();
                new ChatClient(name);
            } else {
                JOptionPane.showMessageDialog(Login.this,"密码输入错误，请重新输入!");
                System.out.println("登录失败");
            }
        });
    }

    public static void main(String[] args) {
        new Login();
    }
}