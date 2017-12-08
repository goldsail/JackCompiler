package JackCompiler;

import org.w3c.dom.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class JackCodeGenerator {

    private class Symbol {
        String name;
        String type;
        Symbol(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    private ArrayList<Document> docs;
    private Document doc;
    private ArrayList<String> codes;
    private HashMap<String, Integer> fieldMap;
    private HashMap<String, Integer> staticMap;
    private ArrayList<String> classNames;
    private int classIndex;

    public JackCodeGenerator(ArrayList<Document> docs) {
        this.docs = docs;
    }

    public ArrayList<String> generate() throws JackCompilerException {

        this.doc = null;
        this.codes = new ArrayList<String>();
        this.fieldMap = new HashMap<String, Integer>();
        this.staticMap = new HashMap<String, Integer>();
        this.classNames = new ArrayList<String>();
        this.classIndex = 0;

        for (Document doc : this.docs) {
            // loop 1
            // generate staticMap, fieldMap, and classNames
            Element classNameNode = getChildren(doc.getDocumentElement()).get(1);
            classNames.add(classNameNode.getFirstChild().getNodeValue());
        }

        for (Document doc : this.docs) {
            // loop 2
            // generate codes for subroutines in each class
            this.doc = doc;
            procClass(doc.getDocumentElement());
            classIndex++;
        }

        return this.codes;
    }

    private void procClass(Element node) throws JackCompilerException {

    }

    private ArrayList<Symbol> getFields(Element classNode) throws JackCompilerException {
        ArrayList<Symbol> ret = new ArrayList<Symbol>();



        return ret;
    }

    private ArrayList<Symbol> getStatics(Element classNode) throws JackCompilerException {
        ArrayList<Symbol> ret = new ArrayList<Symbol>();



        return ret;
    }

    private static ArrayList<Element> getChildren(Element node) {
        ArrayList<Element> arr = new ArrayList<Element>();
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node temp = list.item(i);
            if (temp instanceof Element) {
                arr.add((Element)temp);
            }
        }
        return arr;
    }

    /**
     * Unit test
     * @param args paths of the input files (*.jack) and output file (*.xml)
     */
    public static void main(String[] args) {
        try {

            ArrayList<Document> docs = new ArrayList<Document>();
            for (int i = 0; i < args.length - 1; i++) {
                docs.add(new JackAnalyzer(new JackTokenizer(args[i])).analyze());
            }

            ArrayList<String> codes = (new JackCodeGenerator(docs)).generate();

            FileWriter fw = new FileWriter(args[args.length - 1]);
            for (String code : codes) {
                fw.write(code);
                fw.write("\r\n");
            }
            fw.flush();
            fw.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
