import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Main {
	public static String masterKey = "abcdefghijklmnop";
    public static void main(String[] args) throws IOException {
        HashMap<String, String> passwordDatabase = new HashMap<>();  //hashmap for local memory storage of passwords for encryption
        final Path path = Files.createTempFile("ENCRYPTEDpasswords", ".txt");

        if(Files.exists(path))
        {
            readDecrypt(passwordDatabase);
        }
        else{
            read(passwordDatabase);
        }

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
            String choice;
            System.out.println("Would you like to [1] add a password, [2] find a password, " +
                    "\n[3] randomly generate a password, [4] delete password, or [5] exit? [1, 2, 3, 4, or 5]");
            // kmart.next();
            choice = kmart.nextLine();

            if (choice.equals("1")) {
                System.out.println("What is the password for? ");
                String app = kmart.next();
                System.out.println("What is the password? ");
                String password = kmart.next();
                add(app.toLowerCase(), password, passwordDatabase);
            }

            else if (choice.equals("2")) {
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
                        for (Map.Entry<String, String> entry2 : passwordDatabase.entrySet()) {  //gathers all hashmap values into a set
                            if(entry2.getKey().indexOf(app) >= 0) {
                                System.out.println(entry2.getKey());
                            }
                        }

                    }
                }

            } else if (choice.equals("3")) {  //CREATING THE RANDOMLY GENERATED PASSWORD
                int length1, numNum1, numChar1;
                String response;
                int running2 = 1;
                while(running2 == 1) {
                    try {
                        System.out.println("Length: ");
                        length1 = kmart.nextInt();
                        System.out.println("Maximum number of numbers: ");
                        numNum1 = kmart.nextInt();
                        System.out.println("Maximum number of special characters: ");
                        numChar1 = kmart.nextInt();

                        if ((numNum1 + numChar1) > length1) {
                            System.out.println("[Error] Exceeded maximum length.");
                        } else {
                            Password password1 = new Password(length1, numNum1, numChar1);
                            System.out.println(password1.createPassword());
                            running2 = 0;
                        }
                    } catch(Exception e ) {
                        System.out.println("[Error] Please enter a number.");
                        kmart.next();
                    }
                }



            }
            else if(choice.equals("4")) {
                System.out.println("What application's password would you like to delete? ");
                String app = kmart.next();
                deletePassword(passwordDatabase,app);
            }
            else if(choice.equals("5")){ //PROGRAM STOPS RUNNING
                System.out.println("Bye!");
                running = 0;
            }
            else{
                System.out.println("[Error] Please enter the correct command."); //ADDED ERROR MESSAGE FOR INCORRECT INPUT
            }

        }
    }
    
    public static void setMasterKey(String newMaster) {
    	// guard for nulls and empty strings
    	if (newMaster == null || newMaster.length() < 1) return;
    	// duplicate short keys and cut long keys.
    	// there are likely better ways to handle this.
    	masterKey = (newMaster.repeat(16)).substring(0, 16);
    }

    public static void add(String app, String password, HashMap<String, String> passwordDatabase) {
        passwordDatabase.put(app, password);
    }

    public static void usingBufferedWriter(String app, String password) throws IOException {
        AES encryptor = new AES();
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("ENCRYPTEDpasswords.txt", true)
        );
        writer.newLine();
        writer.write(encryptor.paddedEncryption(app + ", " + password,masterKey));
        writer.close();

    }

    public static void read(HashMap<String, String> x) {       //reads text file and copies it into local memory
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "passwords.txt"));
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

    public static void readDecrypt(HashMap<String, String> x) {       //reads text file and copies it into local memory
        AES decryptor = new AES();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "ENCRYPTEDpasswords.txt"));
            String line = reader.readLine();
            while (line != null) {
               // System.out.println(line.length());
                if(line.length() > 0) {
                    line = decryptor.paddedDecryption(line, masterKey);
                }
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
            File f = new File("ENCRYPTEDpasswords.txt");
            f.delete();
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    public static void createFile() throws IOException {
        File file = new File("ENCRYPTEDpasswords.txt");
        file.createNewFile();
    }

    public static void deletePassword(HashMap<String, String> passwordDatabase, String app) {
        passwordDatabase.remove(app);

    }
}