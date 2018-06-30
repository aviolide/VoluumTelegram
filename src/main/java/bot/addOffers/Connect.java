package bot.addOffers;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Connect {

    public static void main(String[] args) throws TelegramApiException {
        ApiContextInitializer.init(); // Инициализируем апи
        TelegramBotsApi botapi = new TelegramBotsApi();

        System.getProperty("desided.home");
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        HttpHost httpHost = new HttpHost("127.0.0.1", 8080);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();
        botOptions.setRequestConfig(requestConfig);

        Bot myBot = new Bot(botOptions);
        botapi.registerBot(myBot);


    }
}
