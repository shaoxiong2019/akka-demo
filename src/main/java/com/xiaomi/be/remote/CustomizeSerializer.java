package com.xiaomi.be.remote;

import akka.serialization.JSerializer;

public class CustomizeSerializer extends JSerializer {
    @Override
    public int identifier() {
        return 0;
    }

    @Override
    public byte[] toBinary(Object o) {
        return new byte[0];
    }

    @Override
    public boolean includeManifest() {
        return false;
    }

    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
        return null;
    }
}
