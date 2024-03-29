package org.bouncycastle2.jce.provider.test;

import java.security.AlgorithmParameters;
import java.security.Security;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle2.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle2.crypto.Digest;
import org.bouncycastle2.crypto.PBEParametersGenerator;
import org.bouncycastle2.crypto.digests.SHA1Digest;
import org.bouncycastle2.crypto.digests.SHA256Digest;
import org.bouncycastle2.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle2.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle2.crypto.params.KeyParameter;
import org.bouncycastle2.crypto.params.ParametersWithIV;
import org.bouncycastle2.jce.provider.BouncyCastleProvider;
import org.bouncycastle2.util.encoders.Hex;
import org.bouncycastle2.util.test.SimpleTest;

/**
 * test out the various PBE modes, making sure the JCE implementations
 * are compatible woth the light weight ones.
 */
public class PBETest
    extends SimpleTest
{
    private class OpenSSLTest
        extends SimpleTest
    {
        char[]    password;
        String    baseAlgorithm;
        String    algorithm;
        int       keySize;
        int       ivSize;
        
        OpenSSLTest(
            String    baseAlgorithm,
            String    algorithm,
            int       keySize,
            int       ivSize)
        {
            this.password = algorithm.toCharArray();
            this.baseAlgorithm = baseAlgorithm;
            this.algorithm = algorithm;
            this.keySize = keySize;
            this.ivSize = ivSize;
        }
        
        public String getName()
        {
            return "OpenSSLPBE";
        }
    
        public void performTest()
            throws Exception
        {
            byte[] salt = new byte[16];
            int    iCount = 100;
            
            for (int i = 0; i != salt.length; i++)
            {
                salt[i] = (byte)i;
            }

            OpenSSLPBEParametersGenerator   pGen = new OpenSSLPBEParametersGenerator();

            pGen.init(
                    PBEParametersGenerator.PKCS5PasswordToBytes(password),
                    salt,
                    iCount);

            ParametersWithIV params = (ParametersWithIV)pGen.generateDerivedParameters(keySize, ivSize);

            SecretKeySpec   encKey = new SecretKeySpec(((KeyParameter)params.getParameters()).getKey(), baseAlgorithm);

            Cipher          c;

            if (baseAlgorithm.equals("RC4"))
            {
                c = Cipher.getInstance(baseAlgorithm, "BC2");

                c.init(Cipher.ENCRYPT_MODE, encKey);
            }
            else
            {
                c = Cipher.getInstance(baseAlgorithm + "/CBC/PKCS7Padding", "BC2");

                c.init(Cipher.ENCRYPT_MODE, encKey, new IvParameterSpec(params.getIV()));
            }

            byte[]          enc = c.doFinal(salt);

            c = Cipher.getInstance(algorithm, "BC2");

            PBEKeySpec          keySpec = new PBEKeySpec(password, salt, iCount);
            SecretKeyFactory    fact = SecretKeyFactory.getInstance(algorithm, "BC2");

            c.init(Cipher.DECRYPT_MODE, fact.generateSecret(keySpec));

            byte[]          dec = c.doFinal(enc);

            if (!arrayEquals(salt, dec))
            {
                fail("" + algorithm + "failed encryption/decryption test");
            }
        }
    }
    
    private class PKCS12Test
        extends SimpleTest
    {
        char[]    password;
        String    baseAlgorithm;
        String    algorithm;
        Digest    digest;
        int       keySize;
        int       ivSize;
        
        PKCS12Test(
            String    baseAlgorithm,
            String    algorithm,
            Digest    digest,
            int       keySize,
            int       ivSize)
        {
            this.password = algorithm.toCharArray();
            this.baseAlgorithm = baseAlgorithm;
            this.algorithm = algorithm;
            this.digest = digest;
            this.keySize = keySize;
            this.ivSize = ivSize;
        }
        
        public String getName()
        {
            return "PKCS12PBE";
        }
    
        public void performTest()
            throws Exception
        {
            byte[] salt = new byte[digest.getDigestSize()];
            int    iCount = 100;
            
            digest.doFinal(salt, 0);

            PKCS12ParametersGenerator   pGen = new PKCS12ParametersGenerator(digest);

            pGen.init(
                    PBEParametersGenerator.PKCS12PasswordToBytes(password),
                    salt,
                    iCount);

            ParametersWithIV params = (ParametersWithIV)pGen.generateDerivedParameters(keySize, ivSize);

            SecretKeySpec   encKey = new SecretKeySpec(((KeyParameter)params.getParameters()).getKey(), baseAlgorithm);

            Cipher          c;

            if (baseAlgorithm.equals("RC4"))
            {
                c = Cipher.getInstance(baseAlgorithm, "BC2");

                c.init(Cipher.ENCRYPT_MODE, encKey);
            }
            else
            {
                c = Cipher.getInstance(baseAlgorithm + "/CBC/PKCS7Padding", "BC2");

                c.init(Cipher.ENCRYPT_MODE, encKey, new IvParameterSpec(params.getIV()));
            }

            byte[]          enc = c.doFinal(salt);

            c = Cipher.getInstance(algorithm, "BC2");

            PBEKeySpec          keySpec = new PBEKeySpec(password, salt, iCount);
            SecretKeyFactory    fact = SecretKeyFactory.getInstance(algorithm, "BC2");

            c.init(Cipher.DECRYPT_MODE, fact.generateSecret(keySpec));

            byte[]          dec = c.doFinal(enc);

            if (!arrayEquals(salt, dec))
            {
                fail("" + algorithm + "failed encryption/decryption test");
            }

            //
            // get the parameters
            //
            AlgorithmParameters param = checkParameters(c, salt, iCount);

            //
            // try using parameters
            //
            c = Cipher.getInstance(algorithm, "BC2");

            keySpec = new PBEKeySpec(password);

            c.init(Cipher.DECRYPT_MODE, fact.generateSecret(keySpec), param);

            checkParameters(c, salt, iCount);

            dec = c.doFinal(enc);

            if (!arrayEquals(salt, dec))
            {
                fail("" + algorithm + "failed encryption/decryption test");
            }

            //
            // try using PBESpec
            //
            c = Cipher.getInstance(algorithm, "BC2");

            keySpec = new PBEKeySpec(password);

            c.init(Cipher.DECRYPT_MODE, fact.generateSecret(keySpec), param.getParameterSpec(PBEParameterSpec.class));

            checkParameters(c, salt, iCount);

            dec = c.doFinal(enc);

            if (!arrayEquals(salt, dec))
            {
                fail("" + algorithm + "failed encryption/decryption test");
            }
        }

        private AlgorithmParameters checkParameters(Cipher c, byte[] salt, int iCount)
            throws InvalidParameterSpecException
        {
            AlgorithmParameters param = c.getParameters();
            PBEParameterSpec spec = (PBEParameterSpec)param.getParameterSpec(PBEParameterSpec.class);

            if (!arrayEquals(salt, spec.getSalt()))
            {
                fail("" + algorithm + "failed salt test");
            }

            if (iCount != spec.getIterationCount())
            {
                fail("" + algorithm + "failed count test");
            }
            return param;
        }
    }
    
    private PKCS12Test[] pkcs12Tests = {
        new PKCS12Test("DESede", "PBEWITHSHAAND3-KEYTRIPLEDES-CBC",  new SHA1Digest(),   192,  64),
        new PKCS12Test("DESede", "PBEWITHSHAAND2-KEYTRIPLEDES-CBC",  new SHA1Digest(),   128,  64),
        new PKCS12Test("RC4",    "PBEWITHSHAAND128BITRC4",           new SHA1Digest(),   128,   0),
        new PKCS12Test("RC4",    "PBEWITHSHAAND40BITRC4",            new SHA1Digest(),    40,   0),
        new PKCS12Test("RC2",    "PBEWITHSHAAND128BITRC2-CBC",       new SHA1Digest(),   128,  64),
        new PKCS12Test("RC2",    "PBEWITHSHAAND40BITRC2-CBC",        new SHA1Digest(),    40,  64),
        new PKCS12Test("AES",    "PBEWithSHA1And128BitAES-CBC-BC",   new SHA1Digest(),   128, 128),
        new PKCS12Test("AES",    "PBEWithSHA1And192BitAES-CBC-BC",   new SHA1Digest(),   192, 128),
        new PKCS12Test("AES",    "PBEWithSHA1And256BitAES-CBC-BC",   new SHA1Digest(),   256, 128),
        new PKCS12Test("AES",    "PBEWithSHA256And128BitAES-CBC-BC", new SHA256Digest(), 128, 128),
        new PKCS12Test("AES",    "PBEWithSHA256And192BitAES-CBC-BC", new SHA256Digest(), 192, 128),   
        new PKCS12Test("AES",    "PBEWithSHA256And256BitAES-CBC-BC", new SHA256Digest(), 256, 128),
        new PKCS12Test("Twofish","PBEWithSHAAndTwofish-CBC",         new SHA1Digest(),   256, 128),
        new PKCS12Test("IDEA",   "PBEWithSHAAndIDEA-CBC",            new SHA1Digest(),   128,  64),
        new PKCS12Test("AES",    BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId(),   new SHA1Digest(),   128, 128),
        new PKCS12Test("AES",    BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId(),   new SHA1Digest(),   192, 128),
        new PKCS12Test("AES",    BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId(),   new SHA1Digest(),   256, 128),
        new PKCS12Test("AES",    BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId(), new SHA256Digest(), 128, 128),
        new PKCS12Test("AES",    BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId(), new SHA256Digest(), 192, 128),
        new PKCS12Test("AES",    BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId(), new SHA256Digest(), 256, 128),
    };
    
    private OpenSSLTest openSSLTests[] = {
        new OpenSSLTest("AES", "PBEWITHMD5AND128BITAES-CBC-OPENSSL", 128, 128),
        new OpenSSLTest("AES", "PBEWITHMD5AND192BITAES-CBC-OPENSSL", 192, 128),
        new OpenSSLTest("AES", "PBEWITHMD5AND256BITAES-CBC-OPENSSL", 256, 128)
    };
    
    static byte[]   message = Hex.decode("4869205468657265");
    
    private byte[] hMac1 = Hex.decode("bcc42174ccb04f425d9a5c8c4a95d6fd7c372911");
    private byte[] hMac2 = Hex.decode("cb1d8bdb6aca9e3fa8980d6eb41ab28a7eb2cfd6");
    
    private Cipher makePBECipherUsingParam(
        String  algorithm,
        int     mode,
        char[]  password,
        byte[]  salt,
        int     iterationCount)
        throws Exception
    {
        PBEKeySpec          pbeSpec = new PBEKeySpec(password);
        SecretKeyFactory    keyFact = SecretKeyFactory.getInstance(algorithm, "BC2");
        PBEParameterSpec    defParams = new PBEParameterSpec(salt, iterationCount);

        Cipher cipher = Cipher.getInstance(algorithm, "BC2");

        cipher.init(mode, keyFact.generateSecret(pbeSpec), defParams);

        return cipher;
    }

    private Cipher makePBECipherWithoutParam(
        String  algorithm,
        int     mode,
        char[]  password,
        byte[]  salt,
        int     iterationCount)
        throws Exception
    {
        PBEKeySpec          pbeSpec = new PBEKeySpec(password, salt, iterationCount);
        SecretKeyFactory    keyFact = SecretKeyFactory.getInstance(algorithm, "BC2");

        Cipher cipher = Cipher.getInstance(algorithm, "BC2");

        cipher.init(mode, keyFact.generateSecret(pbeSpec));

        return cipher;
    }
    
    private boolean arrayEquals(
        byte[]  a,
        byte[]  b)
    {
        if (a.length != b.length)
        {
            return false;
        }

        for (int i = 0; i != a.length; i++)
        {
            if (a[i] != b[i])
            {
                return false;
            }
        }

        return true;
    }

    public void testPBEHMac(
        String  hmacName,
        byte[]  output)
    {
        SecretKey           key;
        byte[]              out;
        Mac                 mac;

        try
        {
            SecretKeyFactory    fact = SecretKeyFactory.getInstance(hmacName, "BC2");

            key = fact.generateSecret(new PBEKeySpec("hello".toCharArray()));
            
            mac = Mac.getInstance(hmacName, "BC2");
        }
        catch (Exception e)
        {
            fail("Failed - exception " + e.toString(), e);
            return;
        }

        try
        {
            mac.init(key, new PBEParameterSpec(new byte[20], 100));
        }
        catch (Exception e)
        {
            fail("Failed - exception " + e.toString(), e);
            return;
        }

        mac.reset();
        
        mac.update(message, 0, message.length);

        out = mac.doFinal();

        if (!arrayEquals(out, output))
        {
            fail("Failed - expected " + new String(Hex.encode(output)) + " got " + new String(Hex.encode(out)));
        }
    }
    
    public void performTest()
        throws Exception
    {
        byte[] input = Hex.decode("1234567890abcdefabcdef1234567890fedbca098765");

        //
        // DES
        //
        Cipher  cEnc = Cipher.getInstance("DES/CBC/PKCS7Padding", "BC2");

        cEnc.init(Cipher.ENCRYPT_MODE,
            new SecretKeySpec(Hex.decode("30e69252758e5346"), "DES"),
            new IvParameterSpec(Hex.decode("7c1c1ab9c454a688")));

        byte[]  out = cEnc.doFinal(input);

        char[]  password = { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };

        Cipher  cDec = makePBECipherUsingParam(
                            "PBEWithSHA1AndDES",
                            Cipher.DECRYPT_MODE,
                            password,
                            Hex.decode("7d60435f02e9e0ae"),
                            2048);

        byte[]  in = cDec.doFinal(out);

        if (!arrayEquals(input, in))
        {
            fail("DES failed");
        }

        cDec = makePBECipherWithoutParam(
                "PBEWithSHA1AndDES",
                Cipher.DECRYPT_MODE,
                password,
                Hex.decode("7d60435f02e9e0ae"),
                2048);

        in = cDec.doFinal(out);
        
        if (!arrayEquals(input, in))
        {
            fail("DES failed without param");
        }
        
        //
        // DESede
        //
        cEnc = Cipher.getInstance("DESede/CBC/PKCS7Padding", "BC2");

        cEnc.init(Cipher.ENCRYPT_MODE,
            new SecretKeySpec(Hex.decode("732f2d33c801732b7206756cbd44f9c1c103ddd97c7cbe8e"), "DES"),
            new IvParameterSpec(Hex.decode("b07bf522c8d608b8")));

        out = cEnc.doFinal(input);

        cDec = makePBECipherUsingParam(
                            "PBEWithSHAAnd3-KeyTripleDES-CBC",
                            Cipher.DECRYPT_MODE,
                            password,
                            Hex.decode("7d60435f02e9e0ae"),
                            2048);

        in = cDec.doFinal(out);

        if (!arrayEquals(input, in))
        {
            fail("DESede failed");
        }

        //
        // 40Bit RC2
        //
        cEnc = Cipher.getInstance("RC2/CBC/PKCS7Padding", "BC2");

        cEnc.init(Cipher.ENCRYPT_MODE,
            new SecretKeySpec(Hex.decode("732f2d33c8"), "RC2"),
            new IvParameterSpec(Hex.decode("b07bf522c8d608b8")));

        out = cEnc.doFinal(input);

        cDec = makePBECipherUsingParam(
                            "PBEWithSHAAnd40BitRC2-CBC",
                            Cipher.DECRYPT_MODE,
                            password,
                            Hex.decode("7d60435f02e9e0ae"),
                            2048);

        in = cDec.doFinal(out);

        if (!arrayEquals(input, in))
        {
            fail("RC2 failed");
        }

        //
        // 128bit RC4
        //
        cEnc = Cipher.getInstance("RC4", "BC2");

        cEnc.init(Cipher.ENCRYPT_MODE,
            new SecretKeySpec(Hex.decode("732f2d33c801732b7206756cbd44f9c1"), "RC4"));

        out = cEnc.doFinal(input);

        cDec = makePBECipherUsingParam(
                            "PBEWithSHAAnd128BitRC4",
                            Cipher.DECRYPT_MODE,
                            password,
                            Hex.decode("7d60435f02e9e0ae"),
                            2048);

        in = cDec.doFinal(out);

        if (!arrayEquals(input, in))
        {
            fail("RC4 failed");
        }

        cDec = makePBECipherWithoutParam(
                "PBEWithSHAAnd128BitRC4",
                Cipher.DECRYPT_MODE,
                password,
                Hex.decode("7d60435f02e9e0ae"),
                2048);

        in = cDec.doFinal(out);
        
        if (!arrayEquals(input, in))
        {
            fail("RC4 failed without param");
        }

        for (int i = 0; i != pkcs12Tests.length; i++)
        {
            pkcs12Tests[i].perform();
        }
        
        for (int i = 0; i != openSSLTests.length; i++)
        {
            openSSLTests[i].perform();
        }

        testPBEHMac("PBEWithHMacSHA1", hMac1);
        testPBEHMac("PBEWithHMacRIPEMD160", hMac2);
    }

    public String getName()
    {
        return "PBETest";
    }


    public static void main(
        String[]    args)
    {
        Security.addProvider(new BouncyCastleProvider());

        runTest(new PBETest());
    }
}
