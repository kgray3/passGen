import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws IOException {
        HashMap<String, String> passwordDatabase = new HashMap<>();  //hashmap for local memory storage of passwords for encryption
        read(passwordDatabase);
        deleteFile();
        int running = 1;


       Runnable doShutDown = () -> {       //in case of crash creates file of passwords still
            try {
                createFile();
                for (Map.Entry<String, String> entry : passwordDatabase.entrySet()) {        //gathers all hashmap values into a set
                    usingBufferedWriter(entry.getKey(), entry.getValue());
                }
            } catch (Exception e){}
        };

        Runtime.getRuntime().addShutdownHook(new Thread(doShutDown, "ShutdownHook"));

        while (running == 1) {
            Scanner kmart = new Scanner(System.in);
            int choice;
            System.out.println("Would you like to [1] add a password, [2] find a password, " +
                    "\n[3] randomly generate a password, [4] delete password, or [5] exit? [1, 2, 3, 4, or 5]");
            choice = kmart.nextInt();

            if (choice == 1) {
                System.out.println("What is the password for? ");
                String app = kmart.next();
                System.out.println("What is the password ");
                String password = kmart.next();
                add(app.toLowerCase(), password, passwordDatabase);
            }

            else if (choice == 2) {
                int temp = 0;
                while(temp == 0) {
                System.out.println("What is the password for? ");
                String app = kmart.next();


                if(app.indexOf('*') == -1) {
                    System.out.println("The password for " + app + " is:\n" + passwordDatabase.get(app.toLowerCase()) + "\n");
                    temp = 1;
                }
                else {
                    app = app.substring(app.indexOf('*') + 1);
                    System.out.println("The choices are: ");
                    for (Map.Entry<String, String> entry2 : passwordDatabase.entrySet()) {        //gathers all hashmap values into a set
                        if(entry2.getKey().indexOf(app) >= 0) {
                            System.out.println(entry2.getKey());
                        }
                    }

                }
                }

            } else if (choice == 3) {  //CREATING THE RANDOMLY GENERATED PASSWORD
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


            }
            else if(choice == 4) {
                System.out.println("What application's password would you like to delete? ");
                String app = kmart.next();
                deletePassword(passwordDatabase,app);
            }
            else { //PROGRAM STOPS RUNNING
                //createFile();
                //for (Map.Entry<String, String> entry : passwordDatabase.entrySet()) {        //gathers all hashmap values into a set
                  //  usingBufferedWriter(entry.getKey(), entry.getValue());
                //}

                running = 0;
            }

        }
    }

    public static void add(String app, String password, HashMap<String, String> passwordDatabase) {
        passwordDatabase.put(app, password);
    }

    public static void usingBufferedWriter(String app, String password) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("C:/Users/kayla/Desktop/PassGen/passwords.txt", true)
        );
        writer.newLine();
        writer.write(app + ", " + password);
        writer.close();

    }

    public static void read(HashMap<String, String> x) {       //reads text file and copies it into local memory
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "C:/Users/kayla/Desktop/PassGen/passwords.txt"));
            String line = reader.readLine();
            while (line != null) {
                //   System.out.println(line);
                int commaPlace = line.indexOf(',');
                if (commaPlace != -1) {
                    x.put(line.substring(0, commaPlace), line.substring(commaPlace + 2));
                }
                line = reader.readLine();   // read next line

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile() {
        try {
            File f = new File("C:/Users/kayla/Desktop/PassGen/passwords.txt");
            f.delete();
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    public static void createFile() throws IOException {
        File file = new File("C:/Users/kayla/Desktop/PassGen/passwords.txt");
        file.createNewFile();
    }

    public static void deletePassword(HashMap<String, String> passwordDatabase, String app) {
        passwordDatabase.remove(app);

    }
}

