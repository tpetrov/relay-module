package twist.uk.co.robotelectronics;

import twist.uk.co.robotelectronics.data.ModuleInfo;

public interface Client {

    ModuleInfo getModuleInfo();

    boolean activate(int itemNumber);

    boolean activateForMillis(int itemNumber, long millis);

    boolean deactivate(int itemNumber);

    boolean deactivateForMillis(int itemNumber, long millis);

    boolean isByPasswordProtected();

    boolean login(String password);

    int timeBeforeSessionEnd() throws NoPasswordProtectionException;

    void close();

    public static class NoPasswordProtectionException extends RuntimeException { }
}
