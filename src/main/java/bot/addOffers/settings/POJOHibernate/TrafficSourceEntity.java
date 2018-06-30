package bot.addOffers.settings.POJOHibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "traffic_source", schema = "spring", catalog = "")
public class TrafficSourceEntity {
    private int id;
    private String idVoluum;
    private String nameVoluum;
    private String login;
    private String password;
    private String nickname;
    private String nickId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "nickname")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Basic
    @Column(name = "nick_id")
    public String getNickId() {
        return nickId;
    }

    public void setNickId(String nickId) {
        this.nickId = nickId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrafficSourceEntity that = (TrafficSourceEntity) o;
        return id == that.id &&
                Objects.equals(idVoluum, that.idVoluum) &&
                Objects.equals(nameVoluum, that.nameVoluum) &&
                Objects.equals(login, that.login) &&
                Objects.equals(password, that.password) &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(nickId, that.nickId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, idVoluum, nameVoluum, login, password, nickname, nickId);
    }
}
