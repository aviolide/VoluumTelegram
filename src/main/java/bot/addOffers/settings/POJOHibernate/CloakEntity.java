package bot.addOffers.settings.POJOHibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cloak", schema = "spring", catalog = "")
public class CloakEntity {
    private int id;
    private String nickname;
    private String cloakOfferName;
    private String cloakOfferId;
    private String cloakLanderName;
    private String cloakLanderId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "nickname")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Basic
    @Column(name = "cloak_offer_name")
    public String getCloakOfferName() {
        return cloakOfferName;
    }

    public void setCloakOfferName(String cloakOfferName) {
        this.cloakOfferName = cloakOfferName;
    }

    @Basic
    @Column(name = "cloak_offer_id")
    public String getCloakOfferId() {
        return cloakOfferId;
    }

    public void setCloakOfferId(String cloakOfferId) {
        this.cloakOfferId = cloakOfferId;
    }

    @Basic
    @Column(name = "cloak_lander_name")
    public String getCloakLanderName() {
        return cloakLanderName;
    }

    public void setCloakLanderName(String cloakLanderName) {
        this.cloakLanderName = cloakLanderName;
    }

    @Basic
    @Column(name = "cloak_lander_id")
    public String getCloakLanderId() {
        return cloakLanderId;
    }

    public void setCloakLanderId(String cloakLanderId) {
        this.cloakLanderId = cloakLanderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloakEntity that = (CloakEntity) o;
        return id == that.id &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(cloakOfferName, that.cloakOfferName) &&
                Objects.equals(cloakOfferId, that.cloakOfferId) &&
                Objects.equals(cloakLanderName, that.cloakLanderName) &&
                Objects.equals(cloakLanderId, that.cloakLanderId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, nickname, cloakOfferName, cloakOfferId, cloakLanderName, cloakLanderId);
    }
}
