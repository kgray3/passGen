import java.io.*;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws IOException {
        int running = 1;
        while (running == 1) {
        Scanner kmart = new Scanner(System.in);
        int choice;
        System.out.println("Would you like to [1] add a password, [2] find a password, " +
                "\n[3] randomly generate a password, or [4] exit? [1, 2, 3, or 4]");
        choice = kmart.nextInt();

        if(choice == 1) {
            System.out.println("What is the password for? ");
            String app = kmart.next();
            System.out.println("What is the password :3");
            String password = kmart.next();
            usingBufferedWriter(app, password);
        }

        else if(choice == 2) {
            System.out.println("What is the password for? ");
            String app = kmart.next();
            System.out.println("The password for " + app + " is:\n" + readFile(app) + "\n");
        }

        else if(choice == 3) {  //CREATING THE RANDOMLY GENERATED PASSWORD
            int length1, numNum1, numChar1;
            String response;

            System.out.println("Length: ");
            length1 = kmart.nextInt();
            System.out.println("Maximum number of numbers: ");
            numNum1 = kmart.nextInt();
            System.out.println("Maximum number of characters: ");
            numChar1 = kmart.nextInt();


            Password password1 = new Password(length1, numNum1, numChar1);
            System.out.println(password1.createPassword());

           // System.out.println("Again? ");
         //   response = kmart.next();
          //  if(response.equalsIgnoreCase("no")) {
           //     running = 0;
          //  }
        }
        else { //PROGRAM STOPS RUNNING
            running = 0;
        }

        }
    }

    public static void usingBufferedWriter(String app, String password) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("C:/Users/kayla/Desktop/PassGen/passwords.txt", true)
        );
        writer.newLine();
        writer.write(app + ", "+ password);
        writer.close();

    }

    public static String readFile(String app) {
       // app = app.toLowerCase();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "C:/Users/kayla/Desktop/PassGen/passwords.txt"));
            String line = reader.readLine();
            while (line != null) {
                //System.out.println(line);
                // read next line
                line = reader.readLine();     //.toLowerCase();
                if(line.indexOf(app) >= 0) {
                    return line.substring(line.indexOf(",") + 2);
                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "no such app";
    }
}
