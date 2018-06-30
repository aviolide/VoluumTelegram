package bot.addOffers.settings.POJOHibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "spring", catalog = "")
@IdClass(UsersEntityPK.class)
public class UsersEntity {
    private int userId;
    private String email;
    private String nick;
    private String login;
    private String chatId;
    private String voluumLogin;
    private String voluumPassword;
    private String voluumAccessId;
    private String voluumAccessKey;
    private String voluumClientId;
    private String workspace;
    private String workspaceId;
    private Integer offerCount;
    private Integer del;

    @Id
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Id
    @Column(name = "nick")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
    @Column(name = "chat_id")
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Basic
    @Column(name = "voluum_login")
    public String getVoluumLogin() {
        return voluumLogin;
    }

    public void setVoluumLogin(String voluumLogin) {
        this.voluumLogin = voluumLogin;
    }

    @Basic
    @Column(name = "voluum_password")
    public String getVoluumPassword() {
        return voluumPassword;
    }

    public void setVoluumPassword(String voluumPassword) {
        this.voluumPassword = voluumPassword;
    }

    @Basic
    @Column(name = "voluum_access_id")
    public String getVoluumAccessId() {
        return voluumAccessId;
    }

    public void setVoluumAccessId(String voluumAccessId) {
        this.voluumAccessId = voluumAccessId;
    }

    @Basic
    @Column(name = "voluum_access_key")
    public String getVoluumAccessKey() {
        return voluumAccessKey;
    }

    public void setVoluumAccessKey(String voluumAccessKey) {
        this.voluumAccessKey = voluumAccessKey;
    }

    @Basic
    @Column(name = "voluum_client_id")
    public String getVoluumClientId() {
        return voluumClientId;
    }

    public void setVoluumClientId(String voluumClientId) {
        this.voluumClientId = voluumClientId;
    }

    @Basic
    @Column(name = "workspace")
    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    @Basic
    @Column(name = "workspace_id")
    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    @Basic
    @Column(name = "offer_count")
    public Integer getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(Integer offerCount) {
        this.offerCount = offerCount;
    }

    @Basic
    @Column(name = "del")
    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return userId == that.userId &&
                Objects.equals(email, that.email) &&
                Objects.equals(nick, that.nick) &&
                Objects.equals(login, that.login) &&
                Objects.equals(chatId, that.chatId) &&
                Objects.equals(voluumLogin, that.voluumLogin) &&
                Objects.equals(voluumPassword, that.voluumPassword) &&
                Objects.equals(voluumAccessId, that.voluumAccessId) &&
                Objects.equals(voluumAccessKey, that.voluumAccessKey) &&
                Objects.equals(voluumClientId, that.voluumClientId) &&
                Objects.equals(workspace, that.workspace) &&
                Objects.equals(workspaceId, that.workspaceId) &&
                Objects.equals(offerCount, that.offerCount) &&
                Objects.equals(del, that.del);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, email, nick, login, chatId, voluumLogin, voluumPassword, voluumAccessId, voluumAccessKey, voluumClientId, workspace, workspaceId, offerCount, del);
    }
}
