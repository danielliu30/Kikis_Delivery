package bakery.Services;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bakery.Services.BakedFormation;

@Component
class DynamoSecurity {

    private DynamoSecurity() {

    }

    public String encryptPassWord(String value) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generateHash(value);
    }

    public Boolean validiatePassword(String currentPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return comparePassword(currentPassword, storedPassword);
    }

    private String generateHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iteration = 1000;
        char[] charPassword = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(charPassword, salt, iteration, 64*8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iteration +":"+toHex(salt)+":"+toHex(hash);
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRand = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRand.nextBytes(salt);
        return salt;
    }

    private String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    Boolean comparePassword(String currentPassword, String storedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] splitStored = storedPassword.split(":");
        int iteration = Integer.parseInt(splitStored[0]);
        byte[] salt = fromHex(splitStored[1]);
        byte[] hash = fromHex(splitStored[2]);

        PBEKeySpec spec = new PBEKeySpec(currentPassword.toCharArray(),salt,iteration, hash.length *8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
         
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private byte[] fromHex(String hex){
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
    

}
