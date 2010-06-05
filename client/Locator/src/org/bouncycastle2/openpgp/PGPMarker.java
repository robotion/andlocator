/*
 * Created on Mar 6, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.bouncycastle2.openpgp;

import java.io.IOException;

import org.bouncycastle2.bcpg.BCPGInputStream;
import org.bouncycastle2.bcpg.MarkerPacket;

/**
 * a PGP marker packet - in general these should be ignored other than where
 * the idea is to preserve the original input stream.
 */
public class PGPMarker
{
    private MarkerPacket p;
    
    /**
     * Default constructor.
     * 
     * @param in
     * @throws IOException
     */
    public PGPMarker(
        BCPGInputStream in) 
        throws IOException
    {
        p = (MarkerPacket)in.readPacket();
    }
}
