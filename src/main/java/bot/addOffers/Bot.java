package bot.addOffers;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import bot.addOffers.settings.Advidi;
import bot.addOffers.settings.ClickDealer;
import bot.addOffers.settings.HibernateSessionFactory;
import bot.addOffers.settings.POJOHibernate.UsersEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 * @author timofeevan
 */

public class Bot extends TelegramLongPollingBot {

    public static String TOKEN = "";
    public static String USERNAME = "";

    public Bot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(update.getMessage().getChatId().toString());

        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println(update.getMessage().getChatId());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            processCommand(update);
        } else if (update.hasMessage() && update.getMessage().hasDocument()){
            System.out.println("photo");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            System.out.println(update.getMessage().getChatId());
            try {
                getPhoto(update);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            try {
                processCallbackQuery(update);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (update.hasInlineQuery()) {
        }
    }

    public void getPhoto(Update update) throws TelegramApiException, IOException {

        Document document= update.getMessage().getDocument();
        String filename = document.getFileId();
        System.out.println(filename);
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(document.getFileId());
        File file = execute(getFileMethod);

        java.io.File fileJava = downloadFile(file);
        java.io.File fileCopy = new java.io.File("files/csvs/" + filename + ".csv");
        Files.copy(fileJava.toPath(), fileCopy.toPath());


        startAddOffers(update.getMessage().getChatId().toString(), filename);

    }

    private void processCallbackQuery(Update update) throws InterruptedException, IOException {
        List<SendMessage> answerMessage = null;
        String data = update.getCallbackQuery().getData();
        if (data == null) {
            return;
        }
        else if (update.hasCallbackQuery()){
            String call_data = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();


            String string = call_data;
            System.out.println(string);
            switch (string) {
                case "Add Offers" : {
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    SendMessage answer = new SendMessage();
                    answer.setChatId(chatId);
                    answer.setReplyMarkup(markupInline);
                    answer.setText("please upload csv file (id, creo, affiliate)");

                    try {
                        execute(answer);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                }
                break;
                case "costs" : System.out.println("costs");
                    try {
                        String msgText = "wait please";
                        SendMessage message = new SendMessage()
                                .setText(msgText)
                                .setChatId(chatId);
                        execute(message);
//                        msgText = new StartCost().StartCost();
                        message = new SendMessage()
                                .setText(msgText)
                                .setChatId(chatId);
                        execute(message);
//                    } catch (IOException e) {
//                        e.printStackTrace();
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "start" : System.out.println("start");
                    break;
                case "results" : System.out.println("results");
                    break;
                case "newCampaing" : {
//                    new Propeller(true);
                    break;
                }
                case "allCampaing" : {
//                    new Propeller(false);
                    break;
                }
            }

        }

        if (answerMessage != null && answerMessage.isEmpty()) {
            answerMessage.clear();
        }
    }

    private void startAddOffers(String chatId, String fileName) throws IOException, TelegramApiException {
        Reader reader = Files.newBufferedReader(Paths.get("files/csvs/" + fileName + ".csv"));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase());

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from UsersEntity where chatId = :param");
        query.setParameter("param",  chatId);
        UsersEntity user = (UsersEntity) query.list().get(0);
        int offerIndex = user.getOfferCount();
//        System.out.println(user.getName() + user.getOfferCount());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("starting add offers");
        execute(sendMessage);

        for (CSVRecord csvRecord : csvParser){

            String affiliateNetwork = csvRecord.get("affiliate");
            offerIndex++;
            System.out.println(offerIndex);
            session.getTransaction().begin();
            query = session.createQuery("update UsersEntity SET offerCount = :count where nick = :name");
            query.setParameter("count", offerIndex);
            query.setParameter("name", user.getNick());
            query.executeUpdate();
            session.getTransaction().commit();

            if (affiliateNetwork.contains("advidi")){
                Advidi advidi = new Advidi();
                sendMessage.setText(advidi.startAdvidi(csvRecord, offerIndex, user, session));

            } else if (affiliateNetwork.contains("clickdealer")){
                ClickDealer clickDealer = new ClickDealer();
                sendMessage.setText(clickDealer.startClickdealer(csvRecord, offerIndex, user, session));
            }

            execute(sendMessage);

        }
        sendMessage.setText("added complete");
        execute(sendMessage);
//        session.close();
//        HibernateSessionFactory.shutdown();
        }

    private void processCommand(Update update) {
        SendMessage answerMessage = null;
        try {
            answerMessage = _processCommand(update);
            if (answerMessage != null) {
                execute(answerMessage);
            }

        } catch (TelegramApiException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public SendMessage _processCommand(Update update) {
        SendMessage answerMessage = null;
        String text = update.getMessage().getText();
        if ("/start".equalsIgnoreCase(text)) {
            answerMessage = new SendMessage();
            answerMessage.setText("Hi, what do we do??");

            answerMessage.setReplyMarkup(keyboard());
            answerMessage.setParseMode("HTML");
            answerMessage.setChatId(update.getMessage().getChatId());
        }
        return answerMessage;
    }



    private InlineKeyboardMarkup keyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Add Offers").setCallbackData("Add Offers"));
//        rowInline.add(new InlineKeyboardButton().setText("Costs").setCallbackData("costs"));
        rowsInline.add(rowInline);
        rowInline = new ArrayList<>();
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
    public ReplyKeyboardMarkup keyboardButton(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("/start"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}