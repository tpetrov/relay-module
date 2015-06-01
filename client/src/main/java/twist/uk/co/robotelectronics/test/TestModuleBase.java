package twist.uk.co.robotelectronics.test;

import twist.uk.co.robotelectronics.Client;
import twist.uk.co.robotelectronics.data.ModuleInfo;
import twist.uk.co.robotelectronics.impl.Utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class TestModuleBase {

    private final ModuleInfo moduleInfo;
    private final Map<Integer, Boolean> digitalOutputsState;
    private final Map<Integer, Boolean> digitalInputsState;
    private final Map<Integer, Integer> analogInputsState;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private enum Command {
        MODULE_INFO((byte) 0x10) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 0);
                return new byte[]{
                        (byte)testModule.moduleInfo.getModuleId(),
                        (byte)testModule.moduleInfo.getHardwareVersion(),
                        (byte)testModule.moduleInfo.getFirmwareVersion(),
                };
            }
        },
        DIGIT_ACTIVATE((byte) 0x20) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 2);
                int itemNumber = body[0];
                if(!testModule.digitalOutputsState.containsKey(itemNumber)) {
                    return new byte[]{1};
                }
                testModule.digitalOutputsState.put(itemNumber, true);
                if(body[1] > 0) {
                    testModule.executorService.schedule(
                            () -> testModule.digitalOutputsState.put(itemNumber, false),
                            body[1] * 100, TimeUnit.MILLISECONDS);
                }
                return new byte[]{0};
            }
        },
        DIGIT_INACTIVATE((byte) 0x21) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 2);
                int itemNumber = body[0];
                if(!testModule.digitalOutputsState.containsKey(itemNumber)) {
                    return new byte[]{1};
                }
                testModule.digitalOutputsState.put(itemNumber, false);
                if(body[1] > 0) {
                    testModule.executorService.schedule(
                            () -> testModule.digitalOutputsState.put(itemNumber, true),
                            body[1] * 100, TimeUnit.MILLISECONDS);
                }
                return new byte[]{0};
            }
        },
        DIGITAL_SET_OUTPUTS((byte) 0x23) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 2);
                Set<Integer> outputs = Utils.arrayToSet(body);
                if(!testModule.digitalOutputsState.keySet().containsAll(outputs)) {
                    return new byte[]{1};
                }
                for(Map.Entry<Integer, Boolean> entry : testModule.digitalOutputsState.entrySet()) {
                    entry.setValue(outputs.contains(entry.getKey()));
                }
                return new byte[]{0};
            }
        },
        DIGITAL_GET_OUTPUTS((byte) 0x24) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 0);
                Set<Integer> result = testModule.digitalOutputsState.entrySet().stream().filter(
                        Map.Entry::getValue).map(Map.Entry<Integer, Boolean>::getKey).collect(Collectors.toSet());
                return Utils.setToArray(result, 2);
            }
        },
        DIGITAL_GET_INPUTS((byte) 0x25) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 0);
                Set<Integer> result = testModule.digitalInputsState.entrySet().stream().filter(
                        Map.Entry::getValue).map(Map.Entry<Integer, Boolean>::getKey).collect(Collectors.toSet());
                return Utils.setToArray(result, 2);
            }
        },
        ANALOG_GET_INPUT((byte) 0x32) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                assertBodyLength(body, 1);
                int itemNumber = body[0];
                if(!testModule.analogInputsState.keySet().contains(itemNumber)) {
                    return new byte[]{0, 0};
                }
                int value = testModule.analogInputsState.get(itemNumber);
                return Utils.intToArray(value, 2);
            }
        },
        ASCII_COMMAND((byte) 0x3A) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                throw new UnsupportedOperationException();
            }
        },
        PASSWORD_ENTRY((byte) 0x79) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                throw new UnsupportedOperationException();
            }
        },
        GET_UNLOCK_TIME((byte) 0x7A) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                throw new UnsupportedOperationException();
            }
        },
        LOGOUT((byte) 0x7B) {
            public byte[] execute(byte[] body, TestModuleBase testModule) {
                throw new UnsupportedOperationException();
            }
        };

        private final byte flag;

        private Command(byte flag) {
            this.flag = flag;
        }

        public abstract byte[] execute(byte[] body, TestModuleBase testModule);

        public static Command getCommandName(byte flag) {
            for(Command command : Command.values()) {
                if(command.flag == flag) {
                    return command;
                }
            }
            throw new IllegalArgumentException("Unknown command flag : " + flag);
        }

        private static void assertBodyLength(byte[] body, int expectedLength) {
            if(body.length != expectedLength) {
                throw new IllegalArgumentException("Expected body length : " + expectedLength);
            }
        }
    }

    protected TestModuleBase(ModuleInfo moduleInfo,
                             Set<Integer> digitalOutputs,
                             Set<Integer> digitalInputs,
                             Set<Integer> analogInputs) {
        this.moduleInfo = moduleInfo;
        this.digitalOutputsState = new ConcurrentHashMap<>();
        for(Integer digitalOutput : digitalOutputs) {
            this.digitalOutputsState.put(digitalOutput, false);
        }
        this.digitalInputsState = new ConcurrentHashMap<>();
        for(Integer digitalInput : digitalInputs) {
            this.digitalInputsState.put(digitalInput, false);
        }
        this.analogInputsState = new ConcurrentHashMap<>();
        for(Integer analogInput : analogInputs) {
            this.analogInputsState.put(analogInput, 0);
        }
    }

    protected byte[] onMessage(byte[] message) {
        if(message.length <= 0) {
            throw new IllegalArgumentException("No command flag specified");
        }
        byte[] body = new byte[message.length - 1];
        System.arraycopy(message, 1, body, 0, message.length - 1);
        return Command.getCommandName(message[0]).execute(body, this);
    }

    public void activateInput(int inputNumber) {
        if(!this.digitalInputsState.keySet().contains(inputNumber)) {
            throw new IllegalArgumentException();
        }
        this.digitalInputsState.put(inputNumber, true);
    }

    public void setAnalogValue(int inputNumber, int value) {
        if(!this.analogInputsState.keySet().contains(inputNumber)) {
            throw new IllegalArgumentException();
        }
        this.analogInputsState.put(inputNumber, value);
    }

    public abstract Client getClient();
}
