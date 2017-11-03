package JackCompiler;

import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;

public class JackCompilationEngine {
    private JackTokenizer tokenizer;
    private Document doc;

    private HashSet<Byte> operators;
    private HashSet<Byte> unaryOperators;
    private HashMap<JackTokenizer.KeyWord, String> keywordConstants;

    public JackCompilationEngine(JackTokenizer tokenizer) {
        try {
            this.tokenizer = tokenizer;

            operators = new HashSet<Byte>();
            operators.add((byte)'+');
            operators.add((byte)'-');
            operators.add((byte)'*');
            operators.add((byte)'/');
            operators.add((byte)'&');
            operators.add((byte)'|');
            operators.add((byte)'<');
            operators.add((byte)'>');
            operators.add((byte)'=');

            unaryOperators = new HashSet<Byte>();
            unaryOperators.add((byte)'-');
            unaryOperators.add((byte)'~');

            keywordConstants = new HashMap<JackTokenizer.KeyWord, String>();
            keywordConstants.put(JackTokenizer.KeyWord.TRUE, "true");
            keywordConstants.put(JackTokenizer.KeyWord.FALSE, "false");
            keywordConstants.put(JackTokenizer.KeyWord.NULL, "null");
            keywordConstants.put(JackTokenizer.KeyWord.THIS, "this");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Document compile() {

        try {
            compileClassNotDelayToken();
            return this.doc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void writeXML(Document document, String filename) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result =  new StreamResult(new StringWriter());

            //t.setParameter(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);

            //writing to file
            FileOutputStream fop = null;
            File file;
            try {

                file = new File(filename);
                fop = new FileOutputStream(file);

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                // get the content in bytes
                String xmlString = result.getWriter().toString();
                // System.out.println(xmlString);
                byte[] contentInBytes = xmlString.getBytes();

                fop.write(contentInBytes);
                fop.flush();
                fop.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fop != null) {
                        fop.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void _assert(boolean condition) throws JackCompilerException{
        if (!condition) {
            throw new JackCompilerException();
        }
    }

    private void assertIdentifier(boolean condition) throws JackCompilerException {
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.IDENTIFIER && condition);
    }

    private void assertSymbol(boolean condition) throws JackCompilerException {
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.SYMBOL && condition);
    }

    private void assertKeyword(boolean condition) throws JackCompilerException {
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.KEYWORD && condition);
    }

    private void assertIntConst(boolean condition) throws JackCompilerException {
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.INT_CONST && condition);
    }

    private void assertStringConst(boolean condition) throws JackCompilerException {
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.STRING_CONST && condition);
    }

    private Element textElement(String tagName, String text) {
        Element t1 = this.doc.createElement(tagName);
        Text t2 = this.doc.createTextNode(text);
        t1.appendChild(t2);
        return t1;
    }

    private void compileClassNotDelayToken() throws Exception {
        this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element currNode = doc.createElement("class");

        tokenizer.advance();
        assertKeyword(tokenizer.getKeyWord() == JackTokenizer.KeyWord.CLASS);

        tokenizer.advance();
        assertIdentifier(true);
        String className = tokenizer.getIdentifier();
        currNode.appendChild(textElement("className", className));

        tokenizer.advance();
        assertSymbol(tokenizer.getSymbol() == '{');

        tokenizer.advance();
        compileClassVarDec(currNode);

        compileSubroutineDec(currNode);

        assertSymbol(tokenizer.getSymbol() == '}');

        doc.appendChild(currNode);
    }

    private void compileClassVarDec(Element currNode) throws JackCompilerException {

        Element newNode = doc.createElement("classVarDecs");

        boolean continueFlag = true;

        while (continueFlag && tokenizer.getTokenType() == JackTokenizer.TokenType.KEYWORD
                && (tokenizer.getKeyWord() == JackTokenizer.KeyWord.STATIC || tokenizer.getKeyWord() == JackTokenizer.KeyWord.FIELD)) {

            Element decNode = doc.createElement("classVarDec");

            if (tokenizer.getKeyWord() == JackTokenizer.KeyWord.STATIC) {
                decNode.appendChild(textElement("type", "static"));
            } else {
                decNode.appendChild(textElement("type", "field"));
            }

            String typeName = compileType();
            decNode.appendChild(textElement("typeName", typeName));

            String varName = compileIdentifier();
            decNode.appendChild(textElement("varName", varName));

            assertSymbol(tokenizer.getSymbol() == ',' || tokenizer.getSymbol() == ';');

            continueFlag = (tokenizer.getSymbol() == ',');

            newNode.appendChild(decNode);
        }

        currNode.appendChild(newNode);
        tokenizer.advance();
    }

    private void compileSubroutineDec(Element currNode) throws JackCompilerException {

    }

    private String compileType() throws JackCompilerException {
        String ret = null;
        switch (tokenizer.getTokenType()) {
            case KEYWORD:
                switch (tokenizer.getKeyWord()) {
                    case INT:
                        ret = "int";
                        break;
                    case BOOLEAN:
                        ret = "boolean";
                        break;
                    case CHAR:
                        ret = "char";
                        break;
                }
                break;
            case IDENTIFIER:
                ret = tokenizer.getIdentifier();
                break;
        }
        _assert(ret != null);
        tokenizer.advance();
        return ret;
    }

    private String compileVoidOrType() throws JackCompilerException {
        String ret = null;
        if (tokenizer.getTokenType() == JackTokenizer.TokenType.KEYWORD
                && tokenizer.getKeyWord() == JackTokenizer.KeyWord.VOID) {
            ret = "void";
            tokenizer.advance();
        } else {
            ret = compileType();
        }
        return ret;
    }

    private String compileIdentifier() throws JackCompilerException {
        String ret = null;
        assertIdentifier(true);
        ret = tokenizer.getIdentifier();
        tokenizer.advance();
        return ret;
    }
}
