package client.view;

import client.ClientMain;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {
    private JTextField txtIP;
    private JTextField txtPort;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JLabel lblStatus;
    private JLabel lblMonitorPath;

    private ClientMain client;

    public ClientFrame() {
        setupUI();
        client = new ClientMain(this);
    }

    private void setupUI() {
        setTitle("Client Monitor System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        // Cấu hình kết nối
        JPanel pnlConfig = new JPanel(new GridLayout(2, 1));
        pnlConfig.add(new JLabel("Server IP: localhost", JLabel.CENTER));
        txtIP = new JTextField("localhost", 10);
        pnlConfig.add(new JLabel("Port: 9999", JLabel.CENTER));
        txtPort = new JTextField("9999", 5);
        add(pnlConfig);

        // Nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout());
        btnConnect = new JButton("Kết nối");
        btnDisconnect = new JButton("Ngắt kết nối");
        btnDisconnect.setEnabled(false); // Mới vào chưa kết nối thì nút này mờ đi
        pnlButtons.add(btnConnect);
        pnlButtons.add(btnDisconnect);
        add(pnlButtons);

        // Trạng thái
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblStatus = new JLabel("Trạng thái: Chưa kết nối");
        lblStatus.setForeground(Color.RED);
        pnlStatus.add(lblStatus);
        add(pnlStatus);

        // Thông tin giám sát
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblMonitorPath = new JLabel("Đang giám sát: (Chưa có lệnh)");
        lblMonitorPath.setForeground(Color.BLUE);
        pnlInfo.add(lblMonitorPath);
        add(pnlInfo);

        // XỬ LÝ SỰ KIỆN
        btnConnect.addActionListener(e -> {
            String ip = txtIP.getText();
            int port = Integer.parseInt(txtPort.getText());
            client.connect(ip, port);
        });

        btnDisconnect.addActionListener(e -> {
            client.disconnect();
        });
    }

    // update giao diện
    public void updateStatus(String msg) {
        SwingUtilities.invokeLater(() -> lblStatus.setText("Trạng thái: " + msg));
    }

    public void updateMonitoringPath(String path) {
        SwingUtilities.invokeLater(() -> lblMonitorPath.setText("Đang giám sát: " + path));
    }

    public void setConnectedState(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            btnConnect.setEnabled(!connected);
            btnDisconnect.setEnabled(connected);
            txtIP.setEnabled(!connected);
            txtPort.setEnabled(!connected);
            lblStatus.setForeground(connected ? new Color(0, 150, 0) : Color.RED);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientFrame().setVisible(true));
    }
}