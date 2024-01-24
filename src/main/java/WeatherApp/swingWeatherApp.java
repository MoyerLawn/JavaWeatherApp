package WeatherApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class swingWeatherApp {

    public static JLabel resultLabel = new JLabel("");
    private static final String CITY_PLACEHOLDER = "Enter your city...";
    private static final String API_KEY = "409009ca0e73e6e88ff13b0440a0206a";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(swingWeatherApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                enforceMinMaxSize(frame);
            }
        });

        JPanel panel = createPanel();
        frame.add(panel);

        frame.setVisible(true);
    }

    private static JPanel createPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel label = new JLabel("Need your weather?");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField cityTextField = createCityTextField();

        JButton button = createWeatherButton(cityTextField);

        panel.add(label);
        panel.add(cityTextField);
        panel.add(button);
        panel.add(resultLabel);

        return panel;
    }

    private static JTextField createCityTextField() {
        JTextField cityTextField = new JTextField(CITY_PLACEHOLDER);
        cityTextField.setEditable(false);
        addBorder(cityTextField);

        cityTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCityTextFieldClick(cityTextField);
            }
        });

        return cityTextField;
    }

    private static JButton createWeatherButton(JTextField cityTextField) {
        JButton button = new JButton("Get Weather");
        addBorder(button);

        button.addActionListener(e -> handleWeatherButtonClick(cityTextField));

        return button;
    }

    private static void handleCityTextFieldClick(JTextField cityTextField) {
        String currentText = cityTextField.getText();
        if (currentText.isEmpty() || currentText.equals(CITY_PLACEHOLDER)) {
            cityTextField.setEditable(true);
            cityTextField.setText("");
            cityTextField.requestFocusInWindow();
        }
    }

    private static void handleWeatherButtonClick(JTextField cityTextField) {
        String city = cityTextField.getText();

        if (city.contains(" ")) {
            city = city.replace(" ", "%20");
        }

        if (city.isEmpty()) {
            // Show an alert or message
            JOptionPane.showMessageDialog(null, "City field must be populated.");
        } else {
            makeApiRequest(city);
        }
    }

    private static void makeApiRequest(String city) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            String apiUrl = API_URL + city + "&appid=" + API_KEY;

            HttpGet request = new HttpGet(apiUrl);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                // Process the API response
                processApiResponse(response);
            } else {
                // Handle failed API request
                resultLabel.setText("Failed to fetch weather data. Check your API key and city");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultLabel.setText("An error occurred while fetching weather data.");
        }
    }

    private static void processApiResponse(HttpResponse response) {
        try {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println(responseBody);

            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONObject main = jsonResponse.getJSONObject("main");
            JSONArray weatherArray = jsonResponse.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);

            double tempInKelvin = main.getBigDecimal("temp").doubleValue();
            double tempInFahrenheit = (tempInKelvin - 273.15) * 9/5 + 32;

            DecimalFormat temperatureFormat = new DecimalFormat("#");
            String formattedTemperature = temperatureFormat.format(tempInFahrenheit);

            String description = weather.getString("description");

            resultLabel.setText("Temperature: " + formattedTemperature + " °F\n Description: " + description);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultLabel.setText("An error occurred while processing weather data.");
        }
    }

    private static void addBorder(JComponent component) {
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        component.setBorder(lineBorder);
    }

    private static void enforceMinMaxSize(JFrame frame) {
        // Set minimum and maximum sizes
        Dimension minSize = new Dimension(300, 200);
        Dimension maxSize = new Dimension(500, 400);

        int width = frame.getWidth();
        int height = frame.getHeight();

        // Enforce minimum size
        if (width < minSize.width || height < minSize.height) {
            frame.setSize(
                Math.max(width, minSize.width),
                Math.max(height, minSize.height)
            );
        }

        // Enforce maximum size
        if (width > maxSize.width || height > maxSize.height) {
            frame.setSize(
                Math.min(width, maxSize.width),
                Math.min(height, maxSize.height)
            );
        }
    }
}
