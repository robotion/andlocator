package org.bouncycastle2.jce;

import org.bouncycastle2.asn1.ASN1EncodableVector;
import org.bouncycastle2.asn1.ASN1InputStream;
import org.bouncycastle2.asn1.ASN1Sequence;
import org.bouncycastle2.asn1.DERBitString;
import org.bouncycastle2.asn1.DERInteger;
import org.bouncycastle2.asn1.DERNull;
import org.bouncycastle2.asn1.DERObjectIdentifier;
import org.bouncycastle2.asn1.DEROutputStream;
import org.bouncycastle2.asn1.DERSequence;
import org.bouncycastle2.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle2.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle2.asn1.x509.TBSCertificateStructure;
import org.bouncycastle2.asn1.x509.Time;
import org.bouncycastle2.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle2.asn1.x509.X509CertificateStructure;
import org.bouncycastle2.asn1.x509.X509Name;
import org.bouncycastle2.jce.provider.X509CertificateObject;
import org.bouncycastle2.util.Strings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;

/**
 * class to produce an X.509 Version 1 certificate.
 * 
 * @deprecated use the equivalent class in org.bouncycastle2.x509
 */
public class X509V1CertificateGenerator
{
    private V1TBSCertificateGenerator   tbsGen;
    private DERObjectIdentifier         sigOID;
    private AlgorithmIdentifier         sigAlgId;
    private String                      signatureAlgorithm;

    private static Hashtable            algorithms = new Hashtable();

    static
    {
        algorithms.put("MD2WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
        algorithms.put("MD2WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.2"));
        algorithms.put("MD5WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
        algorithms.put("MD5WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.4"));
        algorithms.put("SHA1WITHRSAENCRYPTION", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
        algorithms.put("SHA1WITHRSA", new DERObjectIdentifier("1.2.840.113549.1.1.5"));
        algorithms.put("RIPEMD160WITHRSAENCRYPTION", new DERObjectIdentifier("1.3.36.3.3.1.2"));
        algorithms.put("RIPEMD160WITHRSA", new DERObjectIdentifier("1.3.36.3.3.1.2"));
        algorithms.put("SHA1WITHDSA", new DERObjectIdentifier("1.2.840.10040.4.3"));
        algorithms.put("DSAWITHSHA1", new DERObjectIdentifier("1.2.840.10040.4.3"));
        algorithms.put("SHA1WITHECDSA", new DERObjectIdentifier("1.2.840.10045.4.1"));
        algorithms.put("ECDSAWITHSHA1", new DERObjectIdentifier("1.2.840.10045.4.1"));
    }

    public X509V1CertificateGenerator()
    {
        tbsGen = new V1TBSCertificateGenerator();
    }

    /**
     * reset the generator
     */
    public void reset()
    {
        tbsGen = new V1TBSCertificateGenerator();
    }

    /**
     * set the serial number for the certificate.
     */
    public void setSerialNumber(
        BigInteger      serialNumber)
    {
        tbsGen.setSerialNumber(new DERInteger(serialNumber));
    }

    /**
     * Set the issuer distinguished name - the issuer is the entity whose private key is used to sign the
     * certificate.
     */
    public void setIssuerDN(
        X509Name   issuer)
    {
        tbsGen.setIssuer(issuer);
    }

    public void setNotBefore(
        Date    date)
    {
        tbsGen.setStartDate(new Time(date));
    }

    public void setNotAfter(
        Date    date)
    {
        tbsGen.setEndDate(new Time(date));
    }

    /**
     * Set the subject distinguished name. The subject describes the entity associated with the public key.
     */
    public void setSubjectDN(
        X509Name   subject)
    {
        tbsGen.setSubject(subject);
    }

    public void setPublicKey(
        PublicKey       key)
    {
        try
        {
            tbsGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(
                                new ByteArrayInputStream(key.getEncoded())).readObject()));
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("unable to process key - " + e.toString());
        }
    }

    public void setSignatureAlgorithm(
        String  signatureAlgorithm)
    {
        this.signatureAlgorithm = signatureAlgorithm;

        sigOID = (DERObjectIdentifier)algorithms.get(Strings.toUpperCase(signatureAlgorithm));

        if (sigOID == null)
        {
            throw new IllegalArgumentException("Unknown signature type requested");
        }

        sigAlgId = new AlgorithmIdentifier(this.sigOID, new DERNull());

        tbsGen.setSignature(sigAlgId);
    }

    /**
     * generate an X509 certificate, based on the current issuer and subject
     * using the default provider "BC2".
     */
    public X509Certificate generateX509Certificate(
        PrivateKey      key)
        throws SecurityException, SignatureException, InvalidKeyException
    {
        try
        {
            return generateX509Certificate(key, "BC2", null);
        }
        catch (NoSuchProviderException e)
        {
            throw new SecurityException("BC provider not installed!");
        }
    }

    /**
     * generate an X509 certificate, based on the current issuer and subject
     * using the default provider "BC2" and the passed in source of randomness
     */
    public X509Certificate generateX509Certificate(
        PrivateKey      key,
        SecureRandom    random)
        throws SecurityException, SignatureException, InvalidKeyException
    {
        try
        {
            return generateX509Certificate(key, "BC2", random);
        }
        catch (NoSuchProviderException e)
        {
            throw new SecurityException("BC provider not installed!");
        }
    }

    /**
     * generate an X509 certificate, based on the current issuer and subject,
     * using the passed in provider for the signing, and the passed in source
     * of randomness (if required).
     */
    public X509Certificate generateX509Certificate(
        PrivateKey      key,
        String          provider)
        throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException
    {
        return generateX509Certificate(key, provider, null);
    }

    /**
     * generate an X509 certificate, based on the current issuer and subject,
     * using the passed in provider for the signing, and the passed in source
     * of randomness (if required).
     */
    public X509Certificate generateX509Certificate(
        PrivateKey      key,
        String          provider,
        SecureRandom    random)
        throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException
    {
        Signature sig = null;

        try
        {
            sig = Signature.getInstance(sigOID.getId(), provider);
        }
        catch (NoSuchAlgorithmException ex)
        {
            try
            {
                sig = Signature.getInstance(signatureAlgorithm, provider);
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new SecurityException("exception creating signature: " + e.toString());
            }
        }

        if (random != null)
        {
            sig.initSign(key, random);
        }
        else
        {
            sig.initSign(key);
        }

        TBSCertificateStructure tbsCert = tbsGen.generateTBSCertificate();

        try
        {
            ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);

            dOut.writeObject(tbsCert);

            sig.update(bOut.toByteArray());

            ASN1EncodableVector  v = new ASN1EncodableVector();

            v.add(tbsCert);
            v.add(sigAlgId);
            v.add(new DERBitString(sig.sign()));

            return new X509CertificateObject(new X509CertificateStructure(new DERSequence(v)));
        }
        catch (Exception e)
        {
            throw new SecurityException("exception encoding TBS cert - " + e);
        }
    }
}
