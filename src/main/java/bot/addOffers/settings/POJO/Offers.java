package bot.addOffers.settings.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonRootName(value = "offers")
public class Offers {

    @JsonProperty
    private Map<String, Offer> offers;

    public Map<String, Offer> getOffers() {
        return offers;
    }

    public void setOffers(Map<String, Offer> offers) {
        this.offers = offers;
    }

    @JsonProperty
    private List<Offer> rows;

    public List<Offer> getRows() {
        return rows;
    }

    public void setRows(List<Offer> rows) {
        this.rows = rows;
    }
}
