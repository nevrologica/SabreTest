/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sabretest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Alex
 */
public class SabreTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        final String clientId = "V1:muvcpcbvyt5xpb30:DEVCENTER:EXT";
        final String clientSecret= "m6uKyLA7";

        //base64 encode clientId and clientSecret
        String encodedClientId = Base64.encodeBase64String((clientId).getBytes());
        String encodedClientSecret = Base64.encodeBase64String((clientSecret).getBytes());

        //Concatenate encoded client and secret strings, separated with colon
        String encodedClientIdSecret = encodedClientId+":"+encodedClientSecret;

        //Convert the encoded concatenated string to a single base64 encoded string.
        encodedClientIdSecret = Base64.encodeBase64String(encodedClientIdSecret.getBytes());
		
        String token = getAuthToken("https://api.sabre.com",encodedClientIdSecret);
        
        String params ="{\n" +
                        "    \"GetHotelListRQ\": {\n" +
                        "        \"SearchCriteria\": {\n" +
                        "            \"IncludedFeatures\": true, \n" +
                        "            \"HotelRefs\": {\n" +
                        "                \"HotelRef\": [\n" +
                        "                    {\n" +
                        "                        \"HotelCode\": \"7521\"\n" +
                        "                    }, \n" +
                        "                    {\n" +
                        "                        \"HotelCode\": \"22390\"\n" +
                        "                    }, \n" +
                        "                    {\n" +
                        "                        \"HotelCode\": \"22570\"\n" +
                        "                    }\n" +
                        "                ]\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";
        
        String response = sendRequest("https://api.sabre.com/v1.0.0/shop/hotels/list?mode=list", params, token);
        
        //Display the response String
        System.out.println("SDS Response: " + response);
        
    }
    
    public static String getAuthToken(String apiEndPoint, String encodedCliAndSecret){
		
            String strRet = null;

            try {

                    URL urlConn = new URL(apiEndPoint + "/v2/auth/token");
                    URLConnection conn = urlConn.openConnection();

                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);

                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Authorization", "Basic " + encodedCliAndSecret);
                    conn.setRequestProperty("Accept", "application/json");
                 
                    //send request
                    DataOutputStream dataOut = new DataOutputStream(conn.getOutputStream());
                    dataOut.writeBytes("grant_type=client_credentials");
                    dataOut.flush();
                    dataOut.close();

                    //get response
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String strChunk="";
                    StringBuilder sb = new StringBuilder();
                    while(null != ((strChunk=rd.readLine())))
                            sb.append(strChunk);

                    //parse the token
                    JSONObject respParser = new JSONObject(sb.toString());
                    strRet = respParser.getString("access_token");

            } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            return strRet;
    }
	
    @SuppressWarnings("deprecation")
    public static String sendRequest(String payLoad, String params, String authToken){
        
        HttpURLConnection conn = null;
        String strRet = null;
        
        byte[] postData = params.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        try {
                URL urlConn = new URL(payLoad);

                conn = null;
                conn = (HttpURLConnection) urlConn.openConnection();
               
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                
                try (DataOutputStream dataOut = new DataOutputStream(conn.getOutputStream())) {
                    dataOut.write(postData);
                    dataOut.flush();
                }

                DataInputStream dataIn = new DataInputStream(conn.getInputStream());
                String strChunk="";
                StringBuilder sb = new StringBuilder("");
                while(null != ((strChunk = dataIn.readLine())))
                        sb.append(strChunk);

                strRet = sb.toString();

        } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("IOException: " + conn.getHeaderField(0));
        }
        
        return strRet;
    }
    
}
