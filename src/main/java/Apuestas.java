import java.util.Scanner;
import java.sql.*;
public class Apuestas {
    private static Connection con;
    private static int currentScreen = 0;
    private static int userId = -1;
    private static String username = "";
    public static final String green = "\u001B[32m";
    public static final String reset = "\u001B[0m";
    public static final String blue = "\u001B[34m";


    public static void main(String[] args) throws SQLException{
        int option;
        String host = "jdbc:sqlite:src/main/resources/Apuestas";
        con = java.sql.DriverManager.getConnection( host );
        System.out.println();
        System.out.println(green + "$$\\      $$\\ $$$$$$$$\\ $$\\       $$$$$$\\   $$$$$$\\  $$\\      $$\\ $$$$$$$$\\ \n" +
                "$$ | $\\  $$ |$$  _____|$$ |     $$  __$$\\ $$  __$$\\ $$$\\    $$$ |$$  _____|\n" +
                "$$ |$$$\\ $$ |$$ |      $$ |     $$ /  \\__|$$ /  $$ |$$$$\\  $$$$ |$$ |      \n" +
                "$$ $$ $$\\$$ |$$$$$\\    $$ |     $$ |      $$ |  $$ |$$\\$$\\$$ $$ |$$$$$\\    \n" +
                "$$$$  _$$$$ |$$  __|   $$ |     $$ |      $$ |  $$ |$$ \\$$$  $$ |$$  __|   \n" +
                "$$$  / \\$$$ |$$ |      $$ |     $$ |  $$\\ $$ |  $$ |$$ |\\$  /$$ |$$ |      \n" +
                "$$  /   \\$$ |$$$$$$$$\\ $$$$$$$$\\\\$$$$$$  | $$$$$$  |$$ | \\_/ $$ |$$$$$$$$\\ \n" +
                "\\__/     \\__|\\________|\\________|\\______/  \\______/ \\__|     \\__|\\________|" + reset);
        while (true){
            printMenu();
            option = getOption();
            if (option == 0) break;
            if (currentScreen == 0){
                switch (option){
                    case 1: login();
                    break;
                    case 2: register();
                    break;
                }
            }else{
                switch (option){
                    case 1: myBets();
                    break;
                    case 2: newBet();
                    break;
                    case 3: info();
                    break;
                    case 4: logout();
                    break;
                }
            }
        }
    }
    private static int getOption(){
        Scanner sc = new Scanner(System.in);
        int option = -1;
        try{
            option = Integer.parseInt(sc.next());
            if ((currentScreen == 0 && option > 2) || (currentScreen == 1 && option > 4)){
                System.out.println("Incorrect Option");
            }
        }catch (IllegalArgumentException iae){
            System.out.println("Incorrect Option");
        }
        return option;
    }
    private static void printMenu(){
        System.out.println(blue + "----------------------------------------------------------------------------");
        if (currentScreen == 0){
            System.out.println("[0] Exit | [1] Login | [2] Register");
        }else{
            System.out.println("[0] Exit | [1] My Bets | [2] New Bet | [3] User Info | [4] Logout " + username);
        }
        System.out.println("----------------------------------------------------------------------------" + reset);
    }
    private static void login() throws SQLException{
        Scanner sc = new Scanner(System.in);
        System.out.println("Username: ");
        String uname = sc.nextLine();
        PreparedStatement st = null;
        String query = "SELECT * FROM users WHERE username = ?";
        st = con.prepareStatement(query);
        st.setString(1, uname);
        ResultSet rs = st.executeQuery();
        if (rs.next()){
            userId = rs.getInt("id");
            username = rs.getString("username");
            currentScreen = 1;
        }else{
            System.out.println("User not found");
        }
    }
    private static void logout(){
        currentScreen = 0;
        username = "";
        userId = -1;
    }
    private static void register() throws SQLException{
        Scanner sc = new Scanner(System.in);
        PreparedStatement st = null;
        System.out.println("Username: ");
        String uname = sc.nextLine();
        System.out.println("Password: ");
        String password = sc.nextLine();
        if (password.isEmpty()){
            password = "password";
        }
        System.out.println("Name: ");
        String name = sc.nextLine();
        System.out.println("Age: ");
        String ageInput = sc.nextLine();
        int age;
        if (ageInput.isEmpty()){
            age = 18;
        }else{
            age = Integer.parseInt(ageInput);
        }
        System.out.println("Country: ");
        String country = sc.nextLine();
        String query = "INSERT INTO users (username, password, name, age, country)" +
                "            VALUES (?,?,?,?,?)";
        st = con.prepareStatement(query);
        st.setString(1, uname);
        st.setString(2, password);
        st.setString(3, name);
        st.setInt(4, age);
        st.setString(5, country);
        st.executeUpdate();
    }
    private static void myBets() throws SQLException{
        PreparedStatement st = null;
        String query = "SELECT * FROM bets b inner join users u on b.user_id = u.id" +
                "           WHERE b.user_id = ?";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        while (rs.next()){
            System.out.println(rs.getString("event") + " - " + " Bet: " +
                    rs.getString("bet") + " - " + " Amount: "  + rs.getInt("amount") +
                    " - " + rs.getDate("fecha") + " - " + rs.getString("username"));
        }
    }
    private static void newBet() throws SQLException{
        Scanner sc = new Scanner(System.in);
        PreparedStatement st = null;
        System.out.println("Event: ");
        String event = sc.nextLine();
        System.out.println("Bet (1 x 2): ");
        String bet = sc.nextLine();
        System.out.println("Amount: ");
        String amountInput = sc.nextLine();
        int amount;
        if (amountInput.isEmpty()){
            amount = 1;
        }else{
            amount = Integer.parseInt(amountInput);
        }
        String query = "INSERT INTO bets (event, bet, amount, user_id)" +
                "           VALUES (?,?,?,?)";
        st = con.prepareStatement(query);
        st.setString(1, event);
        st.setString(2, bet);
        st.setInt(3, amount);
        st.setInt(4, userId);
        st.executeUpdate();
    }
    private static void info() throws SQLException{
        PreparedStatement st = null;
        String query = "SELECT * FROM users WHERE id = ?";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        while (rs.next()){
            System.out.println(rs.getInt("id") + " - " + "Username: " +
                    rs.getString("username") + " - " + "Name: " +
                    rs.getString("name") + " - " + "Password: " +
                    rs.getString("password") + " - " + "Age: " + rs.getInt("age") +
                    " - " + "Country: " + rs.getString("country"));
        }
    }
}