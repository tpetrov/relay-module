package twist.uk.co.robotelectronics.impl;

import twist.uk.co.robotelectronics.Client;
import twist.uk.co.robotelectronics.ClientException;
import twist.uk.co.robotelectronics.data.ModuleInfo;

import java.nio.charset.StandardCharsets;

public abstract class ClientBase implements Client {

    private static final byte[] EMPTY_ARRAY = new byte[]{};

    private enum CommandName {
        MODULE_INFO((byte) 0x10),
        DIGIT_ACTIVATE((byte) 0x20),
        DIGIT_INACTIVATE((byte) 0x21),
        PASSWORD_ENTRY((byte) 0x79),
        GET_UNLOCK_TIME((byte) 0x7A);

        private final byte value;

        private CommandName(byte value) {
            this.value = value;
        }
    }

    @Override
    public ModuleInfo getModuleInfo() {
        byte[] result = new byte[3];
        sendAndReceive(CommandName.MODULE_INFO, EMPTY_ARRAY, result);
        return new ModuleInfo(result[0], result[1], result[2]);
    }

    @Override
    public boolean activate(int itemNumber) {
        return activateForMillis(itemNumber, 0L);
    }

    @Override
    public boolean activateForMillis(int itemNumber, long millis) {
        validateDigitalItemNumber(itemNumber);
        byte[] result = new byte[1];
        sendAndReceive(
                CommandName.DIGIT_ACTIVATE,
                new byte[] {
                        (byte)itemNumber,
                        (byte)(millis > 25500L ? 0 : (millis / 100))
                },
                result);
        return result[0] == 0;
    }

    @Override
    public boolean deactivate(int itemNumber) {
        return deactivateForMillis(itemNumber, 0L);
    }

    @Override
    public boolean deactivateForMillis(int itemNumber, long millis) {
        validateDigitalItemNumber(itemNumber);
        byte[] result = new byte[1];
        sendAndReceive(
                CommandName.DIGIT_INACTIVATE,
                new byte[] {
                        (byte)itemNumber,
                        (byte)(millis > 25500L ? 0 : (millis / 100))
                },
                result);
        return result[0] == 0;
    }

    @Override
    public boolean isByPasswordProtected() {
        byte[] result = new byte[1];
        sendAndReceive(
                CommandName.GET_UNLOCK_TIME,
                EMPTY_ARRAY,
                result);
        return (result[0] & 0xFF) != 255;
    }

    @Override
    public boolean login(String password) {
        byte[] result = new byte[1];
        sendAndReceive(
                CommandName.PASSWORD_ENTRY,
                password.getBytes(StandardCharsets.UTF_8),
                result);
        return result[0] == 1;
    }

    @Override
    public int timeBeforeSessionEnd() throws NoPasswordProtectionException {
        byte[] result = new byte[1];
        sendAndReceive(
                CommandName.GET_UNLOCK_TIME,
                EMPTY_ARRAY,
                result);
        if((result[0] & 0xFF) == 255) {
            throw new NoPasswordProtectionException();
        }
        return (result[0] & 0xFF);
    }

    protected abstract void validateDigitalItemNumber(int itemNumber) throws IllegalArgumentException;

    protected abstract void validateDigitalInputItemNumber(int itemNumber) throws IllegalArgumentException;

    protected abstract void validateAnalogItemNumber(int itemNumber) throws IllegalArgumentException;

    protected void sendAndReceive(CommandName commandName, byte[] params, byte[] result) {
        sendAndReceive(toCommand(commandName.value, params), result);
    }

    protected abstract void sendAndReceive(byte[] command, byte[] result) throws ClientException;

    private static byte[] toCommand(byte name, byte[] params) {
        byte[] command = new byte[params.length + 1];

        command[0] = name;
        System.arraycopy(params, 0, command, 1, params.length);

        return command;
    }
}
