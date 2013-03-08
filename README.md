test-jpa
========

Template project for any user testcase using JPA. 
To create a DataNucleus test simply fork this project, and add/edit as necessary to add your model and persistence commands.
The files that you'll likely need to edit are
src/main/java/mydomain/model/    <== Put your model classes here
src/main/resources/META-INF/persistence.xml    <== Put your datastore details in here
src/test/java/org/datanucleus/test/SimpleTest.java   <== Edit this if a single-thread test is required
src/test/java/org/datanucleus/test/MultithreadTest.java   <== Edit this if a multi-thread test is required

To run this, simply type "mvn clean compile test"
