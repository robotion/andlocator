package com.jaeckel.locator.pgp;

import org.bouncycastle2.openpgp.*;
import org.bouncycastle2.bcpg.ArmoredOutputStream;
import org.bouncycastle2.jce.provider.BouncyCastleProvider;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.util.Date;


/**
 * User: biafra
 * Date: Jun 12, 2010
 * Time: 7:49:58 PM
 */
public class PubKeyGenerator {


    private static void exportKeyPair(
            OutputStream secretOut,
            OutputStream publicOut,
            PublicKey publicKey,
            PrivateKey privateKey,
            String identity,
            char[] passPhrase) {

        secretOut = new ArmoredOutputStream(secretOut);

        PGPSecretKey secretKey = null;
        try {
            secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, PGPPublicKey.RSA_GENERAL, publicKey, privateKey,
                    new Date(), identity, PGPEncryptedData.CAST5, passPhrase, null, null, new SecureRandom(), "BC2");


            secretKey.encode(secretOut);

            secretOut.close();

            publicOut = new ArmoredOutputStream(publicOut);

            PGPPublicKey key = secretKey.getPublicKey();

            key.encode(publicOut);

            publicOut.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public String[] createKeyPair() {

        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = null;
        try {

            kpg = KeyPairGenerator.getInstance("RSA", "BC2");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        kpg.initialize(1024);

        KeyPair kp = kpg.generateKeyPair();


        ByteArrayOutputStream secretKeyArray = new ByteArrayOutputStream();

        ByteArrayOutputStream publicKeyArray = new ByteArrayOutputStream();

        exportKeyPair(secretKeyArray, publicKeyArray, kp.getPublic(), kp.getPrivate(), "id", "passphrase".toCharArray());


        return new String[]{secretKeyArray.toString(), publicKeyArray.toString()};
    }

}
