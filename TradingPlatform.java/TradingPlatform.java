import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;

public class TradingPlatform extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    
    private Map<String, Double> stockPrices = new HashMap<>();
    private Map<String, Integer> portfolio = new HashMap<>();
    private double balance = 10000.0;
    
    private JLabel balanceLabel;
    private DefaultTableModel tableModel;
    private JTextArea logArea; // For detailed history
    private JComboBox<String> stockSelector;

    public TradingPlatform() {
        setTitle("CodeAlpha Pro Trading Suite");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        stockPrices.put("AAPL", 150.0);
        stockPrices.put("GOOGL", 2800.0);
        stockPrices.put("TSLA", 700.0);

        // --- LOGIN ---
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(30, 30, 30));
        
        JLabel loginHeader = new JLabel("SECURE SYSTEM ACCESS", SwingConstants.CENTER);
        loginHeader.setFont(new Font("SansSerif", Font.BOLD, 55));
        loginHeader.setForeground(new Color(0, 200, 83));
        
        JTextField userField = new JTextField(15);
        userField.setFont(new Font("SansSerif", Font.PLAIN, 25));
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("SansSerif", Font.PLAIN, 25));
        
        JButton loginBtn = new JButton("LOGIN TO TERMINAL");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 25));
        loginBtn.setBackground(new Color(0, 200, 83));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; loginPanel.add(loginHeader, gbc);
        gbc.gridy = 1; loginPanel.add(new JLabel("Username:") {{ setForeground(Color.WHITE);setFont(new Font("SansSerif",Font.BOLD,20)); }}, gbc);
        gbc.gridy = 2; loginPanel.add(userField, gbc);
        gbc.gridy = 3; loginPanel.add(new JLabel("Password:") {{ setForeground(Color.WHITE);setFont(new Font("SansSerif",Font.BOLD,20)); }}, gbc);
        gbc.gridy = 4; loginPanel.add(passField, gbc);
        gbc.gridy = 5; loginPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            if (userField.getText().equals("nabia") && new String(passField.getPassword()).equals("nabia123")) {
                cardLayout.show(mainContainer, "DASHBOARD");
            } else { JOptionPane.showMessageDialog(this, "Access Denied"); }
        });

        // --- DASHBOARD ---
        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(new Color(20, 20, 20));
        dashboard.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel dashTitle = new JLabel("TRADING PORTFOLIO MONITOR", SwingConstants.CENTER);
        dashTitle.setFont(new Font("SansSerif", Font.BOLD, 55));
        dashTitle.setForeground(Color.WHITE);
        dashboard.add(dashTitle, BorderLayout.NORTH);

        balanceLabel = new JLabel("Current Balance: $10,000.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 35));
        balanceLabel.setForeground(new Color(0, 200, 83));
        dashboard.add(balanceLabel, BorderLayout.SOUTH);

        // Portfolio Table
        tableModel = new DefaultTableModel(new String[]{"Asset", "Quantity", "Market Price", "Total Value"}, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 22));
        
        // Detailed Transaction Log (The new requirement)
        logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        logArea.setBackground(new Color(40, 40, 40));
        logArea.setForeground(Color.CYAN);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(null, "DETAILED TRANSACTION HISTORY", 0, 0, new Font("SansSerif", Font.BOLD, 20), Color.WHITE));
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), logScroll);
        split.setDividerLocation(800);
        dashboard.add(split, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        stockSelector = new JComboBox<>(stockPrices.keySet().toArray(new String[0]));
        stockSelector.setFont(new Font("SansSerif", Font.BOLD, 20));
        JButton buyBtn = new JButton("BUY SELECTED");
        JButton sellBtn = new JButton("SELL SELECTED");
        buyBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        sellBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        buyBtn.addActionListener(e -> performTrade(true));
        sellBtn.addActionListener(e -> performTrade(false));
        
        controlPanel.add(stockSelector); controlPanel.add(buyBtn); controlPanel.add(sellBtn);
        dashboard.add(controlPanel, BorderLayout.EAST);

        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(dashboard, "DASHBOARD");
        add(mainContainer);
    }

    private void performTrade(boolean isBuying) {
    String s = (String) stockSelector.getSelectedItem();
    double p = stockPrices.get(s);
    if (isBuying && balance >= p) {
        balance -= p; portfolio.put(s, portfolio.getOrDefault(s, 0) + 1);
        logArea.append("[BUY]  " + s + "  at  $" + p + "\n");
        saveLog("[BUY]  " + s + "  at  $" + p);
    } else if (!isBuying && portfolio.getOrDefault(s, 0) > 0) {
        balance += p; portfolio.put(s, portfolio.get(s) - 1);
        logArea.append("[SELL] " + s + "  at  $" + p + "\n");
        saveLog("[SELL] " + s + "  at  $" + p);
    }
    updateTable();
}

private void saveLog(String entry) {
    try (FileWriter writer = new FileWriter("transactions.txt", true)) {
        writer.write(entry + "\n");
    } catch (IOException e) {
        logArea.append("Error saving to file!\n");
    }
}
    private void updateTable() {
        tableModel.setRowCount(0);
        portfolio.forEach((s, q) -> { if(q > 0) tableModel.addRow(new Object[]{s, q, "$" + stockPrices.get(s), "$" + (q * stockPrices.get(s))}); });
        balanceLabel.setText("Current Balance: $" + String.format("%.2f", balance));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TradingPlatform().setVisible(true));
    }
}