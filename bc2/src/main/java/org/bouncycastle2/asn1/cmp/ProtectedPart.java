package org.bouncycastle2.asn1.cmp;

import org.bouncycastle2.asn1.ASN1Encodable;
import org.bouncycastle2.asn1.ASN1EncodableVector;
import org.bouncycastle2.asn1.ASN1Sequence;
import org.bouncycastle2.asn1.DERObject;
import org.bouncycastle2.asn1.DERSequence;

public class ProtectedPart
    extends ASN1Encodable
{
    private PKIHeader header;
    private PKIBody body;

    private ProtectedPart(ASN1Sequence seq)
    {
        header = PKIHeader.getInstance(seq.getObjectAt(0));
        body = PKIBody.getInstance(seq.getObjectAt(1));
    }

    public static ProtectedPart getInstance(Object o)
    {
        if (o instanceof ProtectedPart)
        {
            return (ProtectedPart)o;
        }

        if (o instanceof ASN1Sequence)
        {
            return new ProtectedPart((ASN1Sequence)o);
        }

        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }

    public PKIHeader getHeader()
    {
        return header;
    }

    public PKIBody getBody()
    {
        return body;
    }

    /**
     * <pre>
     * ProtectedPart ::= SEQUENCE {
     *                    header    PKIHeader,
     *                    body      PKIBody
     * }
     * </pre>
     * @return a basic ASN.1 object representation.
     */
    public DERObject toASN1Object()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(header);
        v.add(body);

        return new DERSequence(v);
    }
}
