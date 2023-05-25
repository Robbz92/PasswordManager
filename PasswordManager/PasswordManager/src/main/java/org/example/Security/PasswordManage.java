package org.example.Security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PasswordManage {
    private final String LOWER_CHARS ="abcdefghijklmnopqrstuvwxyz";
    private final String UPPER_CHARS = LOWER_CHARS.toUpperCase();
    private final String NUMBERS = "0123456789";
    private final String ALL_CHARS = LOWER_CHARS+UPPER_CHARS+NUMBERS;
    private Random random = new Random();

    public String generatePassword(int passwordLength){
        // Grab random char from lower, upper and number
        // then grab the rest from all chars

        StringBuilder password = new StringBuilder();

        password.append(getRandomCharacter(LOWER_CHARS));
        password.append(getRandomCharacter(UPPER_CHARS));
        password.append(getRandomCharacter(NUMBERS));

        int remainingLength = passwordLength -= 3;

        for (int i = 0; i< remainingLength; i++){
            password.append(getRandomCharacter(ALL_CHARS));
        }

        return shufflePassword(password);
    }
    private char getRandomCharacter(String char_pool){
        int index = random.nextInt(char_pool.length());

        return char_pool.charAt(index);
    }
    private String shufflePassword(StringBuilder password){
        ArrayList<String> list = new ArrayList<>();
        for(Character character : password.toString().toCharArray()){
            list.add(String.valueOf(character));
        }

        Collections.shuffle(list);
        password.setLength(0);

        for (String s : list){
            password.append(s);
        }

        return password.toString();
    }
}
