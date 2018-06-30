package bot.addOffers.settings;

import bot.addOffers.settings.POJO.Campaigns;
import bot.addOffers.settings.POJO.Creative;
import bot.addOffers.settings.POJO.Offer;
import bot.addOffers.settings.POJO.Offers;
import bot.addOffers.settings.POJOHibernate.AffiliateNetworksEntity;
import bot.addOffers.settings.POJOHibernate.UsersEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClickDealer {

    private static HttpClient httpClient;
    private static String UA = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:55.0) Gecko/20100101 Firefox/55.0";
    private static List<Offer> offerList = new ArrayList<>();
    private static Session SESSION;
    private static String DOMAIN;
    private static String TRAFFIC_TYPE;
    private static String OFFER_ID;
    private static String AFFILIATE_LOGIN;
    private static String AFFILIATE_PASSWORD;
    private static String AFFILIATE_ID;
    private static String API_KEY;
    private static String CREO_ID;
    private static String AFFILIATE_NETWORK_NAME;
    private static UsersEntity usersEntity;
    private static String BOT_ANSWER;

    final static Logger logger = Logger.getLogger(ClickDealer.class);

    public String startClickdealer(CSVRecord csvRecord, int offerIndex, UsersEntity usersEntity, Session session) throws IOException {

        AFFILIATE_NETWORK_NAME = csvRecord.get("affiliate");
        OFFER_ID = csvRecord.get("id");
        CREO_ID = csvRecord.get("creo");
        System.out.println(AFFILIATE_NETWORK_NAME);
        this.usersEntity = usersEntity;
        SESSION = session;
//        session = HibernateSessionFactory.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
        Query query = session.createQuery("from AffiliateNetworksEntity where nameVoluum like :param");
        query.setParameter("param", "%" + AFFILIATE_NETWORK_NAME + "%");
        AffiliateNetworksEntity networkEntity = (AffiliateNetworksEntity) query.list().get(0);
        API_KEY = networkEntity.getApiKey();
        AFFILIATE_ID = networkEntity.getAffiliateId();
        AFFILIATE_LOGIN = networkEntity.getLogin();
        AFFILIATE_PASSWORD = networkEntity.getPassword();

//        try {
//            addAll();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        setupClient();
        System.out.println(startOffers(networkEntity, offerIndex));
        return BOT_ANSWER;
    }


    public static void setupClient(){

        ZoneId zoneId = ZoneId.of("-05:00");
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        String time_now = DateTimeFormatter.ofPattern("yyyy-M-dd").format(time);
        System.out.println(time_now);

        List<Header> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.USER_AGENT, UA),
                new BasicHeader(HttpHeaders.ACCEPT, "application/json"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
                new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

        HttpHost proxy = new HttpHost("127.0.0.1", 8080);
        httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .setProxy(proxy)
                .build();
    }


    public static void addAll() throws IOException, InterruptedException {

        ZoneId zoneId = ZoneId.of("-05:00");
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        String time_now = DateTimeFormatter.ofPattern("yyyy-M-dd").format(time);
        System.out.println(time_now);

//
        List<Header> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.USER_AGENT, UA),
                new BasicHeader(HttpHeaders.ACCEPT, "application/json"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
                new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

        HttpHost proxy = new HttpHost("127.0.0.1", 8080);
        httpClient = HttpClientBuilder.create()
            .setDefaultHeaders(headers)
            .setProxy(proxy)
            .build();

        HttpGet get = new HttpGet("https://partners.clickdealer.com/affiliates/api/1/offers.asmx/OfferFeed?api_key=" + API_KEY + "&affiliate_id=" + AFFILIATE_ID +"&all" +
                "owed_countries=au,ca,de,it,no,uk,nz,pl,es,gb,us,dk,cz,fi,be,at,bg,gr,pt,ch,sw&vertical_id=50,53&offer_status_id=1,2,3&&platform=1,2,3,4&flow=5,6&adult=1");
        String out = makeRequest(httpClient, get);
        ObjectMapper objectMapper = new ObjectMapper();
        Offers offers = objectMapper.readValue(out, Offers.class);
        String[] stringBuffer = new String[offers.getOffers().size()];
        int i = 0;

        for (Map.Entry<String, Offer> entry : offers.getOffers().entrySet()){
            Offer offer = entry.getValue();
            System.out.println(offer.getOffer_id());
            System.out.println(offer.getAllowed_countries().get(0));
            System.out.println(offer.getOffer_name());
            System.out.println(offer.getPayout_converted());
            stringBuffer[i] = offer.getOffer_id();
            System.out.println(offer.getOffer_id());
            i++;

            if (i % 10 == 0){
                Thread.sleep(10000);
            }

            if (offer.getOffer_status().containsValue("2")){
                get = new HttpGet("https://partners.clickdealer.com/affiliates/api/1/offers.asmx/ApplyForOffer?api_key=" + API_KEY + "&affiliate_id=" + AFFILIATE_ID +"&" +
                        "offer_id=" + offer.getOffer_id() + "&offer_contract_id=" + offer.getOffer_contract_id() + "&media_type_id=7&notes=banners&agreed_to_terms=TRUE");
                makeRequest(httpClient, get);
            }
        }

        String join = String.join(",", Arrays.asList(stringBuffer));
        System.out.println(join);

        get = new HttpGet("https://partners.clickdealer.com/affiliates/api/1/offers.asmx/GetCampaign?api_key=" + API_KEY + "&affiliate_id=" + AFFILIATE_ID +"&offer_id=" + join);
        out = makeRequest(httpClient, get);

        int ind = 0;
        Campaigns campaigns = objectMapper.readValue(out, Campaigns.class);

        for (Map.Entry<String, Offer> entry : campaigns.get().entrySet()){
            ind++;
            Offer offer = entry.getValue();
            offerList.add(offer);
            offer.setLink(offer.getCreatives().get(0).getUnique_link().replace("\\/", "/"));
            offer.setLink(offer.getLink()  + "&s1={os}&s3={osversion}&s2=");
            System.out.println(offer.getLink());

            if (ind > 22) {
//                new Voluum(true, offer, ind, );
            }
        }
    }

    private static String startOffers(AffiliateNetworksEntity networkEntity, int index) throws IOException {

        HttpGet get = new HttpGet("https://partners.clickdealer.com/affiliates/api/1/offers.asmx/OfferFeed?api_key=" + API_KEY + "&affiliate_id=" + AFFILIATE_ID +"&campaign_name=" + OFFER_ID);
        String out = makeRequest(httpClient, get);

        int i = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        Offers offers = objectMapper.readValue(out, Offers.class); //exception catch if offer not available

//         stringBuffer = new String[offers.getOffers().size()];
        String output = "offer not available";

        if (offers.getOffers() != null) {
            for (Map.Entry<String, Offer> entry : offers.getOffers().entrySet()) {
                Offer offer = entry.getValue();
//            String stringBuffer = offer.getOffer_id();
                i++;

                if (offer.getOffer_status().containsValue("2")) {
                    get = new HttpGet("https://partners.clickdealer.com/affiliates/api/1/offers.asmx/ApplyForOffer?api_key=" + API_KEY + "&affiliate_id=" + AFFILIATE_ID +"&" +
                            "offer_id=" + offer.getOffer_id() + "&offer_contract_id=" + offer.getOffer_contract_id() + "&media_type_id=7&notes=banners&agreed_to_terms=TRUE");
                    makeRequest(httpClient, get);
                    output = "offer apply to run";
                    logger.warn("offer apply" + offer.getStatus());
                    break;
                }

                get = new HttpGet("https://partners.clickdealer.com/affiliates/api/1/offers.asmx/GetCampaign?api_key=" + API_KEY + "&affiliate_id=" + AFFILIATE_ID +"&offer_id=" + OFFER_ID);
                out = makeRequest(httpClient, get);
                Campaigns campaigns = objectMapper.readValue(out, Campaigns.class);

                if (i % 5 == 0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (Map.Entry<String, Offer> entryInner : campaigns.get().entrySet()) {

                    offer = entryInner.getValue();
                    offerList.add(offer);

                    for (int countCreo = 0; countCreo < offer.getCreatives().size(); countCreo++) {
                        Creative creative = offer.getCreatives().get(countCreo);
                        System.out.println("creo " + creative.getCreative_id());

                        if (!CREO_ID.equals("") && creative.getCreative_id().contains(CREO_ID)) {
                            offer.setLink(offer.getCreatives().get(countCreo).getUnique_link().replace("\\/", "/"));
                            offer.setLink(offer.getLink() + "&s1={os}&s3={osversion}&s2=");
                            break;
                        } else {
                            offer.setLink(offer.getCreatives().get(0).getUnique_link().replace("\\/", "/"));
                            offer.setLink(offer.getLink() + "&s1={os}&s3={osversion}&s2=");
                        }
                    }
                    System.out.println(CREO_ID + " " + offer.getLink());
                    try {
                        new Voluum(true, offer, index, networkEntity, usersEntity);
                    } catch (Voluum.MyException e) {
                        BOT_ANSWER = "offer id - " + OFFER_ID + " " + e.getMessage();
                        return BOT_ANSWER;
                    }

                    output = String.format("offer %s complete. link - %s, compaign id - %s, name - %s",
                            OFFER_ID, offer.getLink(), offer.getCampaign_id(), offer.getName());
                }


            }
        }

        BOT_ANSWER = output;
        return BOT_ANSWER;
    }

    private static String makeRequest(HttpClient httpClient, HttpRequestBase httpPost) throws IOException {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                logger.info("error");
                return makeRequest(httpClient, httpPost);
            }
        };
        String responseBody = httpClient.execute(httpPost, responseHandler);
//        System.out.println(responseBody);
        if (responseBody == null){
            makeRequest(httpClient, httpPost);
        }
        return responseBody;
    }
}
