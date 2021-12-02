package hp.server.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * Return true if a string chain containd any numerical character,
     * else return false
     * @param chain
     * @return
     */
    public static boolean cadContainsDigit(String chain) {
        for (int i = 0; i < chain.length(); i++) {
            char character = chain.charAt(i);
            if (Character.isDigit(character)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the chain that receive such as parameter in uppercase
     * @param chain
     * @return
     */
    public static String cadToUpperCase(String chain) {
        return chain.toUpperCase();
    }

    /**
     * Return true when email is valid, else return false
     * @param email
     * @return
     */
    public static boolean validEmail(String email) {
        // Patron para validar el email
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher match = pattern.matcher(email);
        if (match.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return true if one chain that receive such as parameter contains any letter character else return false
     * @param chain
     * @return
     */
    public static boolean cadContainsLetters(String chain) {
        for (int i = 0; i < chain.length(); i++) {
            char character = chain.charAt(i);
            if (!Character.isDigit(character)) {
                return true;
            }
        }
        return false;
    }
}
