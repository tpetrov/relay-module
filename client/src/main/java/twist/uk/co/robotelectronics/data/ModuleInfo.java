package twist.uk.co.robotelectronics.data;

public class ModuleInfo {

    private final int moduleId;
    private final int hardwareVersion;
    private final int firmwareVersion;

    public ModuleInfo(int moduleId, int hardwareVersion, int firmwareVersion) {
        this.moduleId = moduleId;
        this.hardwareVersion = hardwareVersion;
        this.firmwareVersion = firmwareVersion;
    }

    public int getModuleId() {
        return moduleId;
    }

    public int getHardwareVersion() {
        return hardwareVersion;
    }

    public int getFirmwareVersion() {
        return firmwareVersion;
    }
}
