package com.inv.data.access.sec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * @author Xdsswar
 */
public class Security {

    // not too safe to include keys here
//    Why It's Not Safe:
//    Easy to Reverse Engineer:
//    Java bytecode can be decompiled easily using tools like JD-GUI, Fernflower, or Procyon, even if bundled into a
//    native image using jpackage. Attackers can extract your executable, reverse-engineer it, and locate hardcoded
//    security keys or secrets.
//
//    Static Analysis:
//    Even without decompilation, tools like strings (on Unix) or binary analysis tools can scan for plaintext strings
//    inside executables.
//
//    No Obfuscation by Default:
//    jpackage doesn't obfuscate code or strings by default. Your keys will still be there in some form.
//
//    Regulatory/Compliance Risk:a
//    Storing keys insecurely may violate compliance standards (e.g., PCI-DSS, HIPAA) or internal security policies.

//    Safer Alternatives:
//    External Configuration (Encrypted):
//    Store keys in a secure configuration file outside the binary and encrypt it. Decrypt at runtime using a secure mechanism.
//
//    Key Vaults / Secrets Management:
//    Use cloud-based secret managers (e.g., AWS Secrets Manager, HashiCorp Vault, Azure Key Vault) to fetch keys at runtime.
//
//    Environment Variables:
//    Pass secrets via environment variables during execution, not hardcoded in the binary.
//
//    Obfuscation (Not a solution, but a layer):
//    If you must include sensitive data (not recommended), use tools like ProGuard or Obfuscator to make reverse
//    engineering harder—but this only slows attackers down, it doesn't prevent access.

    private static final String secretKey = "$ecurityIsImport@nt2019";
    private static final String salt = "rQGgJCx7PaeL9dq5VbDKLPYxtnwKDCSY";

    /**
     * Encryption
     * @param strToEncrypt password
     * @return hash
     */
    public static String encryptPassword(String strToEncrypt)
    {

        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
