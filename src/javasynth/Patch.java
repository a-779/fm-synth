package javasynth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author a
 */
public class Patch {
    final String PATCH_PATH = "patches/";
    ArrayList<String> patches = new ArrayList<>();
    
    public int currentPatch = 0;
    
    // xml tags
    private final String PATCH = "patch";
    private final String OPERATOR = "operator";
    private final String CARRIER = "carrier";
    private final String RATIO = "ratio";
    private final String MODULATORS = "modulators";
    private final String MODULATIONS_SENS = "modulationSens";
    private final String FEEDBACK_SENS = "feedbackSens";
    private final String A = "a";
    private final String D = "d";
    private final String S = "s";
    private final String R = "r";
    private final String VOLUME = "volume";
    
    public Patch() {
        getPatches();
    }
    
    private void getPatches() {   
        File patchFolder = new File(PATCH_PATH);                
        
        for (File patch : patchFolder.listFiles()) {
            patches.add(patch.getName());
        }
    }

    public Operator[] get() {
        if (currentPatch >= patches.size()) {
            currentPatch = 0;
        } else if (currentPatch < 0) {
            currentPatch = patches.size() - 1;
        } 
        
        try {
            File patch = new File(PATCH_PATH + patches.get(currentPatch));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(patch);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(OPERATOR);

            
            Operator algorithm[] = new Operator[nodeList.getLength()];
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)node;
                    
                    boolean carrier = Boolean.parseBoolean(element.getElementsByTagName(CARRIER).item(0).getTextContent());
                    
                    int ratio = Integer.parseInt(element.getElementsByTagName(RATIO).item(0).getTextContent());
                    
                    String modulatorsString = element.getElementsByTagName(MODULATORS).item(0).getTextContent();
                    String modulatorsStringArray[] = modulatorsString.split(",");
                    int modulators[];
           
                    if (modulatorsStringArray[0].equals("")) {
                        modulators = new int[0];
                    } else  {
                        modulators = new int[modulatorsStringArray.length];
                        
                        for (int j = 0; j < modulators.length; j++) {
                            modulators[j] = Integer.parseInt(modulatorsStringArray[j]);
                        }
                    }                    
                    
                    String modulationSensString = element.getElementsByTagName(MODULATIONS_SENS).item(0).getTextContent();
                    String modulationSensStringArray[] = modulationSensString.split(",");
                    double modulationSens[];
           
                    if (modulationSensStringArray[0].equals("")) {
                        modulationSens = new double[0];
                    } else  {
                        modulationSens = new double[modulationSensStringArray.length];
                        
                        for (int j = 0; j < modulationSens.length; j++) {
                            modulationSens[j] = Double.parseDouble(modulationSensStringArray[j]);
                        }
                    }

                    double feedbackSens = Double.parseDouble(element.getElementsByTagName(FEEDBACK_SENS).item(0).getTextContent());
                    
                    double a = Double.parseDouble(element.getElementsByTagName(A).item(0).getTextContent());
                    
                    double d = Double.parseDouble(element.getElementsByTagName(D).item(0).getTextContent());
                    
                    double s = Double.parseDouble(element.getElementsByTagName(S).item(0).getTextContent());
                    
                    double r = Double.parseDouble(element.getElementsByTagName(R).item(0).getTextContent());
                    
                    double volume = Double.parseDouble(element.getElementsByTagName(VOLUME).item(0).getTextContent());
                    
                    algorithm[i] = new Operator(carrier, ratio, modulators, modulationSens, feedbackSens, a, d, s, r, volume);
                }
            }
            
            return algorithm;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return null; // change  this
        }
    }
    
    public void save(boolean newfile, Operator[] algorithm) {
        try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement(PATCH);
        doc.appendChild(root);

        for (int i = 0; i < algorithm.length; i++) {
            System.out.println(i);

            Element operator = doc.createElement(OPERATOR);
            root.appendChild(operator);

            Element carrier = doc.createElement(CARRIER);
            carrier.appendChild(doc.createTextNode(Boolean.toString(algorithm[i].carrier)));
            operator.appendChild(carrier);

            Element ratio = doc.createElement(RATIO);
            ratio.appendChild(doc.createTextNode(Integer.toString(algorithm[i].ratio)));
            operator.appendChild(ratio);

            // Modulators string
            StringBuilder modulatorsString = new StringBuilder();
            for (int j = 0; j < algorithm[i].modulators.length; j++) {
                modulatorsString.append(algorithm[i].modulators[j]).append(",");
            }
            if (modulatorsString.length() > 0) {
                modulatorsString.setLength(modulatorsString.length() - 1); // remove last comma
            }

            Element modulators = doc.createElement(MODULATORS);
            modulators.appendChild(doc.createTextNode(modulatorsString.toString()));
            operator.appendChild(modulators);

            // Modulation sensitivity string
            StringBuilder modulationSensString = new StringBuilder();
            for (int j = 0; j < algorithm[i].modulationSens.length; j++) {
                modulationSensString.append(algorithm[i].modulationSens[j]).append(",");
            }
            if (modulationSensString.length() > 0) {
                modulationSensString.setLength(modulationSensString.length() - 1);
            }

            Element modulationSens = doc.createElement(MODULATIONS_SENS);
            modulationSens.appendChild(doc.createTextNode(modulationSensString.toString()));
            operator.appendChild(modulationSens);

            Element feedbackSens = doc.createElement(FEEDBACK_SENS);
            feedbackSens.appendChild(doc.createTextNode(Double.toString(algorithm[i].feedbackSens)));
            operator.appendChild(feedbackSens);

            Element a = doc.createElement(A);
            a.appendChild(doc.createTextNode(Double.toString(algorithm[i].a)));
            operator.appendChild(a);

            Element d = doc.createElement(D);
            d.appendChild(doc.createTextNode(Double.toString(algorithm[i].d)));
            operator.appendChild(d);        

            Element s = doc.createElement(S);
            s.appendChild(doc.createTextNode(Double.toString(algorithm[i].s)));
            operator.appendChild(s);

            Element r = doc.createElement(R);
            r.appendChild(doc.createTextNode(Double.toString(algorithm[i].r)));
            operator.appendChild(r);
            
            Element volume = doc.createElement(VOLUME);
            volume.appendChild(doc.createTextNode(Double.toString(algorithm[i].volume)));
            operator.appendChild(volume);
        }

    TransformerFactory tff = TransformerFactory.newInstance();
    Transformer t = tff.newTransformer();
    DOMSource src = new DOMSource(doc);

    if (newfile) {
        final String PATH = PATCH_PATH + "newPatch" + (patches.size() + 1) + ".xml";
        StreamResult result = new StreamResult(PATH);
        t.transform(src, result);
    } else {
        // TODO: Handle saving to existing file
    }
    } catch (Exception e) {
            e.printStackTrace(); // Always helpful during debugging
        }
    }
}
