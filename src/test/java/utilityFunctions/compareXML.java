package utilityFunctions;

import com.aventstack.extentreports.ExtentTest;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.lang.invoke.ConstantCallSite;
import java.util.*;

public class compareXML {


    static ExtentTest test;
   static String configPropertiesPath="/src/test/resources/config.properties";
    static Properties configuration=new Properties();;
    @Test
    public void compareXMLs() throws Exception{
        String path = new File(".").getCanonicalPath();
        configuration.load(new FileInputStream(path+configPropertiesPath));
        FileInputStream fis1 = new FileInputStream(path+configuration.getProperty("sourceFile"));
        FileInputStream fis2 = new FileInputStream(path+configuration.getProperty("targetFile"));
        BufferedReader source = new BufferedReader(new InputStreamReader(fis1));
        BufferedReader target = new BufferedReader(new InputStreamReader(fis2));
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        reporter reporter = new reporter();
        test = reporter.extent.createTest("Compare two xml files");
        System.out.println("Creating Extent Report");
        List differences = compareXML(source, target);
        System.out.println("Comparing XMLs");
        printDifferences(differences);
        reporter.extent.flush();
        System.out.println("Test result and reports updated . Report is available @ /target/Spark.html");
    }


    public static void printDifferences(List<Difference> differences) throws Exception {

       Boolean status=false;
        List<LinkedHashMap<String, Object>> differenceReport = new ArrayList<LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object>  comparisionDetail=new LinkedHashMap<String, Object>();
        NodeList nodesAtTest=null;
        NodeList nodesAtControl=null;
        for (Difference difference : differences) {
            LinkedHashMap<String, Object> differenceDetail = new LinkedHashMap<String, Object>();
            if (!(difference.getDescription().equalsIgnoreCase("sequence of child nodes"))) {
                status=true;

                if(difference.getDescription().equalsIgnoreCase("presence of child node")){
                    differenceDetail.put("Difference Description", "BLOCK MISSING   ");
                    differenceDetail.put("Actual vs Expected ",  difference);
                    differenceDetail.put("Actual Value ", difference.getControlNodeDetail().getNode());
                    differenceDetail.put("Expected Value ", difference.getTestNodeDetail().getNode());
                }


                try {
                    nodesAtTest = difference.getTestNodeDetail().getNode().getParentNode().getParentNode().getChildNodes();
                    nodesAtControl = difference.getControlNodeDetail().getNode().getParentNode().getParentNode().getChildNodes();

                for (int i = 0; i < nodesAtTest.getLength(); ++i) {

                    Element eTest = (Element) nodesAtTest.item(i);
                    Element eControl = (Element) nodesAtControl.item(i);
                    if (nodesAtTest.item(i).getLocalName().equalsIgnoreCase("txn_reference_id"))
                        comparisionDetail.put("TXN - Test", eTest.getTextContent());
                    if (nodesAtTest.item(i).getLocalName().equalsIgnoreCase("description"))
                        comparisionDetail.put("DESC - Test", eTest.getTextContent());
                    if (nodesAtControl.item(i).getLocalName().equalsIgnoreCase("txn_reference_id"))
                        comparisionDetail.put("TXN - Control", eControl.getTextContent());
                    if (nodesAtControl.item(i).getLocalName().equalsIgnoreCase("description"))
                        comparisionDetail.put("DESC - Control", eControl.getTextContent());
                }
  if(comparisionDetail.get("TXN - Test").toString().equalsIgnoreCase(comparisionDetail.get("TXN - Control").toString())
   &
          comparisionDetail.get("DESC - Test").toString().equalsIgnoreCase(comparisionDetail.get("DESC - Control").toString() ))
   {
       if (difference.getTestNodeDetail().getNode() != null)
           differenceDetail.put("Difference at Field ", difference.getTestNodeDetail().getNode().getParentNode().getLocalName());
       differenceDetail.put("Difference Description",  "VALUE MISMATCHING   ");
    differenceDetail.put("Actual vs Expected ",  difference);
    differenceDetail.put("Actual Value ", difference.getControlNodeDetail().getValue());
    differenceDetail.put("Expected Value ", difference.getTestNodeDetail().getValue());
       differenceDetail.put("TXN - Test",  comparisionDetail.get("TXN - Test").toString());
       differenceDetail.put("DESC - Test",  comparisionDetail.get("DESC - Test").toString());
       differenceDetail.put("TXN - Control",  comparisionDetail.get("TXN - Control").toString());
       differenceDetail.put("DESC - Control",  comparisionDetail.get("DESC - Control").toString());
    differenceDetail.put("Difference at node Position ", getNodePosition(difference.getTestNodeDetail().getNode()));
}
                    differenceReport.add(differenceDetail);

                } catch (Exception e) {
                    differenceReport.add(differenceDetail);
                }

            }

        }
        if (status )
            reporter.reporterFunction(test, "FAIL", "There are  differences in source and target xml ");

         else
            reporter.reporterFunction(test, "PASS", "There are no differences in source and target xml ");


    for (HashMap<String, Object> differenceValues : differenceReport) {
        if (differenceValues.size() > 0) {
            reporter.reporterFunction(test, "INFO", differenceValues.toString());
        }
    }

    }



    public static List compareXML(Reader source, Reader target) throws
            SAXException, IOException {

        Diff xmlDiff = new Diff(source, target);
        DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);
        return detailXmlDiff.getAllDifferences();

    }

    public static int getNodePosition(Node node) {
        int pos = -1;
        while (node != null) {
            ++pos;
            node = node.getPreviousSibling();
        }
        return pos;
    }
}
