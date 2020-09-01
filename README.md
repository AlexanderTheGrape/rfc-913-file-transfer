![Build Status](https://travis-ci.com/AlexanderTheGrape/rfc-913-file-transfer.svg?token=jdneeXHk7zBz2GJAsBqg&branch=master)

# RFC-913 File Transfer - Java Implementation

## Compilation Steps
The recommended testing environment is Intellij, and the procedure for testing will be described for this medium.

There are a number of test cases as part of the TCPServerTest.java file, which can be run to show how the protocol run by the server responds to given text from the client. It assumes that the text from the client has been deserialised correctly and is in the form of a String. It is recommended to use these tests to quickly mark the majority of test cases. However, the procedure for testing the client and server for integration testing by running them both is also listed here for more thorough testing.

## Quick Tests
The following instructions are for running the quick tests.

Open an Intellij window and open the rfc-913-file-transfer as the project folder.

Set up the build environment by clicking the 'edit configurations' drop-down button located up the top right of the screen. ![edit configurations](./images/capture.png)

Click the '+' button up the top left of the tab to add a new configuration, and select JUnit.

![create new configuration](./images/capture2.png)

Select the "..." button for the "Class" option, and select the TCPServerTest option once it appears.
![... button](./images/capture3.png) ![TCPServerTest option](./images/capture4.png)

Apply and save your changes, then click the "Run" green arrow button at the top right of the screen, next to the configurations button.

The tests should then run and you can see the results in the lower half of the Intellij window.

Repeat the process again for creating the testing environment for the Client as well (TCPClientTest).



## Integration Tests

To undertake whole-system integration tests and tests with custom input, please follow these steps.

Two configurations need to be made, being for the client and server respectively. The configuration setup is the same as in the Quick Tests section up until selecting the application, where "Application" must be selected instead of JUnit.

 ![Application](./images/capture5.png)

 The main class must then be selected (either client or server), and program arguments provided. In the case of the client, select main.client.TCPClient as the main class, and "localhost 1234" as the program arguments (being the target server address and port). When setting the server configurations as a separate configuration, it needs the corresponding main class for the server, and its program arguments only include the port. This port must be the same 4-digit number used in the client configuration.

 ![client configuration example](./images/capture6.png)

 From there, run the server and then the client (so that both are running at the same time). This can be done by using the drop-down configurations menu to select a configuration to run, then selecting another configuration and running that as well. A console should pop up at the bottom of the screen, and in the console for the client, you can then start entering commands to test the system.




