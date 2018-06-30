package bot.addOffers.settings.POJO;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(value = { "row_count", "success" })
public class Campaigns {

    private Map<String, Offer> properties = new HashMap<>();

    public Map<String, Offer> get() {
        return properties;
    }
    @JsonAnySetter
    public void set(String fieldName, Offer value) {
        System.out.println(fieldName);
        this.properties.put(fieldName, value);
    }
}
