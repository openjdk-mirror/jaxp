/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class StripPropertiesTask extends MatchingTask {
    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void execute() {
        StripProperties.Log log = new StripProperties.Log() {
            public void error(String msg, Exception e) {
                log(msg, Project.MSG_ERR);
            }
            public void info(String msg) {
                log(msg, Project.MSG_INFO);
            }
            public void verbose(String msg) {
                log(msg, Project.MSG_VERBOSE);
            }
        };
        List<String> mainOpts = new ArrayList<String>();
        int count = 0;
        DirectoryScanner s = getDirectoryScanner(srcDir);
        for (String path: s.getIncludedFiles()) {
            if (path.endsWith(".properties")) {
                File srcFile = new File(srcDir, path);
                File destFile = new File(destDir, path);
                // Arguably, the comparison in the next line should be ">", not ">="
                // but that assumes the resolution of the last modified time is fine
                // grained enough; in practice, it is better to use ">=".
                if (destFile.exists() && destFile.lastModified() >= srcFile.lastModified())
                    continue;
                destFile.getParentFile().mkdirs();
                mainOpts.add("-strip");
                mainOpts.add(srcFile.getPath());
                mainOpts.add(destFile.getPath());
                count++;
            }
        }
        if (mainOpts.size() > 0) {
            log("Generating " + count + " resource files to " + destDir, Project.MSG_INFO);
            StripProperties sp = new StripProperties();
            sp.setLog(log);
            boolean ok = sp.run((String[])mainOpts.toArray(new String[mainOpts.size()]));
            if (!ok)
                throw new BuildException("StripProperties failed.");
        }
    }

    private File srcDir;
    private File destDir;
}
