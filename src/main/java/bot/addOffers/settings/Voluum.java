package bot.addOffers.settings;

import bot.addOffers.settings.POJO.CommonObject;
import bot.addOffers.settings.POJO.ObjLists.FlowsList;
import bot.addOffers.settings.POJO.Offer;
import bot.addOffers.settings.POJO.voluum.FlowVoluum;
import bot.addOffers.settings.POJO.voluum.OfferListVoluum;
import bot.addOffers.settings.POJO.voluum.OfferVoluum;
import bot.addOffers.settings.POJOHibernate.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class Voluum {

    private static Boolean flowExist = true;
    private final static Logger logger = Logger.getLogger(Voluum.class);
    private static final String UA = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:55.0) Gecko/20100101 Firefox/55.0";
    private static String TOKEN;
    private static String CONSTVAR3 = "18";
    private static String DATE;
    private HttpClient httpClient;
    private Offer offer;
    private static Session session;
    private static String OFFER_ID;
    private static String VOLUUM_ACCESS_ID;
    private static String VOLUUM_ACCESS_KEY;
    private static String CLIENT_ID;
    private static String VOLUUM_EMAIL;
    private static String VOLUUM_PASSWORD;
    private static String TRAFFICSOURCE_EMAIL;
    private static String TRAFFICSOURCE_PASSWORD;
    private static String TRAFFIC_SOURCE_ID_VOLUUM;
    private static String TRAFFICSOURCE_NAME;
    private static String DOMAIN;
    private static String TRAFFIC_TYPE;
    private static String USER_NAME;
    private static String AFFILIATE_NETWORK_NAME_VOLUUM;
    private static String AFFILIATE_NETWOK_ID;
    private static String OFFER_SHORT_URL;
    private static String COST_PAY;
    private static String VOLUUM_CAMPAIGN_LINK;
    private static String CLOAK_OFFER_NAME;
    private static String CLOAK_OFFER_ID;
    private static String CLOAK_LANDER_NAME;
    private static String CLOAK_LANDER_ID;
    private static String OFFER_WORKSPACE;
    private static String OFFER_WORKSPACE_ID;

    public Voluum(boolean loginApi, Offer offer, int off_index, AffiliateNetworksEntity AffiliateNetworksEntity, UsersEntity usersEntity) throws IOException, ParseException, MyException {

        this.offer = offer;
//
        Date date = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("ddMMyy");
        simpleDate.setTimeZone(TimeZone.getTimeZone("EST"));
        DATE = simpleDate.format(date);

//        this.DATE = setDateYear();
        session = HibernateSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
//        usersEntity = (UsersEntity) session.createQuery("from UsersEntity").list().get(0);

        System.out.println(usersEntity.getNick());
        Query query = session.createQuery("from  TrafficSourceEntity where nickname = :nickname");
        query.setParameter("nickname", usersEntity.getNick());
        List<TrafficSourceEntity> sourceEntityList = query.list();
        TrafficSourceEntity trafficSourceEntity = sourceEntityList.get(0);
        List<CountriesSettingsEntity> countriesSettings = session.createQuery("from CountriesSettingsEntity ").list();
        query = session.createQuery("from CloakEntity where nickname = :nickname");
        query.setParameter("nickname", usersEntity.getNick());
        List<CloakEntity> cloakEntityList = query.list();
        CloakEntity cloakEntity = cloakEntityList.get(0);


        USER_NAME = usersEntity.getNick();
        VOLUUM_ACCESS_ID = usersEntity.getVoluumAccessId();
        VOLUUM_EMAIL = usersEntity.getVoluumLogin();
        VOLUUM_ACCESS_KEY = usersEntity.getVoluumAccessKey();
        VOLUUM_PASSWORD = usersEntity.getVoluumPassword();
        CLIENT_ID = usersEntity.getVoluumClientId();
        TRAFFIC_SOURCE_ID_VOLUUM = trafficSourceEntity.getIdVoluum();
        TRAFFICSOURCE_NAME = trafficSourceEntity.getNameVoluum();
        TRAFFICSOURCE_PASSWORD = trafficSourceEntity.getPassword();
        TRAFFICSOURCE_EMAIL = trafficSourceEntity.getLogin();
        AFFILIATE_NETWORK_NAME_VOLUUM = AffiliateNetworksEntity.getNameVoluum();
        AFFILIATE_NETWOK_ID = AffiliateNetworksEntity.getIdVoluum();
        CLOAK_LANDER_NAME = cloakEntity.getCloakLanderName();
        CLOAK_LANDER_ID = cloakEntity.getCloakLanderId();
        CLOAK_OFFER_NAME = cloakEntity.getCloakOfferName();
        CLOAK_OFFER_ID = cloakEntity.getCloakOfferId();
        OFFER_WORKSPACE = usersEntity.getWorkspace();
        OFFER_WORKSPACE_ID = usersEntity.getWorkspaceId();
        OFFER_ID = offer.getOffer_id();

        String country_code = offer.getAllowed_countries().get(0).getCountry_code().toUpperCase();
        for (CountriesSettingsEntity country : countriesSettings){
            if (country.getCountryCode().equalsIgnoreCase(country_code)){
                COST_PAY = country.getCost();
                break;
            } else {
                COST_PAY = "0.61";
            }

        }
        if (offer.getOffer_name().toLowerCase().contains("web") || offer.getOffer_name().toLowerCase().contains("responsive")){
            TRAFFIC_TYPE = "WEB" + off_index;
            DOMAIN = AffiliateNetworksEntity.getDomainWeb();
            if (country_code.equalsIgnoreCase("gb")) {
                DOMAIN = DOMAIN.replace("{country}", "UK");
            } else {
                DOMAIN = DOMAIN.replace("{country}", country_code.toUpperCase());
            }
        } else {
            TRAFFIC_TYPE = "MOB" + off_index;
            DOMAIN = AffiliateNetworksEntity.getDomainMob();
            if (country_code.equalsIgnoreCase("gb")) {
                DOMAIN = DOMAIN.replace("{country}", "uk");
            } else {
                DOMAIN = DOMAIN.replace("{country}", country_code.toLowerCase());
            }
        }

        HttpHost proxy = new HttpHost("127.0.0.1", 8080);
        List<Header> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
//                new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
                new BasicHeader(HttpHeaders.USER_AGENT, UA),
                new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
        );
        List<Header> headersFinal = new ArrayList<>(headers);

        httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .setProxy(proxy)
                .build();

        List<Header> newHeaders = new ArrayList<>();

        if (true) {
            HttpPost httpPost = new HttpPost("https://api.voluum.com/auth/access/session");
            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("accessId", VOLUUM_ACCESS_ID);
            ((ObjectNode) rootNode).put("accessKey", VOLUUM_ACCESS_KEY);

            System.out.println(rootNode.toString());
            StringEntity entityBody = new StringEntity(rootNode.toString());
            httpPost.setEntity(entityBody);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            String responseBody = makeRequest(httpClient, httpPost);

            ObjectNode node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
            TOKEN = node.get("token").textValue();
            System.out.println(TOKEN);

            newHeaders = Arrays.asList(
                    new BasicHeader("clientId", CLIENT_ID),
                    new BasicHeader("cwauth-token", TOKEN),
                    new BasicHeader(HttpHeaders.REFERER, "https://panel.voluum.com/?clientId=" + CLIENT_ID)
            );

        } else {

            HttpPost httpPost = new HttpPost("https://panel-api.voluum.com/auth/session");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("email", VOLUUM_EMAIL);
            ((ObjectNode) rootNode).put("password", VOLUUM_PASSWORD);

            StringEntity entityBody = new StringEntity(rootNode.toString());
            httpPost.setEntity(entityBody);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            String responseBody = makeRequest(httpClient, httpPost);
            ObjectNode node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
            TOKEN = node.get("token").textValue();
            logger.info(TOKEN);

            newHeaders = Arrays.asList(
                    new BasicHeader("clientId", CLIENT_ID),
                    new BasicHeader("cwauth-token", TOKEN),
                    new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                    new BasicHeader(HttpHeaders.REFERER, "https://panel.voluum.com/?clientId=" + CLIENT_ID)
            );
        }

        headersFinal.addAll(newHeaders);
        httpClient = HttpClientBuilder.create()
                .setProxy(proxy)
                .setDefaultHeaders(headersFinal)
                .build();
        setupVoluum();


        /**
         * temporary
         */
//        VOLUUM_CAMPAIGN_LINK = "http://create.dns-source.com/8ba3d5d2-8522-4368-9204-0170a83c4b2b?zoneid={zoneid}&campaignid={campaignid}&bannerid={bannerid}&propellerads=propellerads";
//        OFFER_SHORT_URL = "8ba3d5d2";
//        System.out.println(OFFER_SHORT_URL);
//        propeller();
    }

    private void setupVoluum() throws IOException, MyException {

        /**
         * setup list counties
         * change affiliate id
         * change affiliate name
         *
         */
        String country_code = offer.getAllowed_countries().get(0).getCountry_code().toUpperCase();
        String country_name = offer.getAllowed_countries().get(0).getCountry_name();
        HttpPost httpPost = new HttpPost("https://panel-api.voluum.com/offer");
        String offer_name = String.format("%s ($%s) %s %s %s", offer.getOffer_name(), offer.getPayout_converted(), TRAFFIC_TYPE, USER_NAME, DATE);

        String body = "{\n" +
                "  \"name\": \"" + AFFILIATE_NETWORK_NAME_VOLUUM +" - " + offer.getAllowed_countries().get(0).getCountry_name() + " - " + offer_name + "\",\n" +
                "  \"namePostfix\" : \"" + offer_name + "\",\n" +
                "  \"deleted\" : false,\n" +
                "  \"url\": \"" + offer.getLink() + "\",\n" +
                "  \"country\": {\n" +
                "    \"code\": \"" + offer.getAllowed_countries().get(0).getCountry_code()+ "\",\n" +
                "    \"name\": \"" + offer.getAllowed_countries().get(0).getCountry_name()+ "\"\n" +
                "  },\n" +
                "  \"affiliateNetwork\" : {\n" +
                "    \"id\" : \"" + AFFILIATE_NETWOK_ID + "\"\n" +
                "  },\n" +
                "  \"payout\" : {\n" +
                "    \"type\" : \"AUTO\"\n" +
                "  },\n" +
                "  \"tags\": [\n" +
                "    \"" + USER_NAME + "\"\n" +
                "  ],\n" +
                "  \"workspace\" : {\n" +
                "    \"id\" : \"" + OFFER_WORKSPACE_ID + "\"\n" +
                "  },\n" +
                "  \"allowedActions\" : [ \"EDIT\", \"DELETE\" ]\n" +
                "}";
//                "{\n" +
//                "  \"namePostfix\": \"" + offer_name + "\",\n" +
//                "  \"url\": \"" + offer.getLink() + "\",\n" +
//                "  \"country\": {\n" +
//                "    \"code\": \"" + offer.getAllowed_countries().get(0).getCountry_code()+ "\",\n" +
//                "    \"name\": \"" + offer.getAllowed_countries().get(0).getCountry_name()+ "\"\n" +
//                "  },\n" +
//                "  \"affiliateNetwork\": {\n" +
//                "    \"id\": \"" +AFFILIATE_NETWOK_ID + "\",\n" +
//                "    \"name\": \"" + AFFILIATE_NETWORK_NAME_VOLUUM + "\",\n" +
//                "    \"postbackUrl\": \"\",\n" +
//                "    \"appendClickIdToOfferUrl\": false\n" +
//                "  },\n" +
//                "  \"payout\": {\n" +
//                "    \"type\": \"AUTO\"\n" +
//                "  },\n" +
//                "  \"tags\": [\n" +
//                "    \"" + DATE + "\",\n" +
//                "    \"" + USER_NAME + "\"\n" +
//                "  ],\n" +
//                "  \"workspace\": null\n" +
//                "}";
        StringEntity entityBody = new StringEntity(body);
        httpPost.setEntity(entityBody);
        String responseBody = makeRequest(httpClient, httpPost);
        ObjectNode node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
        String guid_offer = node.get("id").textValue();

        /**
         * link format http://athellas.net/sexbadoo/coutnry/index.html
         *
         */

        httpPost = new HttpPost("https://panel-api.voluum.com/lander");
        String lander_name = TRAFFIC_TYPE + " " + USER_NAME + " " + DATE;

        body =
//                "{\n" +
//                "  \"name\": \"" + country_name + " - " + lander_name + "\"\n" +
//                "  \"namePostfix\" : \"" + lander_name + "\"\n" +
//                "  \"deleted\" : false,\n" +
//                "  \"url\" : \"" + DOMAIN + "\",\n" +
//                "  \"numberOfOffers\" : 1,\n" +
//                "  \"country\": {\n" +
//                "    \"code\": \"" + country_code + "\",\n" +
//                "    \"name\": \"" + country_name + "\"\n" +
//                "  },\n" +
//                "  \"tags\": [\n" +
//                "    \"" + USER_NAME + "\"\n" +
//                "  ],\n" +
//                "  \"workspace\" : {\n" +
//                "    \"id\" : \"" + OFFER_WORKSPACE_ID +"\"\n" +
//                "  },\n" +
//                "  \"allowedActions\" : [ \"EDIT\", \"DELETE\" ]\n" +
//                "}";
                "{\n" +
                "  \"namePostfix\": \"" + lander_name + "\",\n" +
                "  \"url\": \"" + DOMAIN + "\",\n" +
                "  \"numberOfOffers\": 1,\n" +
                "  \"country\": {\n" +
                "    \"code\": \"" + country_code + "\",\n" +
                "    \"name\": \"" + country_name + "\"\n" +
                "  },\n" +
                "  \"tags\": [\n" +
                "    \"" + DATE + "\",\n" +
                "    \"" + USER_NAME + "\"\n" +
                "  ],\n" +
                "  \"workspace\" : {\n" +
                "    \"id\" : \"" + OFFER_WORKSPACE_ID +"\"\n" +
                "  }\n" +
                "}";
        entityBody = new StringEntity(body);
        httpPost.setEntity(entityBody);
        responseBody = makeRequest(httpClient, httpPost);
        node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
        String guid_lander = node.get("id").textValue();

        UUID guid_flow = UUID.randomUUID();
        UUID guid_flow_path = UUID.randomUUID();
        UUID guid_flow_path_groups = UUID.randomUUID();
        UUID guid_flow_path_groups_paths = UUID.randomUUID();

        /**
         * flow list add new compaign
         */


        if(flowExist) {
            String flowsRequest = checkFlows();
            FlowsList flowList = new ObjectMapper().readValue(flowsRequest, FlowsList.class);
            for (CommonObject flow : flowList.getRows()) {
                if (StringUtils.containsIgnoreCase(flow.getFlowName(), country_name) &&
                        StringUtils.containsIgnoreCase(flow.getFlowWorkspaceName(), OFFER_WORKSPACE) &&
                        flow.getFlowName().contains(TRAFFIC_TYPE.substring(0, 2))) {
                    guid_flow = UUID.fromString(flow.getFlowId());

                    HttpGet get = new HttpGet("https://api.voluum.com/flow/" + guid_flow);
                    responseBody = makeRequest(httpClient, get);
                    FlowVoluum flowVoluum = new ObjectMapper().readValue(responseBody, FlowVoluum.class);
                    OfferListVoluum offerListVoluum = new OfferListVoluum();
                    OfferVoluum offerVoluum = new OfferVoluum();
                    offerVoluum.setId(guid_offer);
                    offerListVoluum.setOffer(offerVoluum);
                    offerListVoluum.setWeight(100);
                    flowVoluum.getDefaultPaths().get(0).getOffers().add(offerListVoluum);

                    System.out.println(flowVoluum.getId());
                    HttpPut httpPut = new HttpPut("https://panel-api.voluum.com/flow/" + guid_flow);
                    body = new ObjectMapper().writeValueAsString(flowVoluum);
                    entityBody = new StringEntity(body);
                    httpPut.setEntity(entityBody);
                    makeRequest(httpClient, httpPut);
                    return;
                }
            }
        }


        String name_flow = country_name + " " + TRAFFIC_TYPE + " " + USER_NAME + " " + DATE;
        /**
         * flows
         * cloak ids - are constants
         */
        httpPost = new HttpPost("https://panel-api.voluum.com/flow");
        body = "{\n" +
                "  \"id\": \"" + guid_flow+ "\",\n" +
                "  \"name\": \"" + name_flow + "\",\n" +
                "  \"countries\": [\n" +
                "    {\n" +
                "      \"code\": \"" + country_code + "\",\n" +
                "      \"name\": \"" + country_name + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"defaultPaths\": [\n" +
                "    {\n" +
                "      \"id\": \"" + guid_flow_path + "\",\n" +
                "      \"name\": \"Path 1\",\n" +
                "      \"active\": true,\n" +
                "      \"weight\": 100,\n" +
                "      \"landers\": [\n" +
                "        {\n" +
                "          \"lander\": {\n" +
                "            \"id\": \"" + guid_lander+ "\",\n" +
                "            \"name\": \"" + lander_name + "\"\n" +
                "          },\n" +
                "          \"weight\": 100\n" +
                "        }\n" +
                "      ],\n" +
                "      \"offers\": [\n" +
                "        {\n" +
                "          \"offer\": {\n" +
                "            \"id\": \"" + guid_offer + "\",\n" +
                "            \"name\": \"" + offer_name + "\"\n" +
                "          },\n" +
                "          \"weight\": 100\n" +
                "        }\n" +
                "      ],\n" +
                "      \"offerRedirectMode\": \"REGULAR\",\n" +
                "      \"realtimeRoutingApiState\": \"DISABLED\",\n" +
                "      \"autoOptimized\" : false\n" +
                "    }\n" +
                "  ],\n" +
                "  \"conditionalPathsGroups\": [\n" +
                "    {\n" +
                "      \"id\": \"" + guid_flow_path_groups + "\",\n" +
                "      \"name\": \"cloak\",\n" +
                "      \"active\": true,\n" +
                "      \"conditions\": {\n" +
                "        \"customVariable\": {\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"predicate\": \"MUST_NOT_BE\",\n" +
                "              \"variableIndex\": 3,\n" +
                "              \"variableValues\": [\n" +
                "                \"00000000\"\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"predicate\": \"MUST_BE\",\n" +
                "              \"variableIndex\": 4,\n" +
                "              \"variableValues\": [\n" +
                "                \"propellerads\"\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"paths\": [\n" +
                "        {\n" +
                "          \"id\": \"" + guid_flow_path_groups_paths + "\",\n" +
                "          \"name\": \"Path 1\",\n" +
                "          \"active\": true,\n" +
                "          \"weight\": 100,\n" +
                "          \"landers\": [\n" +
                "            {\n" +
                "              \"lander\": {\n" +
                "                \"id\": \"" + CLOAK_LANDER_ID + "\",\n" +
                "                \"name\": \"" + CLOAK_LANDER_NAME + "\"\n" +
                "              },\n" +
                "              \"weight\": 100\n" +
                "            }\n" +
                "          ],\n" +
                "          \"offers\": [\n" +
                "            {\n" +
                "              \"offer\": {\n" +
                "                \"id\": \"" + CLOAK_OFFER_ID + "\",\n" +
                "                \"name\": \"" + CLOAK_OFFER_NAME +"\"\n" +
                "              },\n" +
                "              \"weight\": 100\n" +
                "            }\n" +
                "          ],\n" +
                "          \"offerRedirectMode\": \"REGULAR\",\n" +
                "          \"realtimeRoutingApiState\": \"DISABLED\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"realtimeRoutingApi\": \"DISABLED\",\n" +
                "  \"defaultOfferRedirectMode\": \"REGULAR\",\n" +
                "  \"workspace\" : {\n" +
                "       \"id\" : \"" + OFFER_WORKSPACE_ID + "\"\n" +
                "  }\n" +
                "}";

        entityBody = new StringEntity(body);
        httpPost.setEntity(entityBody);
        responseBody = makeRequest(httpClient, httpPost);
        node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
        String guid_flow_for_campaign = node.get("id").textValue();
        String campaign_name = String.format("%s %s ($%s) %s", TRAFFIC_TYPE, USER_NAME, offer.getPayout_converted(), DATE);
        BigDecimal voluum_pay = new BigDecimal(COST_PAY).multiply(new BigDecimal(1)).divide(new BigDecimal(1000), 4, ROUND_HALF_UP);
        System.out.println("voluum pay " + voluum_pay);

        httpPost = new HttpPost("https://panel-api.voluum.com/campaign");
        body =
                "{\n" +
                "  \"namePostfix\": \"" + campaign_name+ "\",\n" +
                "  \"url\": \"\",\n" +
                "  \"impressionUrl\": \"\",\n" +
                "  \"costModel\": {\n" +
                "    \"type\": \"CPC\",\n" +
                "    \"value\": \"" + voluum_pay + "\"\n" +
                "  },\n" +
                "  \"country\": {\n" +
                "    \"code\": \"" + country_code + "\",\n" +
                "    \"name\": \"" + country_name + "\"\n" +
                "  },\n" +
                "  \"trafficSource\": {\n" +
                "    \"id\": \"" + TRAFFIC_SOURCE_ID_VOLUUM + "\",\n" +
                "    \"name\": \"" + TRAFFICSOURCE_NAME + "\",\n" +
                "    \"impressionSpecific\": false,\n" +
                "    \"customVariables\": [\n" +
                "      {\n" +
                "        \"index\": 1,\n" +
                "        \"name\": \"zoneid\",\n" +
                "        \"parameter\": \"zoneid\",\n" +
                "        \"placeholder\": \"{zoneid}\",\n" +
                "        \"trackedInReports\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": 2,\n" +
                "        \"name\": \"campaignid\",\n" +
                "        \"parameter\": \"campaignid\",\n" +
                "        \"placeholder\": \"{campaignid}\",\n" +
                "        \"trackedInReports\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": 3,\n" +
                "        \"name\": \"bannerid\",\n" +
                "        \"parameter\": \"bannerid\",\n" +
                "        \"placeholder\": \"{bannerid}\",\n" +
                "        \"trackedInReports\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"index\": 4,\n" +
                "        \"name\": \"propellerads\",\n" +
                "        \"parameter\": \"propellerads\",\n" +
                "        \"placeholder\": \"propellerads\",\n" +
                "        \"trackedInReports\": true\n" +
                "      }\n" +
                "    ],\n" +
                "    \"workspace\": \"" + OFFER_WORKSPACE_ID + "\"\n" +
                "  },\n" +
                "  \"redirectTarget\": {\n" +
                "    \"flow\": {\n" +
                "      \"id\": \"" + guid_flow_for_campaign +"\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"tags\": [],\n" +
                "  \"workspace\": {\n" +
                "    \"id\": \"" + OFFER_WORKSPACE_ID + "\"\n" +
                "  },\n" +
                "  \"customPostbacksConfiguration\": {\n" +
                "    \"customConversionPostbacks\": []\n" +
                "  }\n" +
                "}";

//                "{\n" +
//                "  \"namePostfix\": \"" + campaign_name+ "\",\n" +
//                "  \"costModel\": {\n" +
//                "    \"type\": \"CPC\",\n" +
//                "    \"value\": \"" + voluum_pay + "\"\n" +
//                "  },\n" +
//                "  \"trafficSource\": {\n" +
//                "    \"id\": \"" + TRAFFIC_SOURCE_ID_VOLUUM + "\"\n" +
//                "  },\n" +
//                "  \"redirectTarget\": {\n" +
//                "    \"flow\": {\n" +
//                "      \"id\": \"" + guid_flow_for_campaign + "\"\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"country\": {\n" +
//                "    \"code\": \"" + country_code + "\"\n" +
//                "  },\n" +
//                "  \"workspace\": {\n" +
//                "    \"id\": \"" + OFFER_WORKSPACE_ID + "d\"\n" +
//                "  },\n" +
//                "  \"tags\": [\n" +
//                "    \"" + DATE + "\",\n" +
//                "    \"" + USER_NAME + "\"\n" +
//                "  ]\n" +
//                "}";
        entityBody = new StringEntity(body);
        httpPost.setEntity(entityBody);
        responseBody = makeRequest(httpClient, httpPost);
        node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
        VOLUUM_CAMPAIGN_LINK = node.get("url").textValue();
        Pattern pattern = Pattern.compile("(?<=\\/).{8,8}(?=-)");
        Matcher matcher = pattern.matcher(VOLUUM_CAMPAIGN_LINK);
        matcher.find();
        OFFER_SHORT_URL = matcher.group();
        System.out.println(OFFER_SHORT_URL);
        propeller();

    }

    private void propeller() throws IOException, MyException {

        List<Header> headers = Arrays.asList(
                new BasicHeader(HttpHeaders.USER_AGENT, UA),
                new BasicHeader(HttpHeaders.REFERER, "https://partners.propellerads.com/"),
                new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
                new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"),
                new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json")

        );
        HttpHost proxy = new HttpHost("127.0.0.1", 8080);
        HttpClient propellerClient = HttpClientBuilder.create()
                .setProxy(proxy)
                .setDefaultHeaders(headers)
                .build();

        HttpPost httpPost = new HttpPost("https://partners.propellerads.com/v1.0/login_check");
        String body = "{\n" +
                "  \"username\": \"" + TRAFFICSOURCE_EMAIL + "\",\n" +
                "  \"password\": \"" + TRAFFICSOURCE_PASSWORD + "\",\n" +
                "  \"gRecaptchaResponse\": null,\n" +
                "  \"type\": \"ROLE_ADVERTISER\",\n" +
                "  \"fingerprint\": \"6dabbc2b9ac8d06a961fd6efa56130bb\"\n" +
                "}";
        StringEntity entityBody = new StringEntity(body);
        httpPost.setEntity(entityBody);
        String responseBody = makeRequest(propellerClient, httpPost);
        JsonNode node = new ObjectMapper().readValue(responseBody, ObjectNode.class);
        String accessToken = node.get("result").get("accessToken").textValue();

        List<Header> authHeaders = new ArrayList<>(headers);
        authHeaders.add(new BasicHeader("Authorization", "Bearer " + accessToken));

        BasicCookieStore cookieStore = new BasicCookieStore();
//        BasicClientCookie clientCookie = new BasicClientCookie("user", "%7B%22id%22%3A121137%2C%22login%22%3A%22darvare27%40gmail.com%22%2C%22email%22%3A%22darvare27%40gmail.com%22%2C%22roles%22%3A%5B%22ROLE_ADVERTISER%22%2C%22ROLE_ADVERTISER_EXTENDED%22%2C%22ROLE_ADVERTISER_SELF_REG%22%2C%22ROLE_VAT_CHECK_DISABLED%22%2C%22ROLE_MODERATION_OK%22%2C%22ROLE_PAYMENT_AVAILABLE%22%2C%22ROLE_PAYMENT_TYPE_PREPAY%22%2C%22ROLE_PAYMENT_ALLOWED%22%5D%2C%22token%22%3A%22YmQyNzAyMzNkNWNjMTc0MTAwNGI0OGMwM2E2OWRmYWE2YTEwZGFjZTM5MDQ2MDRhNTA1MTU1Yjc4Y2Q3NWZhYw%22%2C%22tokenType%22%3A%22Bearer%22%2C%22meta%22%3A%7B%22showQualitySurvey%22%3Atrue%2C%22recurringPaymentAllowed%22%3Atrue%2C%22paymentTypesAmount%22%3A%7B%22mblztr%22%3A%7B%22min%22%3A100%2C%22max%22%3A0%7D%2C%22mblztrUnionPayUsd%22%3A%7B%22min%22%3A100%2C%22max%22%3A0%7D%2C%22wire%22%3A%7B%22min%22%3A1000%2C%22max%22%3A0%7D%2C%22paypal%22%3A%7B%22min%22%3A100%2C%22max%22%3A1000%7D%2C%22skrillEur%22%3A%7B%22min%22%3A100%2C%22max%22%3A0%7D%2C%22safeChargeUsd%22%3A%7B%22min%22%3A100%2C%22max%22%3A0%7D%7D%2C%22recurringPaymentStatus%22%3A0%2C%22recurringPaymentAmount%22%3A%220.00%22%2C%22recurringPaymentThreshold%22%3A%220.00%22%2C%22showTutorial%22%3Afalse%2C%22whoisRequired%22%3Afalse%2C%22acceptedOldTerms%22%3Atrue%2C%22acceptedNewTerms%22%3Atrue%2C%22isLivechatAvailable%22%3Atrue%2C%22isSmartCpmAvailable%22%3Atrue%2C%22isAudienceAvailable%22%3Atrue%2C%22isInterstitialAvailable%22%3Atrue%2C%22isWebPushAvailable%22%3Atrue%2C%22isSmartCpmSliceLimitsAvailable%22%3Afalse%2C%22isCityTargetingAvailable%22%3Atrue%2C%22isCpcAvailable%22%3Afalse%2C%22isCpaGoalOptimizationAvailable%22%3Afalse%7D%2C%22accountType%22%3A1%2C%22legalInfo%22%3A%7B%22firstName%22%3A%22Dmitry%22%2C%22lastName%22%3A%22Makarov%22%2C%22residenceCountry%22%3A%22BY%22%7D%2C%22livechatNotes%22%3A%22http%3A%2F%2Fadplato.com%2Fadv%2Fadvertiser%2F121137%2Fview%5CnM%3A%20Ar-Niki%5CnS%3A%20SMB%20Advertiser%22%7D");
        BasicClientCookie clientCookie = new BasicClientCookie("user", URLEncoder.encode(responseBody, "UTF-8"));
        clientCookie.setDomain("partners.propellerads.com");
        cookieStore.addCookie(clientCookie);

        propellerClient = HttpClientBuilder.create()
                .setDefaultHeaders(authHeaders)
                .setProxy(proxy)
                .setDefaultCookieStore(cookieStore)
                .build();

        ZoneId zoneId = ZoneId.of("-05:00");
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        String time_now = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(time);

        String country_code = offer.getAllowed_countries().get(0).getCountry_code().toLowerCase();
        String offer_name = String.format("%s - %s-%s ($%s) %s", country_code.toUpperCase(), TRAFFIC_TYPE, offer.getOffer_id(),
                offer.getPayout_converted(), OFFER_SHORT_URL);
        httpPost = new HttpPost("https://partners.propellerads.com/v1.0/advertiser/campaigns/?dateFrom=" + time_now + "&dateTill=" + time_now+ "&isArchived=0&orderBy=id&orderDest=desc&page=1&perPage=100&refresh=0");

        if (TRAFFIC_TYPE.contains("WEB")) {
            body = "{\"rateModel\":\"scpm\",\"direction\":\"onclick\",\"frequency\":3,\"capping\":86400,\"ipFrequency\":0,\"ipCapping\":0,\"status\":1,\"targeting\":{\"connection\":\"all\",\"proxy\":\"0\",\"country\":{\"isExcluded\":false,\"list\":[\"" + country_code + "\"]},\"osType\":{\"list\":[\"desktop\"],\"isExcluded\":false},\"os\":{\"list\":[\"mac\",\"windows\"],\"isExcluded\":false},\"osVersion\":{\"list\":[\"mac10.13\",\"mac10.12\",\"mac10.11\",\"win10\",\"win8\",\"win7\"],\"isExcluded\":false},\"deviceType\":{\"list\":[\"desktop\"],\"isExcluded\":false},\"device\":{\"list\":[\"desktop\"],\"isExcluded\":false},\"zone\":{\"list\":[],\"isExcluded\":false},\"mobileIsp\":{\"list\":[],\"isExcluded\":false},\"browser\":{\"list\":[\"safari\",\"chrome\",\"firefox\"],\"isExcluded\":false},\"language\":{\"list\":[],\"isExcluded\":false},\"city\":{\"list\":[],\"isExcluded\":false},\"timeTable\":{\"list\":[\"Mon00\",\"Mon01\",\"Mon02\",\"Mon03\",\"Mon04\",\"Mon05\",\"Mon06\",\"Mon07\",\"Mon08\",\"Mon09\",\"Mon10\",\"Mon11\",\"Mon12\",\"Mon13\",\"Mon14\",\"Mon15\",\"Mon16\",\"Mon17\",\"Mon18\",\"Mon19\",\"Mon20\",\"Mon21\",\"Mon22\",\"Mon23\",\"Tue00\",\"Tue01\",\"Tue02\",\"Tue03\",\"Tue04\",\"Tue05\",\"Tue06\",\"Tue07\",\"Tue08\",\"Tue09\",\"Tue10\",\"Tue11\",\"Tue12\",\"Tue13\",\"Tue14\",\"Tue15\",\"Tue16\",\"Tue17\",\"Tue18\",\"Tue19\",\"Tue20\",\"Tue21\",\"Tue22\",\"Tue23\",\"Wed00\",\"Wed01\",\"Wed02\",\"Wed03\",\"Wed04\",\"Wed05\",\"Wed06\",\"Wed07\",\"Wed08\",\"Wed09\",\"Wed10\",\"Wed11\",\"Wed12\",\"Wed13\",\"Wed14\",\"Wed15\",\"Wed16\",\"Wed17\",\"Wed18\",\"Wed19\",\"Wed20\",\"Wed21\",\"Wed22\",\"Wed23\",\"Thu00\",\"Thu01\",\"Thu02\",\"Thu03\",\"Thu04\",\"Thu05\",\"Thu06\",\"Thu07\",\"Thu08\",\"Thu09\",\"Thu10\",\"Thu11\",\"Thu12\",\"Thu13\",\"Thu14\",\"Thu15\",\"Thu16\",\"Thu17\",\"Thu18\",\"Thu19\",\"Thu20\",\"Thu21\",\"Thu22\",\"Thu23\",\"Fri00\",\"Fri01\",\"Fri02\",\"Fri03\",\"Fri04\",\"Fri05\",\"Fri06\",\"Fri07\",\"Fri08\",\"Fri09\",\"Fri10\",\"Fri11\",\"Fri12\",\"Fri13\",\"Fri14\",\"Fri15\",\"Fri16\",\"Fri17\",\"Fri18\",\"Fri19\",\"Fri20\",\"Fri21\",\"Fri22\",\"Fri23\",\"Sat00\",\"Sat01\",\"Sat02\",\"Sat03\",\"Sat04\",\"Sat05\",\"Sat06\",\"Sat07\",\"Sat08\",\"Sat09\",\"Sat10\",\"Sat11\",\"Sat12\",\"Sat13\",\"Sat14\",\"Sat15\",\"Sat16\",\"Sat17\",\"Sat18\",\"Sat19\",\"Sat20\",\"Sat21\",\"Sat22\",\"Sat23\",\"Sun00\",\"Sun01\",\"Sun02\",\"Sun03\",\"Sun04\",\"Sun05\",\"Sun06\",\"Sun07\",\"Sun08\",\"Sun09\",\"Sun10\",\"Sun11\",\"Sun12\",\"Sun13\",\"Sun14\",\"Sun15\",\"Sun16\",\"Sun17\",\"Sun18\",\"Sun19\",\"Sun20\",\"Sun21\",\"Sun22\",\"Sun23\"],\"isExcluded\":false}},\"evenlyLimitsUsage\":false,\"trafficQuality\":1,\"autoLinkNewZones\":false,\"linkNewZonesOnce\":false,\"isAdblockBuy\":true,\"startedAt\":\"" + time_now + "\",\"rates\":[{\"countries\":[\"" + country_code + "\"],\"amount\":\"" + COST_PAY + "\"}],\"name\":\"" + offer_name + "\",\"cpaGoalStatus\":false,\"cpaGoalBid\":null,\"targetUrl\":\"" + VOLUUM_CAMPAIGN_LINK + "\",\"totalAmount\":null}";
        } else {
            body = "{\"rateModel\":\"scpm\",\"direction\":\"onclick\",\"frequency\":3,\"capping\":86400,\"ipFrequency\":0,\"ipCapping\":0,\"status\":1,\"targeting\":{\"connection\":\"all\",\"proxy\":\"0\",\"country\":{\"isExcluded\":false,\"list\":[\"" + country_code + "\"]},\"osType\":{\"list\":[\"mobile\"],\"isExcluded\":false},\"os\":{\"list\":[\"android\",\"ios\"],\"isExcluded\":false},\"osVersion\":{\"list\":[\"android8\",\"android7\",\"android6\",\"android5\",\"ios11\",\"ios10\",\"ios9\"],\"isExcluded\":false},\"deviceType\":{\"list\":[\"phone\"],\"isExcluded\":false},\"device\":{\"list\":[],\"isExcluded\":false},\"zone\":{\"list\":[],\"isExcluded\":false},\"mobileIsp\":{\"list\":[],\"isExcluded\":false},\"browser\":{\"list\":[\"safari\",\"chrome\"],\"isExcluded\":false},\"language\":{\"list\":[],\"isExcluded\":false},\"city\":{\"list\":[],\"isExcluded\":false},\"timeTable\":{\"list\":[\"Mon00\",\"Mon01\",\"Mon02\",\"Mon03\",\"Mon04\",\"Mon05\",\"Mon06\",\"Mon07\",\"Mon08\",\"Mon09\",\"Mon10\",\"Mon11\",\"Mon12\",\"Mon13\",\"Mon14\",\"Mon15\",\"Mon16\",\"Mon17\",\"Mon18\",\"Mon19\",\"Mon20\",\"Mon21\",\"Mon22\",\"Mon23\",\"Tue00\",\"Tue01\",\"Tue02\",\"Tue03\",\"Tue04\",\"Tue05\",\"Tue06\",\"Tue07\",\"Tue08\",\"Tue09\",\"Tue10\",\"Tue11\",\"Tue12\",\"Tue13\",\"Tue14\",\"Tue15\",\"Tue16\",\"Tue17\",\"Tue18\",\"Tue19\",\"Tue20\",\"Tue21\",\"Tue22\",\"Tue23\",\"Wed00\",\"Wed01\",\"Wed02\",\"Wed03\",\"Wed04\",\"Wed05\",\"Wed06\",\"Wed07\",\"Wed08\",\"Wed09\",\"Wed10\",\"Wed11\",\"Wed12\",\"Wed13\",\"Wed14\",\"Wed15\",\"Wed16\",\"Wed17\",\"Wed18\",\"Wed19\",\"Wed20\",\"Wed21\",\"Wed22\",\"Wed23\",\"Thu00\",\"Thu01\",\"Thu02\",\"Thu03\",\"Thu04\",\"Thu05\",\"Thu06\",\"Thu07\",\"Thu08\",\"Thu09\",\"Thu10\",\"Thu11\",\"Thu12\",\"Thu13\",\"Thu14\",\"Thu15\",\"Thu16\",\"Thu17\",\"Thu18\",\"Thu19\",\"Thu20\",\"Thu21\",\"Thu22\",\"Thu23\",\"Fri00\",\"Fri01\",\"Fri02\",\"Fri03\",\"Fri04\",\"Fri05\",\"Fri06\",\"Fri07\",\"Fri08\",\"Fri09\",\"Fri10\",\"Fri11\",\"Fri12\",\"Fri13\",\"Fri14\",\"Fri15\",\"Fri16\",\"Fri17\",\"Fri18\",\"Fri19\",\"Fri20\",\"Fri21\",\"Fri22\",\"Fri23\",\"Sat00\",\"Sat01\",\"Sat02\",\"Sat03\",\"Sat04\",\"Sat05\",\"Sat06\",\"Sat07\",\"Sat08\",\"Sat09\",\"Sat10\",\"Sat11\",\"Sat12\",\"Sat13\",\"Sat14\",\"Sat15\",\"Sat16\",\"Sat17\",\"Sat18\",\"Sat19\",\"Sat20\",\"Sat21\",\"Sat22\",\"Sat23\",\"Sun00\",\"Sun01\",\"Sun02\",\"Sun03\",\"Sun04\",\"Sun05\",\"Sun06\",\"Sun07\",\"Sun08\",\"Sun09\",\"Sun10\",\"Sun11\",\"Sun12\",\"Sun13\",\"Sun14\",\"Sun15\",\"Sun16\",\"Sun17\",\"Sun18\",\"Sun19\",\"Sun20\",\"Sun21\",\"Sun22\",\"Sun23\"],\"isExcluded\":false}},\"evenlyLimitsUsage\":false,\"trafficQuality\":1,\"autoLinkNewZones\":false,\"linkNewZonesOnce\":false,\"isAdblockBuy\":true,\"startedAt\":\"" + time_now + "\",\"rates\":[{\"countries\":[\"" + country_code + "\"],\"amount\":\"" + COST_PAY + "\"}],\"name\":\"" + offer_name + "\",\"cpaGoalStatus\":false,\"cpaGoalBid\":null,\"targetUrl\":\"" + VOLUUM_CAMPAIGN_LINK + "\",\"totalAmount\":null}";
        }
        entityBody = new StringEntity(body);
        httpPost.setEntity(entityBody);
        makeRequest(propellerClient, httpPost);

    }

    private String checkFlows() throws IOException, MyException {

        String date = setDateYear();
        StringBuffer url = new StringBuffer("https://panel-api.voluum.com/report?from=" + date + "&tz=America%2FNew_York&sort=visits&direction=desc&columns=flowName&columns=flowWorkspaceName&columns=impressions&columns=visits&columns=clicks&columns=conversions&columns=revenue&columns=cost&columns=profit&columns=cpv&columns=ctr&columns=cr&columns=cv&columns=roi&columns=epv&columns=epc&groupBy=flow&offset=0&limit=100&include=ACTIVE&conversionTimeMode=VISIT");
        HttpGet get = new HttpGet(url.toString());
        return makeRequest(httpClient, get);
    }

    private String setDateYear(){
        Date date = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        simpleDate.setTimeZone(TimeZone.getTimeZone("EST"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return "2018-05-05&to=" + simpleDate.format(calendar.getTime());
    }

    private static String makeRequest(HttpClient httpClient, HttpRequestBase http) throws IOException, MyException {
        System.out.println("url " + http.getURI());
        String httpMethod = http.getURI().toString().substring(http.getURI().toString().lastIndexOf("/") + 1,
                http.getURI().toString().length());

        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {

                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                logger.info("error");

                return EntityUtils.toString(response.getEntity());

//                return makeRequest(httpClient, http);
            }
        };

        String responseBody = httpClient.execute(http, responseHandler);
        if (responseBody.contains("Name must be unique")){
            String out =  "exception in create " + httpMethod + " - Name must be unique";
            throw new MyException(out);
        }
        return responseBody;
    }

    public static class MyException extends Exception{
        public MyException(String out){
            super(out);
        }
    }
}
