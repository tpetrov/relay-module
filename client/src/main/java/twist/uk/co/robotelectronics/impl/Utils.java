package twist.uk.co.robotelectronics.impl;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Utils {

    private Utils() {}

    public static Set<Integer> arrayToSet(byte[] value) {
        Set<Integer> resultSet = new LinkedHashSet<>();
        BitSet bitSet = BitSet.valueOf(value);
        int lastSetBit = bitSet.nextSetBit(0);
        while(lastSetBit != -1) {
            resultSet.add(lastSetBit + 1);
            lastSetBit = bitSet.nextSetBit(lastSetBit + 1);
        }
        return resultSet;
    }

    public static byte[] setToArray(Set<Integer> value, int resultLength) {
        BitSet bitSet = new BitSet();
        for(Integer itemNumber : value) {
            bitSet.set(itemNumber - 1);
        }
        byte[] stateArray = bitSet.toByteArray();
        byte[] result = new byte[resultLength];
        System.arraycopy(stateArray, 0, result, 0, Math.min(stateArray.length, resultLength));
        return result;
    }

    public static int arrayToInt(byte[] value) {
        if(value.length > 4) {
            throw new IllegalArgumentException(
                    String.format("Byte array of size %s cannot be converted to integer", value.length));
        }
        int result = 0;
        for(int i = 0; i < value.length; i++) {
            result = result + ((value[value.length - i - 1] & 0xFF) << 8 * i);
        }
        return result;
    }

    public static byte[] intToArray(int value, int resultLength) {
        if(resultLength > 4) {
            throw new IllegalArgumentException("Integer value can be converted to byte array of maximum size = 4");
        }
        byte[] result = new byte[resultLength];
        for(int i = 0; i < resultLength; i++) {
            result[i] = (byte)((value >> 8 * (resultLength - i - 1)) & 0xFF);
        }
        return result;
    }
}
