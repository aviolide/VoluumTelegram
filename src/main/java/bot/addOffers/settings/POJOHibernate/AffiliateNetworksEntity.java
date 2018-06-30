package bot.addOffers.settings.POJOHibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "affiliate_networks", schema = "spring", catalog = "")
public class AffiliateNetworksEntity {
    private int id;
    private String nick;
    private String idVoluum;
    private String nameVoluum;
    private String login;
    private String password;
    private String domainMob;
    private String domainWeb;
    private String affiliateId;
    private String apiKey;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "nick")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Basic
    @Column(name = "id_voluum")
    public String getIdVoluum() {
        return idVoluum;
    }

    public void setIdVoluum(String idVoluum) {
        this.idVoluum = idVoluum;
    }

    @Basic
    @Column(name = "name_voluum")
    public String getNameVoluum() {
        return nameVoluum;
    }

    public void setNameVoluum(String nameVoluum) {
        this.nameVoluum = nameVoluum;
    }

    @Basic
    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "domain_mob")
    public String getDomainMob() {
        return domainMob;
    }

    public void setDomainMob(String domainMob) {
        this.domainMob = domainMob;
    }

    @Basic
    @Column(name = "domain_web")
    public String getDomainWeb() {
        return domainWeb;
    }

    public void setDomainWeb(String domainWeb) {
        this.domainWeb = domainWeb;
    }

    @Basic
    @Column(name = "affiliate_id")
    public String getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(String affiliateId) {
        this.affiliateId = affiliateId;
    }

    @Basic
    @Column(name = "api_key")
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AffiliateNetworksEntity that = (AffiliateNetworksEntity) o;
        return id == that.id &&
                Objects.equals(nick, that.nick) &&
                Objects.equals(idVoluum, that.idVoluum) &&
                Objects.equals(nameVoluum, that.nameVoluum) &&
                Objects.equals(login, that.login) &&
                Objects.equals(password, that.password) &&
                Objects.equals(domainMob, that.domainMob) &&
                Objects.equals(domainWeb, that.domainWeb) &&
                Objects.equals(affiliateId, that.affiliateId) &&
                Objects.equals(apiKey, that.apiKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, nick, idVoluum, nameVoluum, login, password, domainMob, domainWeb, affiliateId, apiKey);
    }
}
