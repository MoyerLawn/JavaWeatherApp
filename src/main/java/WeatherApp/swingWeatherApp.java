package WeatherApp;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class swingWeatherApp
{
    
    private static String cityText = "Enter your city...";

    public static void main (String[] args)
    {
        // Create the main frame
        JFrame frame = new JFrame("Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Create a panel to hold components
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Create a label
        JLabel label = new JLabel("Need your weather?");
        
        // Create text label for city input
        JTextField cityTextField = new JTextField(cityText);
        cityTextField.setEditable(false);
        addBorder(cityTextField);
        
        // Add a mouse listener to the text label
        cityTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String currentText = cityTextField.getText();
                if (currentText.isEmpty() || currentText.equals(cityText)) {
                    cityTextField.setEditable(true);
                    cityTextField.setText("");
                    cityTextField.requestFocusInWindow();
                }
            }
        });

        // Create a button
        JButton button = new JButton("Get Weather");
        addBorder(button);
        
        // Create a result label
        JLabel resultLabel = new JLabel("");

        // Add an action listener to the button
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent e)
            {
                // Get city from text field
                String city = cityTextField.getText();
                
                if (city.contains(" ")) {
                    city = city.replace(" ", "%20");
                }
                System.out.println(city);
                
                // Make API Request to OpenWeatherMap
                try
                {
                    HttpClient httpClient = HttpClients.createDefault();
                    String apiKey = "409009ca0e73e6e88ff13b0440a0206a";
                    String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

                    HttpGet request = new HttpGet(apiUrl);
                    HttpResponse response = httpClient.execute(request);

                    // Check if the response is successful
                    if (response.getStatusLine().getStatusCode() == 200)
                    {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        System.out.println(responseBody);
                        
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        
                        // Extract relevant information
                        JSONObject main = jsonResponse.getJSONObject("main");
                        JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                        JSONObject weather = weatherArray.getJSONObject(0);
                        
                        // Extract temperature and convert to Fahrenheit
                        double tempInKelvin = main.getBigDecimal("temp").doubleValue();
                        double tempInFahrenheit = (tempInKelvin - 273.15) * 9/5 + 32;
                        
                        // Format temperature to whole digits
                        DecimalFormat temperatureFormat = new DecimalFormat("#");
                        String formattedTemperature = temperatureFormat.format(tempInFahrenheit);
                        
                        String description = weather.getString("description");

                        resultLabel.setText("Temperature: " + formattedTemperature + " °F\n Description: " + description);
                    }
                    else
                    {
                        resultLabel.setText("Failed to fetch weather data. Check your API key and city");
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    resultLabel.setText("An error occurred while fetching weather data.");
                }
            }
        });

        // Add padding to the panel
        panel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Adjust values for top, left, bottom, right padding

        
        // Add components to the panel
        panel.add(label);
        panel.add(cityTextField);
        panel.add(button);
        panel.add(resultLabel);
        
        // Add the panel to the frame
        frame.add(panel);

        // Set the frame visibility
        frame.setVisible(true);
    }
    
    // Method to set placeholder text and add FocusListener
    private static void setPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
    
 // Method to add a line border to components
    private static void addBorder(JComponent component) {
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        component.setBorder(lineBorder);
    }
}
