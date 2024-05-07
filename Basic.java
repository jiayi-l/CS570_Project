import java.io.*;
import java.util.*;

public class Basic {
   // set gap penalty value
   private static int gapPenalty = 30;

   // set mismatch cost matrix
   private static int[][] mismatchCost = {
         {0, 110, 48, 94},  // A
         {110, 0, 118, 48}, // C
         {48, 118, 0, 110}, // G
         {94, 48, 110, 0}   // T
   };

   // convert character into index for matrix
   private static int charToIndex(char c) {
      if (Character.toUpperCase(c) == 'A') {
         return 0;
      } else if (Character.toUpperCase(c) == 'C') {
         return 1;
      } else if (Character.toUpperCase(c) == 'G') {
         return 2;
      } else if (Character.toUpperCase(c) == 'T') {
         return 3;
      } else {
            throw new IllegalArgumentException("Unexpected character: " + c);
      }
   }

   // Dynamic programming for minimum cost match
   public static int[][] computeAlignmentMatrix(String x, String y) {
      int len1 = x.length();
      int len2 = y.length();
      int[][] OPT = new int[len1 + 1][len2 + 1];

      // initialization for base case
      for (int i = 0; i <= len1; i++) {
         OPT[i][0] = i * gapPenalty;
      }
      for (int j = 0; j <= len2; j++) {
         OPT[0][j] = j * gapPenalty;
      }

      // dynamic programming process
      for (int i = 1; i <= len1; i++) {
         for (int j = 1; j <= len2; j++) {
            int matchCost = mismatchCost[charToIndex(x.charAt(i  - 1))][charToIndex(y.charAt(j - 1))];
            int minCost = Math.min(OPT[i - 1][j - 1] + matchCost, OPT[i - 1][j] + gapPenalty);
            OPT[i][j] = Math.min(minCost, OPT[i][j - 1] + gapPenalty);
         }
      }
      return OPT;
   }

   public static String[] reconstructAlignment(String x, String y, int[][] dp) {
      StringBuilder alignmentX = new StringBuilder();
      StringBuilder alignmentY = new StringBuilder();
      int i = x.length();
      int j = y.length();

      while (i > 0 && j > 0) {
         int score = dp[i][j];
         int scoreDiag = dp[i - 1][j - 1];
         int scoreLeft = dp[i - 1][j];
         int scoreDown = dp[i][j - 1];

         if (score == scoreDiag + mismatchCost[charToIndex(x.charAt(i - 1))][charToIndex(y.charAt(j - 1))]) {
            alignmentX.append(x.charAt(i - 1));
            alignmentY.append(y.charAt(j - 1));
            i--;
            j--;
         } else if (score == scoreLeft + gapPenalty) {
            alignmentX.append(x.charAt(i - 1));
            alignmentY.append('_');
            i--;
         } else if (score == scoreDown + gapPenalty) {
            alignmentX.append('_');
            alignmentY.append(y.charAt(j - 1));
            j--;
         }
      }

      while (i > 0) {
         alignmentX.append(x.charAt(i - 1));
         alignmentY.append('_');
         i--;
      }

      while (j > 0) {
         alignmentX.append('_');
         alignmentY.append(y.charAt(j - 1));
         j--;
      }

      alignmentX.reverse();
      alignmentY.reverse();

      return new String[]{String.valueOf(dp[x.length()][y.length()]), alignmentX.toString(), alignmentY.toString()};
   }

   private static String generateString(String base, List<Integer> steps) {
      StringBuilder sb = new StringBuilder(base);
      for (int step : steps) {
         sb.insert(step+1, sb.toString());
      }
      return sb.toString();
   }

   public static boolean isNumeric(String str) {
      try {
         Double.parseDouble(str);
         return true;
      } catch(NumberFormatException e){
         return false;
      }
   }
      public static void main(String[] args) {
         if (args.length < 2) {
            System.out.println("Usage: java Basic <inputFilePath> <outputFilePath>");
            return;
         }
         String inputFilePath = args[0];
         String outputFilePath = args[1];

         try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String alphabet1 = reader.readLine();
            System.out.println(alphabet1);

            List<Integer> index1 = new ArrayList<>();
            String newLine = reader.readLine();
            while (isNumeric(newLine)) {
               index1.add(Integer.parseInt(newLine));
               newLine = reader.readLine();
            }
            String alphabet2 = newLine;
            newLine = reader.readLine();
            List<Integer> index2 = new ArrayList<>();
            while (newLine!=null) {
               index2.add(Integer.parseInt(newLine));
               newLine = reader.readLine();
            }
            System.out.println("finish reading file");
            String string1 = generateString(alphabet1, index1);
            System.out.println(string1);
            String string2 = generateString(alphabet2, index2);
            System.out.println(string2);
            long start = System.nanoTime();
            Runtime.getRuntime().gc();
            long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            String[] output = reconstructAlignment(string1, string2, computeAlignmentMatrix(string1,string2));
            for(String i:output){
               System.out.println(i);
            }
            long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long end = System.nanoTime();
            double timeTaken = (end - start) / 1000000000.0;

            try (PrintWriter writer = new PrintWriter(new File(outputFilePath))) {
               writer.println(output[0]+"\n");
               writer.println(output[1]+"\n");
               writer.println(output[2]+"\n");
               writer.println(String.format("%.3f", timeTaken) + "\n");
               writer.println(String.format("%.3f", (memoryAfter - memoryBefore) / 1000.0));
            } catch (FileNotFoundException e) {
               System.err.println("File not found: " + e.getMessage());
            } catch (Exception e) {
               System.err.println("Error processing the file: " + e.getMessage());
            }
         } catch (IOException e) {
            e.printStackTrace();
         }

      }
   }
