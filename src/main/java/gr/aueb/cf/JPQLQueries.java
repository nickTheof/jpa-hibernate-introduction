package gr.aueb.cf;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPQLQueries {
    private static final Map<String, String> persistenceConfig = new HashMap<>();
    static {
        persistenceConfig.put("jakarta.persistence.jdbc.password", System.getenv("PASSWD_USER7"));
    }
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("school7PU", persistenceConfig);
    private static final EntityManager em = emf.createEntityManager();


    public static void main(String[] args) {
        try {
            em.getTransaction().begin();



            em.getTransaction().commit();
        } finally {
            em.close();
            emf.close();
        }
    }



}
