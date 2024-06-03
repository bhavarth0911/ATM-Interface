import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ATM extends JFrame {
    private Bank bank;
    private User currentUser;

    private JTextField userIdField;
    private JPasswordField pinField;
    private JTextArea resultArea;
    private JTextField amountField;
    private JTextField transferUserIdField;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public ATM(Bank bank) {
        this.bank = bank;
        setTitle("ATM System");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel();
        JPanel mainPanel = createMainPanel();

        cardPanel.add(loginPanel, "Login");
        cardPanel.add(mainPanel, "Main");

        add(cardPanel);
        cardLayout.show(cardPanel, "Login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userIdLabel = new JLabel("User ID:");
        userIdField = new JTextField();
        JLabel pinLabel = new JLabel("PIN:");
        pinField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String userId = userIdField.getText();
            String userPin = new String(pinField.getPassword());
            currentUser = bank.authenticateUser(userId, userPin);
            if (currentUser != null) {
                cardLayout.show(cardPanel, "Main");
                resultArea.setText("Welcome " + userId + "!\n");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid User ID or PIN");
            }
        });

        panel.add(userIdLabel);
        panel.add(userIdField);
        panel.add(pinLabel);
        panel.add(pinField);
        panel.add(new JLabel());
        panel.add(loginButton);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));

        JButton historyButton = new JButton("Transaction History");
        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton transferButton = new JButton("Transfer");
        JButton quitButton = new JButton("Quit");

        historyButton.addActionListener(e -> showTransactionHistory());
        withdrawButton.addActionListener(e -> withdraw());
        depositButton.addActionListener(e -> deposit());
        transferButton.addActionListener(e -> transfer());
        quitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(historyButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(quitButton);

        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void showTransactionHistory() {
        resultArea.append("\nTransaction History:\n");
        for (Transaction transaction : currentUser.getTransactionHistory()) {
            resultArea.append(transaction + "\n");
        }
    }

    private void withdraw() {
        amountField = new JTextField();
        int result = JOptionPane.showConfirmDialog(this, amountField, "Enter amount to withdraw:", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            double amount = Double.parseDouble(amountField.getText());
            if (currentUser.withdraw(amount)) {
                resultArea.append("Withdrawal successful. New balance: $" + currentUser.getBalance() + "\n");
            } else {
                resultArea.append("Insufficient balance.\n");
            }
        }
    }

    private void deposit() {
        amountField = new JTextField();
        int result = JOptionPane.showConfirmDialog(this, amountField, "Enter amount to deposit:", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            double amount = Double.parseDouble(amountField.getText());
            currentUser.deposit(amount);
            resultArea.append("Deposit successful. New balance: $" + currentUser.getBalance() + "\n");
        }
    }

    private void transfer() {
        JPanel transferPanel = new JPanel(new GridLayout(2, 2));
        transferUserIdField = new JTextField();
        amountField = new JTextField();

        transferPanel.add(new JLabel("Recipient User ID:"));
        transferPanel.add(transferUserIdField);
        transferPanel.add(new JLabel("Amount:"));
        transferPanel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, transferPanel, "Enter transfer details:", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String recipientId = transferUserIdField.getText();
            double amount = Double.parseDouble(amountField.getText());
            User recipient = bank.authenticateUser(recipientId, ""); // Empty string for PIN since we are just looking up the user
            if (recipient != null && currentUser.withdraw(amount)) {
                recipient.deposit(amount);
                currentUser.addTransaction(new Transaction("Transfer to " + recipientId, amount));
                recipient.addTransaction(new Transaction("Transfer from " + currentUser.getUserId(), amount));
                resultArea.append("Transfer successful.\n");
            } else {
                resultArea.append("Transfer failed. Check recipient ID and balance.\n");
            }
        }
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        SwingUtilities.invokeLater(() -> {
            ATM atm = new ATM(bank);
            atm.setVisible(true);
        });
    }
}
