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

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlPageTest;
import com.gargoylesoftware.htmlunit.util.MimeType;

/**
 * Tests for {@link com.gargoylesoftware.htmlunit.javascript.host.file.Blob}.
 *
 * @author Ronald Brill
 */
@RunWith(BrowserRunner.class)
public class BlobTest extends WebDriverTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"3", MimeType.TEXT_HTML})
    public void properties() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html>\n"
            + "<head><title>foo</title>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var blob = new Blob(['abc'], {type : 'text/html'});\n"

            + "  alert(blob.size);\n"
            + "  alert(blob.type);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"function", "Hello HtmlUnit"},
            FF68 = {"undefined", "TypeError true"},
            IE = {"undefined", "TypeError true"})
    public void text() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "function test() {\n"
                + "  var blob = new Blob(['Hello HtmlUnit'], {type : 'text/html'});\n"

                + "  alert(typeof blob.text);\n"
                + "  try {\n"
                + "    blob.text().then(function(text) { alert(text); });\n"
                + "  } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "}\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("text/plain")
    public void typeTxt() throws Exception {
        type(MimeType.TEXT_PLAIN);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("htmlunit")
    public void typeHtmlUnit() throws Exception {
        type("htmlunit");
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("")
    public void typeEmpty() throws Exception {
        type("");
    }

    private void type(final String type) throws Exception {
        final String html
            = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html>\n"
            + "<head><title>foo</title>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var blob = new Blob(['Hello HtmlUnit'], {type : '" + type + "'});\n"
            + "  alert(blob.type);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("")
    public void typeDefault() throws Exception {
        final String html
            = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html>\n"
            + "<head><title>foo</title>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var blob = new Blob(['Hello HtmlUnit']);\n"
            + "  alert(blob.type);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"0", ""},
            IE = {"0", "", "TypeError true"})
    public void ctorNoArgs() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "  function test() {\n"
                + "    var blob = new Blob();\n"

                + "    alert(blob.size);\n"
                + "    alert(blob.type);\n"
                + "    try {\n"
                + "      blob.text().then(function(text) { alert(text); });\n"
                + "    } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"0", ""})
    public void ctorEmpty() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "  function test() {\n"
                + "    var blob = new Blob([]);\n"

                + "    alert(blob.size);\n"
                + "    alert(blob.type);\n"
                + "    try {\n"
                + "      blob.text().then(function(text) { alert(text); });\n"
                + "    } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"8", "", "HtmlUnit"},
            FF68 = {"8", "", "TypeError true"},
            IE = {"8", "", "TypeError true"})
    public void ctorString() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "  function test() {\n"
                + "    var blob = new Blob(['HtmlUnit']);\n"

                + "    alert(blob.size);\n"
                + "    alert(blob.type);\n"
                + "    try {\n"
                + "      blob.text().then(function(text) { alert(text); });\n"
                + "    } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"8", "application/octet-stream", "HtmlUnit"},
            FF68 = {"8", "application/octet-stream", "TypeError true"},
            IE = {"8", "application/octet-stream", "TypeError true"})
    public void ctorStringWithOptions() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "  function test() {\n"
                + "    var blob = new Blob(['Html', 'Unit'], {type: 'application/octet-stream'});\n"

                + "    alert(blob.size);\n"
                + "    alert(blob.type);\n"
                + "    try {\n"
                + "      blob.text().then(function(text) { alert(text); });\n"
                + "    } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"16", "", "HtmlUnitis great"},
            FF68 = {"16", "", "TypeError true"},
            IE = {"16", "", "TypeError true"})
    public void ctorStrings() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "  function test() {\n"
                + "    var blob = new Blob(['Html', 'Unit', 'is great']);\n"

                + "    alert(blob.size);\n"
                + "    alert(blob.type);\n"
                + "    try {\n"
                + "      blob.text().then(function(text) { alert(text); });\n"
                + "    } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"12", "", "HtmlUnitMMMK"},
            FF68 = {"12", "", "TypeError true"},
            IE = {"12", "", "TypeError true"})
    public void ctorMixed() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
                + "<html>\n"
                + "<head><title>foo</title>\n"
                + "<script>\n"
                + "  function test() {\n"
                + "    var nab = new ArrayBuffer(2);\n"
                + "    var nabv = new Uint8Array(nab, 0, 2);\n"
                + "    nabv.set([77, 77], 0);\n"
                + "    var blob = new Blob(['HtmlUnit',"
                                        + "nab, new Int8Array([77,75])]);\n"

                + "    alert(blob.size);\n"
                + "    alert(blob.type);\n"
                + "    try {\n"
                + "      blob.text().then(function(text) { alert(text); });\n"
                + "    } catch(e) { alert('TypeError ' + (e instanceof TypeError)); }\n"
                + "  }\n"
                + "</script>\n"
                + "</head>\n"
                + "<body onload='test()'>\n"
                + "</body>\n"
                + "</html>";

        loadPageWithAlerts2(html);
    }
}