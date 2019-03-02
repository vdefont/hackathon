import java.util.*;
import java.io.*;

import Util.*;
import Util.Graph.*;

class Test {

  public Test () {
    try (BufferedReader br = new BufferedReader(new FileReader("data/happiness/clean.csv"))) {
      String line;
      line = br.readLine();
      System.out.println("Line: " + line);
      line = br.readLine();
      System.out.println("Line: " + line);
      line = br.readLine();
      System.out.println("Line: " + line);
      line = br.readLine();
      System.out.println("Line: " + line);
    } catch (IOException e) {
      System.out.println("Error reading file: " + e);
    }
  }

  public static void main (String[] args) {
    new Test();
  }

}
