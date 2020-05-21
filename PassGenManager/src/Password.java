import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;

public class Password {

    private int length, maxNum, maxChar;
    private int count = 0;
    private String singleChar;
    private String password;
    private String characters = "~!@#$%^&*()-_+?><";
    private String letters = "abcdefghijklmnopqrstuvwyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String numbers = "1234567890";
    ArrayList<String> usedCharacters = new ArrayList<>();

    public Password(int length, int numNum, int numChar) {
        this.length = length;
        this.maxNum = numNum;
        this.maxChar = numChar;
        this.password = "";
    }

    public int getLength() {
        return this.length;
    }
    public int getNumNum() {
        return this.maxNum;
    }
    public int getNumChar() {
        return this.maxChar;
    }

    public String createPassword() {
        password = "";
        //Generates characters
        String chars = "";
        while(chars.length() < maxChar) {
            chars += characters.charAt((int)(Math.random() * characters.length()));
        }
        //Generates Numbers
        String nums = "";
        while(nums.length() < maxNum){
            nums += numbers.charAt((int)(Math.random() * numbers.length()));
        }
        //Generates Letters
        String letts = "";
        while(letts.length() < length - (maxNum + maxChar)) {
            letts += letters.charAt((int)(Math.random() * letters.length()));
        }
        //Weak Password Concatenation
        String weakPassword = chars + nums + letts;
        //Randomization of weakPassword into actual password
        while(password.length() < weakPassword.length()) {
            int random = (int) (Math.random() * weakPassword.length());
            singleChar = weakPassword.substring(random,random + 1);
            if(usedCharacters.size() == 0) {
                password += singleChar;
               usedCharacters.add(singleChar);
            }
            else {
                if(isUnique()){
                    password += singleChar;
                    usedCharacters.add(singleChar);
                }
            }
            count++;
            if(count > weakPassword.length() + 10) {
                password += singleChar;
            }

        }

        return password;
    }

    public boolean isUnique() {
        for(String c: usedCharacters) {
            if (singleChar.equalsIgnoreCase(c)) {
                return false;
            }

        }
        return true;
    }
        }


