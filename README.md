# ipr

several things needed to do in order to run this in your local machines.


typeInfo.txt
  a txt file used to record the types of the variables in our targeted file. Feel free to edit it based on you situations, but the format needs to be followed.
 
CloneInstrument.java:
  in the method showDiff(), there are some paths that need to be changed.
    1. Scanner sc = new Scanner(new File("/Users/eddiii/Desktop/courses/ipr/typeInfo.txt"));
    2. processBuilder.command("/Users/eddiii/Desktop/courses/ipr/Grafter/code/myscript.sh", testPath, testName, methodName);
    3. processBuilder.command("/Users/eddiii/Desktop/courses/ipr/Grafter/code/myscript.sh", testPath, moduleName, testName, methodName);
    4. local tests should be changed if you want to run them on your local machines.
    
GrafterConfig.java:
  change the value of the path indicated below:
    final static String path = "/Users/eddiii/Desktop/courses/ipr/grafter-code/Grafter/code/Grafter(Ant).config";

Grafter(Ant).config:
  in this file, there are 6 paths that should be changed according to your own local paths.
  
myscript.sh:
  you should type "chmod 777 myscript.sh" and "sed -i -e 's/\r$//' myscript.sh" to open the access.

