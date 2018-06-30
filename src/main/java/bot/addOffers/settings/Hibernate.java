package bot.addOffers.settings;


import bot.addOffers.settings.POJOHibernate.AffiliateNetworksEntity;
import org.hibernate.Session;

import java.util.List;

public class Hibernate {

    public static void main(String[] args) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        session.beginTransaction();
        List<AffiliateNetworksEntity> networkEntityList = session.createQuery("from AffiliateNetworksEntity").list();
        session.close();
//        System.out.println();
        AffiliateNetworksEntity contactEntity = networkEntityList.get(0);
        System.out.println(contactEntity.getId());
        System.out.println(contactEntity.getIdVoluum());
        System.out.println(contactEntity.getLogin());
        System.out.println(contactEntity.getPassword());
        System.out.println(contactEntity.getIdVoluum());
    }
}
