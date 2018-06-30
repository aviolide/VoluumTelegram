package bot.addOffers.settings.POJOHibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "countries_settings", schema = "spring", catalog = "")
public class CountriesSettingsEntity {
    private int id;
    private String countryCode;
    private String countryName;
    private String cost;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "country_code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Basic
    @Column(name = "country_name")
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Basic
    @Column(name = "cost")
    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountriesSettingsEntity that = (CountriesSettingsEntity) o;
        return id == that.id &&
                Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(countryName, that.countryName) &&
                Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, countryCode, countryName, cost);
    }
}
