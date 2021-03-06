/**
 * Created by Salah on 25/02/2016.
 */

//import org.jdom.*;
//import org.jdom.filter.*;
//import org.jdom.input.*;
//import org.jdom.output.Format;
//import org.jdom.output.XMLOutputter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreatHTML {

    public static void main(String[] args) {
        /**
         *
         * * Liste des argumments necessaire
         *
         * 0 dossier contenant .xml
         * 1 html destination (nom compris)
         * 2 xsl
         */
        String xsl = args[2];
        try {
            String xmlFile = testMerge(getAllXML(args[0], ".\\.xml" ), args[0]);
            System.out.println(xmlFile);
            //TODO chimin du html
            traitement(xmlFile ,args[1], xsl);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }



    }

    public static List<String> getAllXML(String path, String regx ) {


        Pattern p = Pattern.compile(regx);
        String[] s = new File(path).list();
        List<String> listeFichiers = new ArrayList<>();
        for (String value : s) {
            Matcher m = p.matcher(value);
            if (m.find()) {
                listeFichiers.add(value);
            }
        }

        return listeFichiers;
    }

    public static String testMerge(List<String> allXML, String path) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        String fileName = "final.xml";
        // supression du fichier s'il existe deja
        File f = new File(path + fileName);
        f.delete();




        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);


        allXML.remove("final.xml");

        for(String tmp : allXML) {


            Document doc1 = docBuilder.parse(new File(path + tmp));

            NodeList nodes1 = doc1.getElementsByTagName("Mutation");

            for (int i = 0; i < nodes1.getLength(); i = i + 1) {

                Node n = doc.importNode(nodes1.item(i), true);
                rootElement.appendChild(n);

            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        Writer output = null;
        output = new BufferedWriter(new FileWriter(path +fileName));
        String xmlOutput = result.getWriter().toString();
        output.write(xmlOutput);
        output.close();


        System.out.println("merge complete");
        return path + fileName;
    }

    public static void traitement(String xmlFile, String resultPath, String xslFile ) {
        try
        {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Source xmlDoc = new StreamSource(xmlFile);
            Source xslDoc = new StreamSource(xslFile);

            String outputFileName = resultPath;
            OutputStream htmlFile = new FileOutputStream(outputFileName);

            Transformer transformer = tFactory.newTransformer(xslDoc);
            transformer.transform(xmlDoc, new StreamResult(htmlFile));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
