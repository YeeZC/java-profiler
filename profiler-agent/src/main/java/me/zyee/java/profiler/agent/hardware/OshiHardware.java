package me.zyee.java.profiler.agent.hardware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.zyee.java.profiler.bean.Memory;
import me.zyee.java.profiler.utils.OS;
import org.apache.commons.lang3.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import oshi.hardware.PhysicalMemory;
import oshi.util.Constants;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/19
 */
public class OshiHardware implements Hardware {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();

    @Override
    public long getProcessorFreq() {
        return SYSTEM_INFO.getHardware().getProcessor().getProcessorIdentifier().getVendorFreq();
    }

    @Override
    public String getProcessorVendor() {
        return SYSTEM_INFO.getHardware().getProcessor().getProcessorIdentifier().getVendor();
    }

    @Override
    public int getLogicalProcessorCount() {
        return SYSTEM_INFO.getHardware().getProcessor().getLogicalProcessorCount();
    }

    @Override
    public int getPhysicalProcessorCount() {
        return SYSTEM_INFO.getHardware().getProcessor().getPhysicalProcessorCount();
    }

    @Override
    public Map<String, Long> getNetIfs() {
        return SYSTEM_INFO.getHardware().getNetworkIFs()
                .stream().collect(Collectors.toMap(NetworkIF::getName, NetworkIF::getSpeed));
    }

    @Override
    public List<String> getMemories() {
        List<PhysicalMemory> memories = null;
        if (OS.getOSType() == OS.OSType.Macintosh) {
            memories = getMacPhysicalMemory();
        } else {
            memories = SYSTEM_INFO.getHardware().getMemory().getPhysicalMemory();
        }
        return memories.stream().map(memory -> Memory.builder()
                .setManufacturer(memory.getManufacturer())
                .setCapacity(memory.getCapacity())
                .setClockSpeed(memory.getClockSpeed())
                .setBankLabel(memory.getBankLabel())
                .setMemoryType(memory.getMemoryType()).build().toString()
        ).collect(Collectors.toList());
    }

    private List<PhysicalMemory> getMacPhysicalMemory() {
        List<PhysicalMemory> pmList = new ArrayList<>();
        List<String> sp = ExecutingCommand.runNative("system_profiler SPMemoryDataType")
                .stream().map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        int bank = 0;
        String bankLabel = Constants.UNKNOWN;
        long capacity = 0L;
        long speed = 0L;
        String manufacturer = Constants.UNKNOWN;
        String memoryType = Constants.UNKNOWN;

        for (String line : sp) {
            if (line.startsWith("BANK") || line.startsWith("Memory Channel")) {
                // Save previous bank
                if (bank++ > 0) {
                    pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
                }
                bankLabel = line;
                int colon = bankLabel.lastIndexOf(':');
                if (colon > 0) {
                    bankLabel = bankLabel.substring(0, colon - 1);
                }
            } else if (bank > 0) {
                String[] split = line.split(":");
                if (split.length == 2) {
                    switch (split[0]) {
                        case "Size":
                            capacity = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
                            break;
                        case "Type":
                            memoryType = split[1];
                            break;
                        case "Speed":
                            speed = ParseUtil.parseHertz(split[1]);
                            break;
                        case "Manufacturer":
                            manufacturer = split[1];
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));

        return pmList;
    }

}
