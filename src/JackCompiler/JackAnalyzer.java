package JackCompiler;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Jack Analyzer
 * reference: pp. 217-218 textbook
 */
public class JackAnalyzer {

    private JackTokenizer tokenizer;
    private Document xmlDocument;

    public JackAnalyzer(JackTokenizer tokenizer) {
        try {
            this.tokenizer = tokenizer;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Document analyze() {

        try {
            this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            procClass();

            // deep copy
            return this.xmlDocument;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void _assert(boolean condition) throws JackCompilerException{
        if (!condition) {
            throw new JackCompilerException();
        }
    }

    private Element _createTextElement(String tagName, String text, Document doc) {
        Element t1 = doc.createElement(tagName);
        Text t2 = doc.createTextNode(text);
        t1.appendChild(t2);
        return t1;
    }

    private void procClass() throws JackCompilerException {
        tokenizer.advance();
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.KEYWORD
                && tokenizer.getKeyWord() == JackTokenizer.KeyWord.CLASS);
        tokenizer.advance();
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.IDENTIFIER);
        String className = tokenizer.getIdentifier();

        tokenizer.advance();
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.SYMBOL
                && tokenizer.getSymbol() == '{');

        Element currNode = xmlDocument.createElement("class");
        currNode.appendChild(_createTextElement("keyword", " class ", xmlDocument));
        currNode.appendChild(_createTextElement("identifier", " " + className + " ", xmlDocument));
        currNode.appendChild(_createTextElement("symbol", " { ", xmlDocument));

        procClassVarDecOrSubroutineDec(currNode);

        currNode.appendChild(_createTextElement("symbol", " } ", xmlDocument));
        xmlDocument.appendChild(currNode);
    }

    private void procClassVarDecOrSubroutineDec(Element currNode) throws JackCompilerException {
        tokenizer.advance();
        if (tokenizer.getTokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.getSymbol() == '}') {
            return;
        }
        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.KEYWORD);

        Element newNode = null;

        switch (tokenizer.getKeyWord()) {
            case STATIC:
            case FIELD:
                newNode = xmlDocument.createElement("classVarDec");

                switch (tokenizer.getKeyWord()) {
                    case STATIC:
                        newNode.appendChild(_createTextElement("keyword", " static ", xmlDocument));
                        break;
                    case FIELD:
                        newNode.appendChild(_createTextElement("keyword", " field ", xmlDocument));
                        break;
                }

                procType(newNode);

                tokenizer.advance();
                _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.IDENTIFIER);
                newNode.appendChild(_createTextElement("identifier", " " + tokenizer.getIdentifier() + " ", xmlDocument));

                while (true) {
                    tokenizer.advance();
                    _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.SYMBOL);
                    if (tokenizer.getSymbol() == ';') {
                        newNode.appendChild(_createTextElement("symbol", " ; ", xmlDocument));
                        break;
                    } else {
                        _assert(tokenizer.getSymbol() == ',');
                        newNode.appendChild(_createTextElement("symbol", " , ", xmlDocument));
                        tokenizer.advance();
                        _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.IDENTIFIER);
                        newNode.appendChild(_createTextElement("identifier", " " + tokenizer.getIdentifier() + " ", xmlDocument));
                    }
                }

                break;
            case CONSTRUCTOR:
            case FUNCTION:
            case METHOD:
                newNode = xmlDocument.createElement("subroutineDec");

                switch (tokenizer.getKeyWord()) {
                    case CONSTRUCTOR:
                        newNode.appendChild(_createTextElement("keyword", " constructor ", xmlDocument));
                        break;
                    case FUNCTION:
                        newNode.appendChild(_createTextElement("keyword", " function ", xmlDocument));
                        break;
                    case METHOD:
                        newNode.appendChild(_createTextElement("keyword", " method ", xmlDocument));
                        break;
                }

                procTypeOrVoid(newNode);

                tokenizer.advance();
                _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.IDENTIFIER);
                newNode.appendChild(_createTextElement("identifier", " " + tokenizer.getIdentifier() + " ", xmlDocument));

                tokenizer.advance();
                _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.getSymbol() == '(');
                newNode.appendChild(_createTextElement("symbol", " ( ", xmlDocument));

                procParameterList(newNode);

                tokenizer.advance();
                _assert(tokenizer.getTokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.getSymbol() == ')');
                newNode.appendChild(_createTextElement("symbol", " ) ", xmlDocument));

                procSubroutineBody(newNode);

                break;
            default:
                _assert(false);
        }

        currNode.appendChild(newNode);
        procClassVarDecOrSubroutineDec(currNode);
    }

    private void procType(Element currNode) {

    }

    private void procTypeOrVoid(Element currNode) {

    }

    private void procParameterList(Element currNode) {

    }

    private void procSubroutineBody(Element currNode) {

    }

    private static void writeXML(Document document, String filename) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result =  new StreamResult(new StringWriter());

            //t.setParameter(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
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
                System.out.println(xmlString);
                byte[] contentInBytes = xmlString.getBytes();

                fop.write(contentInBytes);
                fop.flush();
                fop.close();

                System.out.println("Done");

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

    public static void main(String[] args) {
        try {
            JackAnalyzer test = new JackAnalyzer(new JackTokenizer("C:/Users/kingi/Desktop/JackTest/test.jack"));
            Document doc = test.analyze();

            writeXML(doc, "C:/Users/kingi/Desktop/JackTest/test.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
