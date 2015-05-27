package twist.uk.co.robotelectronics.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class ETH484ModuleClient extends ETHModuleClient {

    public ETH484ModuleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void validateDigitalItemNumber(int itemNumber) throws IllegalArgumentException {
        if(1 <= itemNumber && itemNumber <= 4) {
            return;
        }
        if(9 <= itemNumber && itemNumber <= 16) {
            return;
        }
        throw new IllegalArgumentException("Valid digital item number must be in range [1-4, 9-16]");
    }

    @Override
    protected void validateDigitalInputItemNumber(int itemNumber) throws IllegalArgumentException {
        if(!(9 <= itemNumber && itemNumber <= 16)) {
            throw new IllegalArgumentException("Valid digital input item number must be in range [9-16]");
        }
    }

    @Override
    protected void validateAnalogItemNumber(int itemNumber) throws IllegalArgumentException {
        if(!(1 <= itemNumber && itemNumber <= 4)) {
            throw new IllegalArgumentException("Valid analog item number must be in range [1-4]");
        }
    }

    public static void main(String[] args) {
        ETHModuleClient client = new ETH484ModuleClient("192.168.1.4", 17494);
        System.out.println(client.isActivate(16));
//        System.out.println("Module id: " + client.getModuleInfo().getModuleId());
//        System.out.println("Activated: " + client.activate(1));
//        System.out.println("Is By Password Protected: " + client.isByPasswordProtected());
//        System.out.println("login: " + client.login("password"));
//        System.out.println("Time Before Session End: " + client.timeBeforeSessionEnd());
//        System.out.println("State changed: " + client.setDigitalOutputsState(
//                new HashSet<>(Arrays.asList(1, 3, 4, 9, 10, 11, 14, 15, 16))));
//        //System.out.println("Deactivated: " + client.deactivate(1));
//        System.out.println("Is active: " + client.isActivate(1));
//        System.out.println("Outputs state: " + client.getDigitalOutputsState());
//        System.out.println("Inputs state: " + client.getDigitalInputsState());
//        System.out.println("Analog utput #1 state: " + client.getAnalogValue(1));

//        client.executeCommand("DOA,1,30");
        client.close();
    }
}
