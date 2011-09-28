/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: FeatureState.java 3024 2011-03-01 03:46:13Z joehw $
 */

package com.sun.org.apache.xerces.internal.util;

public class FeatureState {

    public final Status status;
    public final boolean state;

    public static final FeatureState SET_ENABLED = new FeatureState(Status.SET, true);
    public static final FeatureState SET_DISABLED = new FeatureState(Status.SET, false);
    public static final FeatureState UNKNOWN = new FeatureState(Status.UNKNOWN, false);
    public static final FeatureState RECOGNIZED = new FeatureState(Status.RECOGNIZED, false);
    public static final FeatureState NOT_SUPPORTED = new FeatureState(Status.NOT_SUPPORTED, false);
    public static final FeatureState NOT_RECOGNIZED = new FeatureState(Status.NOT_RECOGNIZED, false);
    public static final FeatureState NOT_ALLOWED = new FeatureState(Status.NOT_ALLOWED, false);

    public FeatureState(Status status, boolean state) {
        this.status = status;
        this.state = state;
    }

    public static FeatureState of(Status status) {
        return new FeatureState(status, false);
    }

    public static FeatureState is(boolean value) {
        return new FeatureState(Status.SET, value);
    }

    public boolean isExceptional() {
        return this.status.isExceptional();
    }
}
