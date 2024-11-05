import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

public class ShamirSecretSharing {

    public static void main(String[] args) throws IOException {
        // Load JSON file and parse it
        String filePath = "testcase.json"; // Replace with your actual JSON file path
        JSONObject jsonObject = new JSONObject(new FileReader(filePath));
        
        // Retrieve keys for 'n' and 'k'
        JSONObject keysObject = jsonObject.getJSONObject("keys");
        int n = keysObject.getInt("n");
        int k = keysObject.getInt("k");

        // Parse points from the JSON object
        Map<Integer, BigInteger> points = new TreeMap<>();
        for (String key : jsonObject.keySet()) {
            if (!key.equals("keys")) {
                JSONObject point = jsonObject.getJSONObject(key);
                int x = Integer.parseInt(key);
                int base = point.getInt("base");
                String valueStr = point.getString("value");
                BigInteger y = new BigInteger(valueStr, base); // Decode y using the specified base
                points.put(x, y);
            }
        }
        
        // Calculate the constant term 'c' using Lagrange Interpolation
        BigInteger secret = lagrangeInterpolation(points, k);
        System.out.println("The secret (constant term c) is: " + secret);
    }

    private static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Select the first k points (minimum required to find the polynomial)
        Iterator<Map.Entry<Integer, BigInteger>> iterator = points.entrySet().iterator();
        for (int i = 0; i < k && iterator.hasNext(); i++) {
            Map.Entry<Integer, BigInteger> pointI = iterator.next();
            int xi = pointI.getKey();
            BigInteger yi = pointI.getValue();

            BigInteger term = yi;
            for (Map.Entry<Integer, BigInteger> pointJ : points.entrySet()) {
                if (pointJ.getKey() != xi) {
                    int xj = pointJ.getKey();
                    term = term.multiply(BigInteger.valueOf(-xj))
                               .divide(BigInteger.valueOf(xi - xj));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
