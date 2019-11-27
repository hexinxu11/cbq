package org.cbq.common.http.jdk11;

import java.io.IOException;

public interface ConnFactory<T, C> {
    C create(T var1) throws IOException;
}
