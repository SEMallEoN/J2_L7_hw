package service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientService extends JFrame {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String myNick;
    JTextArea jta = new JTextArea();
    JTextField jtf = new JTextField();
    JButton jb = new JButton("Send");

    public ClientService() {
        setTitle("SuperChat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(50, 50, 400, 600);

        setLayout(new BorderLayout());

        JPanel chat = new JPanel();
        JPanel input = new JPanel();

        jta.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientService.this.jb(jtf, jta);
            }
        });

        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientService.this.jb(jtf, jta);
            }
        });

        chat.setLayout(new BoxLayout(chat, BoxLayout.X_AXIS));
        chat.add(jtf);
        chat.add(jb);

        input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));
        input.add(jsp);

        getContentPane().add(chat, BorderLayout.SOUTH);
        getContentPane().add(input, BorderLayout.CENTER);

        setVisible(true);
    }

    private void jb(JTextField jtf, JTextArea jta) {
        String strField = jtf.getText();
        if (strField.startsWith("/auth")) {
            startChat();
            try {
                dos.writeUTF(jtf.getText());
                jtf.setText("");
            } catch (IOException e) {
                e.printStackTrace();}
        } else {
            try {
                dos.writeUTF(jtf.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        jta.append("\n" + jtf.getText());
        jtf.setText("");
    }

    public void startChat() {
        myNick = "";

        try {
            socket = new Socket("localhost", 8189);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            setAutorized(false);


            Thread t1 = new Thread(() -> {
                try {
                    while (true) {
                        String strMsg = dis.readUTF();
                        jtf.append(strMsg + "\n");
                        if (strMsg.startsWith("/authOk")) {
                            setAutorized(true);
                            myNick = strMsg.split("\\s")[1];
                            break;
                        }
                    }
                    while (true) {
                        String strMsg = dis.readUTF();
                        if (strMsg.equals("/exit")) {
                            break;
                        }
                        jtf.append(strMsg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAutorized(false);
                    try {
                        socket.close();
                        myNick = "";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            t1.setDaemon(true);
            t1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setAutorized(boolean b) {

    }
}