package bot.addOffers.settings.POJOHibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "campaign", schema = "spring", catalog = "")
public class CampaignEntity {
    private int id;
    private String name;
    private String flow;
    private String voluumFullId;
    private String voluumShortId;
    private long impressions;
    private int spent;
    private int statusCompaign;
    private int conversions;
    private byte cloak;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "flow")
    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    @Basic
    @Column(name = "voluum_full_id")
    public String getVoluumFullId() {
        return voluumFullId;
    }

    public void setVoluumFullId(String voluumFullId) {
        this.voluumFullId = voluumFullId;
    }

    @Basic
    @Column(name = "voluum_short_id")
    public String getVoluumShortId() {
        return voluumShortId;
    }

    public void setVoluumShortId(String voluumShortId) {
        this.voluumShortId = voluumShortId;
    }

    @Basic
    @Column(name = "impressions")
    public long getImpressions() {
        return impressions;
    }

    public void setImpressions(long impressions) {
        this.impressions = impressions;
    }

    @Basic
    @Column(name = "spent")
    public int getSpent() {
        return spent;
    }

    public void setSpent(int spent) {
        this.spent = spent;
    }

    @Basic
    @Column(name = "status_compaign")
    public int getStatusCompaign() {
        return statusCompaign;
    }

    public void setStatusCompaign(int statusCompaign) {
        this.statusCompaign = statusCompaign;
    }

    @Basic
    @Column(name = "conversions")
    public int getConversions() {
        return conversions;
    }

    public void setConversions(int conversions) {
        this.conversions = conversions;
    }

    @Basic
    @Column(name = "cloak")
    public byte getCloak() {
        return cloak;
    }

    public void setCloak(byte cloak) {
        this.cloak = cloak;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampaignEntity that = (CampaignEntity) o;
        return id == that.id &&
                impressions == that.impressions &&
                spent == that.spent &&
                statusCompaign == that.statusCompaign &&
                conversions == that.conversions &&
                cloak == that.cloak &&
                Objects.equals(name, that.name) &&
                Objects.equals(flow, that.flow) &&
                Objects.equals(voluumFullId, that.voluumFullId) &&
                Objects.equals(voluumShortId, that.voluumShortId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, flow, voluumFullId, voluumShortId, impressions, spent, statusCompaign, conversions, cloak);
    }
}
