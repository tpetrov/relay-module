package twist.uk.co.robotelectronics.impl;

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
    protected void validateAnalogItemNumber(int itemNumber) throws IllegalArgumentException {
        if(!(1 <= itemNumber && itemNumber <= 4)) {
            throw new IllegalArgumentException("Valid analog item number must be in range [1-4]");
        }
    }
}
