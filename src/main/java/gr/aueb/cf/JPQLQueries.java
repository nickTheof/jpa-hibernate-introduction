package gr.aueb.cf;

import gr.aueb.cf.model.Region;
import gr.aueb.cf.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
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
            listAllRegionsOrderedByTitleDesc();
            findTeachersByRegion("Αθήνα");
            findTeachersByRegion("Αγρίνιο");
            countTeachersInRegion("Αθήνα");
            countTeachersInRegion("Αγρίνιο");
            listTeachersAndRegionTitles();
            listTeachersAndCoursesTitles();
            findTeachersByCourseTitle("Java");
            findTeachersByCourseTitle("Javascript");
            countTeachersByLastname();
            listTeachersWithNoCourses();
            listTeachersWithCourseCounts();


            em.getTransaction().commit();
        } finally {
            em.close();
            emf.close();
        }
    }


    private static void listAllRegionsOrderedByTitleDesc() {
        String sql = "SELECT r FROM Region r ORDER BY r.title ASC";
        TypedQuery<Region> query = em.createQuery(sql, Region.class);
        List<Region> regions = query.getResultList();
        System.out.println("----------REGIONS SORTED BY TITLE (ASC ORDER)------------------");
        regions.forEach(System.out::println);
    }

    private static void findTeachersByRegion(String regionTitle) {
        String sql = "SELECT t FROM Teacher t WHERE t.region.title=:regionTitle";
        TypedQuery<Teacher> query = em.createQuery(sql, Teacher.class);
        query.setParameter("regionTitle", regionTitle);
        List<Teacher> teachers = query.getResultList();
        System.out.printf("----------TEACHERS IN REGION %s ------------------\n", regionTitle);
        teachers.forEach(System.out::println);
    }

    private static void countTeachersInRegion(String regionTitle) {
        String sql = "SELECT COUNT(t) FROM Teacher t WHERE t.region.title = :regionTitle";
        TypedQuery<Object> query = em.createQuery(sql, Object.class);
        query.setParameter("regionTitle", regionTitle);
        Long count = (Long) query.getSingleResult();
        System.out.printf("----------TEACHERS' COUNT IN REGION %s ------------------\n", regionTitle);
        System.out.println(count);
    }


    private static void listTeachersAndRegionTitles() {
        String sql = "SELECT t, r.title FROM Teacher t JOIN t.region r";
        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class);
        List<Object[]> objects = query.getResultList();
        System.out.println("----------TEACHERS - REGIONS ----------------");
        for (Object[] obj : objects) {
            Teacher teacher = (Teacher) obj[0];
            String regionTitle = (String) obj[1];
            System.out.println(teacher + " - region: " + regionTitle);
        }
    }

    private static void listTeachersAndCoursesTitles() {
        String sql = "SELECT t, c.title FROM Teacher t JOIN t.courses c";
        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class);
        List<Object[]> objects = query.getResultList();
        System.out.println("----------TEACHERS - COURSES ----------------");
        for (Object[] obj : objects) {
            Teacher teacher = (Teacher) obj[0];
            String courseTitle = (String) obj[1];
            System.out.println(teacher + " - " + courseTitle);
        }
    }

    private static void findTeachersByCourseTitle(String courseTitle) {
        String sql = "SELECT t FROM Teacher t JOIN t.courses c WHERE c.title = :courseTitle";
        TypedQuery<Teacher> query = em.createQuery(sql, Teacher.class);
        query.setParameter("courseTitle", courseTitle);
        List<Teacher> teachers = query.getResultList();
        System.out.printf("----------Teachers By Course %s ------------------\n", courseTitle);
        teachers.forEach(System.out::println);
    }

    private static void countTeachersByLastname() {
        String sql = "SELECT t.lastname, COUNT(c) FROM Teacher t LEFT JOIN t.courses c GROUP BY t.lastname";
        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class);
        List<Object[]> objects = query.getResultList();
        System.out.println("----------Count of teachers by Lastname ----------------");
        for (Object[] obj: objects) {
            String lastname = (String) obj[0];
            Long count = (Long) obj[1];
            System.out.println(lastname + " - " + count);
        }
    }

    private static void listTeachersWithNoCourses() {
        String sql = "SELECT t FROM Teacher t WHERE t.courses IS EMPTY";
        List<Teacher> teachers = em.createQuery(sql, Teacher.class).getResultList();
        System.out.println("----------List of Teachers With No Courses ----------------");
        teachers.forEach(System.out::println);
    }

    private static void listTeachersWithCourseCounts() {
        String sql = "SELECT c.title, COUNT(t) FROM Course c LEFT JOIN c.teachers t GROUP BY c.title ORDER BY COUNT(t) DESC";
        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class);
        List<Object[]> objects = query.getResultList();
        System.out.println("----------List of Teachers With their courses' count ----------------");
        for (Object[] obj: objects) {
            String course = (String) obj[0];
            Long count = (Long) obj[1];
            System.out.println(course + " - " + count);
        }
    }
}
