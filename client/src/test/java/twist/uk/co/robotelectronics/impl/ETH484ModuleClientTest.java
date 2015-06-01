package twist.uk.co.robotelectronics.impl;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import twist.uk.co.robotelectronics.Client;
import twist.uk.co.robotelectronics.data.ModuleInfo;
import twist.uk.co.robotelectronics.test.ETH484TestModule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ETH484ModuleClientTest {

    private static ETH484TestModule module;
    private static Client client;

    @BeforeClass
    public static void init() {
        module = new ETH484TestModule();
        client = module.getClient();
    }

    @AfterClass
    public static void clean() {
        client.close();
    }

    @Test
    public void getModuleInfoTest() {
        ModuleInfo moduleInfo = client.getModuleInfo();
        assertEquals(1, moduleInfo.getModuleId());
        assertEquals(2, moduleInfo.getHardwareVersion());
        assertEquals(3, moduleInfo.getFirmwareVersion());
    }

    @Test
    public void activateTest() {
        assertFalse(client.isActivated(1));
        assertTrue(client.activate(1));
        assertTrue(client.isActivated(1));
        assertTrue(client.deactivate(1));
        assertFalse(client.isActivated(1));
    }

    @Test
    public void activateForMillisTest() throws InterruptedException {
        assertFalse(client.isActivated(2));
        assertTrue(client.activateForMillis(2, 100L));
        assertTrue(client.isActivated(2));
        Thread.sleep(100L);
        assertFalse(client.isActivated(2));
        assertTrue(client.deactivateForMillis(2, 100L));
        assertFalse(client.isActivated(2));
        Thread.sleep(100L);
        assertTrue(client.isActivated(2));
    }

    @Test
    public void setDigitalOutputStateTest() {
        assertTrue(client.setDigitalOutputsState(new HashSet<>(Arrays.asList(1, 2, 4))));
        assertEquals(new HashSet<>(Arrays.asList(1, 2, 4)), client.getDigitalOutputsState());
        assertTrue(client.setDigitalOutputsState(Collections.emptySet()));
        assertEquals(Collections.<Integer>emptySet(), client.getDigitalOutputsState());
    }

    @Test
    public void getDigitalInputsStateTest() {
        assertEquals(Collections.<Integer>emptySet(), client.getDigitalInputsState());
        module.activateInput(9);
        assertEquals(Collections.singleton(9), client.getDigitalInputsState());
    }

    @Test
    public void getAnalogInputsStateTest() {
        assertEquals(0, client.getAnalogValue(1));
        module.setAnalogValue(1, 400);
        assertEquals(400, client.getAnalogValue(1));
    }
}
