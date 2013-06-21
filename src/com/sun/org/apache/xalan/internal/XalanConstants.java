/*
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

package com.sun.org.apache.xalan.internal;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;

/**
 * Commonly used constants.
 *
 * @author Huizhe Wang, Oracle
 *
 * @version $Id: Constants.java,v 1.14 2011-06-07 04:39:40 joehw Exp $
 */
public final class XalanConstants {

    //
    // Constants
    //
    // Oracle Feature:
    /**
     * <p>Use Service Mechanism</p>
     *
     * <ul>
     *   <li>
         * {@code true} instruct an object to use service mechanism to
         * find a service implementation. This is the default behavior.
         *   </li>
         *   <li>
         * {@code false} instruct an object to skip service mechanism and
         * use the default implementation for that service.
     *   </li>
     * </ul>
    */

    public static final String ORACLE_FEATURE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";

    /** Oracle JAXP property prefix ("http://www.oracle.com/xml/jaxp/properties/"). */
    public static final String ORACLE_JAXP_PROPERTY_PREFIX =
        "http://www.oracle.com/xml/jaxp/properties/";

    //System Properties corresponding to ACCESS_EXTERNAL_* properties
    public static final String SP_ACCESS_EXTERNAL_STYLESHEET = "javax.xml.accessExternalStylesheet";
    public static final String SP_ACCESS_EXTERNAL_DTD = "javax.xml.accessExternalDTD";


    //all access keyword
    public static final String ACCESS_EXTERNAL_ALL = "all";

    /**
     * Default value when FEATURE_SECURE_PROCESSING (FSP) is set to true
     */
    public static final String EXTERNAL_ACCESS_DEFAULT_FSP = "";
    /**
     * JDK version by which the default is to restrict external connection
     */
    public static final int RESTRICT_BY_DEFAULT_JDK_VERSION = 8;
    /**
     * FEATURE_SECURE_PROCESSING (FSP) is false by default
     */
    public static final String EXTERNAL_ACCESS_DEFAULT = getExternalAccessDefault(false);

    /**
     * Determine the default value of the external access properties
     *
     * jaxp 1.5 does not require implementations to restrict by default
     *
     * For JDK8:
     * The default value is 'file' (including jar:file); The keyword "all" grants permission
     * to all protocols. When {@link javax.xml.XMLConstants#FEATURE_SECURE_PROCESSING} is on,
     * the default value is an empty string indicating no access is allowed.
     *
     * For JDK7:
     * The default value is 'all' granting permission to all protocols. If by default,
     * {@link javax.xml.XMLConstants#FEATURE_SECURE_PROCESSING} is true, it should
     * not change the default value. However, if {@link javax.xml.XMLConstants#FEATURE_SECURE_PROCESSING}
     * is set explicitly, the values of the properties shall be set to an empty string
     * indicating no access is allowed.
     *
     * @param isSecureProcessing indicating if Secure Processing is set
     * @return default value
     */
    public static String getExternalAccessDefault(boolean isSecureProcessing) {
        String defaultValue = "all";
        if (isJDKandAbove(RESTRICT_BY_DEFAULT_JDK_VERSION)) {
            defaultValue = "file";
            if (isSecureProcessing) {
                defaultValue = EXTERNAL_ACCESS_DEFAULT_FSP;
            }
        }
        return defaultValue;
    }

    /*
     * Check the version of the current JDK against that specified in the
     * parameter
     *
     * There is a proposal to change the java version string to:
     * MAJOR.MINOR.FU.CPU.PSU-BUILDNUMBER_BUGIDNUMBER_OPTIONAL
     * This method would work with both the current format and that proposed
     *
     * @param compareTo a JDK version to be compared to
     * @return true if the current version is the same or above that represented
     * by the parameter
     */
    public static boolean isJDKandAbove(int compareTo) {
        String javaVersion = SecuritySupport.getSystemProperty("java.version");
        String versions[] = javaVersion.split("\\.", 3);
        if (Integer.parseInt(versions[0]) >= compareTo ||
            Integer.parseInt(versions[1]) >= compareTo) {
            return true;
        }
        return false;
    }

} // class Constants
