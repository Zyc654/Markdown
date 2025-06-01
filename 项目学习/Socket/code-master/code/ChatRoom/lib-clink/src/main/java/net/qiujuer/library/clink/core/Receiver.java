package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.io.IOException;

public interface Receiver extends Closeable {
    void setReceiveListener(IoArgs.IoArgsEventProcessor processor);

    boolean postReceiveAsync() throws IOException;

    /*
     * boolean setReceiveListener(IoArgs.IoArgsEventProcessor listener);
     * boolean receiveAsync(IoArgs args) throws IOException;
     * 
     * 
     * 
     * 
     * 
    */
}
