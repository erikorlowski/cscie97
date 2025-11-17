package cscie97.asn4.housemate.entitlement;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a user's credential, either a password or a voiceprint.
 * Password credentials are considered administrative credentials per the
 * system design.
 */
public class Credential implements Visitable {
    private final String userId;
    private final boolean isPassword;
    private final String value;
    private final boolean isAdmin;

    private static final String HASH_ID = "PBKDF2";
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes
    private static final Pattern PASSWORD_SIGNIN_PATTERN =
        Pattern.compile("^user\\s+([^\\s,]+)\\s*,\\s*password\\s+(.+)$");

    /**
     * Create a credential for the given user.
     *
     * @param userId the associated user id
     * @param isPassword true if this credential is a password, false for voiceprint
     * @param value the stored credential value
     */
    public Credential(String userId, boolean isPassword, String value) {
        this.userId = userId;
        this.isPassword = isPassword;
        if (isPassword) {
            // If the provided value already looks like a stored hash, keep it as-is.
            if (value != null && value.startsWith(HASH_ID + "$")) {
                this.value = value;
            } else {
                // Hash the plain-text password for secure storage
                this.value = createHash(value == null ? "" : value);
            }
        } else {
            this.value = value;
        }
        this.isAdmin = isPassword; // password credentials denote admin per design
    }

    /**
     * Get the user id associated with this credential.
     *
     * @return the user id
     */
    public String getUserId() { return userId; }

    /**
     * Returns true if this credential represents a password (rather than a voiceprint).
     *
     * @return true for password credentials
     */
    public boolean isPassword() { return isPassword; }

    /**
     * Get the stored credential value. For passwords this will be the hashed
     * representation (or an existing legacy value). For voiceprints this is the
     * raw stored voiceprint value.
     *
     * @return the stored credential value
     */
    public String getValue() { return value; }

    /**
     * Returns true if this credential confers administrative privileges.
     * By design, password credentials are treated as admin credentials.
     *
     * @return true if credential is administrative
     */
    public boolean isAdmin() { return isAdmin; }

    /**
     * Check whether the provided sign-in text matches this credential.
     *
     * @param signInText text supplied during authentication
     * @return true if the value matches the stored credential
     */
    public boolean isMatch(String signInText) {
        if (signInText == null) return false;
        if (!isPassword) {
            // Start of voiceprint case
            // Expect signInText in the form: "voiceprint <value>"
            String prefix = "voiceprint";
            if (!signInText.startsWith(prefix)) return false;
            // After the prefix there must be at least one whitespace char followed by the value
            if (signInText.length() == prefix.length()) return false;
            char next = signInText.charAt(prefix.length());
            if (!Character.isWhitespace(next)) return false;
            // Extract the value after the whitespace(s)
            String providedValue = signInText.substring(prefix.length()).trim();
            return value != null && value.equals(providedValue);
        }

        if (value == null) return false;

        // Start of password case

        // Expect signInText in the form: "user <userId>, password <value>"
        Matcher m = PASSWORD_SIGNIN_PATTERN.matcher(signInText);
        if (!m.matches()) return false;
        String providedUserId = m.group(1);
        String providedPassword = m.group(2);
        if (!providedUserId.equals(this.userId)) return false;

        // If stored as PBKDF2 hash, verify using PBKDF2; otherwise fallback to plain-text compare
        if (value.startsWith(HASH_ID + "$")) {
            return verifyPassword(providedPassword, value);
        } else {
            return value.equals(providedPassword);
        }
    }

    private static String createHash(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom sr = SecureRandom.getInstanceStrong();
            sr.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);

            return String.format("%s$%d$%s$%s", HASH_ID, ITERATIONS, saltB64, hashB64);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private static boolean verifyPassword(String password, String stored) {
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) return false;
            // parts: [0]=HASH_ID, [1]=iterations, [2]=saltB64, [3]=hashB64
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, expectedHash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] actualHash = skf.generateSecret(spec).getEncoded();

            return slowEquals(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Constant-time comparison to avoid timing attacks.
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * Accept a Visitor to operate on this Credential.
     *
     * @param visitor the visitor handling this credential
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visitCredential(this);
    }
}
