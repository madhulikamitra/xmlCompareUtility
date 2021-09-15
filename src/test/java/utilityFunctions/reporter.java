package utilityFunctions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.Assert;


import java.io.File;
import java.io.IOException;


public class reporter {
   public  static ExtentReports extent = new ExtentReports();
  public static ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark.html");
    public  static void reporterFunction(ExtentTest test, String status, String message) {

        try {
            extent.attachReporter(spark);
            switch (status) {
                case "PASS":
                    test.pass(message);
                    break;

                case "INFO":
                    test.info(message);
                    break;

                case "FAIL":
                   // Assert.assertTrue("fail",1==0);
                    test.fail(message);
                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }




}
