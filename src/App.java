import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

/*
 * How to make this Project?
 * 1. I need Location (I'll get the city name from the user) and then fetch the long. and latt.
 * 2. I need API for that, I'll use open-meteo (limit - 10k per day) 
 * API for Weather -> https://api.open-meteo.com/v1/forecast?latitude=[ltt.]&longitude=[long..]&current=temperature_2m,relative_humidity_2m,rain,cloud_cover,wind_speed_10m,wind_direction_10m
 * API for fetching Long. & Latt. -> https://geocoding-api.open-meteo.com/v1/search?name=[City]]&count=1&language=en&format=json
 */

public class App {

    // private void DisplayWeather(String someResponse) {
    //     JsonElement jspar = JsonParser.parseString(someResponse);
    // }

    private static String getLoc(String City) throws MalformedURLException, IOException {
        // Creating URL Connection
        HttpsURLConnection con = (HttpsURLConnection) URI.create("https://geocoding-api.open-meteo.com/v1/search?name="+City+"&count=1&language=en&format=json").toURL().openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/Firefox");
        con.setRequestProperty("Content-Type", "application/json");

        // In case if there's a user input error whule typing the city OR there is any error encountered with the server 
        if (con.getResponseCode() != 200) {
            System.out.println("An Error Occurred"+"\n"+con.getResponseMessage());
            return null;
        }
        // Reading the Response
        Scanner inp = new Scanner(con.getInputStream());
        String x = "";
        while (inp.hasNext()) {
            x=x+inp.next();
        }
        // Closing the open connection and the scanner
        con.disconnect();
        inp.close();

        // Creating the JsonObject of the JsonArray of the JsonObject of the Response from the server (try to understand the chronology lmao)
        JsonObject resultObject = JsonParser.parseString(x).getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject();
        String latitude = resultObject.get("latitude").toString(); // Latitude we need
        String longitude = resultObject.get("longitude").toString(); // Longitude we need
        // Returning the required URL
        return "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current=temperature_2m,relative_humidity_2m,rain,cloud_cover,wind_speed_10m,wind_direction_10m";
    }

    private static void FetchWeather(String URLToUse) throws MalformedURLException, IOException {
        // Getting the Connections, Setting up the Headers... etc
        HttpsURLConnection con = (HttpsURLConnection) URI.create(URLToUse).toURL().openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/Firefox");
        con.setRequestProperty("Content-Type", "application/json");

        // Reading the Response
        Scanner inp = new Scanner(con.getInputStream());
        String x = "";
        while (inp.hasNext()) {
            x=x+inp.next();
        }
        // Closing the open connection and the scanner
        con.disconnect();
        inp.close();

        JsonObject resultObject = JsonParser.parseString(x).getAsJsonObject();

        JsonObject Values = resultObject.get("current").getAsJsonObject();
        JsonObject Units = resultObject.get("current_units").getAsJsonObject();

        // Fetching all the metrics
        String temp = "Temperature (2 M) : "+Values.get("temperature_2m").toString()+Units.get("temperature_2m").toString().substring(1, 3);
        String humid = "Relative Humidity (2 M) : "+Values.get("relative_humidity_2m").toString()+Units.get("relative_humidity_2m").toString().substring(1, 2);
        String windSpeed = "Wind Speed (10 M) : "+Values.get("wind_speed_10m").toString()+" "+Units.get("wind_speed_10m").toString().substring(1,5);
        String windDirection = "Wind Direction (10 M) : "+Values.get("wind_direction_10m").toString()+Units.get("wind_direction_10m").toString().substring(1, 2);
        String cloudCover = "Cloud Cover : "+Values.get("cloud_cover").toString()+Units.get("cloud_cover").toString().substring(1,2);
        String rain = "Rain : "+Values.get("rain").toString()+" "+Units.get("rain").toString().substring(1,3);
        String measures = temp+"\n"+humid+"\n"+windSpeed+"\n"+windDirection+"\n"+cloudCover+"\n"+rain;
        System.out.println(measures);

    }

    public static void main(String[] args) throws Exception {

        Scanner forInput = new Scanner(System.in);
        System.out.println("Welcome to Weather App CLI\n");
        try {
            while (true) {

                System.out.print("======================================================================================================\nType \"exit\" in order to exit the program.\nEnter the city. -> ");
                
                String city = forInput.nextLine();

                if (city.equals("exit")) {System.out.println("Thanks for using the program"); return; }
                
                StringBuffer checkedCity = new StringBuffer(city.trim());
                for (byte i = 0; i<checkedCity.length(); i++) {
                    if (checkedCity.charAt((int) i) == ' ') {
                        checkedCity.replace(i, i+1, "+");
                    }
                }

                String otherApiLink = getLoc(checkedCity.toString());
                System.out.println(checkedCity);
                if (otherApiLink == null) return;
                FetchWeather(otherApiLink);
                continue;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            forInput.close();
        }
    }


}