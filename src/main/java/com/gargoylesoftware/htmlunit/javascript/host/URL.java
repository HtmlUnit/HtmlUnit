/*
 * Copyright (c) 2002-2021 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.javascript.host;

import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.CHROME;
import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.EDGE;
import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.FF;
import static com.gargoylesoftware.htmlunit.javascript.configuration.SupportedBrowser.FF78;

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

import com.gargoylesoftware.htmlunit.javascript.SimpleScriptable;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxClass;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxConstructor;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxFunction;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxGetter;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxSetter;
import com.gargoylesoftware.htmlunit.javascript.configuration.JsxStaticFunction;
import com.gargoylesoftware.htmlunit.javascript.host.file.File;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import net.sourceforge.htmlunit.corejs.javascript.Context;
import net.sourceforge.htmlunit.corejs.javascript.ScriptRuntime;
import net.sourceforge.htmlunit.corejs.javascript.Undefined;

/**
 * A JavaScript object for {@code URL}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
@JsxClass
public class URL extends SimpleScriptable {

    private java.net.URL url_;

    /**
     * Creates an instance.
     */
    public URL() {
    }

    /**
     * Creates an instance.
     * @param url a string representing an absolute or relative URL.
     * If url is a relative URL, base is required, and will be used
     * as the base URL. If url is an absolute URL, a given base will be ignored.
     * @param base a string representing the base URL to use in case url
     * is a relative URL. If not specified, it defaults to ''.
     */
    @JsxConstructor({CHROME, EDGE, FF, FF78})
    public URL(final String url, final Object base) {
        String baseStr = null;
        if (!Undefined.isUndefined(base)) {
            baseStr = Context.toString(base);
        }

        try {
            if (StringUtils.isBlank(baseStr)) {
                url_ = UrlUtils.toUrlUnsafe(url);
            }
            else {
                final java.net.URL baseUrl = UrlUtils.toUrlUnsafe(baseStr);
                url_ = new java.net.URL(baseUrl, url);
            }
        }
        catch (final MalformedURLException e) {
            throw ScriptRuntime.typeError(e.toString());
        }
    }

    /**
     * The URL.createObjectURL() static method creates a DOMString containing a URL
     * representing the object given in parameter.
     * The URL lifetime is tied to the document in the window on which it was created.
     * The new object URL represents the specified File object or Blob object.
     *
     * @param fileOrBlob Is a File object or a Blob object to create a object URL for.
     * @return the url
     */
    @JsxStaticFunction
    public static String createObjectURL(final Object fileOrBlob) {
        if (fileOrBlob instanceof File) {
            final File file = (File) fileOrBlob;
            return file.getFile().toURI().normalize().toString();
        }

        return null;
    }

    /**
     * @param objectURL String representing the object URL that was
     *          created by calling URL.createObjectURL().
     */
    @JsxStaticFunction
    public static void revokeObjectURL(final Object objectURL) {
    }

    /**
     * @return hash property of the URL containing a '#' followed by the fragment identifier of the URL.
     */
    @JsxGetter
    public String getHash() {
        if (url_ == null) {
            return null;
        }
        String ref = url_.getRef();
        return ref == null ? "" : "#" + ref;
    }

    @JsxSetter
    public void setHash(String fragment) throws MalformedURLException {
        if (url_ == null) {
            return;
        }
        url_ = UrlUtils.getUrlWithNewRef(url_, fragment.isEmpty() ? null : fragment);
    }

    /**
     * @return the host, that is the hostname, and then, if the port of the URL is nonempty,
     * a ':', followed by the port of the URL.
     */
    @JsxGetter
    public String getHost() {
        if (url_ == null) {
            return null;
        }
        int port = url_.getPort();
        return url_.getHost() + (port > 0 ? ":" + port : "");
    }

    @JsxSetter
    public void setHost(String host) throws MalformedURLException {
        if (url_ == null || host.isEmpty()) {
            return;
        }
        url_ = UrlUtils.getUrlWithNewHost(url_, host);
        checkRemoveRedundantPort();
    }

    /** Removes port if it can be deduced from protocol **/
    private void checkRemoveRedundantPort() throws MalformedURLException {
        if (("https".equals(url_.getProtocol()) && url_.getPort() == 443)
                || ("http".equals(url_.getProtocol()) && url_.getPort() == 80)) {
            url_ = UrlUtils.getUrlWithNewPort(url_, -1);
        }
    }

    /**
     * @return the host, that is the hostname, and then, if the port of the URL is nonempty,
     * a ':', followed by the port of the URL.
     */
    @JsxGetter
    public String getHostname() {
        if (url_ == null) {
            return null;
        }

        return url_.getHost();
    }

    @JsxSetter
    public void setHostname(String hostname) throws MalformedURLException {
        if (url_ == null || hostname.isEmpty()) {
            return;
        }

        url_ = UrlUtils.getUrlWithNewHost(url_, hostname);
    }

    /**
     * @return whole URL
     */
    @JsxGetter
    public String getHref() {
        if (url_ == null) {
            return null;
        }

        return jsToString();
    }

    @JsxSetter
    public void setHref(String href) throws MalformedURLException {
        if (url_ == null) {
            return;
        }

        url_ = UrlUtils.toUrlUnsafe(href);
    }

    /**
     * @return the origin
     */
    @JsxGetter
    public Object getOrigin() {
        if (url_ == null) {
            return null;
        }

        return url_.getProtocol() + "://" + url_.getHost();
    }

    /**
     * @return a URLSearchParams object allowing access to the GET decoded query arguments contained in the URL.
     */
    @JsxGetter
    public URLSearchParams getSearchParams() {
        if (url_ == null) {
            return null;
        }

        return new URLSearchParams(url_.getQuery());
    }

    /**
     * @return the password specified before the domain name.
     */
    @JsxGetter
    public String getPassword() {
        if (url_ == null) {
            return null;
        }

        String userInfo = url_.getUserInfo();
        int idx = userInfo == null ? -1 : userInfo.indexOf(':');
        return idx == -1 ? "" : userInfo.substring(idx + 1);
    }

    @JsxSetter
    public void setPassword(String password) throws MalformedURLException {
        if (url_ == null) {
            return;
        }

        url_ = UrlUtils.getUrlWithNewUserPassword(url_, password.isEmpty() ? null : password);
    }

    /**
     * @return a URLSearchParams object allowing access to the GET decoded query arguments contained in the URL.
     */
    @JsxGetter
    public String getPathname() {
        if (url_ == null) {
            return null;
        }

        String path = url_.getPath();
        return path.isEmpty() ? "/" : path;
    }

    @JsxSetter
    public void setPathname(String path) throws MalformedURLException {
        if (url_ == null) {
            return;
        }

        url_ = UrlUtils.getUrlWithNewPath(url_, path.startsWith("/") ? path : "/" + path);
    }

    /**
     * @return the port number of the URL. If the URL does not contain an explicit port number,
     * it will be set to ''
     */
    @JsxGetter
    public String getPort() {
        if (url_ == null) {
            return null;
        }

        int port = url_.getPort();
        return port == -1 ? "" : Integer.toString(port);
    }

    @JsxSetter
    public void setPort(String port) throws MalformedURLException {
        if (url_ == null) {
            return;
        }
        int portInt = port.isEmpty() ? -1 : Integer.parseInt(port);
        url_ = UrlUtils.getUrlWithNewPort(url_, portInt);
        checkRemoveRedundantPort();
    }

    /**
     * @return the protocol scheme of the URL, including the final ':'.
     */
    @JsxGetter
    public String getProtocol() {
        if (url_ == null) {
            return null;
        }
        String protocol = url_.getProtocol();
        return protocol.isEmpty() ? "" : (protocol + ":");
    }

    @JsxSetter
    public void setProtocol(String protocol) throws MalformedURLException {
        if (url_ == null || protocol.isEmpty()) {
            return;
        }
        url_ = UrlUtils.getUrlWithNewProtocol(url_, protocol);
    }

    /**
     * @return the query string containing a '?' followed by the parameters of the URL
     */
    @JsxGetter
    public String getSearch() {
        if (url_ == null) {
            return null;
        }
        String search = url_.getQuery();
        return search == null ? "" : search;
    }

    @JsxSetter
    public void setSearch(String search) throws MalformedURLException {
        if (url_ == null) {
            return;
        }
        url_ = UrlUtils.getUrlWithNewQuery(url_, search.isEmpty() ? null : search);
    }

    /**
     * @return the username specified before the domain name.
     */
    @JsxGetter
    public String getUsername() {
        if (url_ == null) {
            return null;
        }
        String userInfo = url_.getUserInfo();
        int colonIdx = userInfo == null ? -1 : userInfo.indexOf(':');
        return colonIdx == -1 ? "" : userInfo.substring(0, colonIdx);
    }

    @JsxSetter
    public void setUsername(String username) throws MalformedURLException {
        if (url_ == null) {
            return;
        }
        url_ = UrlUtils.getUrlWithNewUserName(url_, username.isEmpty() ? null : username);
    }

    /**
     * Calls for instance for implicit conversion to string.
     * @see com.gargoylesoftware.htmlunit.javascript.SimpleScriptable#getDefaultValue(java.lang.Class)
     * @param hint the type hint
     * @return the default value
     */
    @Override
    public Object getDefaultValue(final Class<?> hint) {
        if (url_ == null) {
            return super.getDefaultValue(hint);
        }

        if (StringUtils.isEmpty(url_.getPath())) {
            return url_.toExternalForm() + "/";
        }
        return url_.toExternalForm();
    }

    /**
     * @return a serialized version of the URL,
     * although in practice it seems to have the same effect as URL.toString().
     */
    @JsxFunction(functionName = "toJSON")
    public String toJSON() {
        return jsToString();
    }

    /**
     * Returns the text of the URL.
     * @return the text
     */
    @JsxFunction(functionName = "toString")
    public String jsToString() {
        if (StringUtils.isEmpty(url_.getPath())) {
            return url_.toExternalForm() + "/";
        }
        return url_.toExternalForm();
    }
}
