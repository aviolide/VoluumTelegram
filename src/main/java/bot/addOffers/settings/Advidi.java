package bot.addOffers.settings;


import bot.addOffers.settings.POJO.Allowed_Countries;
import bot.addOffers.settings.POJO.Creative;
import bot.addOffers.settings.POJO.Offer;
import bot.addOffers.settings.POJO.Offers;
import bot.addOffers.settings.POJOHibernate.AffiliateNetworksEntity;
import bot.addOffers.settings.POJOHibernate.CountriesSettingsEntity;
import bot.addOffers.settings.POJOHibernate.UsersEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.dialect.MySQL5Dialect;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Advidi {

    private static HttpClient httpClient;
    private static String UA = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:55.0) Gecko/20100101 Firefox/55.0";
    final static Logger logger = Logger.getLogger(ClickDealer.class);
    private static UsersEntity usersEntity;

    private static Session SESSION;
    private static String DOMAIN;
    private static String TRAFFIC_TYPE;
    private static String OFFER_ID;
    private static String AFFILIATE_LOGIN;
    private static String AFFILIATE_PASSWORD;
    private static String AFFILIATE_NETWORK_NAME;
    private static String CREO_ID;
    private static String BOT_ANSWER;

    public static String startAdvidi(CSVRecord csvRecord, int offerIndex, UsersEntity users, Session session) throws IOException {

        usersEntity = users;
        AFFILIATE_NETWORK_NAME = csvRecord.get("affiliate");
        OFFER_ID = csvRecord.get("id");
        CREO_ID = csvRecord.get("creo");
        System.out.println(AFFILIATE_NETWORK_NAME);
        SESSION = session;
//        session = HibernateSessionFactory.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
        Query query = session.createQuery("from AffiliateNetworksEntity where nameVoluum like :param");
        query.setParameter("param", "%" + AFFILIATE_NETWORK_NAME + "%");
        AffiliateNetworksEntity networkEntity = (AffiliateNetworksEntity) query.list().get(0);
        AFFILIATE_LOGIN = networkEntity.getLogin();
        AFFILIATE_PASSWORD = networkEntity.getPassword();

        setupClient();
        System.out.println(startOffers(networkEntity, offerIndex));
        return BOT_ANSWER;
    }
    private static void setupClient() throws IOException {

        ZoneId zoneId = ZoneId.of("-05:00");
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        String time_now = DateTimeFormatter.ofPattern("yyyy-M-dd").format(time);
        System.out.println(time_now);

        List<Header> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.USER_AGENT, UA),
                new BasicHeader(HttpHeaders.ACCEPT, "*/*"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
                new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
        );

        HttpHost proxy = new HttpHost("127.0.0.1", 8080);
        httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .setProxy(proxy)
                .build();

        HttpPost post = new HttpPost("https://ctrack.Advidi.com/login.ashx");
        String body = "u=" + AFFILIATE_LOGIN + "&p=" + AFFILIATE_PASSWORD + "";
        StringEntity stringEntity = new StringEntity(body);
        post.setEntity(stringEntity);

        HttpResponse response = httpClient.execute(post);
        Header[] headers1 = response.getHeaders("Set-Cookie");
        Header hd = headers1[0];
        System.out.println(hd.getValue());
        List<Header> authHeaders = Arrays.asList(response.getHeaders("Set-Cookie"));
        String cookieJoin = String.join("; ", authHeaders.get(0).getValue(),
                authHeaders.get(1).getValue());
        System.out.println(cookieJoin);

        List<Header> newHeaders = Arrays.asList(
                new BasicHeader("X-Requested-With", "XMLHttpRequest"),
                new BasicHeader(HttpHeaders.REFERER, "https://ctrack.Advidi.com/affiliates/"),
                new BasicHeader("Cookie", cookieJoin)
        );
        List<Header> finalHeaders = new ArrayList<>(headers);
        finalHeaders.addAll(newHeaders);
        httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(finalHeaders)
                .setProxy(proxy)
                .build();

    }

    private static String startOffers(AffiliateNetworksEntity AffiliateNetworksEntity, int offerIndex) throws IOException {


        HttpPost post = new HttpPost("https://ctrack.Advidi.com/affiliates/Extjs.ashx?s=contracts");
        String body = "groupBy=&groupDir=ASC&cu=1&c=" + OFFER_ID + "&cat=0&sv=&cn=&pf=&st=0&m=&ct=&pmin=&pmax=&mycurr=true&t=&p=0&n=30";
        StringEntity stringEntity = new StringEntity(body);
        post.setEntity(stringEntity);
        String out = makeRequest(httpClient, post);
        String output = null;

        ObjectMapper objectMapper = new ObjectMapper();
        Offers offers = objectMapper.readValue(out, Offers.class);

        for (Offer offer : offers.getRows()){
//            Offer offer = entry.getValue();

            String offerName = offer.getName();
            offer.setOffer_id(offer.getCampaign_id());
            offer.setOffer_name(offerName);
            offer.setPayout_converted(new BigDecimal(offer.getPrice_converted()).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros().toString());
            String country_code;
            String country_name;

            if (offer.getStatus().equals("Public")){
                out = applyPublicOffer(offer);
                ObjectNode node = new ObjectMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                        .readValue(out, ObjectNode.class);
                offer.setContract_id(node.get("contract_id").asText());

            } else if (offer.getStatus().equals("Apply To Run")){
                applyPublicOffer(offer);
                BOT_ANSWER = OFFER_ID + " Apply To Run";
                System.out.println(BOT_ANSWER);
                return BOT_ANSWER;
            } else if (offer.getPrice_converted() == null){
                BOT_ANSWER = OFFER_ID + " not available price";
                System.out.println(BOT_ANSWER);
                return BOT_ANSWER;
            }


            try {
                String countryLine = offerName.substring(offerName.lastIndexOf("- "), offerName.indexOf(" ["));
                countryLine = countryLine.replace("- ", "");

                if(countryLine.contains("UK")){
                    countryLine = countryLine.replace("UK", "GB");
                }

                if (countryLine.matches(".*\\d+.*")){
                    break;
                } else if (countryLine.contains("/")){

                    List<String> splitList = Arrays.asList(countryLine.split("/"));
                    List<Allowed_Countries> allowedCountriesList = new ArrayList<>();
                    for (String codeStr : splitList) {

                        Allowed_Countries allowed_countries = new Allowed_Countries();
                        allowed_countries.setCountry_code(codeStr.toLowerCase());
                        System.out.println(countryLine);
                        Query query = SESSION.createQuery("from CountriesSettingsEntity where countryCode = :param");
                        query.setParameter("param", codeStr.toLowerCase());
                        List<CountriesSettingsEntity> countriesList = query.list();

                        allowed_countries.setCountry_name(countriesList.get(0).getCountryName());
                        allowedCountriesList.add(allowed_countries);
                    }
                    offer.setAllowed_countries(allowedCountriesList);

                } else {
                    List<Allowed_Countries> allowedCountriesList = new ArrayList<>();
                    Allowed_Countries allowed_countries = new Allowed_Countries();
                    allowed_countries.setCountry_code(countryLine.toLowerCase());

                    Query query = SESSION.createQuery("from CountriesSettingsEntity where countryCode = :param");
                    query.setParameter("param", countryLine.toLowerCase());
                    List<CountriesSettingsEntity> countriesList = query.list();

                    allowed_countries.setCountry_name(countriesList.get(0).getCountryName());
                    allowedCountriesList.add(allowed_countries);
                    offer.setAllowed_countries(allowedCountriesList);
                }
                System.out.println(countryLine);

            } catch (StringIndexOutOfBoundsException ex){
                System.out.println(ex.toString());
            }

            country_code = offer.getAllowed_countries().get(0).getCountry_code().toUpperCase();
            country_name = offer.getAllowed_countries().get(0).getCountry_name();
            if (offer.getOffer_name().toLowerCase().contains("web") || offer.getOffer_name().toLowerCase().contains("responsive")){
                TRAFFIC_TYPE = "WEB";
                DOMAIN = AffiliateNetworksEntity.getDomainWeb();
                DOMAIN = DOMAIN.replace("{country}", country_code);
            } else {
                TRAFFIC_TYPE = "WAP";
                DOMAIN = AffiliateNetworksEntity.getDomainMob();
                DOMAIN = DOMAIN.replace("{country}", country_code.toLowerCase());
            }

            post = new HttpPost("https://ctrack.Advidi.com/affiliates/Extjs.ashx?s=creatives&cont_id=" + offer.getContract_id() + "");
            body = "groupBy=&groupDir=ASC";
            stringEntity = new StringEntity(body);
            post.setEntity(stringEntity);
            out = makeRequest(httpClient, post); //return null if offer pending

            if (out.equals("")){
                return "offer " + OFFER_ID + " is pending, try later";
            }
            objectMapper = new ObjectMapper();
            offer.setAdvidi_creatives(objectMapper.readValue(out, Offer.class).getAdvidi_creatives());

            for (int countCreo = 0; countCreo < offer.getAdvidi_creatives().size(); countCreo++){
                Creative creative = offer.getAdvidi_creatives().get(countCreo);
                System.out.println("creo " + creative.getId());

                if (!CREO_ID.equals("") && creative.getId().contains(CREO_ID)){
                    offer.setLink(creative.getUnique_link()  + "{os}&s3={osversion}&s2=");
                    break;
                } else {
                    offer.setLink(offer.getAdvidi_creatives().get(0).getUnique_link() + "{os}&s3={osversion}&s2=");
                }
                System.out.println(creative.getCreative_id() + " " + offer.getLink());
            }

            output = String.format("offer %s complete. link - %s, campaign id - %s, name - %s",
                    OFFER_ID, offer.getLink(), offer.getCampaign_id(), offer.getOffer_name());

            System.out.println(TRAFFIC_TYPE);
            System.out.println(DOMAIN);
            System.out.println(country_code);
            System.out.println(country_name);
            System.out.println(offer.getOffer_name());
            System.out.println(offer.getPayout_converted());
            System.out.println(CREO_ID + " " + offer.getLink());
            try {
                new Voluum(true, offer, offerIndex, AffiliateNetworksEntity, usersEntity);//exception proxy propeller
            } catch (Voluum.MyException e) {
                BOT_ANSWER = "offer id - " + OFFER_ID + " " + e.getMessage();
                return BOT_ANSWER;
            }
            break;
        }

        BOT_ANSWER = output;
        if (offers.getRows().size() == 0) {
            BOT_ANSWER = OFFER_ID + " offer not available";
            System.out.println(BOT_ANSWER);
            return BOT_ANSWER;
        }
        return BOT_ANSWER;
    }

    private static String applyPublicOffer(Offer offer) throws IOException {

        HttpPost post = new HttpPost("https://ctrack.Advidi.com/affiliates/Extjs.ashx?s=public&agreed=1");
        String body = "media_type=7&notes=&ccid=" + offer.getId() + "";
        StringEntity stringEntity = new StringEntity(body);
        post.setEntity(stringEntity);
        return makeRequest(httpClient, post);
    }

    private static String makeRequest(HttpClient httpClient, HttpRequestBase http) throws IOException {
        String httpMethod = http.toString().substring(http.toString().lastIndexOf("/"), http.toString().length() - 1);

        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                logger.info("error");

                return makeRequest(httpClient, http);
            }
        };

        String responseBody = httpClient.execute(http, responseHandler);
        if (responseBody == null){
            makeRequest(httpClient, http);
        }
        return responseBody;
    }
}
