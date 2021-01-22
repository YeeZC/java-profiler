package me.zyee.java.profiler.bean;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.StringJoiner;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/12
 */
public class Memory {
    private final String bankLabel;
    private final long capacity;
    private final long clockSpeed;
    private final String manufacturer;
    private final String memoryType;

    private Memory(Builder builder) {
        this.bankLabel = builder.bankLabel;
        this.capacity = Objects.requireNonNull(builder.capacity, "capacity");
        this.clockSpeed = Objects.requireNonNull(builder.clockSpeed, "clockSpeed");
        this.manufacturer = builder.manufacturer;
        this.memoryType = builder.memoryType;
    }

    public static Builder builder() {
        return new Builder();
    }


    public String getBankLabel() {
        return bankLabel;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getClockSpeed() {
        return clockSpeed;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getMemoryType() {
        return memoryType;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(";");
        final Field[] fields = FieldUtils.getAllFields(Memory.class);
        for (Field field : fields) {

            try {
                final Object value = FieldUtils.readField(field, this, true);
                if (null != value) {
                    joiner.add(field.getName() + "=" + value);
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        return joiner.toString();
    }

    public static class Builder {
        private String bankLabel;
        private Long capacity = 0L;
        private Long clockSpeed = 0L;
        private String manufacturer;
        private String memoryType;

        private Builder() {
        }

        public Builder setBankLabel(String bankLabel) {
            this.bankLabel = bankLabel;
            return this;
        }

        public Builder setCapacity(long capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder setClockSpeed(long clockSpeed) {
            this.clockSpeed = clockSpeed;
            return this;
        }

        public Builder setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder setMemoryType(String memoryType) {
            this.memoryType = memoryType;
            return this;
        }

        public Builder of(Memory memory) {
            this.bankLabel = memory.bankLabel;
            this.capacity = memory.capacity;
            this.clockSpeed = memory.clockSpeed;
            this.manufacturer = memory.manufacturer;
            this.memoryType = memory.memoryType;
            return this;
        }

        public Memory build() {
            return new Memory(this);
        }
    }
}
