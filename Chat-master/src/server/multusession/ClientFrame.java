package server.multusession;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientFrame extends JFrame {
    // адрес сервера
    private static String SERVER_HOST = "localhost";
    // порт
    private static int SERVER_PORT = 8080;
    // клиентский сокет
    private Socket clientSocket;
    // входящее сообщение
   // private Scanner inMessage;
    private DataInputStream inMessage;
    // исходящее сообщение
    //private PrintWriter outMessage;
    private DataOutputStream outMessage;
    // следующие поля отвечают за элементы формы
    private JTextField jtfMessage;
    private JTextArea jtaTextAreaMessage;
    private String cmBck;
    private String localName;
    // имя клиента
    private String clientName = "";
    // получаем имя клиента
    public String getClientName() {
        return this.clientName;
    }

    // конструктор
    public ClientFrame() {
        JFrame jFrame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(6, 1));
        JLabel l1 = new JLabel("");
       // JLabel l2 = new JLabel("Введите номер порта");
        JLabel l3 =new JLabel("Введите имя");
        JLabel l4 =new JLabel("Введите пароль");
        JButton button = new JButton("OK");
     //   JTextField host = new JTextField();
     //   JTextField port = new JTextField();
        JTextField name = new JTextField();
        JTextField pass = new JTextField();
        cmBck = new String();
        panel.add(l1);
//        panel.add(host);
//        panel.add(l2);
//        panel.add(port);
        panel.add(l3);
        panel.add(name);
        panel.add(l4);
        panel.add(pass);
        panel.add(button);

        jFrame.add(panel);
        jFrame.setBounds(0, 0, 300, 200);
        jFrame.setVisible(true);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        button.addActionListener(e->{

//                SERVER_HOST = host.getText();
////              SERVER_PORT = Integer.parseInt(port.getText());
            SERVER_HOST="localhost";
            SERVER_PORT=8080;
                if(!name.getText().isEmpty()&&!pass.getText().isEmpty()) {
                    try {
                        // подключаемся к серверу
                        clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
                        System.out.println(clientSocket.isConnected());
                        localName=name.getText();

                        inMessage = new DataInputStream(clientSocket.getInputStream());
                        outMessage = new DataOutputStream(clientSocket.getOutputStream());




                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if(clientSocket.isConnected()) {
                        try {
                            outMessage.writeUTF(localName);
                            outMessage.flush();
                            outMessage.writeUTF(pass.getText());
                            outMessage.flush();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        System.out.println("отправлено");

                        try {
                            cmBck = inMessage.readUTF();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        System.out.println("Получено");

                        System.out.println(cmBck);
                    }

                }

            if(cmBck.compareTo("WrongAnswer")==0){
                System.out.println("Wrong");
                l1.setText("Неверные логин или пароль//Этот логин уже занят");
            }
            if(clientSocket.isConnected() && cmBck.equalsIgnoreCase("Good")) {



                jFrame.dispose();

                // Задаём настройки элементов на форме
                setBounds(400, 800, 600, 800);
                setTitle("Client");
                setResizable(false);
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                jtaTextAreaMessage = new JTextArea();
                jtaTextAreaMessage.setEditable(false);
                jtaTextAreaMessage.setLineWrap(true);
                JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
                add(jsp, BorderLayout.CENTER);
                // label, который будет отражать количество клиентов в чате
                JLabel jlNumberOfClients = new JLabel("Количество клиентов в чате: ");
                add(jlNumberOfClients, BorderLayout.NORTH);
                JPanel bottomPanel = new JPanel(new BorderLayout());
                add(bottomPanel, BorderLayout.SOUTH);
                JButton jbSendMessage = new JButton("Отправить");
                bottomPanel.add(jbSendMessage, BorderLayout.EAST);
                jtfMessage = new JTextField("Введите ваше сообщение: ");
                bottomPanel.add(jtfMessage, BorderLayout.CENTER);
                        //jtfName = new JTextField("Введите ваше имя: ");
                        //bottomPanel.add(jtfName, BorderLayout.WEST);
                // обработчик события нажатия кнопки отправки сообщения
                jbSendMessage.addActionListener(event -> {
                    // если сообщение непустые, то отправляем сообщение
                    if (!jtfMessage.getText().trim().isEmpty()) {

                        sendMsg();
                        // фокус на текстовое поле с сообщением и очищение поля
                        jtfMessage.grabFocus();
                        jtfMessage.setText("");
                    }
                });

                // в отдельном потоке начинаем работу с сервером
                new Thread(() -> {
                    try {
                        // бесконечный цикл
                        while (true) {
                            // если есть входящее сообщение

                            if (inMessage.available()!=-1) {
                                // считываем его
                                String inMes = inMessage.readUTF();
                                String clientsInChat = "Клиентов в чате = ";
                                if (inMes.indexOf(clientsInChat) == 0) {
                                    jlNumberOfClients.setText(inMes);
                                } else {
                                    // выводим сообщение
                                    jtaTextAreaMessage.append(inMes);
                                    // добавляем строку перехода
                                    jtaTextAreaMessage.append("\n");
                                }
                            }
                        }
                    } catch (Exception e1) {
                    }
                }).start();
                // добавляем обработчик события закрытия окна клиентского приложения
                addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        try {
                            // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию

                            outMessage.writeUTF(clientName + " вышел из чата!");

                            // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                            outMessage.writeUTF("##session##end##");

                            outMessage.flush();

                            outMessage.close();

                            inMessage.close();
                            clientSocket.close();
                        } catch (IOException exc) {

                        }
                    }
                });
                // отображаем форму
                setVisible(true);
            }
        });

    }


    // отправка сообщения
    public void sendMsg() {
        // формируем сообщение для отправки на сервер
        String messageStr =jtfMessage.getText();
        // отправляем сообщение
        try {
            outMessage.writeUTF(messageStr);
            outMessage.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jtfMessage.setText("");
    }

    public static void main(String[] args) {
        new ClientFrame();
    }

}