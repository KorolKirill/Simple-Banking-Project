package banking;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ConsoleInreface.start(new BankingSystem("400000", 4, args[1]));
    }
}