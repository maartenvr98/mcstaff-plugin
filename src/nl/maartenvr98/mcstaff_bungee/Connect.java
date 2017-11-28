package nl.maartenvr98.mcstaff_bungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.maartenvr98.mcstaff_bungee.config.Config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connect {

    private Config config;
    private boolean enabled;
    private String url;
    private String key;

    public Connect(Config config) {
        this.config = config;

        this.enabled = config.getConfig().getBoolean("enabled");
        this.url = config.getConfig().getString("url");
        this.key = config.getConfig().getString("key");

        createConnection(true, true);
    }

    /**
     * Connect to REST Service
     *
     * @param message
     */
    public void createConnection(Boolean message, Boolean checkEnabled) {
        if(checkEnabled) {
            if(!this.enabled) {
                return;
            }
        }
        try {
            String result = executePost("connect", "key="+this.key);

            Gson gson = new Gson();
            JsonObject jsonResult = gson.fromJson(result, JsonObject.class);

            String status = jsonResult.get("status").getAsString();
            if(!status.equals("granted")) {
                this.enabled = false;
                if(message) {
                    SendMessage("Plugin disabled due incorrect key");
                }

            }else {
                System.out.println("Mcstaff plugin enabled");
            }
        } catch (IOException e) {
            this.enabled = false;
            if(message) {
                SendMessage("Could not connect to REST service");
            }
        }
    }

    /**
     * Send post http post request to webserver
     *
     * @param targetURL
     * @param urlParameters
     * @return
     * @throws IOException
     */
    public String executePost(String targetURL, String urlParameters) throws IOException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(this.url + "/api/v1/" + targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    /**
     * Send message to command line
     *
     * @param message
     */
    private void SendMessage(String message) {
        System.out.println("----------------Mcstaff-----------------");
        System.out.println(" ");
        System.out.println(message);
        System.out.println(" ");
        System.out.println("----------------------------------------");
    }

    /**
     * Check if connected to REST Service
     *
     * @return
     */
    public boolean connected() {
        createConnection(false, false);
        if(enabled) {
            return true;
        }
        return false;
    }

}
