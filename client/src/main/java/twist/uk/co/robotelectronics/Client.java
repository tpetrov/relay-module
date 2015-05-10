package twist.uk.co.robotelectronics;

import twist.uk.co.robotelectronics.data.ModuleInfo;

import java.util.Set;

public interface Client {

    ModuleInfo getModuleInfo();

    boolean activate(int itemNumber);

    boolean activateForMillis(int itemNumber, long millis);

    boolean deactivate(int itemNumber);

    boolean deactivateForMillis(int itemNumber, long millis);

    boolean isActivate(int itemNumber);

    boolean setDigitalOutputsState(Set<Integer> state);

    Set<Integer> getDigitalOutputsState();

    boolean isByPasswordProtected();

    boolean login(String password);

    int timeBeforeSessionEnd() throws NoPasswordProtectionException;

    void close();

    public static class NoPasswordProtectionException extends RuntimeException { }
}
