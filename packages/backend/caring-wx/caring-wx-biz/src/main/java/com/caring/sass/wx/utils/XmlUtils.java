package com.caring.sass.wx.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public final class XmlUtils {
    public static final EntityResolver NONE_DTD_RESOLVER = new EntityResolver() {
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
        }
    };
    private DOMReader domReader;
    private SAXReader saxReader;

    public SAXReader createSAXReader(String file, List errorsList, EntityResolver entityResolver) {
        if (this.saxReader == null) {
            this.saxReader = new SAXReader();
        }

        this.saxReader.setEntityResolver(NONE_DTD_RESOLVER);
        this.saxReader.setErrorHandler(new XmlUtils.ErrorLogger(file, errorsList));
        this.saxReader.setMergeAdjacentText(true);
        this.saxReader.setValidation(false);
        return this.saxReader;
    }

    public String Dom2String(Document doc) {
        XMLWriter writer = null;

        try {
            StringWriter sw = new StringWriter();
            writer = new XMLWriter(sw);
            writer.write(doc);
            String var4 = sw.toString();
            return var4;
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception var13) {
                }
            }

        }

        return null;
    }

    public String Dom2String(Document doc, String encoding) {
        XMLWriter writer = null;

        try {
            StringWriter sw = new StringWriter();
            writer = new XMLWriter(sw);
            writer.write(doc);
            String xml = sw.toString();
            String pattern = ">";
            int idx = xml.indexOf(pattern);
            String last = xml.substring(idx + 1);
            xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + last;
            String var9 = xml;
            return var9;
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception var18) {
                }
            }

        }

        return null;
    }

    public void saveXml2File(String xml, String fileName) {
        File f = new File(fileName);
        this.saveXml2File(xml, f);
    }

    public void saveXml2File(String xml, File file) {
        try {
            Document doc = this.load(xml);
            this.saveXml2File(doc, file);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void saveXml2File(Document doc, File file) {
        XMLWriter writer = null;

        try {
            FileWriter fw = new FileWriter(file);
            writer = new XMLWriter(fw);
            writer.write(doc);
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception var12) {
                }
            }

        }

    }

    public void saveXml2File(Document doc, File file, String encoding) {
        Object writer = null;

        try {
            String sXMLContent = this.Dom2String(doc, "GBK");
            FileWriter filer = new FileWriter(file);
            filer.write(sXMLContent);
            filer.close();
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    ((XMLWriter)writer).close();
                } catch (Exception var14) {
                }
            }

        }

    }

    public Document load(InputStream in) throws DocumentException {
        return this.createSAXReader("XML InputStream", new ArrayList(), (EntityResolver)null).read(new InputSource(in));
    }

    public Document load(String xml) throws DocumentException {
        return this.createSAXReader("XML InputStream", new ArrayList(), (EntityResolver)null).read(new StringReader(xml));
    }

    public DOMReader createDOMReader() {
        if (this.domReader == null) {
            this.domReader = new DOMReader();
        }

        return this.domReader;
    }

    public static Element generateDom4jElement(String elementName) {
        return DocumentFactory.getInstance().createElement(elementName);
    }

    public static void removeChildren(Element el) {
        Iterator iter = el.elements().iterator();

        while(iter.hasNext()) {
            el.remove((Element)iter.next());
        }

    }

    public static class ErrorLogger implements ErrorHandler {
        private List errors;

        ErrorLogger(String file, List errors) {
            this.errors = errors;
        }

        public void error(SAXParseException error) {
            this.errors.add(error);
        }

        public void fatalError(SAXParseException error) {
            this.error(error);
        }

        public void warning(SAXParseException warn) {
        }
    }
}
