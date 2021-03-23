package banking;

import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleInreface {
    static BankingSystem bankingSystem;
    static Scanner scanner = new Scanner(System.in);

    public static void start(BankingSystem bankingSystem) throws SQLException {
        ConsoleInreface.bankingSystem = bankingSystem;
        start();
    }

    private static void start() throws SQLException {
        System.out.println(
                "1. Create an account\n" +
                        "2. Log into account\n" +
                        "0. Exit"
        );
        String userOption = scanner.nextLine();
        switch (Integer.parseInt(userOption)) {
            case 1:
                createNewAccount();
                start();
                break;
            case 2:
                logIntoAccount();
                start();
                break;
            case 3:
                exit();
                break;
        }
    }

    private static void logIntoAccount() throws SQLException {
        System.out.println();
        System.out.println("Enter your card number:");
        String cardNubmer = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        BankAccount account = bankingSystem.logIntoAccount(cardNubmer, pin);
        if (account == null) {
            System.out.println("\nWrong card number or PIN!");
            System.out.println();
        } else {
            System.out.println("\nYou have successfully logged in!");
            workingWithAccount(account);
        }
    }

    private static void workingWithAccount(BankAccount account) throws SQLException {
        System.out.println(
                        "1. Balance\n" +
                        "2. Add income\n" +
                        "3. Do transfer\n" +
                        "4. Close account\n" +
                        "5. Log out\n" +
                        "0. Exit");
        String userOption = scanner.nextLine();
        System.out.println();
        switch (Integer.parseInt(userOption)) {
            case 1:
                System.out.printf("Balance: %d\n", account.balance);
                workingWithAccount(account);
                break;
            case 2:
                addIncome(account);
                workingWithAccount(account);
                break;
            case 3:
                transferMoney(account);
                System.out.println();
                workingWithAccount(account);
                break;
            case 4:
                bankingSystem.closeAccount(account);
                System.out.println("The account has been closed!");
                break;
            case 0:
                exit();
                break;
        }
    }
    private  static  void  transferMoney(BankAccount account) throws SQLException {
        System.out.println("Transfer\n" + "Enter card number:");
        String number = scanner.nextLine();
        if (account.cardNumber.equals(number)) {
            System.out.println("You can't transfer money to the same account!");
        }else {
            String cardExistance = bankingSystem.cardExists(number);
            if (cardExistance.equals("OK")) {
                System.out.println("Enter how much money you want to transfer:");
                System.out.println( bankingSystem.transferMoney(account,number,Integer.parseInt(scanner.nextLine())));
            }
            else {
                System.out.println(cardExistance);
            }
        }
    }
    private static void addIncome(BankAccount account) throws SQLException {
        System.out.println("Enter income:");
        bankingSystem.addIncome(account,Integer.parseInt(scanner.nextLine()));
        System.out.println("Income was added!");
        System.out.println("");
    }

    private static void exit() throws SQLException {
        System.out.println("\nBye!");
        bankingSystem.exit();
        System.exit(0);
    }


    private static void createNewAccount() throws SQLException {
        BankAccount account = bankingSystem.createNewAccount();
        System.out.printf(
                "\nYour card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                        "Your card PIN:\n" +
                        "%s" + "\n", account.cardNumber, account.pin);
        System.out.println();

    }
}
