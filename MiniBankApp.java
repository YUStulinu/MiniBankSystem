package MiniBankSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

// Bank account class
class Account {
    private String accountNumber;
    private double balance;

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    // Deposit money
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    // Withdraw money
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    // Transfer to another account
    public boolean transfer(Account target, double amount) {
        if (withdraw(amount)) {
            target.deposit(amount);
            return true;
        }
        return false;
    }
}

// Client class
class Client {
    private String name;
    private Account account;

    public Client(String name, Account account) {
        this.name = name;
        this.account = account;
    }

    public String getName() { return name; }
    public Account getAccount() { return account; }
}

// Main GUI Application
public class MiniBankApp extends JFrame {
    private HashMap<String, Client> clients;
    private JList<String> clientList;
    private JTextField balanceField, amountField;
    private JComboBox<String> targetBox;
    private JButton depositBtn, withdrawBtn, transferBtn;

    public MiniBankApp() {
        setTitle("Mini Bank System");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setLayout(new BorderLayout(10, 10));

        // Create clients and accounts
        clients = new HashMap<>();
        clients.put("Alice", new Client("Alice", new Account("A001", 1000)));
        clients.put("Bob", new Client("Bob", new Account("B001", 500)));
        clients.put("Charlie", new Client("Charlie", new Account("C001", 750)));

        // ===== LEFT PANEL: CLIENT LIST =====
        clientList = new JList<>(clients.keySet().toArray(new String[0]));
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientList.setSelectedIndex(0);
        clientList.setFont(new Font("Arial", Font.BOLD, 14));
        clientList.setBackground(new Color(220, 235, 255));
        clientList.setBorder(BorderFactory.createTitledBorder("Clients"));
        add(new JScrollPane(clientList), BorderLayout.WEST);

        // ===== RIGHT PANEL: ACCOUNT DETAILS & OPERATIONS =====
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBorder(new EmptyBorder(10,10,10,10));
        rightPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Account number label
        gbc.gridx = 0; gbc.gridy = 0;
        rightPanel.add(new JLabel("Account Number:"), gbc);
        JTextField accNumberField = new JTextField();
        accNumberField.setEditable(false);
        accNumberField.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 0;
        rightPanel.add(accNumberField, gbc);

        // Balance label
        gbc.gridx = 0; gbc.gridy = 1;
        rightPanel.add(new JLabel("Balance:"), gbc);
        balanceField = new JTextField();
        balanceField.setEditable(false);
        balanceField.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 1;
        rightPanel.add(balanceField, gbc);

        // Amount input
        gbc.gridx = 0; gbc.gridy = 2;
        rightPanel.add(new JLabel("Amount:"), gbc);
        amountField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        rightPanel.add(amountField, gbc);

        // Deposit button with color and icon
        depositBtn = new JButton("Deposit", UIManager.getIcon("OptionPane.informationIcon"));
        depositBtn.setBackground(new Color(180, 230, 180));
        gbc.gridx = 0; gbc.gridy = 3;
        rightPanel.add(depositBtn, gbc);

        // Withdraw button with color and icon
        withdrawBtn = new JButton("Withdraw", UIManager.getIcon("OptionPane.warningIcon"));
        withdrawBtn.setBackground(new Color(255, 200, 200));
        gbc.gridx = 1; gbc.gridy = 3;
        rightPanel.add(withdrawBtn, gbc);

        // Transfer target dropdown
        gbc.gridx = 0; gbc.gridy = 4;
        rightPanel.add(new JLabel("Transfer to:"), gbc);
        targetBox = new JComboBox<>(clients.keySet().toArray(new String[0]));
        gbc.gridx = 1; gbc.gridy = 4;
        rightPanel.add(targetBox, gbc);

        // Transfer button
        transferBtn = new JButton("Transfer", UIManager.getIcon("OptionPane.questionIcon"));
        transferBtn.setBackground(new Color(200, 220, 255));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        rightPanel.add(transferBtn, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // Initialize details
        updateClientDetails();

        // ===== LISTENERS =====
        clientList.addListSelectionListener(e -> updateClientDetails());

        depositBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                Client client = clients.get(clientList.getSelectedValue());
                client.getAccount().deposit(amount);
                updateClientDetails();
                JOptionPane.showMessageDialog(this, "Deposit successful!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number!");
            }
        });

        withdrawBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                Client client = clients.get(clientList.getSelectedValue());
                if(client.getAccount().withdraw(amount)) {
                    updateClientDetails();
                    JOptionPane.showMessageDialog(this, "Withdrawal successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number!");
            }
        });

        transferBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                Client source = clients.get(clientList.getSelectedValue());
                Client target = clients.get((String) targetBox.getSelectedItem());
                if(source == target) {
                    JOptionPane.showMessageDialog(this, "Cannot transfer to the same client!");
                    return;
                }
                if(source.getAccount().transfer(target.getAccount(), amount)) {
                    updateClientDetails();
                    JOptionPane.showMessageDialog(this, "Transfer successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number!");
            }
        });
    }

    // Update displayed client details
    private void updateClientDetails() {
        String selected = clientList.getSelectedValue();
        if(selected == null) return;
        Client client = clients.get(selected);
        balanceField.setText(String.valueOf(client.getAccount().getBalance()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MiniBankApp app = new MiniBankApp();
            app.setVisible(true);
        });
    }
}
