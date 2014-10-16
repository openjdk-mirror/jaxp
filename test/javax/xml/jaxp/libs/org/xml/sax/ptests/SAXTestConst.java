/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.xml.sax.ptests;

import static jaxp.library.JAXPTestUtilities.FILE_SEP;
import static jaxp.library.JAXPTestUtilities.USER_DIR;

/**
 * This is the Base test class provide basic support for JAXP SAX functional
 * test. These are JAXP SAX functional related properties that every test suite
 * has their own TestBase class.
 */
public class SAXTestConst {
    /**
     * Current test directory.
     */
    public static final String CLASS_DIR
            = System.getProperty("test.classes", ".") + FILE_SEP;

    /**
     * Package name that separates by slash.
     */
    public static final String PACKAGE_NAME = FILE_SEP +
            SAXTestConst.class.getPackage().getName().replaceAll("[.]", FILE_SEP);

    /**
     * Test base directory. Every package has its own test package directory.
     */
    public static final String BASE_DIR
            = System.getProperty("test.src", USER_DIR).replaceAll("\\" + System.getProperty("file.separator"), "/")
                + PACKAGE_NAME + FILE_SEP + "..";

    /**
     * Source XML file directory.
     */
    public static final String XML_DIR = BASE_DIR + FILE_SEP + "xmlfiles" + FILE_SEP;

    /**
     * Golden output file directory. We pre-define all expected output in golden
     * output file. Test verifies whether the standard output is same as content
     * of golden file.
     */
    public static final String GOLDEN_DIR = XML_DIR + FILE_SEP + "out" + FILE_SEP;
}
