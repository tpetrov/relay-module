package twist.uk.co.robotelectronics;

import twist.uk.co.robotelectronics.data.ModuleInfo;

import java.util.Set;

public interface Client {

    ModuleInfo getModuleInfo();

    boolean activate(int itemNumber);

    boolean activateForMillis(int itemNumber, long millis);

    boolean deactivate(int itemNumber);

    boolean deactivateForMillis(int itemNumber, long millis);

    boolean isActivated(int itemNumber);

    boolean setDigitalOutputsState(Set<Integer> state);

    Set<Integer> getDigitalOutputsState();

    Set<Integer> getDigitalInputsState();

    int getAnalogValue(int itemNumber);

    void executeCommand(String command);

    boolean isByPasswordProtected();

    boolean login(String password);

    int timeBeforeSessionEnd() throws NoPasswordProtectionException;

    void logout();

    void close();

    public static class NoPasswordProtectionException extends RuntimeException { }
}
