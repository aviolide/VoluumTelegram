package bot.addOffers.settings.POJOHibernate;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class UsersEntityPK implements Serializable {
    private int userId;
    private String nick;

    @Column(name = "user_id")
    @Id
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "nick")
    @Id
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntityPK that = (UsersEntityPK) o;
        return userId == that.userId &&
                Objects.equals(nick, that.nick);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, nick);
    }
}
