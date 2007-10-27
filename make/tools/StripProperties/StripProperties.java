/*
 * Copyright 2001 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Reads a properties file from standard input and writes an equivalent
 * properties file without comments to standard output.
 */
public class StripProperties {

    public static void main(String[] args) {
        StripProperties sp = new StripProperties();
        boolean ok = sp.run(args);
        if ( !ok ) {
            System.exit(1);
        }
    }

    static interface Log {
        void info(String msg);
        void verbose(String msg);
        void error(String msg, Exception e);
    }

    private String propfiles[];
    private String outfiles[] ;
    private int stripCount = 0;
    private boolean quiet = false;
    private Log log;

    public void setLog(Log log) {
        this.log = log;
    }

    private boolean parseOptions(String args[]) {
        boolean ok = true;
        if ( stripCount > 0 ) {
            String new_propfiles[] = new String[stripCount + args.length];
            String new_outfiles[]  = new String[stripCount + args.length];
            System.arraycopy(propfiles, 0, new_propfiles, 0, stripCount);
            System.arraycopy(outfiles, 0, new_outfiles, 0, stripCount);
            propfiles = new_propfiles;
            outfiles  = new_outfiles;
        } else {
            propfiles = new String[args.length];
            outfiles  = new String[args.length];
        }

        for ( int i = 0; i < args.length ; i++ ) {
            if ( "-strip".equals(args[i]) && i+2 < args.length ) {
                propfiles[stripCount] = args[++i];
                outfiles[stripCount]    = args[++i];
                stripCount++;
            } else if ( "-optionsfile".equals(args[i]) && i+1 < args.length ) {
                String filename = args[++i];
                FileInputStream finput = null;
                byte contents[] = null;
                try {
                    finput = new FileInputStream(filename);
                    int byteCount = finput.available();
                    if ( byteCount <= 0 ) {
                        log.error("The -optionsfile file is empty", null);
                        ok = false;
                    } else {
                        contents = new byte[byteCount];
                        int bytesRead = finput.read(contents);
                        if ( byteCount != bytesRead ) {
                            log.error("Cannot read all of -optionsfile file", null);
                            ok = false;
                        }
                    }
                } catch ( IOException e ) {
                    log.error("cannot open " + filename, e);
                    ok = false;
                }
                if ( finput != null ) {
                    try {
                        finput.close();
                    } catch ( IOException e ) {
                        ok = false;
                        log.error("cannot close " + filename, e);
                    }
                }
                if ( ok = true && contents != null ) {
                    String tokens[] = (new String(contents)).split("\\s+");
                    if ( tokens.length > 0 ) {
                        ok = parseOptions(tokens);
                    }
                }
                if ( !ok ) {
                    break;
                }
        } else if ( "-quiet".equals(args[i]) ) {
                quiet = true;
        } else {
                log.error("argument error", null);
                ok = false;
        }
        }
        return ok;
    }

    private boolean stripFiles(String propertiesPath, String outputPath) {
        boolean ok = true;
        Properties prop = new Properties();
        InputStream in = null;
        try {
                in = new BufferedInputStream(new FileInputStream(propertiesPath));
                prop.load(in);
        } catch ( FileNotFoundException e ) {
                log.error("Cannot access file " + propertiesPath, e);
                ok = false;
        } catch ( IOException e ) {
                log.error("IO exception processing file " + propertiesPath, e);
                ok = false;
        }
        if ( in != null ) {
                try {
                    in.close();
                } catch ( IOException e ) {
                    log.error("IO exception closing file " + propertiesPath, e);
                    ok = false;
                }
        }

        OutputStream out = null;
        try {
                out = new FileOutputStream(outputPath);
                storeProperties(prop, out);
                out.flush();
        } catch ( IOException e ) {
                log.error("IO exception processing file " + outputPath, e);
                e.printStackTrace();
                ok = false;
        }
        if ( out != null ) {
           try {
                    out.close();
           } catch ( IOException e ) {
                    log.error("IO exception closing file " + outputPath, e);
                    ok = false;
           }
        }
        return ok;
    }

    /**
     * Strip the properties filenames supplied, replacing their contents.
     * @param args Names of properties files to process and replace contents
     */
    public boolean run(String args[]) {
        if (log == null) {
            log = new Log() {
                public void error(String msg, Exception e) {
                    System.err.println("ERROR: StripProperties: " + msg);
                    if ( e != null ) {
                        System.err.println("EXCEPTION: " + e.toString());
                        e.printStackTrace();
                    }
                }
                public void info(String msg) {
                    System.out.println(msg);
                }
                public void verbose(String msg) {
                    if (!quiet)
                        System.out.println(msg);
                }
            };
        }

        boolean ok = true;
        ok = parseOptions(args);
        if ( ok && stripCount == 0 ) {
                log.error("options parsed but no files to compile", null);
                ok = false;
        }
        /* Need at least one file. */
        if ( !ok ) {
            //usage(log);
        } else {
            /* Process files */
            for ( int i = 0; i < stripCount && ok ; i++ ) {
                    ok = stripFiles(propfiles[i], outfiles[i]);
            }
        }
        return ok;
    }

    // --- code below here is adapted from java.util.Properties ---

    private static final String specialSaveChars = "=: \t\r\n\f#!";

    /*
     * Converts unicodes to encoded &#92;uxxxx
     * and writes out any of the characters in specialSaveChars
     * with a preceding slash
     */
    private static String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                case '\\':
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                default:
                    if ((aChar < 0x0020) || (aChar == 0x007e) || (aChar > 0x00ff)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        if (specialSaveChars.indexOf(aChar) != -1) {
                            outBuffer.append('\\');
                        }
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    /**
     * Writes the content of <code>properties</code> to <code>out</code>.
     * The format is that of Properties.store with the following modifications:
     * <ul>
     * <li>No header or date is written
     * <li>Latin-1 characters are written as single bytes, not escape sequences
     * <li>Line breaks are indicated by a single \n independent of platform
     * <ul>
     */
    private static void storeProperties(Properties properties, OutputStream out)
    throws IOException {
        BufferedWriter awriter;
        awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
        for (Enumeration e = properties.keys(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            String val = (String)properties.get(key);
            key = saveConvert(key, true);

            /* No need to escape embedded and trailing spaces for value, hence
             * pass false to flag.
             */
            val = saveConvert(val, false);
            writeln(awriter, key + "=" + val);
        }
        awriter.flush();
    }

    private static void writeln(BufferedWriter bw, String s) throws IOException {
        bw.write(s);
        bw.write("\n");
    }

    /**
     * Convert a nibble to a hex character
     * @param   nibble  the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /** A table of hex digits */
    private static final char[] hexDigit = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
}
