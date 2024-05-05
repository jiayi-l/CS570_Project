package code;
public class sequenceAlignment_DP {
    public int sequenceAlignment(String X, String Y, int delta, int[][] alpha) {
        int m = X.length(), n = Y.length();
        int[][] dp = new int[m+1][n+1];
    
        // Initialize the DP table
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i * delta;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j * delta;
        }
    
        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int match = dp[i-1][j-1] + alpha[X.charAt(i-1)][Y.charAt(j-1)];
                int delete = dp[i-1][j] + delta;
                int insert = dp[i][j-1] + delta;
                dp[i][j] = Math.min(Math.min(match, delete), insert);
            }
        }
    
        return dp[m][n];
    }
}
