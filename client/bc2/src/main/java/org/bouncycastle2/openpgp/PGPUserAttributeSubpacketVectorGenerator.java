package org.bouncycastle2.openpgp;

import org.bouncycastle2.bcpg.UserAttributeSubpacket;
import org.bouncycastle2.bcpg.attr.ImageAttribute;

import java.util.ArrayList;
import java.util.List;

public class PGPUserAttributeSubpacketVectorGenerator
{
    private List list = new ArrayList();

    public void setImageAttribute(int imageType, byte[] imageData)
    {
        if (imageData == null)
        {
            throw new IllegalArgumentException("attempt to set null image");
        }

        list.add(new ImageAttribute(imageType, imageData));
    }

    public PGPUserAttributeSubpacketVector generate()
    {
        return new PGPUserAttributeSubpacketVector((UserAttributeSubpacket[])list.toArray(new UserAttributeSubpacket[list.size()]));
    }
}
