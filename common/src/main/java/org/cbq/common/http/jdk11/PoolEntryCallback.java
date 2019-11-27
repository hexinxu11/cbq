package org.cbq.common.http.jdk11;



public interface PoolEntryCallback<T, C> {
    void process(PoolEntry<T, C> var1);
}

