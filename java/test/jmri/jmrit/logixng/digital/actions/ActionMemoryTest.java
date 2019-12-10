package jmri.jmrit.logixng.digital.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.Memory;
import jmri.MemoryManager;
import jmri.NamedBean;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.ConditionalNG_Manager;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.util.JUnitAppender;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionMemory
 * 
 * @author Daniel Bergqvist 2018
 */
public class ActionMemoryTest extends AbstractDigitalActionTestBase {

    private LogixNG logixNG;
    private ConditionalNG conditionalNG;
    private ActionMemory actionMemory;
    private Memory memory;
    
    
    @Override
    public ConditionalNG getConditionalNG() {
        return conditionalNG;
    }
    
    @Override
    public LogixNG getLogixNG() {
        return logixNG;
    }
    
    @Override
    public String getExpectedPrintedTree() {
        return String.format("Set memory IM1 to \"\"%n");
    }
    
    @Override
    public String getExpectedPrintedTreeFromRoot() {
        return String.format(
                "LogixNG: A logixNG%n" +
                "   ConditionalNG: A conditionalNG%n" +
                "      ! %n" +
                "         Set memory IM1 to \"\"%n");
    }
    
    @Override
    public NamedBean createNewBean(String systemName) {
        return new ActionMemory(systemName, null);
    }
    
    @Test
    public void testCtor() throws JmriException {
        Assert.assertTrue("object exists", _base != null);
        
        ActionMemory action2;
        Assert.assertNotNull("memory is not null", memory);
        memory.setValue("Old value");
        
        action2 = new ActionMemory("IQDA321", null);
        action2.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_NULL);
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username matches", action2.getUserName());
        Assert.assertEquals("String matches", "Set memory '' to null", action2.getLongDescription());
        
        action2 = new ActionMemory("IQDA321", "My memory");
        action2.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_STRING);
        action2.setNewValue("New value");
        Assert.assertNotNull("object exists", action2);
        Assert.assertEquals("Username matches", "My memory", action2.getUserName());
        Assert.assertEquals("String matches", "Set memory '' to \"New value\"", action2.getLongDescription());
        
        action2 = new ActionMemory("IQDA321", null);
        action2.setMemoryOperation(ActionMemory.MemoryOperation.COPY_MEMORY);
        action2.setMemory(memory);
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM12");
        action2.setCopyToMemory(otherMemory);
        Assert.assertTrue("memory is correct", memory == action2.getMemory().getBean());
        Assert.assertNotNull("object exists", action2);
        Assert.assertNull("Username matches", action2.getUserName());
        Assert.assertEquals("String matches", "Copy memory IM1 to memory IM12", action2.getLongDescription());
        
        boolean thrown = false;
        try {
            // Illegal system name
            new ActionMemory("IQA55:12:XY11", null);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        thrown = false;
        try {
            // Illegal system name
            new ActionMemory("IQA55:12:XY11", "A name");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        // Test setup(). This method doesn't do anything, but execute it for coverage.
        _base.setup();
    }
    
    @Test
    public void testSetMemory() {
        Memory memory11 = InstanceManager.getDefault(MemoryManager.class).provide("IL11");
        Memory memory12 = InstanceManager.getDefault(MemoryManager.class).provide("IL12");
        NamedBeanHandle<Memory> memoryHandle12 = InstanceManager.getDefault(NamedBeanHandleManager.class).getNamedBeanHandle(memory12.getDisplayName(), memory12);
        Memory memory13 = InstanceManager.getDefault(MemoryManager.class).provide("IL13");
        Memory memory14 = InstanceManager.getDefault(MemoryManager.class).provide("IL14");
        memory14.setUserName("Some user name");
        
        actionMemory.setMemory((Memory)null);
        Assert.assertNull("memory handle is null", actionMemory.getMemory());
        
        actionMemory.setMemory(memory11);
        Assert.assertTrue("memory is correct", memory11 == actionMemory.getMemory().getBean());
        
        actionMemory.setMemory((Memory)null);
        Assert.assertNull("memory handle is null", actionMemory.getMemory());
        
        actionMemory.setMemory(memoryHandle12);
        Assert.assertTrue("memory handle is correct", memoryHandle12 == actionMemory.getMemory());
        
        actionMemory.setMemory("A non existent memory");
        Assert.assertNull("memory handle is null", actionMemory.getMemory());
        JUnitAppender.assertErrorMessage("memory \"A non existent memory\" is not found");
        
        actionMemory.setMemory(memory13.getSystemName());
        Assert.assertTrue("memory is correct", memory13 == actionMemory.getMemory().getBean());
        
        actionMemory.setMemory(memory14.getUserName());
        Assert.assertTrue("memory is correct", memory14 == actionMemory.getMemory().getBean());
    }
    
    @Test
    public void testAction() throws SocketAlreadyConnectedException, JmriException {
        // The action is not yet executed so the memory should have the value "Old value"
        Assert.assertEquals("memory has correct value", "Old value", memory.getValue());
        // Execute the conditional
        conditionalNG.execute();
        // The action should now be executed so the memory should be set
        Assert.assertEquals("memory has correct value", "", memory.getValue());
        
        // Test to set memory to null
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_NULL);
        // Execute the setMemoryOperation
        conditionalNG.execute();
        // The action should now be executed so the memory should be set
        Assert.assertEquals("memory has correct value", null, memory.getValue());
        
        // Test to set memory to string
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_STRING);
        actionMemory.setNewValue("New value");
        // Execute the conditional
        conditionalNG.execute();
        // The action should now be executed so the memory should be thrown
        Assert.assertEquals("memory has correct value", "New value", memory.getValue());
        
        // Test to copy memory to memory
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM2");
        memory.setValue("A value");
        otherMemory.setValue("Some other value");
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.COPY_MEMORY);
        actionMemory.setCopyToMemory(otherMemory);
        // Execute the conditional
        conditionalNG.execute();
        // The action should now be executed so the memory should been copied to the other memory
        Assert.assertEquals("memory has correct value", "A value", memory.getValue());
        Assert.assertEquals("memory has correct value", "A value", otherMemory.getValue());
    }
    
    @Test
    public void testVetoableChange() throws PropertyVetoException {
        // Get the action and set the memory
        Memory memory = InstanceManager.getDefault(MemoryManager.class).provide("IM1");
        Assert.assertNotNull("Memory is not null", memory);
        ActionMemory action = new ActionMemory(InstanceManager.getDefault(DigitalActionManager.class).getAutoSystemName(), null);
        action.setMemory(memory);
        
        // Get some other memory for later use
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM99");
        Assert.assertNotNull("Memory is not null", otherMemory);
        Assert.assertNotEquals("Memory is not equal", memory, otherMemory);
        
        // Test vetoableChange() for some other propery
        action.vetoableChange(new PropertyChangeEvent(this, "CanSomething", "test", null));
        Assert.assertEquals("Memory matches", memory, action.getMemory().getBean());
        
        // Test vetoableChange() for a string
        action.vetoableChange(new PropertyChangeEvent(this, "CanDelete", "test", null));
        Assert.assertEquals("Memory matches", memory, action.getMemory().getBean());
        action.vetoableChange(new PropertyChangeEvent(this, "DoDelete", "test", null));
        Assert.assertEquals("Memory matches", memory, action.getMemory().getBean());
        
        // Test vetoableChange() for another memory
        action.vetoableChange(new PropertyChangeEvent(this, "CanDelete", otherMemory, null));
        Assert.assertEquals("Memory matches", memory, action.getMemory().getBean());
        action.vetoableChange(new PropertyChangeEvent(this, "DoDelete", otherMemory, null));
        Assert.assertEquals("Memory matches", memory, action.getMemory().getBean());
        
        // Test vetoableChange() for its own memory
        boolean thrown = false;
        try {
            action.vetoableChange(new PropertyChangeEvent(this, "CanDelete", memory, null));
        } catch (PropertyVetoException ex) {
            thrown = true;
        }
        Assert.assertTrue("Expected exception thrown", thrown);
        
        Assert.assertEquals("Memory matches", memory, action.getMemory().getBean());
        action.vetoableChange(new PropertyChangeEvent(this, "DoDelete", memory, null));
        Assert.assertNull("Memory is null", action.getMemory());
    }
    
    @Test
    public void testCategory() {
        Assert.assertTrue("Category matches", Category.ITEM == _base.getCategory());
    }
    
    @Test
    public void testIsExternal() {
        Assert.assertTrue("is external", _base.isExternal());
    }
    
    @Test
    public void testShortDescription() {
        Assert.assertEquals("String matches", "Set memory", _base.getShortDescription());
    }
    
    @Test
    public void testLongDescription() {
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_NULL);
        Assert.assertEquals("String matches", "Set memory IM1 to null", _base.getLongDescription());
        
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_STRING);
        actionMemory.setNewValue("Some new value");
        Assert.assertEquals("String matches", "Set memory IM1 to \"Some new value\"", _base.getLongDescription());
        
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.COPY_MEMORY);
        Memory otherMemory = InstanceManager.getDefault(MemoryManager.class).provide("IM99");
        actionMemory.setCopyToMemory(otherMemory);
        Assert.assertEquals("String matches", "Copy memory IM1 to memory IM99", _base.getLongDescription());
    }
    
    @Test
    public void testChild() {
        Assert.assertTrue("Num children is zero", 0 == _base.getChildCount());
        boolean hasThrown = false;
        try {
            _base.getChild(0);
        } catch (UnsupportedOperationException ex) {
            hasThrown = true;
            Assert.assertTrue("Error message is correct", "Not supported.".equals(ex.getMessage()));
        }
        Assert.assertTrue("Exception is thrown", hasThrown);
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() throws SocketAlreadyConnectedException {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initMemoryManager();
        
        _category = Category.ITEM;
        _isExternal = true;
        
        memory = InstanceManager.getDefault(MemoryManager.class).provide("IM1");
        memory.setValue("Old value");
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A logixNG");
        conditionalNG = InstanceManager.getDefault(ConditionalNG_Manager.class)
                .createConditionalNG("A conditionalNG");  // NOI18N
        logixNG.addConditionalNG(conditionalNG);
        conditionalNG.setRunOnGUIDelayed(false);
        conditionalNG.setEnabled(true);
        actionMemory = new ActionMemory(InstanceManager.getDefault(DigitalActionManager.class).getAutoSystemName(), null);
        actionMemory.setMemory(memory);
        actionMemory.setMemoryOperation(ActionMemory.MemoryOperation.SET_TO_STRING);
        MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionMemory);
        conditionalNG.getChild(0).connect(socket);
        
        _base = actionMemory;
        _baseMaleSocket = socket;
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
