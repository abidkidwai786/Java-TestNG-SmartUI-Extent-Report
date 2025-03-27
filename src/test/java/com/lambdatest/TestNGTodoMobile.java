package com.lambdatest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import okhttp3.*;
import java.io.IOException;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Iterator;
import org.testng.annotations.Parameters;


public class TestNGTodoMobile {

  private RemoteWebDriver driver;
  private String Status = "failed";
  private static ExtentReports extent;
  private ExtentTest test;
  private static final String SCREENSHOT_DIR = System.getProperty("user.dir") + File.separator + "screenshots";
  String username = "";
  String authkey = "";
  String smartUIProject = "GS-Extent-report";
  String smartUIBuild = RandomStringUtils.randomAlphanumeric(10);

  @BeforeMethod
  public void setup(ITestContext ctx) throws Exception {
    String hub = "@mobile-hub.lambdatest.com/wd/hub";
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability("platformName", "ios");
    caps.setCapability("deviceName", "iPhone 12");
    caps.setCapability("platformVersion", "16");
    caps.setCapability("isRealMobile", true);
    caps.setCapability("visual", true);
    caps.setCapability("build", "TestNG With Java1");
    caps.setCapability("name", "Screenshot Capture Test");
    caps.setCapability("smartUI.project", smartUIProject);
    caps.setCapability("smartUI.build", smartUIBuild);
    caps.setCapability("idleTimeout", 600);

    driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + hub), caps);
    if (extent == null) {
      String reportPath = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "ExtentReport.html";
      ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
      extent = new ExtentReports();
      extent.attachReporter(sparkReporter);
    }
    test = extent.createTest("Screenshot Capture Test").assignAuthor("Aakash").assignCategory("Regression");
  }
  @Test
  @Parameters({"smartUIProject", "smartUIBuild"})
  public void basicTest() throws Exception {
    try {
      System.out.println("Project: " + smartUIProject);
      System.out.println("Build: " + smartUIBuild);
      test.info("Starting the test execution...");
      driver.get("https://lambdatest.github.io/sample-todo-app/");
      Thread.sleep(2000);
      test.info("Taking Lambdatest Screenshot");
      driver.executeScript("smartui.takeScreenshot=sample");
      Thread.sleep(10000);
      driver.get("https://the-internet.herokuapp.com/");
      Thread.sleep(2000);
      test.info("Taking HerokuApp Screenshot");
      driver.executeScript("smartui.takeScreenshot=herokuapp");
      Thread.sleep(10000);
      driver.executeScript("smartui.fetchScreenshotStatus=sample");
      System.out.println(driver.executeScript("smartui.fetchScreenshotStatus=sample"));
      Thread.sleep(2000);
      driver.executeScript("smartui.fetchScreenshotStatus=herokuapp");
      System.out.println(driver.executeScript("smartui.fetchScreenshotStatus=herokuapp"));
      driver.executeScript("lambda-status=" + Status);
      driver.quit();
    } catch (Exception e) {
      System.out.println("Exception encountered");
    } finally {
      Thread.sleep(210000);
      test.info("Screenshots saved in folder: " + SCREENSHOT_DIR);
      OkHttpClient client = new OkHttpClient();
      String credentials = username+":"+authkey;
      String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
      String Curl = "https://api.lambdatest.com/smartui/2.0/build/screenshots?project_name="
              + smartUIProject + "&build_name=" + smartUIBuild + "&baseline=false";
      Request request = new Request.Builder()
              .url(Curl)
              .get()
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", basicAuth)
              .build();
      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          String responseBody = response.body().string();
          JSONObject jsonResponse = new JSONObject(responseBody);
          System.out.println(responseBody);
          ObjectMapper objectMapper = new ObjectMapper();
          try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
//            System.out.println("Extracted Key-Value Pairs:");
            printJsonKeysAndValues(rootNode, "");
          } catch (IOException e) {
            e.printStackTrace();
          }
          // Extract Project Information
          JSONObject project = jsonResponse.getJSONObject("project");
          System.out.println("\n===== Project Details =====");
          Iterator<String> keys1 = project.keys();
          test.info("<div><b>" + "Project Details"+ smartUIProject + "</b></div>");
          while (keys1.hasNext()) {
            String key1 = keys1.next();
            System.out.println(key1 + ": " + project.get(key1).toString());
            test.info(key1 + ": " + project.get(key1).toString());
          }
          // Extract Build Information
          JSONObject build = jsonResponse.getJSONObject("build");
          System.out.println("\n===== Build Details =====");
          Iterator<String> keys = build.keys();
          test.info("<div><b>" + "Build Details" +smartUIBuild+ "</b></div>");
          while (keys.hasNext()) {
            String key = keys.next();
            System.out.println(key + ": " + build.get(key).toString());
            test.info(key + ": " + build.get(key).toString());
          }
        // Extract Screenshot Information
          JSONArray screenshots = jsonResponse.getJSONArray("screenshots");
          for (int i = 0; i < screenshots.length(); i++) {
            JSONObject screenshot = screenshots.getJSONObject(i);
            System.out.println("Screenshot Details:");
            test.info("<div><b>" + "Screenshot Details" + "</b></div>");
            // Download all images for each screenshot entry
            for (String keyx : screenshot.keySet()) {
              if ((keyx.contains("captured_image") || keyx.contains("baseline_image") || keyx.contains("compared_image"))
                      && !keyx.contains("captured_image_timestamp") && !keyx.contains("compared_image_timestamp")) {
                String screenshotUrl = screenshot.getString(keyx);
                String fileName = screenshot.getString("screenshot_name") + "_" + keyx.replace(":", "_") + ".png";
                System.out.println(fileName);
                if (screenshotUrl == null || screenshotUrl.isEmpty()) {
                  System.out.println("Screenshot URL is empty.");
                  continue;
                }
                File screenshotDir = new File(SCREENSHOT_DIR);
                if (!screenshotDir.exists()) {
                  screenshotDir.mkdirs();
                }
                String localFilePath = SCREENSHOT_DIR + File.separator + fileName;
                try {
                  URL url = new URL(screenshotUrl);
                  HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                  connection.setRequestMethod("GET");
                  InputStream inputStream = connection.getInputStream();
                  FileOutputStream outputStream = new FileOutputStream(localFilePath);
                  byte[] buffer = new byte[4096];
                  int bytesRead;
                  while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                  }
                  outputStream.close();
                  inputStream.close();
                  connection.disconnect();
                  System.out.println("Screenshot saved: " + localFilePath);
                  test.info("Screenshot Captured : "+fileName)
                          .addScreenCaptureFromPath(localFilePath, fileName);
                } catch (IOException e) {
                  System.out.println("Failed to download screenshot: " + e.getMessage());
                }
              }
            }
            // Print all details
            for (String keyy : screenshot.keySet()) {
              System.out.println(keyy + ": " + screenshot.get(keyy).toString());
              test.info(keyy + ": " + screenshot.get(keyy).toString());
            }
            System.out.println("---------------------------------");
          }
        }
      }
    }
  }
      @AfterMethod
      public void tearDown () {
        if (extent != null) {
          extent.flush();
        }
        System.out.println("Screenshots saved in folder: " + SCREENSHOT_DIR);
        System.out.println("Test execution finished.");
      }
private static void printJsonKeysAndValues(JsonNode node, String keyPrefix) {
  if (node.isObject()) {
    // Handle JSON Object
    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      printJsonKeysAndValues(field.getValue(), keyPrefix + field.getKey() + ".");
    }
  } else if (node.isArray()) {
    // Handle JSON Array
    for (int i = 0; i < node.size(); i++) {
      printJsonKeysAndValues(node.get(i), keyPrefix + "[" + i + "].");
    }
  } else {
    // Print Key-Value Pair
//    System.out.println(keyPrefix.substring(0, keyPrefix.length() - 1) + " = " + node.asText());
  }}
}