package me.zyee.java.profiler;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2020/12/1
 */
public class ProfilerBuilder {
    private Granularity granularity = Granularity.TEST_CASE;

    ProfilerBuilder() {
    }

    public ProfilerBuilder granularity(Granularity granularity) {
        this.granularity = granularity;
        return this;
    }


}
