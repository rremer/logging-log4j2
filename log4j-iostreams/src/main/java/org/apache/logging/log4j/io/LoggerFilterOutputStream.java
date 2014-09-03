/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.logging.log4j.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

/**
 * Logs each line written to a pre-defined level. Can also be configured with a Marker. This class provides an interface
 * that follows the {@link java.io.OutputStream} methods in spirit, but doesn't require output to any external stream.
 * This class should <em>not</em> be used as a stream for an underlying logger unless it's being used as a bridge.
 * Otherwise, infinite loops may occur!
 */
public class LoggerFilterOutputStream extends FilterOutputStream {
    private static final String FQCN = LoggerFilterOutputStream.class.getName();

    private final ByteStreamLogger logger;
    private final String fqcn;

    public LoggerFilterOutputStream(final OutputStream out, final Charset charset, final ExtendedLogger logger,
            final Level level) {
        this(out, charset, logger, FQCN, level, null);
    }

    public LoggerFilterOutputStream(final OutputStream out, final Charset charset, final ExtendedLogger logger,
            final Level level, final Marker marker) {
        this(out, charset, logger, FQCN, level, marker);
    }

    public LoggerFilterOutputStream(final OutputStream out, final Charset charset, final ExtendedLogger logger,
            final String fqcn, final Level level, final Marker marker) {
        super(out);
        this.logger = new ByteStreamLogger(logger, level, marker, charset);
        this.fqcn = fqcn;
    }

    public LoggerFilterOutputStream(final OutputStream out, final ExtendedLogger logger, final Level level) {
        this(out, Charset.defaultCharset(), logger, FQCN, level, null);
    }

    public LoggerFilterOutputStream(final OutputStream out, final ExtendedLogger logger, final Level level,
            final Marker marker) {
        this(out, Charset.defaultCharset(), logger, FQCN, level, marker);
    }

    @Override
    public void close() throws IOException {
        this.out.close();
        this.logger.close(this.fqcn);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public String toString() {
        return LoggerFilterOutputStream.class.getSimpleName() + "{stream=" + this.out + '}';
    }

    @Override
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
        this.logger.put(this.fqcn, b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
        this.logger.put(this.fqcn, b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
        this.logger.put(this.fqcn, (byte) (b & 0xFF));
    }
}
