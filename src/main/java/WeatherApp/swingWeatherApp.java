package WeatherApp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class swingWeatherApp
{

    public static void main (String[] args)
    {
        // Create the main frame
        JFrame frame = new JFrame("Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Create a panel to hold components
        JPanel panel = new JPanel();

        // Create a label
        JLabel label = new JLabel("Need your weather?");
        
        // Create text label for city input
        JTextField cityTextField = new JTextField("Enter your city:");

        // Create a button
        JButton button = new JButton("Get Weather");
        
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

                        resultLabel.setText(responseBody);
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
}
