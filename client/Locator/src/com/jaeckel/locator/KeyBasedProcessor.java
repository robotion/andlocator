package com.jaeckel.locator;

import org.bouncycastle2.bcpg.ArmoredOutputStream;
import org.bouncycastle2.jce.provider.BouncyCastleProvider;
import org.bouncycastle2.openpgp.*;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;
import java.util.Date;

/**
 * A simple utility class that encrypts/decrypts public key based
 * encryption files.
 * <p/>
 * To encrypt a file: KeyBasedFileProcessor -e [-a|-ai] fileName publicKeyFile.<br>
 * If -a is specified the output file will be "ascii-armored".
 * If -i is specified the output file will be have integrity checking added.
 * <p/>
 * To decrypt: KeyBasedFileProcessor -d fileName secretKeyFile passPhrase.
 * <p/>
 * Note 1: this example will silently overwrite files, nor does it pay any attention to
 * the specification of "_CONSOLE" in the filename. It also expects that a single pass phrase
 * will have been used.
 * <p/>
 * Note 2: if an empty file name has been specified in the literal data object contained in the
 * encrypted packet a file with the name filename.out will be generated in the current working directory.
 */
public class KeyBasedProcessor {

    private FileInputStream keyIn = null;


    /**
     * A simple routine that opens a key ring file and loads the first available key suitable for
     * encryption.
     *
     * @param in
     * @return
     * @throws IOException
     * @throws PGPException
     */
    private static PGPPublicKey readPublicKey(
            InputStream in)
            throws IOException, PGPException {


        in = PGPUtil.getDecoderStream(in);

        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //

        //
        // iterate through the key rings.
        //
        Iterator rIt = pgpPub.getKeyRings();

        while (rIt.hasNext()) {
            PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
            Iterator kIt = kRing.getPublicKeys();

            while (kIt.hasNext()) {
                PGPPublicKey k = (PGPPublicKey) kIt.next();

                if (k.isEncryptionKey()) {
                    return k;
                }
            }
        }

        throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }

    /**
     * Search a secret key ring collection for a secret key corresponding to
     * keyID if it exists.
     *
     * @param pgpSec a secret key ring collection.
     * @param keyID  keyID we want.
     * @param pass   passphrase to decrypt secret key with.
     * @return
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    private static PGPPrivateKey findSecretKey(
            PGPSecretKeyRingCollection pgpSec,
            long keyID,
            char[] pass)
            throws PGPException, NoSuchProviderException {
        PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

        if (pgpSecKey == null) {
            return null;
        }

        return pgpSecKey.extractPrivateKey(pass, "BC2");
    }

    /**
     * decrypt the passed in message stream
     */
    private static void decrypt(
            InputStream in,
            InputStream keyIn,
            char[] passwd,
            OutputStream decypheredOut)

            throws Exception {
        in = PGPUtil.getDecoderStream(in);

        try {
            PGPObjectFactory pgpF = new PGPObjectFactory(in);
            PGPEncryptedDataList enc;

            Object o = pgpF.nextObject();
            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList) {
                enc = (PGPEncryptedDataList) o;
            } else {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }

            //
            // find the secret key
            //
            Iterator it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey = null;
            PGPPublicKeyEncryptedData pbe = null;
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                    PGPUtil.getDecoderStream(keyIn));

            while (sKey == null && it.hasNext()) {
                pbe = (PGPPublicKeyEncryptedData) it.next();

                sKey = findSecretKey(pgpSec, pbe.getKeyID(), passwd);
            }

            if (sKey == null) {
                throw new IllegalArgumentException("secret key for message not found.");
            }

            InputStream clear = pbe.getDataStream(sKey, "BC2");

            PGPObjectFactory plainFact = new PGPObjectFactory(clear);

            Object message = plainFact.nextObject();

            if (message instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) message;
                PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream());

                message = pgpFact.nextObject();
            }

            if (message instanceof PGPLiteralData) {
                PGPLiteralData ld = (PGPLiteralData) message;
                String outFileName = ld.getFileName();
                if (ld.getFileName().length() == 0) {
//                    outFileName = defaultFileName;
                }

                InputStream unc = ld.getInputStream();
                int ch;

                while ((ch = unc.read()) >= 0) {
                    decypheredOut.write(ch);
                }
            } else if (message instanceof PGPOnePassSignatureList) {
                throw new PGPException("encrypted message contains a signed message - not literal data.");
            } else {
                throw new PGPException("message is not a simple encrypted file - type unknown.");
            }

            if (pbe.isIntegrityProtected()) {
                if (!pbe.verify()) {
                    System.err.println("message failed integrity check");
                } else {
                    System.err.println("message integrity check passed");
                }
            } else {
                System.err.println("no message integrity check");
            }
        }
        catch (PGPException e) {
            System.err.println(e);
            if (e.getUnderlyingException() != null) {
                e.getUnderlyingException().printStackTrace();
            }
        }
    }

    private static void encrypt(
            OutputStream out,
            String cleartext,
            PGPPublicKey encKey,
            boolean armor,
            boolean withIntegrityCheck)
            throws IOException, NoSuchProviderException {
        if (armor) {
            out = new ArmoredOutputStream(out);
        }

        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();


            PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                    PGPCompressedData.ZIP);

            writeStringToLiteralData(comData.open(bOut), PGPLiteralData.TEXT, cleartext);

            comData.close();

            PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.BLOWFISH, withIntegrityCheck, new SecureRandom(), "BC2");

            cPk.addMethod(encKey);

            byte[] bytes = bOut.toByteArray();

            OutputStream cOut = cPk.open(out, bytes.length);

            cOut.write(bytes);

            cOut.close();

            out.close();
        }
        catch (PGPException e) {
            System.err.println(e);
            if (e.getUnderlyingException() != null) {
                e.getUnderlyingException().printStackTrace();
            }
        }
    }

    public KeyBasedProcessor() {
        Security.addProvider(new BouncyCastleProvider());
    }


    public String encrypt(final String cleartext) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            keyIn = new FileInputStream("/sdcard/pub.asc");


            final PGPPublicKey encKey = readPublicKey(keyIn);


            encrypt(out, cleartext, encKey, true, true);

            System.out.println("encrypted text:\n" + out);

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return out.toString();
    }

    public static void main(
            String[] args)
            throws Exception {


//        FileInputStream keyIn = new FileInputStream("/Users/biafra/andlocator/tools/keymanager/test-data/pub.asc");
        FileInputStream keyIn = new FileInputStream("/sdcard/pub.asc");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PGPPublicKey encKey = readPublicKey(keyIn);

        final String cleartext = "Bla blŠ lblŸs lkld ksldk sldk lsdk lskd lksdrtext";


        encrypt(out, cleartext, encKey, true, true);

        System.out.println("out.size(): " + out.size());
        System.out.println("out: " + out);


        ByteArrayInputStream encryptedIn = new ByteArrayInputStream(out.toByteArray());


        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();

//        FileInputStream secKeyIn = new FileInputStream("/Users/biafra/andlocator/tools/keymanager/test-data/secret.asc");
        FileInputStream secKeyIn = new FileInputStream("/sdcard/secret.asc");

        decrypt(encryptedIn, secKeyIn, new char[]{'b'}, decryptedOut);

        System.out.println("decryptedOut.size(): " + decryptedOut.size());
        System.out.println("decryptedOut: " + decryptedOut);

        if (!cleartext.equals(decryptedOut.toString())) {
            System.out.println("-----------------------> cleartext != decryptedOut");
            System.out.println("-----------------------> [" + cleartext + "]");
            System.out.println("-----------------------> [" + decryptedOut + "]");
        }

    }

    public static void writeStringToLiteralData(
            OutputStream out,
            char fileType,
            String str)
            throws IOException {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(out, fileType, "fnord", str.getBytes().length, new Date());
        InputStream in = new ByteArrayInputStream(str.getBytes());
        byte[] buf = new byte[4096];
        int len;

        while ((len = in.read(buf)) > 0) {
            pOut.write(buf, 0, len);
        }

        lData.close();
        in.close();
    }

    public long getKeyId() {
        try {

            keyIn = new FileInputStream("/sdcard/pub.asc");
            PGPPublicKey pubKey = readPublicKey(keyIn);
            return pubKey.getKeyID();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
