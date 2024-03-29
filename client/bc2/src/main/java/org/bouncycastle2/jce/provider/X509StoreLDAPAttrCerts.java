package org.bouncycastle2.jce.provider;

import org.bouncycastle2.jce.X509LDAPCertStoreParameters;
import org.bouncycastle2.util.Selector;
import org.bouncycastle2.util.StoreException;
import org.bouncycastle2.x509.X509AttributeCertStoreSelector;
import org.bouncycastle2.x509.X509StoreParameters;
import org.bouncycastle2.x509.X509StoreSpi;
import org.bouncycastle2.x509.util.LDAPStoreHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A SPI implementation of Bouncy Castle <code>X509Store</code> for getting
 * attribute certificates from an LDAP directory.
 *
 * @see org.bouncycastle2.x509.X509Store
 */
public class X509StoreLDAPAttrCerts extends X509StoreSpi
{

    private LDAPStoreHelper helper;

    public X509StoreLDAPAttrCerts()
    {
    }

    /**
     * Initializes this LDAP attribute cert store implementation.
     *
     * @param parameters <code>X509LDAPCertStoreParameters</code>.
     * @throws IllegalArgumentException if <code>params</code> is not an instance of
     *                                  <code>X509LDAPCertStoreParameters</code>.
     */
    public void engineInit(X509StoreParameters parameters)
    {
        if (!(parameters instanceof X509LDAPCertStoreParameters))
        {
            throw new IllegalArgumentException(
                "Initialization parameters must be an instance of "
                    + X509LDAPCertStoreParameters.class.getName() + ".");
        }
        helper = new LDAPStoreHelper((X509LDAPCertStoreParameters)parameters);
    }

    /**
     * Returns a collection of matching attribute certificates from the LDAP
     * location.
     * <p/>
     * The selector must be a of type
     * <code>X509AttributeCertStoreSelector</code>. If it is not an empty
     * collection is returned.
     * <p/>
     * <p/>
     * The subject and the serial number should be reasonable criterias for a
     * selector.
     *
     * @param selector The selector to use for finding.
     * @return A collection with the matches.
     * @throws StoreException if an exception occurs while searching.
     */
    public Collection engineGetMatches(Selector selector) throws StoreException
    {
        if (!(selector instanceof X509AttributeCertStoreSelector))
        {
            return Collections.EMPTY_SET;
        }
        X509AttributeCertStoreSelector xselector = (X509AttributeCertStoreSelector)selector;
        Set set = new HashSet();
        set.addAll(helper.getAACertificates(xselector));
        set.addAll(helper.getAttributeCertificateAttributes(xselector));
        set.addAll(helper.getAttributeDescriptorCertificates(xselector));
        return set;
    }

}
