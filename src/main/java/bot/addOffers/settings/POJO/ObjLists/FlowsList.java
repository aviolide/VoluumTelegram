package bot.addOffers.settings.POJO.ObjLists;

import bot.addOffers.settings.POJO.CommonObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowsList {

    private List<CommonObject> rows;

    public List<CommonObject> getRows() {
        return rows;
    }

    public void setRows(List<CommonObject> rows) {
        this.rows = rows;
    }
}
