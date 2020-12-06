import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Main {
    private static String MasterKey; //stores user-inputted master key
    private static boolean correctKey = false; //used to decide if files should be overwritten upon shutdown
    public static void main(String[] args) throws IOException {
        HashMap<String, String> passwordDatabase = new HashMap<>();  //hashmap for local memory storage of passwords for encryption
      //  final Path path = Files.createTempFile("keyfile", ".txt");
        boolean fileExists = Files.isReadable(Paths.get("keyfile.txt")) && Files.isWritable(Paths.get("keyfile.txt"));
        Scanner kmart= new Scanner(System.in);
      //  System.out.println("Is this your first time using? [y] or [n] "); //I want to get rid of this
       // String response2 = kmart.nextLine();                            //file.exists was giving problems for some reason

    //things to do: fix file.exists problem -- maybe look into hidden files

        if (!fileExists){ //file doesn't exist -- FIRST TIME USE
            //Scanner tempScan = new Scanner(System.in);
            System.out.println("Welcome, first-time user. Please enter your master key (length 16): ");
            String temp = kmart.nextLine();
            MasterKey = temp;
            boolean needKey = true;

            while(needKey) {  //ensures that some key gets entered that is length 16
               if (MasterKey.length() > 16) {
                    System.out.println("Key length is too long. Please try again: ");
                    MasterKey = kmart.nextLine();
                }
                else if(MasterKey.length() < 16) {
                    System.out.println("Key length is too short. Please try again: ");
                    MasterKey = kmart.nextLine();
                }
                else { //correct key size --> let user know key so they can memorize it
                   System.out.println("[KEY ACCEPTED] Your master key is " + MasterKey +". Don't lose it.");
                   correctKey = true;
                   needKey = false;
               }
            }
        }

        else //user has used program before
        {
            System.out.println("Welcome back! Please enter your master key: ");
            String input = kmart.next();
            int trial = 1;
            while(trial < 3) { //checks trials 1 + 2 and asks for trials 2 + 3
                if(!match(input)) {
                    trial++;
                    System.out.println("Incorrect key. Please try again (attempt " + trial + " of 3): ");
                    input = kmart.next();
                }
                else { //if key matches
                    correctKey = true; //shutdown hook is 'enabled'
                    MasterKey = input;
                    System.out.println("\n[KEY ACCEPTED]"); //notifies user

                    //decrypts password file using master key and deletes files so there isn't issues with overwriting later
                    readDecrypt(passwordDatabase);
                    deleteFile("ENCRYPTEDpasswords.txt");
                    deleteFile("keyfile.txt");
                    trial = 5;
                }
            }
            //checks final trial because the while loop is uneven
            if(trial == 3) {
                if(match(input)) {
                    correctKey = true; //shutdown hook is 'enabled'
                    MasterKey = input;
                    System.out.println("\n[KEY ACCEPTED]"); //notifies user

                    //decrypts password file using master key and deletes files so there isn't issues with overwriting later
                    readDecrypt(passwordDatabase);
                    deleteFile("ENCRYPTEDpasswords.txt");
                    deleteFile("keyfile.txt");
                    trial = 5;
                }
            }

            if(!correctKey){
                /* correct key is false by default -- doesn't delete previous password files in case of mistake
                also circumvents attempts to brute force by exiting */
                System.out.println("Attempts failed. Exiting program.");
                System.exit(0);
            }
        }


        /* SHUTDOWN HOOK
        - in case of crash creates file of passwords only if user has entered correct key
        - an incorrect key means no decryption can take place -->
                then passwords can't be stored locally and returned to the new file properly*/
        Runnable doShutDown = () -> {
            if(correctKey) {
                try {
                    //Stores passwords in 'ENCRYPTEDpasswords.txt'
                    createFile("ENCRYPTEDpasswords.txt");
                    for (Map.Entry<String, String> entry : passwordDatabase.entrySet()) {        //gathers all hashmap values into a set
                        usingBufferedWriter(entry.getKey(), entry.getValue(), "ENCRYPTEDpasswords.txt");
                    }

                    //Stores key in 'KEYFILE.txt'
                    createFile("keyfile.txt");
                    SHA enc = new SHA();
                    BufferedWriter writer = new BufferedWriter(
                            new FileWriter("keyfile.txt", true)
                    );
                    writer.newLine();
                    writer.write(enc.get_SHA_512_SecurePassword(MasterKey)); //uses SHA-512 hash function to securely encrypt MasterKey
                    writer.close();
                } catch (Exception e) {
                }
            }
            //without a correct key nothing happens so previous files are preserved
        };

        Runtime.getRuntime().addShutdownHook(new Thread(doShutDown, "ShutdownHook"));

        int  running = 1;

        while (running == 1) {
            String choice;
            System.out.println("\nWould you like to [1] add a password, [2] find a password, [3] randomly generate a password, " +
                    "\n[4] delete password, [5] reset master key, or [6] exit? [1, 2, 3, 4, 5, or 6]");
            // kmart.next();
            choice = kmart.next();

            //ADD PASSWORD
            if (choice.equals("1")) {
                System.out.println("What is the password for? ");
                String app = kmart.next();
                System.out.println("What is the password? ");
                String password = kmart.next();
                add(app.toLowerCase(), password, passwordDatabase);
            }

            //FIND PASSWORD
            else if (choice.equals("2")) {
                int temp = 0;
                while(temp == 0) {
                    System.out.println("What is the password for? ");
                    String app = kmart.next();


                    if(app.indexOf('*') == -1) {
                        System.out.println("The password for " + app + " is:\n" + passwordDatabase.get(app.toLowerCase()) + "\n");
                        temp = 1;
                    }

                    /* '*' can be used to show multiple passwords
                        '*' alone for all passwords in the system
                        '*' + letters to find passwords containing those letters
                     */
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

                        //error-handling for password creation
                        if ((numNum1 + numChar1) > length1) {
                            System.out.println("[Error] Exceeded maximum length.");
                        } else {
                            Password password1 = new Password(length1, numNum1, numChar1);
                            System.out.println(password1.createPassword());
                            running2 = 0;
                        }
                    } catch(Exception e ) {
                        System.out.println("[Error] Please enter a number.");
                       // kmart.next();
                    }
                }



            }
            //DELETE PASSWORD
            else if(choice.equals("4")) {
                System.out.println("What application's password would you like to delete? ");
                String app = kmart.next();
                deletePassword(passwordDatabase,app);
            }

            //CHANGE MASTER KEY
            else if(choice.equals("5")) {
                System.out.println("Enter new master key (length 16): ");
                String newKey = kmart.next();
                MasterKey = newKey;

                boolean needKey = true;
                while(needKey) {
                    if (newKey.length() !=16) { //makes sure user enters a password of length 16
                        System.out.println("Incorrect length. Please try again: ");
                        String temp = kmart.next();
                        MasterKey = temp;
                    }
                    else {
                        System.out.println("[KEY ACCEPTED] Your master key is " + MasterKey +". Don't lose it.");
                        needKey = false;
                    }
                }
            }

            //EXIT PROGRAM
            else if(choice.equals("6")){
                System.out.println("Bye!");
                running = 0;
            }

            //BUGGY INCORRECT COMMAND
            else{
                System.out.println("[Error] Please enter the correct command."); //ADDED ERROR MESSAGE FOR INCORRECT INPUT
            }

        }
        kmart.close();
    }

    //ADDS PASSWORD TO HASHMAP
    public static void add(String app, String password, HashMap<String, String> passwordDatabase) {
        passwordDatabase.put(app, password);
    }

    //ENCRYPTS PASSWORDS USING MASTERKEY AND WRITES THEM TO 'ENCRYPTEDpasswords.txt' FILE UPON EXIT
    public static void usingBufferedWriter(String app, String password, String fileName) throws IOException {
        AES encryptor = new AES();
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName, true)
        );
        writer.newLine();
        writer.write(encryptor.paddedEncryption(app + ", " + password,MasterKey)); //AES encryption of each password
        writer.close();
    }

    //FUNCTION TO CHECK MASTERKEY WITH USERINPUT UPON OPENING PROGRAM
    public static boolean match(String userInput) {
        try {
            SHA encrypt = new SHA();
           BufferedReader x = new BufferedReader(new FileReader("keyfile.txt")); //checks key in 'KEYFILE.txt'
            String line = x.readLine();
            while(line!= null) {
                //encrypts userInput using SHA-512 to see if it matches the encrypted key stored on file
                if(line.equalsIgnoreCase(encrypt.get_SHA_512_SecurePassword(userInput)))
                {
                    x.close();
                    return true;
                }
                line = x.readLine();
            }
          x.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    //reads text file and copies it into local memory
    public static void readDecrypt(HashMap<String, String> x) {
        AES decryptor = new AES();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "ENCRYPTEDpasswords.txt"));
            String line = reader.readLine();
            while (line != null) {
               // System.out.println(line.length());
                if(line.length() > 0) {
                    line = decryptor.paddedDecryption(line, MasterKey);
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

    //DELETES FILES
    public static void deleteFile(String fileName) {
        try {
            File f = new File(fileName);
            f.delete();
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    //CREATES FILES
    public static void createFile(String name) throws IOException {
        File file = new File(name);
        file.createNewFile();
    }

    //DELETES PASSWORDS IN LOCAL HASHMAP
    public static void deletePassword(HashMap<String, String> passwordDatabase, String app) {
        passwordDatabase.remove(app);

    }
}