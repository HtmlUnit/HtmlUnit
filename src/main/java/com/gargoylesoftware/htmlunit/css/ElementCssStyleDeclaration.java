/*
 * Copyright (c) 2002-2021 Gargoyle Software Inc.
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
package com.gargoylesoftware.htmlunit.css;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.DomElement;

/**
 * A non-js CSSStyleDeclaration for a html element
 *
 */
public class ElementCssStyleDeclaration implements CssStyleDeclaration {

    private final DomElement element;

    /**
     * Creates an instance and sets its parent scope to the one of the provided element.
     * @param element the element to which this style is bound
     */
    ElementCssStyleDeclaration(DomElement element){
        this.element = element;
    }

    protected DomElement getDomElement() {
        return element;
    }

    public BrowserVersion getBrowserVersion() {
        return element.getPage().getWebClient().getBrowserVersion();
    }

    @Override
    public StyleElement getStyleElement(final String name) {
        return element.getStyleElement(name);
    }

    @Override
    public void setStyleAttribute(String name, String newValue, String important) {
        if (null == newValue || "null".equals(newValue)) {
            newValue = "";
        }
        element.replaceStyleAttribute(name, newValue, important);
    }

    @Override
    public String removeStyleAttribute(String name) {
        return element.removeStyleAttribute(name);
    }

    @Override
    public String getCssText() {
        return element.getAttributeDirect("style");
    }

    @Override
    public void setCssText(String value) {
        element.setAttribute("style", value);
    }

    @Override
    public String toString() {
        final String style = element.getAttributeDirect("style");
        return "CSSStyleDeclaration for '" + style + "'";
    }
}