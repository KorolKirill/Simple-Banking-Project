package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BankingSystem {
    List<BankAccount> accounts = new ArrayList<>();
    String BIN;
    int pinLength;
    Statement statementLiteSql;
    SQLiteDataSource dataSource;

    private void initialDataBase(String url) throws SQLException {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        statementLiteSql = dataSource.getConnection().createStatement();
        statementLiteSql.execute("create table if not exists card (" +
                "id Integer PRIMARY KEY AUTOINCREMENT," +
                "number VARCHAR(16)," +
                "pin VARCHAR(" + pinLength + ")," +
                "balance INTEGER DEFAULT 0" +
                "); ");
    }

    protected void exit() throws SQLException {
        //    statementLiteSql.execute("DROP TABLE card;");
        statementLiteSql.close();
    }

    protected String transferMoney(BankAccount from, String toNumber, int amount) throws SQLException {
        if (from.balance < amount) {
            return "Not enough money!";
        } else {
            statementLiteSql.addBatch(String.format("UPDATE card set balance = balance + %d where number = %s", amount, toNumber));
            statementLiteSql.addBatch(String.format("Update card SET balance = balance - %d WHERE number = %s", amount, from.cardNumber));
            from.balance -= amount;
            statementLiteSql.executeBatch();
            statementLiteSql.clearBatch();
            return "Success!";
        }
    }
    private boolean lughAlgoritm(String cardNumber) {
        char[] numbers = cardNumber.toCharArray();
        int checkSum = 0;
        int index = 1;
        for (char number : numbers) {
            int currentNumber = Integer.parseInt(String.valueOf(number));
            if (index % 2 == 1) {
                currentNumber *= 2;
            }
            if (currentNumber > 9) {
                currentNumber -= 9;
            }
            checkSum += currentNumber;
            index++;
        }
        checkSum -= Integer.parseInt(numbers[numbers.length - 1]+"");
        int lastDigit = 10 - checkSum % 10 ==10? 0 : 10 - checkSum % 10;
        return lastDigit == Integer.parseInt(numbers[numbers.length - 1] + "");
    }
    protected String cardExists(String number) throws SQLException {
        if (lughAlgoritm(number)) {
            ResultSet resultSet = statementLiteSql.executeQuery(String.format("select * from card where number = %s", number));
            if (resultSet.next()) {
                return "OK";
            } else {
                return "Such a card does not exist.";
            }
        } else {
            return "Probably you made a mistake in the card number. Please try again!";
        }
    }


    public BankingSystem(String BIN, int pinLength, String dbFile) throws SQLException {
        this.BIN = BIN;
        this.pinLength = pinLength;
        initialDataBase(String.format("jdbc:sqlite:%s", dbFile));
    }

    public BankAccount createNewAccount() throws SQLException {
        BankAccount newAccount = new BankAccount(createNewCardNumber(), createNewPin());
        statementLiteSql.execute(String.format("INSERT into card ('number','pin') VALUES ('%s','%s')",
                newAccount.cardNumber, newAccount.pin));
//        accounts.add(newAccount);
        return newAccount;
    }

    Random random = new Random();

    private String createNewCardNumber() {
        StringBuilder cardnumber = new StringBuilder(BIN);
        for (int i = 0; i < 9; i++) {
            cardnumber.append(random.nextInt(10));
        }
        cardnumber.append(createCheckSum(cardnumber.toString()));
        return cardnumber.toString();
    }

    private String createCheckSum(String str) {
        char[] numbers = str.toCharArray();
        int checkSum = 0;
        int index = 1;
        for (char number : numbers) {
            int currentNumber = Integer.parseInt(String.valueOf(number));
            if (index % 2 == 1) {
                currentNumber *= 2;
            }
            if (currentNumber > 9) {
                currentNumber -= 9;
            }
            checkSum += currentNumber;
            index++;
        }
        int lastDigit = 10 - checkSum % 10;
        return lastDigit == 10 ? String.valueOf(0) : String.valueOf(lastDigit);
    }

    private String createNewPin() {
        StringBuilder newPin = new StringBuilder("");
        for (int i = 0; i < pinLength; i++) {
            newPin.append(random.nextInt(10));
        }
        return newPin.toString();
    }

    public void closeAccount(BankAccount account) throws SQLException {
        statementLiteSql.execute(String.format("DELETE from card where pin=%s and number = %s ;",
                account.pin, account.cardNumber));

    }

    private String addIncome = "Update card set balance = balance + ? where pin = ? and number = ?;";

    public void addIncome(BankAccount account, int income) throws SQLException {
        statementLiteSql.execute(String.format("UPDATE card set balance = balance + %d where number = %s", income, account.cardNumber));
        account.balance+=income;
//        try (PreparedStatement updateCardBalance = dataSource.getConnection().prepareStatement(addIncome)) {
//            updateCardBalance.setInt(1,income);
//            updateCardBalance.setString(2,account.pin);
//            updateCardBalance.setString(3,account.cardNumber);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    BankAccount logIntoAccount(String cardNumber, String pin) throws SQLException {
//        for (BankAccount bankAccount : accounts) {
//            if (bankAccount.cardNumber.equals(cardNumber) && bankAccount.pin.equals(pin)) {
//                return bankAccount;
//            }
//        }
        ResultSet resultSet = statementLiteSql.executeQuery(
                String.format("select * from card where number = %s and pin = %s ;", cardNumber, pin));
        if (resultSet.next()) {
            return new BankAccount(cardNumber, pin, resultSet.getInt("balance"));
        }
        return null;
    }
}
