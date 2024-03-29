package org.bouncycastle2.openpgp.examples;

import java.io.*;
import java.util.*;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle2.bcpg.ArmoredOutputStream;
import org.bouncycastle2.jce.provider.BouncyCastleProvider;
import org.bouncycastle2.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle2.openpgp.PGPEncryptedDataList;
import org.bouncycastle2.openpgp.PGPException;
import org.bouncycastle2.openpgp.PGPLiteralData;
import org.bouncycastle2.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle2.openpgp.PGPCompressedData;
import org.bouncycastle2.openpgp.PGPObjectFactory;
import org.bouncycastle2.openpgp.PGPPBEEncryptedData;
import org.bouncycastle2.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle2.openpgp.PGPUtil;

/**
 * Simple routine to encrypt and decrypt using a passphrase.
 * This service routine provides the basic PGP services between
 * byte arrays.
 * 
 * Note: this code plays no attention to -CONSOLE in the file name
 * the specification of "_CONSOLE" in the filename.
 * It also expects that a single pass phrase will have been used.
 * 
 */
public class ByteArrayHandler
{
    /**
     * decrypt the passed in message stream
     * 
     * @param encrypted  The message to be decrypted.
     * @param passPhrase Pass phrase (key)
     * 
     * @return Clear text as a byte array.  I18N considerations are
     *         not handled by this routine
     * @exception IOException
     * @exception PGPException
     * @exception NoSuchProviderException
     */
    public static byte[] decrypt(
        byte[] encrypted,
        char[] passPhrase)
        throws IOException, PGPException, NoSuchProviderException
    {
        InputStream in = new ByteArrayInputStream(encrypted);

        in = PGPUtil.getDecoderStream(in);

        PGPObjectFactory         pgpF = new PGPObjectFactory(in);
        PGPEncryptedDataList   enc = null;
        Object                          o = pgpF.nextObject();
        
        //
        // the first object might be a PGP marker packet.
        //
        if (o instanceof PGPEncryptedDataList)
        {
            enc = (PGPEncryptedDataList)o;
        }
        else
        {
            enc = (PGPEncryptedDataList)pgpF.nextObject();
        }

        PGPPBEEncryptedData pbe = (PGPPBEEncryptedData)enc.get(0);

        InputStream clear = pbe.getDataStream(passPhrase, "BC2");

        PGPObjectFactory        pgpFact = new PGPObjectFactory(clear);

        PGPCompressedData   cData = (PGPCompressedData)pgpFact.nextObject();

        pgpFact = new PGPObjectFactory(cData.getDataStream());

        PGPLiteralData  ld = (PGPLiteralData)pgpFact.nextObject();

        InputStream unc = ld.getInputStream();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;

        while ((ch = unc.read()) >= 0)
        {
            out.write(ch);

        }
        
        byte[] returnBytes = out.toByteArray();
        out.close();
        return returnBytes;
    }

    /**
     * Simple PGP encryptor between byte[].
     * 
     * @param clearData  The test to be encrypted
     * @param passPhrase The pass phrase (key).  This method assumes that the
     *                   key is a simple pass phrase, and does not yet support
     *                   RSA or more sophisiticated keying.
     * @param fileName   File name. This is used in the Literal Data Packet (tag 11)
     *                   which is really inly important if the data is to be
     *                   related to a file to be recovered later.  Because this
     *                   routine does not know the source of the information, the
     *                   caller can set something here for file name use that
     *                   will be carried.  If this routine is being used to
     *                   encrypt SOAP MIME bodies, for example, use the file name from the
     *                   MIME type, if applicable. Or anything else appropriate.
     *                             
     * @param armor
     * 
     * @return encrypted data.
     * @exception IOException
     * @exception PGPException
     * @exception NoSuchProviderException
     */
    public static byte[] encrypt(
        byte[]     clearData,
        char[]         passPhrase,
        String         fileName,
        int            algorithm,
        boolean     armor)
        throws IOException, PGPException, NoSuchProviderException
    {
        if (fileName == null)
        {
            fileName= PGPLiteralData.CONSOLE;
        }
        
        ByteArrayOutputStream    encOut = new ByteArrayOutputStream();
        
        OutputStream out = encOut;
        if (armor)
        {
            out = new ArmoredOutputStream(out);
        }

        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();


        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                                                        PGPCompressedDataGenerator.ZIP);
        OutputStream cos = comData.open(bOut); // open it with the final destination
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        // we want to generate compressed data. This might be a user option later,
        // in which case we would pass in bOut.
        OutputStream  pOut = lData.open(cos, // the compressed output stream
                                        PGPLiteralData.BINARY,
                                        fileName,  // "filename" to store
                                        clearData.length, // length of clear data
                                        new Date()  // current time
                                      );
        pOut.write(clearData);

        lData.close();
        comData.close();

        PGPEncryptedDataGenerator   cPk = new PGPEncryptedDataGenerator(algorithm, new SecureRandom(), "BC2");

        cPk.addMethod(passPhrase);

        byte[]              bytes = bOut.toByteArray();

        OutputStream    cOut = cPk.open(out, bytes.length);

        cOut.write(bytes);  // obtain the actual bytes from the compressed stream

        cOut.close();

        out.close();

        return  encOut.toByteArray();
    }

    public static void main(
                           String[] args)
    throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        
        String passPhrase = "Dick Beck";
        char[] passArray = passPhrase.toCharArray();

        byte[] original = "Hello world".getBytes();
        System.out.println("Starting PGP test");
        byte[] encrypted = encrypt(original, passArray, "iway", PGPEncryptedDataGenerator.CAST5, true);

        System.out.println("\nencrypted data = '"+new String(encrypted)+"'");
        byte[] decrypted= decrypt(encrypted,passArray);

        System.out.println("\ndecrypted data = '"+new String(decrypted)+"'");
        
        encrypted = encrypt(original, passArray, "iway", PGPEncryptedDataGenerator.AES_256, false);

        System.out.println("\nencrypted data = '"+new String(org.bouncycastle2.util.encoders.Hex.encode(encrypted))+"'");
        decrypted= decrypt(encrypted, passArray);

        System.out.println("\ndecrypted data = '"+new String(decrypted)+"'");
    }
}
