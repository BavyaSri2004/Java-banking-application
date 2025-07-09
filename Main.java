package worspace;

import java.util.*;

class Transaction {
    String type;
    double amount;
    String note;
    Date date;

    public Transaction(String type, double amount, String note) {
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.date = new Date();
    }

    public void print() {
        System.out.printf("%-12s ₹%-10.2f %-20s %s\n", type, amount, note, date.toString());
    }
}

class BankAccount {
    static int nextAccountNumber = 1001;

    int accountNumber;
    String holderName;
    int pin;
    double balance;
    List<Transaction> history;

    public BankAccount(String name, int pin, double initialDeposit) {
        this.accountNumber = nextAccountNumber++;
        this.holderName = name;
        this.pin = pin;
        this.balance = initialDeposit;
        this.history = new ArrayList<>();
        history.add(new Transaction("DEPOSIT", initialDeposit, "Initial Deposit"));
    }

    public boolean verifyPIN(int inputPin) {
        return this.pin == inputPin;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("⚠️ Invalid amount.");
            return;
        }
        balance += amount;
        history.add(new Transaction("DEPOSIT", amount, "Self Deposit"));
        System.out.println("✅ Deposit successful.");
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("⚠️ Invalid amount.");
        } else if (amount > balance) {
            System.out.println("❌ Insufficient balance.");
        } else {
            balance -= amount;
            history.add(new Transaction("WITHDRAW", amount, "Self Withdrawal"));
            System.out.println("✅ Withdrawal successful.");
        }
    }

    public void transfer(BankAccount receiver, double amount) {
        if (amount <= 0) {
            System.out.println("⚠️ Invalid amount.");
        } else if (amount > balance) {
            System.out.println("❌ Not enough balance to transfer.");
        } else {
            this.balance -= amount;
            receiver.balance += amount;

            this.history.add(new Transaction("TRANSFER", amount, "To Acc " + receiver.accountNumber));
            receiver.history.add(new Transaction("RECEIVE", amount, "From Acc " + this.accountNumber));
            System.out.println("✅ Transferred ₹" + amount + " to " + receiver.holderName);
        }
    }

    public void showBalance() {
        System.out.printf("💰 Current Balance: ₹%.2f\n", balance);
    }

    public void showMiniStatement() {
        System.out.println("\n📄 Last 5 Transactions:");
        System.out.println("-----------------------------------------------------------");
        int start = Math.max(0, history.size() - 5);
        for (int i = start; i < history.size(); i++) {
            history.get(i).print();
        }
        System.out.println("-----------------------------------------------------------");
    }

    public void showAccountDetails() {
        System.out.println("👤 Name: " + holderName);
        System.out.println("🏦 Account Number: " + accountNumber);
        showBalance();
    }
}

public class Main {
    static Scanner sc = new Scanner(System.in);
    static Map<Integer, BankAccount> accounts = new HashMap<>();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n======== WELCOME TO JAVA BANK ========");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select option: ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1 -> createAccount();
                case 2 -> login();
                case 3 -> {
                    System.out.println("👋 Thanks for using Java Bank. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid option.");
            }
        }
    }

    static void createAccount() {
        sc.nextLine(); // clear buffer
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Set a 4-digit PIN: ");
        int pin = sc.nextInt();
        System.out.print("Initial deposit: ");
        double deposit = sc.nextDouble();

        BankAccount account = new BankAccount(name, pin, deposit);
        accounts.put(account.accountNumber, account);
        System.out.println("✅ Account created successfully!");
        System.out.println("🎉 Your Account Number: " + account.accountNumber);
    }

    static void login() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();
        System.out.print("Enter PIN: ");
        int pin = sc.nextInt();

        BankAccount acc = accounts.get(accNo);
        if (acc == null || !acc.verifyPIN(pin)) {
            System.out.println("❌ Login failed. Invalid credentials.");
            return;
        }

        System.out.println("✅ Logged in as " + acc.holderName);
        accountMenu(acc);
    }

    static void accountMenu(BankAccount acc) {
        while (true) {
            System.out.println("\n======== ACCOUNT MENU ========");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Mini Statement");
            System.out.println("6. Account Details");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> acc.showBalance();
                case 2 -> {
                    System.out.print("Amount to deposit: ");
                    double amount = sc.nextDouble();
                    acc.deposit(amount);
                }
                case 3 -> {
                    System.out.print("Amount to withdraw: ");
                    double amount = sc.nextDouble();
                    acc.withdraw(amount);
                }
                case 4 -> {
                    System.out.print("Enter receiver account number: ");
                    int toAcc = sc.nextInt();
                    BankAccount receiver = accounts.get(toAcc);
                    if (receiver == null) {
                        System.out.println("❌ Receiver account not found.");
                        break;
                    }
                    System.out.print("Amount to transfer: ");
                    double amount = sc.nextDouble();
                    acc.transfer(receiver, amount);
                }
                case 5 -> acc.showMiniStatement();
                case 6 -> acc.showAccountDetails();
                case 7 -> {
                    System.out.println("🔒 Logged out successfully.");
                    return;
                }
                default -> System.out.println("❌ Invalid option.");
            }
        }
    }
}
