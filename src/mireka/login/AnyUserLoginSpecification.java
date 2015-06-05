package mireka.login;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AnyUserLoginSpecification implements LoginSpecification {

    private String defaultPassword;

    @Override
    public LoginResult evaluatePlain(String usernameString, String password) {
        Username username = new Username(usernameString);
        if (defaultPassword == null || defaultPassword.equals(password)) {
            return new LoginResult(LoginDecision.VALID, new Principal(username.toString()));
        } else {
            return new LoginResult(LoginDecision.PASSWORD_DOES_NOT_MATCH, null);
        }
    }

    @Override
    public LoginResult evaluateApop(String usernameString, String timestamp,
                                    byte[] digestBytes) {
        Username username = new Username(usernameString);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Assertion failed");
        }
        String text = timestamp + defaultPassword;
        byte[] textBytes;
        try {
            textBytes = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Assertion failed");
        }
        byte[] calculatedDigestBytes = digest.digest(textBytes);
        boolean isValid =
                MessageDigest.isEqual(digestBytes, calculatedDigestBytes);
        if (defaultPassword == null || isValid) {
            return new LoginResult(LoginDecision.VALID, new Principal(username.toString()));
        } else {
            return new LoginResult(LoginDecision.INVALID, null);
        }
    }

    /**
     * @x.category GETSET
     */
    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    /**
     * @x.category GETSET
     */
    public String getDefaultPassword() {
        return defaultPassword;
    }
}
