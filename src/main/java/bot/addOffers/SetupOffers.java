package bot.addOffers;

import bot.addOffers.settings.Advidi;
import bot.addOffers.settings.ClickDealer;
import bot.addOffers.settings.HibernateSessionFactory;
import bot.addOffers.settings.POJOHibernate.AffiliateNetworksEntity;
import bot.addOffers.settings.POJOHibernate.UsersEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.interfaces.BotApiObject;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendSticker;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;


public class SetupOffers {

    private static Session session;

//    public void start(String chatId, String fileName) throws IOException {
    public static void main(String[] args) throws IOException {
    String fileName = "offers";
    String chatId = "196221399";
        Reader reader = Files.newBufferedReader(Paths.get("files/csvs/" + fileName + ".csv"));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase());

        session = HibernateSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();

        int offerIndex = 1;
        for (CSVRecord csvRecord : csvParser){
            offerIndex++;
            String affiliateNetwork = csvRecord.get("affiliate");

            Query query = session.createQuery("from UsersEntity where chatId = :param");

            query.setParameter("param",  chatId);
            UsersEntity user = (UsersEntity) query.list().get(0);

            if (affiliateNetwork.contains("advidi")){
                Advidi advidi = new Advidi();
                advidi.startAdvidi(csvRecord, offerIndex, user, session);
            } else if (affiliateNetwork.contains("clickdealer")){
                ClickDealer clickDealer = new ClickDealer();
                clickDealer.startClickdealer(csvRecord, offerIndex, user, session);
            }


        }
        session.close();
        HibernateSessionFactory.shutdown();
    }
}
