package me.zyee.profiler.agent.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import me.zyee.java.profiler.spy.Spy;

/**
 * @author yee
 * @version 1.0
 * created by yee on 2021/1/6
 */
public class ObjectIds {
    private static final int NULL_ID = 0;
    private final Supplier<Integer> objectIDSequencer = Spy::nextSequence;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReferenceQueue<Object> refs = new ReferenceQueue<>();
    private final WeakHashMap<Object, Integer> objectIdMapping = new WeakHashMap<>();
    private final Map<Integer, IdentityReference> identities = new HashMap<>();

    private ObjectIds() {
    }

    public static final ObjectIds instance = new ObjectIds();

    public int identity(Object obj) {
        return Optional.ofNullable(obj).map(object -> {
            lock.readLock().lock();
            try {
                if (objectIdMapping.containsKey(object)) {
                    return objectIdMapping.get(object);
                }
            } finally {
                lock.readLock().unlock();
                expungeIdentities();
            }
            lock.writeLock().lock();
            Integer id = objectIDSequencer.get();
            try {
                objectIdMapping.put(object, id);
                identities.put(id, new IdentityReference(id, object));
                return id;
            } finally {
                lock.writeLock().unlock();
            }
        }).orElse(NULL_ID);
    }

    public void expungeIdentities() {
        for (Object x; (x = refs.poll()) != null; ) {
            synchronized (refs) {
                lock.writeLock().lock();
                try {
                    if (x instanceof IdentityReference) {
                        identities.remove(((IdentityReference) x).id);
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }
    }


    private class IdentityReference extends WeakReference<Object> {
        private final Integer id;

        public IdentityReference(Integer id, Object referent) {
            super(referent, refs);
            this.id = id;
        }
    }
}
