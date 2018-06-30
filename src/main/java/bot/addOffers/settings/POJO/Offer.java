package bot.addOffers.settings.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Offer {

    private Offer offer;
    private String offer_contract_id;
    private String campaign_id;
    private String offer_name;
    private String vertical_id;
    private Map<String, String> offer_status;
    private String payout_converted;
    private List<Allowed_Countries> allowed_countries;
    private List<Creative> creatives;

    @JsonProperty(value = "rows")
    private List<Creative> advidi_creatives;
    private String link;

    private String id;
    private String contract_id;
    private String status;
    private String price_converted;
    private String name;



    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    private String offer_id;

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_contract_id() {
        return offer_contract_id;
    }

    public void setOffer_contract_id(String offer_contract_id) {
        this.offer_contract_id = offer_contract_id;
    }

    public String getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(String campaign_id) {
        this.campaign_id = campaign_id;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    public String getVertical_id() {
        return vertical_id;
    }

    public void setVertical_id(String vertical_id) {
        this.vertical_id = vertical_id;
    }

    public Map<String, String> getOffer_status() {
        return offer_status;
    }

    public void setOffer_status(Map<String, String> offer_status) {
        this.offer_status = offer_status;
    }

    public String getPayout_converted() {
        return payout_converted;
    }

    public void setPayout_converted(String payout_converted) {
        this.payout_converted = payout_converted;
    }

    public List<Creative> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Creative> creatives) {
        this.creatives = creatives;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Allowed_Countries> getAllowed_countries() {
        return allowed_countries;
    }

    public void setAllowed_countries(List<Allowed_Countries> allowed_countries) {
        this.allowed_countries = allowed_countries;
    }

    public String getContract_id() {
        return contract_id;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice_converted() {
        return price_converted;
    }

    public void setPrice_converted(String price_converted) {
        this.price_converted = price_converted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Creative> getAdvidi_creatives() {
        return advidi_creatives;
    }

    public void setAdvidi_creatives(List<Creative> advidi_creatives) {
        this.advidi_creatives = advidi_creatives;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
