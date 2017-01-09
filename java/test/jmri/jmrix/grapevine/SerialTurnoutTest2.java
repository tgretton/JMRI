package jmri.jmrix.grapevine;

import jmri.implementation.AbstractTurnoutTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the jmri.jmrix.grapevine.SerialTurnout class, high part of 24 port
 * card.
 *
 * @author	Bob Jacobsen
  */
public class SerialTurnoutTest2 extends AbstractTurnoutTest {

    private SerialTrafficControlScaffold tcis = null;

    @Before
    @Override
    public void setUp() {
        // prepare an interface
        tcis = new SerialTrafficControlScaffold();
        tcis.registerNode(new SerialNode(1, SerialNode.NODE2002V6));

        t = new SerialTurnout("GT1116", "t4");
    }

    @Override
    public int numListeners() {
        return tcis.numListeners();
    }

    @Override
    public void checkClosedMsgSent() {
        Assert.assertTrue("message sent", tcis.outbound.size() > 0);
        Assert.assertEquals("content", "81 7A 81 1B 81 18 81 0C", tcis.outbound.elementAt(tcis.outbound.size() - 1).toString());  // CLOSED message
    }

    @Override
    public void checkThrownMsgSent() {
        Assert.assertTrue("message sent", tcis.outbound.size() > 0);
        Assert.assertEquals("content", "81 7A 81 1B 81 1E 81 00", tcis.outbound.elementAt(tcis.outbound.size() - 1).toString());  // THROWN message
    }

}
