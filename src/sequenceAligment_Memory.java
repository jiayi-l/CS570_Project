import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * suppose we have x=m, y=n
 * for each time: x/2
 * run dp to find min value of dp[x/2][y] for each y on both half
 * for left half: dp[x/2][y] = cost from source to middle row
 * for the right half: dp[x/2][y] = cost from middle row to sink
 *
 * when the x<=1 && y<=1:
 * if x ==y ==1: dp[x][y]=dp[x-1][y-1]+alpha
 * if either x or y==0: dp[x][y]=dp[x-1][y]/dp[x][y-1]+gap
 */

public class sequenceAligment_Memory {

   public static boolean isNumeric(String strNum) {
      boolean ret = true;
      try {

         Double.parseDouble(strNum);

      }catch (NumberFormatException e) {
         ret = false;
      }
      return ret;
   }

   private static final HashMap<String, Integer> alpha = new HashMap<>();
   static {
      alpha.put("AA", 0);
      alpha.put("CC", 0);
      alpha.put("GG", 0);
      alpha.put("TT", 0);
      alpha.put("AC", 110);
      alpha.put("CA", 110);
      alpha.put("AG", 48);
      alpha.put("GA", 48);
      alpha.put("AT", 94);
      alpha.put("TA", 94);
      alpha.put("CG", 118);
      alpha.put("GC", 118);
      alpha.put("CT", 48);
      alpha.put("TC", 48);
      alpha.put("GT", 110);
      alpha.put("TG", 110);
   }

   public static String stringGenerator(String string, List<Integer> index1) {
      StringBuilder stringBuilder = new StringBuilder(string);
      for (int i : index1) {
         String ins = stringBuilder.toString();
         stringBuilder.insert(i + 1, ins);
         System.out.println(stringBuilder);
      }
      return stringBuilder.toString();
   }

   public static int[] spaceEfficientAlignment(String X, String Y, int flag) {
      int[][] dp = new int[2][Y.length() + 1];
      int gapPenalty = 30;
      for (int i = 0; i < Y.length() + 1; i++) {
         dp[0][i] = gapPenalty * i;
      }

      if (flag == 0) {
         for (int i = 1; i < X.length() + 1; i++) {
            dp[1][0] = i * gapPenalty;
            for (int j = 1; j < Y.length() + 1; j++) {
               dp[1][j] = Math.min(dp[0][j - 1] + alpha.get(X.substring(i - 1, i) + Y.substring(j - 1, j)),
                     Math.min(dp[0][j] + gapPenalty, dp[1][j - 1] + gapPenalty));
            }
            for (int j = 0; j < Y.length() + 1; j++) {
               dp[0][j] = dp[1][j];
            }
         }
      } else if (flag == 1) {
         for (int i = 1; i < X.length() + 1; i++) {
            dp[1][0] = i * gapPenalty;
            for (int j = 1; j < Y.length() + 1; j++) {
               dp[1][j] = Math.min(dp[0][j - 1] + alpha.get(X.substring(X.length() - i, X.length() - i + 1) + Y.substring(Y.length() - j, Y.length() - j + 1)),
                     Math.min(dp[0][j] + gapPenalty, dp[1][j - 1] + gapPenalty));
            }
            for (int j = 0; j < Y.length() + 1; j++) {
               dp[0][j] = dp[1][j];
            }
         }
      }
      return dp[1];
   }

   public static String[] seqAlignment(String string1, String string2) {
      int[][] dp = new int[string1.length() + 1][string2.length() + 1];
      int gapPenalty = 30;

      for (int i = 0; i < string2.length() + 1; i++) {
         dp[0][i] = i * gapPenalty;
      }
      for (int i = 0; i < string1.length() + 1; i++) {
         dp[i][0] = i * gapPenalty;
      }
      for (int i = 1; i < string1.length() + 1; i++) {
         for (int j = 1; j < string2.length() + 1; j++) {
            String string = string1.substring(i - 1, i) + string2.substring(j - 1, j);
            dp[i][j] = Math.min(dp[i - 1][j - 1] + alpha.get(string),
                  Math.min(dp[i][j - 1] + gapPenalty, dp[i - 1][j] + gapPenalty));
         }
      }

      int i = string1.length(), j = string2.length();
      StringBuilder x = new StringBuilder();
      StringBuilder y = new StringBuilder();
      while (i > 0 && j > 0) {
         if (dp[i][j] == dp[i - 1][j - 1] + alpha.get(string1.substring(i - 1, i) + string2.substring(j - 1, j))) {
            x.insert(0, string1.substring(i - 1, i));
            y.insert(0, string2.substring(j - 1, j));
            i--;
            j--;
         } else if (dp[i][j] == dp[i - 1][j] + gapPenalty) {
            x.insert(0, string1.substring(i - 1, i));
            y.insert(0, "_");
            i--;
         } else {
            x.insert(0, "_");
            y.insert(0, string2.substring(j - 1, j));
            j--;
         }
      }
      while (i > 0) {
         x.insert(0, string1.substring(i - 1, i));
         y.insert(0, "_");
         i--;
      }
      while (j > 0) {
         x.insert(0, "_");
         y.insert(0, string2.substring(j - 1, j));
         j--;
      }
      return new String[] {x.toString(), y.toString(), String.valueOf(dp[string1.length()][string2.length()])};
   }

   public static String[] DandC(String str1, String str2) {
      int m = str1.length();
      int n = str2.length();
      if (m < 2 || n < 2) {
         return seqAlignment(str1, str2);
      } else {
         int[] firstHalf = spaceEfficientAlignment(str1.substring(0, m / 2), str2, 0);
         int[] secondHalf = spaceEfficientAlignment(str1.substring(m / 2), str2, 1);
         ArrayList<Integer> newArray = new ArrayList<>();
         for (int j = 0; j <= n; j++) {
            newArray.add(firstHalf[j] + secondHalf[n - j]);
         }
         int q = newArray.indexOf(Collections.min(newArray));
         String[] callLeft = DandC(str1.substring(0, str1.length() / 2), str2.substring(0, q));
         String[] callRight = DandC(str1.substring(str1.length() / 2), str2.substring(q));
         String[] l = new String[3];
         for (int r = 0; r < 2; r++) {
            l[r] = callLeft[r] + callRight[r]; //string_x: l[0] string_y:l[1], cost:l[2]
         }
         l[2]=String.valueOf(Integer.valueOf(callLeft[2])+Integer.valueOf(callRight[2]));

         return l;
      }
   }

   public static void main(String[] args) {
      String filename = "input.txt";
      try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
         String alphabet1 = reader.readLine();
         System.out.println(alphabet1);

         String numbers = reader.readLine();
         List<Integer> index1 = new ArrayList<>();
         while (isNumeric(numbers)) {
            index1.add(Integer.parseInt(numbers));
            numbers = reader.readLine();
         }
         String alphabet2 = numbers;
         numbers = reader.readLine();
         List<Integer> index2 = new ArrayList<>();
         while (numbers!=null) {
            index2.add(Integer.parseInt(numbers));
            numbers = reader.readLine();
         }
         System.out.println("finish reading file");
         String string1 = stringGenerator(alphabet1, index1);
         System.out.println(string1);
         String string2 = stringGenerator(alphabet2, index2);
         System.out.println(string1);
         long start = System.nanoTime();
         Runtime.getRuntime().gc();
         long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         String[] output = DandC(string1, string2);
         for(String i:output){
            System.out.println(i);
         }
         long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         long end = System.nanoTime();
         double timeTaken = (end - start) / 1000000000.0;

         try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write(output[0]+"\n");
            writer.write(output[1]+"\n");
            writer.write(String.valueOf(Float.parseFloat(output[2])) + "\n");
            writer.write(String.format("%.3f", timeTaken) + "\n");
            writer.write(String.format("%.3f", (memoryAfter - memoryBefore) / 1000.0));
         } catch (IOException e) {
            e.printStackTrace();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
