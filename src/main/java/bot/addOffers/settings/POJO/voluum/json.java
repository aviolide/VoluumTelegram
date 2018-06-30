package bot.addOffers.settings.POJO.voluum;

import bot.addOffers.settings.POJO.Offer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class json {
    public static void main(String[] args) throws IOException {
        FlowVoluum flowVoluum = new ObjectMapper().readValue(new File("files/jsontest"), FlowVoluum.class);
        OfferListVoluum offerListVoluum = new OfferListVoluum();
        offerListVoluum.setWeight(100);
        flowVoluum.getDefaultPaths().get(0).getOffers().add(offerListVoluum);

        System.out.println(flowVoluum.getId());
        new ObjectMapper().writeValue(new File("files/jsonout"), flowVoluum);
    }
}
