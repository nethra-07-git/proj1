import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SujaiBank {
    static final Scanner scanner = new Scanner(System.in);
    static final List<User> users = new ArrayList<>();
    static final Random random = new Random();
    static final String ADMIN_CODE = "sujai";
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a", Locale.ENGLISH);

    static class C {
        static final String RESET="\u001B[0m", BOLD="\u001B[1m", DIM="\u001B[2m", CYAN="\u001B[96m", GREEN="\u001B[92m", YELLOW="\u001B[93m", RED="\u001B[91m", MAGENTA="\u001B[95m", BLUE="\u001B[94m";
    }
    static class User {
        Integer accountNumber;
        String name, username, password, role, status="active";
        double balance;
        List<String> notifications = new ArrayList<>();
        List<String> history = new ArrayList<>();
        User(Integer accountNumber, String name, String username, String password, double balance, String role) {
            this.accountNumber=accountNumber; this.name=name; this.username=username;
            this.password=password; this.balance=balance; this.role=role;
        }
    }
    static String c(Object value, String color) { return color + value + C.RESET; }
    static void success(String s) { System.out.println(c(s,C.GREEN)); }
    static void error(String s) { System.out.println(c(s,C.RED)); }
    static void warning(String s) { System.out.println(c(s,C.YELLOW)); }
    static void info(String s) { System.out.println(c(s,C.CYAN)); }
    static void divider() { System.out.println(c("_".repeat(42),C.DIM)); }
    static void header(String title, String color) {
        System.out.println("\n"+c("*"+"-".repeat(42)+"*",color));
        System.out.println(c("* "+title+" *",C.BOLD+color));
        System.out.println(c("*"+"-".repeat(42)+"*",color));
    }
    static void banner() {
        System.out.println(c("*"+"-".repeat(38)+"*",C.CYAN));
        System.out.println(c("|"+String.format("%38s", " ")+"|",C.CYAN));
        System.out.println(c("|"+String.format("%38s", "*  S U J A I   B A N K  *").replaceFirst("^\\s+", " ")+"|",C.BOLD+C.CYAN));
        System.out.println(c("*"+"-".repeat(38)+"*",C.CYAN));
        System.out.println(c("~ your money, minus the boredom ~",C.YELLOW));
    }
    static String ask(String prompt) { System.out.print(c(prompt,C.BLUE)); return scanner.nextLine().trim(); }
    static String choice() { return ask("Select an option: "); }
    static double positiveAmount(String prompt) {
        while (true) {
            try {
                double amount=Double.parseDouble(ask(prompt));
                if (!Double.isFinite(amount) || amount<=0) { warning("⚠️ Please enter a finite positive number."); continue; }
                return amount;
            } catch(NumberFormatException e) { warning("⚠️ Invalid input. Please enter numbers only."); }
        }
    }
    static String money(double amount) { return String.format(Locale.US,"₹%,.2f",amount); }
    static void log(User u,String description) { u.history.add("["+LocalDateTime.now().format(DATE_FORMAT)+"] "+description); }
    static void seedAdmin() {
        if (users.stream().noneMatch(u->u.role.equals("admin"))) users.add(new User(null,"System Admin","admin","admin123",0,"admin"));
    }
    static int accountNumber() {
        int n;
        do { n=100000+random.nextInt(900000); }
        while (accountNumberExists(n));
        return n;
    }
    static boolean accountNumberExists(int number) {
        for (User u : users) if (u.accountNumber != null && u.accountNumber == number) return true;
        return false;
    }
    static User findUser(String username,String role) {
        for(User u:users) if(u.username.equals(username) && u.role.equals(role)) return u;
        return null;
    }
    static User findClient(String key) {
        for(User u:users) if(u.role.equals("client") && (u.username.equals(key) || String.valueOf(u.accountNumber).equals(key))) return u;
        return null;
    }
    static boolean usernameExists(String username) { return users.stream().anyMatch(u->u.username.equals(username)); }
    static String newUsername(String prompt) {
        while(true) {
            String name=ask(prompt);
            if(name.isEmpty()) warning("⚠️ Username cannot be empty.");
            else if(usernameExists(name)) warning("⚠️ Username already taken.");
            else return name;
        }
    }
    static String newPassword(String prompt) {
        while(true) {
            String password=ask(prompt);
            if(password.length()>=4) return password;
            warning("⚠️ Password too short.");
        }
    }
    static boolean retry(int remaining) {
        warning("❌ Invalid username or password. "+remaining+" attempt(s) left.");
        while(true) {
            String response=ask("Retry login? (r = retry / e = exit to main menu): ").toLowerCase(Locale.ROOT);
            if(Arrays.asList("r","retry","y","yes").contains(response)) return true;
            if(Arrays.asList("e","exit","n","no").contains(response)) return false;
            warning("⚠️ Please type 'r' or 'e'.");
        }
    }
    static void signupMenu() {
        header("SIGNUP AS",C.YELLOW);
        System.out.println("1. Client Signup\n2. Admin Signup\n3. Back");
        switch(choice()) {
            case "1" -> clientSignup();
            case "2" -> adminSignup();
            case "3" -> { return; }
            default -> warning("⚠️ Invalid choice. Select 1-3.");
        }
    }
    static void adminSignup() {
        header("ADMIN SIGNUP",C.RED);
        boolean verified=false;
        for(int i=0;i<3;i++) {
            if(ask("Enter Admin Code: ").equals(ADMIN_CODE)) { verified=true; break; }
            if(i==2) error("🚫 Incorrect admin code. Maximum attempts reached.");
            else warning("⚠️ Incorrect admin code. "+(2-i)+" attempt(s) left.");
        }
        if(!verified) { info("Returning to main menu..."); return; }
        success("✅ Admin code verified.");
        String name=ask("Full Name: ");
        String username=newUsername("Create Admin Username: ");
        String password=newPassword("Create Admin Password (min 4 characters): ");
        users.add(new User(null,name,username,password,0,"admin"));
        success("✅ Admin account created successfully!");
        info("You can now log in as admin.");
    }
    static void clientSignup() {
        header("CLIENT SIGNUP",C.GREEN);
        String name=ask("Full Name: ");
        String username=newUsername("Create Username: ");
        String password=newPassword("Create Password (min 4 characters): ");
        double initial;
        do {
            initial=positiveAmount("Initial Deposit (min ₹500): ₹");
            if(initial<500) warning("⚠️ Initial deposit must be at least ₹500.");
        } while(initial<500);
        User user=new User(accountNumber(),name,username,password,initial,"client");
        users.add(user);
        log(user,"Account opened with initial deposit "+money(initial));
        divider();
        success("✅ Account created successfully! Your account number is "+user.accountNumber);
        info("You can now log in with your username and password.");
        divider();
    }
    static void loginMenu() {
        header("LOGIN AS",C.YELLOW);
        System.out.println("1. Client Login\n2. Admin Login\n3. Back");
        switch(choice()) {
            case "1" -> login("client");
            case "2" -> login("admin");
            case "3" -> { return; }
            default -> warning("⚠️ Invalid choice. Select 1-3.");
        }
    }
    static void login(String role) {
        header(role.equals("admin")?"ADMIN LOGIN":"CLIENT LOGIN",role.equals("admin")?C.RED:C.CYAN);
        for(int i=0;i<3;i++) {
            String username=ask("Username: ");
            String password=ask("Password: ");
            User user=findUser(username,role);
            if(user!=null && user.password.equals(password)) {
                if(user.status.equals("frozen")) { error("🔒 Your account is frozen. Contact the bank admin."); return; }
                success("\n✅ Welcome back, "+user.name+"!");
                if(role.equals("client")) {
                    if(!user.notifications.isEmpty()) {
                        info("📢 You have new notifications from the bank:");
                        for(String note:user.notifications) System.out.println(" - "+note);
                        user.notifications.clear();
                    }
                    clientDashboard(user);
                } else adminDashboard();
                return;
            }
            int remaining=2-i;
            if(remaining==0) break;
            if(!retry(remaining)) { info("Returning to main menu..."); return; }
        }
        error("🚫 Too many failed attempts. Returning to main menu.");
    }
    static void clientDashboard(User user) {
        while(true) {
            header("CLIENT MENU - "+user.name,C.MAGENTA);
            System.out.println("1. Deposit\n2. Withdraw\n3. Quick Cash\n4. Balance Check\n5. Transfer Money\n6. Change Password\n7. View Account Details\n8. Transaction History\n9. Logout");
            switch(choice()) {
                case "1" -> deposit(user);
                case "2" -> withdraw(user);
                case "3" -> quickCash(user);
                case "4" -> balanceCheck(user);
                case "5" -> transfer(user);
                case "6" -> changePassword(user);
                case "7" -> accountDetails(user);
                case "8" -> transactionHistory(user);
                case "9" -> { info("Logged out successfully."); return; }
                default -> warning("⚠️ Invalid choice. Select 1-9.");
            }
        }
    }
    static void deposit(User user) {
        double amount=positiveAmount("Enter amount to deposit: ₹");
        user.balance+=amount;
        log(user,"Deposit "+money(amount));
        success("✅ "+money(amount)+" deposited successfully. New balance: "+money(user.balance));
    }
    static void withdraw(User user) {
        double amount=positiveAmount("Enter amount to withdraw: ₹");
        if(amount>user.balance) { error("❌ Insufficient balance."); return; }
        user.balance-=amount;
        log(user,"Withdraw "+money(amount));
        success("✅ "+money(amount)+" withdrawn successfully. New balance: "+money(user.balance));
    }
    static void quickCash(User user) {
        int[] amounts={500,1000,2000,5000};
        header("QUICK CASH",C.YELLOW);
        for(int i=0;i<amounts.length;i++) System.out.println((i+1)+". "+amounts[i]);
        System.out.println("5. Cancel");
        while(true) {
            String selected=choice();
            if(!Arrays.asList("1","2","3","4","5").contains(selected)) { warning("⚠️ Invalid choice. Try again."); continue; }
            int index=Integer.parseInt(selected)-1;
            if(index==4) return;
            double amount=amounts[index];
            if(amount>user.balance) { error("❌ Insufficient balance for this quick cash option."); return; }
            user.balance-=amount;
            log(user,"Quick Cash "+money(amount));
            success("✅ "+money(amount)+" dispensed successfully. New balance: "+money(user.balance));
            return;
        }
    }
    static void balanceCheck(User user) { divider(); success("💰 Current Balance : "+money(user.balance)); divider(); }
    static void transfer(User user) {
        header("TRANSFER MONEY",C.BLUE);
        User receiver=findClient(ask("Enter receiver's username or account number: "));
        if(receiver==null) { error("❌ Receiver not found."); return; }
        if(receiver==user) { error("❌ You cannot transfer money to your own account."); return; }
        if(receiver.status.equals("frozen")) { error("❌ Receiver's account is frozen. Transfer cancelled."); return; }
        double amount=positiveAmount("Enter amount to transfer: ₹");
        if(amount>user.balance) { error("❌ Insufficient balance for this transfer."); return; }
        user.balance-=amount;
        receiver.balance+=amount;
        log(user,"Transfer "+money(amount)+" to "+receiver.name);
        log(receiver,"Received "+money(amount)+" from "+user.name);
        success("✅ "+money(amount)+" transferred to "+receiver.name+" successfully. New balance: "+money(user.balance));
    }
    static void changePassword(User user) {
        if(!ask("Enter current password: ").equals(user.password)) { error("❌ Incorrect current password."); return; }
        String password=ask("Enter new password (min 4 characters): ");
        if(password.length()<4) { warning("⚠️ Password too short. Not changed."); return; }
        user.password=password;
        success("✅ Password changed successfully.");
    }
    static void accountDetails(User user) {
        header("ACCOUNT DETAILS",C.CYAN);
        System.out.println("Account Number : "+user.accountNumber+"\nName           : "+user.name+"\nUsername       : "+user.username+"\nStatus         : "+user.status+"\nBalance        : "+money(user.balance));
        divider();
    }
    static void transactionHistory(User user) {
        header("TRANSACTION HISTORY",C.MAGENTA);
        if(user.history.isEmpty()) { info("No transactions yet."); return; }
        for(String entry:user.history) System.out.println(entry);
        divider();
    }
    static void adminDashboard() {
        while(true) {
            header("ADMIN MENU",C.RED);
            System.out.println("1. View All Accounts\n2. Search User\n3. Check User Balance\n4. Deposit into User Account\n5. Withdraw from User Account\n6. Delete User\n7. Freeze / Unfreeze Account\n8. Notify User\n9. Logout");
            switch(choice()) {
                case "1" -> viewAllAccounts();
                case "2" -> searchUser();
                case "3" -> checkUserBalance();
                case "4" -> adminDeposit();
                case "5" -> adminWithdraw();
                case "6" -> deleteUser();
                case "7" -> freezeAccount();
                case "8" -> notifyUser();
                case "9" -> { info("Admin logged out."); return; }
                default -> warning("⚠️ Invalid choice. Select 1-9.");
            }
        }
    }
    static User promptClient() {
        User user=findClient(ask("Enter username or account number: "));
        if(user==null) error("❌ User not found.");
        return user;
    }
    static void viewAllAccounts() {
        header("ALL ACCOUNTS",C.BLUE);
        if(users.stream().noneMatch(u->u.role.equals("client"))) { info("No clients registered yet."); return; }
        System.out.printf("%-10s %-15s %-20s %-15s %s%n","Acc No","Username","Name","Balance","Status");
        divider();
        for(User u:users) if(u.role.equals("client"))
            System.out.printf("%-10d %-15s %-20s %-15s %s%n",u.accountNumber,u.username,u.name,money(u.balance),c(u.status,u.status.equals("active")?C.GREEN:C.RED));
    }
    static void searchUser() {
        header("SEARCH USER",C.CYAN);
        User u=promptClient();
        if(u!=null) System.out.println("Account No : "+u.accountNumber+"\nUsername   : "+u.username+"\nName       : "+u.name+"\nBalance    : "+money(u.balance)+"\nStatus     : "+u.status);
    }
    static void checkUserBalance() {
        header("CHECK USER BALANCE",C.GREEN);
        User u=promptClient();
        if(u!=null) System.out.println("Username : "+u.username+"\nBalance  : "+money(u.balance));
    }
    static void adminDeposit() {
        header("DEPOSIT TO USER",C.GREEN);
        User u=promptClient();
        if(u==null) return;
        double amount=positiveAmount("Enter amount to deposit: ₹");
        u.balance+=amount;
        log(u,"Admin deposited "+money(amount));
        success("✅ "+money(amount)+" deposited. New balance: "+money(u.balance));
    }
    static void adminWithdraw() {
        header("WITHDRAW FROM USER",C.RED);
        User u=promptClient();
        if(u==null) return;
        double amount=positiveAmount("Enter amount to withdraw: ₹");
        if(amount>u.balance) { error("❌ Insufficient balance in user's account."); return; }
        u.balance-=amount;
        log(u,"Admin withdrew "+money(amount));
        success("✅ "+money(amount)+" withdrawn. New balance: "+money(u.balance));
    }
    static void deleteUser() {
        header("DELETE USER",C.RED);
        User u=promptClient();
        if(u==null) return;
        if(ask("Are you sure you want to delete '"+u.username+"'? (yes/no): ").equalsIgnoreCase("yes")) {
            users.remove(u); success("✅ User account deleted successfully.");
        } else info("Deletion cancelled.");
    }
    static void freezeAccount() {
        header("FREEZE / UNFREEZE",C.CYAN);
        User u=promptClient();
        if(u==null) return;
        if(u.status.equals("active")) { u.status="frozen"; warning("🔒 Account '"+u.username+"' has been frozen. They will not be able to log in."); }
        else { u.status="active"; success("🔓 Account '"+u.username+"' has been unfrozen."); }
    }
    static void notifyUser() {
        header("NOTIFY USER",C.MAGENTA);
        User u=promptClient();
        if(u==null) return;
        u.notifications.add(ask("Enter notification message: "));
        success("✅ Notification sent. The user will see it at their next login.");
    }
    static void mainMenu() {
        seedAdmin(); banner();
        while(true) {
            header("WELCOME TO SUJAI BANK",C.CYAN);
            System.out.println("1. Login\n2. Signup\n3. Exit");
            switch(choice()) {
                case "1" -> loginMenu();
                case "2" -> signupMenu();
                case "3" -> { info("Thank you for using Sujai Bank. Goodbye! 👋"); return; }
                default -> warning("⚠️ Invalid choice. Select 1, 2, or 3.");
            }
        }
    }
    public static void main(String[] args) { mainMenu(); }
}
