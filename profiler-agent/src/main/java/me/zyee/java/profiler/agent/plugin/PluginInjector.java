package me.zyee.java.profiler.agent.plugin;

import java.util.List;
import java.util.stream.Collectors;
import me.zyee.java.profiler.agent.Injector;
import me.zyee.java.profiler.agent.hardware.Hardware;
import me.zyee.java.profiler.agent.utils.Initializer;
import me.zyee.java.profiler.bean.Cpu;
import me.zyee.java.profiler.bean.Memory;
import me.zyee.java.profiler.bean.Net;
import me.zyee.java.profiler.utils.LazyGet;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/22
 */
public interface PluginInjector {
    CpuPlugin inject(CpuPlugin plugin);

    MemoryPlugin inject(MemoryPlugin plugin);

    NetPlugin inject(NetPlugin plugin);

    PluginInjector INSTANCE = new PluginInjector() {
        private final Hardware hardware = Initializer.newHardware();
        private final LazyGet<Cpu> cpu = new LazyGet<Cpu>() {
            @Override
            protected Cpu initialValue() throws Throwable {
                return Cpu.builder().setFreq(hardware.getProcessorFreq())
                        .setVendor(hardware.getProcessorVendor())
                        .setLogical(hardware.getLogicalProcessorCount())
                        .setPhysical(hardware.getPhysicalProcessorCount())
                        .build();
            }
        };
        private final LazyGet<List<Memory>> memories = new LazyGet<List<Memory>>() {
            @Override
            protected List<Memory> initialValue() throws Throwable {
                return hardware.getMemories()
                        .stream().map(args -> Injector.fromArgs(args, Memory.builder().build()))
                        .collect(Collectors.toList());
            }
        };
        private final LazyGet<List<Net>> nets = new LazyGet<List<Net>>() {
            @Override
            protected List<Net> initialValue() throws Throwable {
                return hardware.getNetIfs().entrySet().stream()
                        .filter(entry -> entry.getValue() > 0)
                        .map(entry -> Net.builder().setName(entry.getKey()).setSpeed(entry.getValue()).build())
                        .collect(Collectors.toList());
            }
        };


        @Override
        public CpuPlugin inject(CpuPlugin plugin) {
            plugin.cpu = cpu.get();
            return plugin;
        }

        @Override
        public MemoryPlugin inject(MemoryPlugin plugin) {
            plugin.memories = memories.get();
            return plugin;
        }

        @Override
        public NetPlugin inject(NetPlugin plugin) {
            plugin.nets = nets.get();
            return plugin;
        }
    };
}
