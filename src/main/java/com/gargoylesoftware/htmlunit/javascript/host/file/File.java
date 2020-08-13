/*
 * Copyright (c) 2002-2020 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.javascript.host.file;

import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.CHROME;
import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.FF;
import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.FF68;
import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.IE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxClass;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxConstructor;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxFunction;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxGetter;
import com.gargoylesoftware.htmlunit.javascript.host.Promise;

import net.sourceforge.htmlunit.corejs.javascript.Context;
import net.sourceforge.htmlunit.corejs.javascript.NativeArray;
import net.sourceforge.htmlunit.corejs.javascript.ScriptRuntime;
import net.sourceforge.htmlunit.corejs.javascript.Scriptable;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import net.sourceforge.htmlunit.corejs.javascript.Undefined;
import net.sourceforge.htmlunit.corejs.javascript.typedarrays.NativeArrayBuffer;
import net.sourceforge.htmlunit.corejs.javascript.typedarrays.NativeArrayBufferView;

/**
 * A JavaScript object for {@code File}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
@JsxClass
public class File extends Blob {
    private static final String LAST_MODIFIED_DATE_FORMAT = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)";

    private static final String OPTIONS_LASTMODIFIED = "lastModified";

    private abstract static class Backend implements Serializable {
        abstract String getName();
        abstract long getLastModified();
        abstract long getSize();
        abstract String getType(BrowserVersion browserVersion);
        abstract String getText() throws IOException;

        Backend() {
        }

        // TODO
        abstract java.io.File getFile();
    }

    private static class FileBackend extends Backend {
        private java.io.File file_;

        FileBackend(final String pathname) {
            file_ = new java.io.File(pathname);
        }

        @Override
        public String getName() {
            return file_.getName();
        }

        @Override
        public long getLastModified() {
            return file_.lastModified();
        }

        @Override
        public long getSize() {
            return file_.length();
        }

        @Override
        public String getType(final BrowserVersion browserVersion) {
            return browserVersion.getUploadMimeType(file_);
        }

        @Override
        public String getText() throws IOException {
            return FileUtils.readFileToString(file_, StandardCharsets.UTF_8);
        }

        @Override
        public java.io.File getFile() {
            return file_;
        }
    }

    private static class InMemoryBackend extends Backend {
        private final String fileName_;
        private final String type_;
        private final long lastModified_;
        private final byte[] bytes_;

        InMemoryBackend(final NativeArray fileBits, final String fileName,
                final String type, final long lastModified) {
            fileName_ = fileName;
            type_ = type;
            lastModified_ = lastModified;

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (long i = 0; i < fileBits.getLength(); i++) {
                final Object fileBit = fileBits.get(i);
                if (fileBit instanceof NativeArrayBuffer) {
                    final byte[] bytes = ((NativeArrayBuffer) fileBit).getBuffer();
                    out.write(bytes, 0, bytes.length);
                }
                else if (fileBit instanceof NativeArrayBufferView) {
                    final byte[] bytes = ((NativeArrayBufferView) fileBit).getBuffer().getBuffer();
                    out.write(bytes, 0, bytes.length);
                }
                else {
                    final String bits = Context.toString(fileBits.get(i));
                    // Todo normalize line breaks
                    final byte[] bytes = bits.getBytes(StandardCharsets.UTF_8);
                    out.write(bytes, 0, bytes.length);
                }
            }
            bytes_ = out.toByteArray();
        }

        @Override
        public String getName() {
            return fileName_;
        }

        @Override
        public long getLastModified() {
            return lastModified_;
        }

        @Override
        public long getSize() {
            return bytes_.length;
        }

        @Override
        public String getType(final BrowserVersion browserVersion) {
            return type_;
        }

        @Override
        public String getText() throws IOException {
            return new String(bytes_, StandardCharsets.UTF_8);
        }

        @Override
        public java.io.File getFile() {
            throw new UnsupportedOperationException(
                    "com.gargoylesoftware.htmlunit.javascript.host.file.File.InMemoryBackend.getFile()");
        }
    }

    private Backend backend_;

    /**
     * Prototye ctor.
     */
    public File() {
    }

    /**
     * Creates an instance.
     * @param fileBits the bits
     * @param fileName the Name
     * @param properties the properties
     */
    @JsxConstructor({CHROME, FF, FF68})
    public File(final NativeArray fileBits, final String fileName, final ScriptableObject properties) {
        super(fileBits, properties);
        if (fileBits == null
                || Undefined.isUndefined(fileBits)
                || fileName == null
                || Undefined.isUndefined(fileName)) {
            throw ScriptRuntime.typeError("Failed to construct 'File': 2 arguments required.");
        }

        backend_ = new InMemoryBackend(fileBits, fileName,
                            getMimeType(),
                            extractLastModifiedOrDefault(properties));
    }

    private long extractLastModifiedOrDefault(final ScriptableObject properties) {
        if (properties == null || Undefined.isUndefined(properties)) {
            return System.currentTimeMillis();
        }

        final Object optionsType = properties.get(OPTIONS_LASTMODIFIED, properties);
        if (optionsType != null && properties != Scriptable.NOT_FOUND
                && !Undefined.isUndefined(optionsType)) {
            try {
                return Long.parseLong(Context.toString(optionsType));
            }
            catch (final NumberFormatException e) {
                // fall back to default
            }
        }

        return System.currentTimeMillis();
    }

    File(final String pathname) {
        backend_ = new FileBackend(pathname);
    }

    /**
     * Returns the {@code name} property.
     * @return the {@code name} property
     */
    @JsxGetter
    public String getName() {
        return backend_.getName();
    }

    /**
     * Returns the {@code lastModifiedDate} property.
     * @return the {@code lastModifiedDate} property
     */
    @JsxGetter({CHROME, IE})
    public String getLastModifiedDate() {
        final Date date = new Date(getLastModified());
        final BrowserVersion browser = getBrowserVersion();
        final Locale locale = new Locale(browser.getSystemLanguage());
        final TimeZone timezone = browser.getSystemTimezone();

        final FastDateFormat format = FastDateFormat.getInstance(LAST_MODIFIED_DATE_FORMAT, timezone, locale);
        return format.format(date);
    }

    /**
     * Returns the {@code lastModified} property.
     * @return the {@code lastModified} property
     */
    @JsxGetter({CHROME, FF, FF68})
    public long getLastModified() {
        return backend_.getLastModified();
    }

    /**
     * Returns the {@code webkitRelativePath} property.
     * @return the {@code webkitRelativePath} property
     */
    @JsxGetter({CHROME, FF, FF68})
    public String getWebkitRelativePath() {
        return "";
    }

    /**
     * Returns the {@code size} property.
     * @return the {@code size} property
     */
    @JsxGetter
    public long getSize() {
        return backend_.getSize();
    }

    /**
     * Returns the {@code type} property.
     * @return the {@code type} property
     */
    @JsxGetter
    public String getType() {
        return backend_.getType(getBrowserVersion());
    }

    /**
     * @return a Promise that resolves with a string containing the
     * contents of the blob, interpreted as UTF-8.
     */
    @JsxFunction({CHROME, FF})
    public Promise text() {
        try {
            return Promise.resolve(null, this, new Object[] {backend_.getText()}, null);
        }
        catch (final IOException e) {
            return Promise.reject(null, this, new Object[] {e.getMessage()}, null);
        }
    }

    /**
     * Slices the file.
     */
    @JsxFunction
    public void slice() {
    }

    /**
     * Closes the file.
     */
    @JsxFunction(IE)
    public void msClose() {
    }

    /**
     * Returns the underlying file.
     * @return the underlying file
     */
    public java.io.File getFile() {
        return backend_.getFile();
    }
}
