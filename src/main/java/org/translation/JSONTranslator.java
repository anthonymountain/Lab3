package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    public static final String ALPHA3 = "alpha3";
    private List<String> countryCodes;
    private List<JSONObject> countryData;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            countryCodes = new ArrayList<>();
            countryData = new ArrayList<>();

            // Loop through each country in the JSON array and store relevant information
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject country = jsonArray.getJSONObject(i);
                countryCodes.add(country.getString(ALPHA3));
                countryData.add(country);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        for (JSONObject countries : countryData) {
            if (countries.getString(ALPHA3).equalsIgnoreCase(country)) {
                List<String> languageCodes = new ArrayList<>();
                for (String key : countries.keySet()) {
                    if (!"alpha2".equals(key) && !ALPHA3.equals(key) && !"id".equals(key)) {
                        languageCodes.add(key);
                    }
                }
                return languageCodes;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryCodes);
    }

    @Override
    public String translate(String country, String language) {
        for (JSONObject countries : countryData) {
            if (countries.getString(ALPHA3).equalsIgnoreCase(country)) {
                return countries.getString(language);
            }
        }
        return "Country not found";
    }
}
