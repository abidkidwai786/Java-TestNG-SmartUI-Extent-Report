# Java TestNG SmartUI Testing on LambdaTest

This repository contains a Java TestNG setup for running **SmartUI** tests on **LambdaTest** and fetching the results in an **Extent Report**.

## 🚀 Getting Started

Follow the steps below to set up and execute the tests:

### 1️⃣ Clone the Repository
```sh
git clone https://github.com/abidkidwai786/Java-TestNG-SmartUI-Extent-Report
cd Java-TestNG-SmartUI-Extent-Report
```

### 2️⃣ Add Credentials
Update the **TestNGTodoMobile.java** file with your **LambdaTest Username** and **Access Key**:
```java
String username = "your_username";
String accessKey = "your_access_key";
```

### 3️⃣ Configure Proxy (If Required)
Modify the **TestNGTodoMobile.java** file to add proxy details if your network requires it.

### 4️⃣ Run the Test
Execute the test suite using the **smartui-mobile.xml** file:
```sh
mvn test -DsuiteXmlFile=smartui-mobile.xml
```

### 5️⃣ View Test Reports
Once the job completes, the **Extent Report** will be available in the **test-output** folder:
```sh
test-output/index.html
```

## 📌 Notes
- Ensure you have **Java, Maven, and TestNG** installed.
- The **SmartUI** test results can be viewed on the **LambdaTest dashboard**.

### 🛠 Need Help?
For any queries, reach out to **LambdaTest support** or check the documentation.

---
**Happy Testing! 🚀**
