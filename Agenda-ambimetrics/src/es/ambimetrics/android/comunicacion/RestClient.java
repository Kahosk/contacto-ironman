package es.ambimetrics.android.comunicacion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;

public class RestClient {
 
    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
 
    /* This is a test function which will connects to a given
     * rest service and return the response.
     */
    public static String connect(String url, String Json)
    {   
    	String result = null;
    	try {
        HttpClient httpclient = new DefaultHttpClient();
        // Prepare a request object
        //HttpGet httpget = new HttpGet(url); 
        //or 
        HttpPost httppost = new HttpPost(url);
        
		httppost.setEntity(new StringEntity(Json));

        // Execute the request
        HttpResponse response;
        
       
            response = httpclient.execute(httppost);
            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release
         
            if (entity != null) {
 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result= convertStreamToString(instream);

                /*
                
                Log.i("JSON",result);
 
                // A Simple JSONObject Creation
                JSONObject json=new JSONObject(result);
                String cad = json.toString();
 
                // A Simple JSONObject Parsing
                JSONArray nameArray=json.names();
                JSONArray valArray=json.toJSONArray(nameArray);
                for(int i=0;i<valArray.length();i++)
                {
                    String name = nameArray.getString(i);
					String value = valArray.getString(i);
					}
 
                // A Simple JSONObject Value Pushing
                json.put("sample key", "sample value");
 */
                // Closing the input stream will trigger connection release
                instream.close();
            }
 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }/* catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    
    return result;
    }
    
    
    
 
}