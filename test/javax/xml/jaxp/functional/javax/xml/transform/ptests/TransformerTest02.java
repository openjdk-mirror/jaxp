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
package javax.xml.transform.ptests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import static javax.xml.transform.ptests.TransformerTestConst.CLASS_DIR;
import static javax.xml.transform.ptests.TransformerTestConst.GOLDEN_DIR;
import static javax.xml.transform.ptests.TransformerTestConst.XML_DIR;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import static jaxp.library.JAXPTestUtilities.compareWithGold;
import static jaxp.library.JAXPTestUtilities.failCleanup;
import static jaxp.library.JAXPTestUtilities.failUnexpected;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Here a transformer is created using DOMSource. Some specific output property
 * is set on transformer. Then transform(StreamSource, StreamResult) is tested.
 */
public class TransformerTest02 {
    /**
     * Unit test for transform(StreamSource, StreamResult).
     */
    @Test
    public void testcase01() {
        String outputFile = CLASS_DIR + "transformer02.out";
        String goldFile = GOLDEN_DIR + "transformer02GF.out";
        String xsltFile = XML_DIR + "cities.xsl";
        String xmlFile = XML_DIR + "cities.xml";

        try (FileInputStream fis = new FileInputStream(xmlFile);
                FileOutputStream fos = new FileOutputStream(outputFile)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new File(xsltFile));
            DOMSource domSource = new DOMSource(document);

            Transformer transformer = TransformerFactory.newInstance().
                    newTransformer(domSource);
            StreamSource streamSource = new StreamSource(fis);
            StreamResult streamResult = new StreamResult(fos);

            transformer.setOutputProperty("indent", "no");
            transformer.transform( streamSource, streamResult);
            assertTrue(compareWithGold(goldFile, outputFile));
        } catch (IOException | IllegalArgumentException
                | ParserConfigurationException | TransformerException
                | SAXException ex) {
            failUnexpected(ex);
        } finally {
            try {
                Path outputPath = Paths.get(outputFile);
                if(Files.exists(outputPath))
                    Files.delete(outputPath);
            } catch (IOException ex) {
                failCleanup(ex, outputFile);
            }
        }
    }
}
