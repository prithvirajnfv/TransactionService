# TransactionService

Technology stack

I'd prefer to implement using spring. But due to given condition no spring. The chosen technologies:

Java 8<br />
Jetty + Jersey<br />
Jackson<br />
Maven<br />

Project building

mvn package<br />
mvn install<br />

Project running

java -jar revolut-txmgr.jar<br />

TODO

Service for create account etc, At-present hardcoded and initialized 18 accounts with account id{1 to 18}<br />
Unit test <br />
Many improvements can be done<br />

Conclusion
If I get an opportunity to build this system for real world, I will prefer to analyze micro services, container(docker) and kubernetes based approach with bare metal hardware in-premise.
Alternatively I will also like to analyze deployment in private cloud(openstack).
