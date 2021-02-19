package me.zyee.java.profiler.agent.enhancer;


import java.util.Set;
import me.zyee.java.profiler.event.Event;

/**
 * 代码增强
 *
 * @author yee
 * @version 1.0
 * Create by yee on 2021/1/20
 */
public interface Enhancer {

    /**
     * 转换为增强后的字节码数组
     *
     * @param loader           目标类加载器
     * @param srcByteCodeArray 源字节码数组
     * @param signCodes        需要被增强的行为签名
     * @param listenerId       需要埋入的监听器ID
     * @param eventTypeArray   需要配埋入的事件类型
     * @return 增强后的字节码数组
     */
    byte[] toByteCodeArray(ClassLoader loader,
                           byte[] srcByteCodeArray,
                           Set<String> signCodes,
                           int listenerId,
                           Event.Type[] eventTypeArray);

}
