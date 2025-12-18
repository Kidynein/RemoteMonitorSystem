package server.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM");

    public LogPanel() {
        setLayout(new BorderLayout());

        // Tạo các cột cho bảng
        String[] columnNames = {"Thời gian", "Client", "Hành động", "Đường dẫn"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Thêm thanh cuộn
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Tiêu đề
        setBorder(BorderFactory.createTitledBorder("Nhật ký giám sát (Live Log)"));
    }

    public void addLog(String clientName, String action, String path) {
        String time = sdf.format(new Date());
        Object[] row = {time, clientName, action, path};

        SwingUtilities.invokeLater(() -> {
            tableModel.addRow(row);
            table.changeSelection(table.getRowCount() - 1, 0, false, false);
        });
    }
}