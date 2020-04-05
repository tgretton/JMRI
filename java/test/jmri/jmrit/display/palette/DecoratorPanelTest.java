package jmri.jmrit.display.palette;

import java.awt.GraphicsEnvironment;
import jmri.jmrit.display.EditorScaffold;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.display.DisplayFrame;
import jmri.jmrit.display.PositionableLabel;

/**
 *
 * @author Paul Bender Copyright (C) 2017	
 */
public class DecoratorPanelTest {

    EditorScaffold editor;
    DisplayFrame df;

    @Test
    public void testCTor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        DecoratorPanel dec = new DecoratorPanel(df);
        Assert.assertNotNull("exists", dec);
    }

    @Test
    public void testInit() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        DecoratorPanel dec = new DecoratorPanel(df);
        dec.initDecoratorPanel(new PositionableLabel("one", editor));
        Assert.assertNotNull("exists", dec);
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        editor = new EditorScaffold("Editor");
        df = new DisplayFrame("DisplayFrame", editor);
    }

    @After
    public void tearDown() {
        JUnitUtil.dispose(df);
        JUnitUtil.dispose(editor);
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(DecoratorPanelTest.class);

}
