package gr.aueb.cf;

import gr.aueb.cf.model.Course;
import gr.aueb.cf.model.Region;
import gr.aueb.cf.model.Teacher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaAPIQueries {
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
            listCoursesWithTeachersCount();
            listTeachersWithMoreThanOneCourse();
            em.getTransaction().commit();
        } finally {
            em.close();
            emf.close();
        }
    }


    private static void listAllRegionsOrderedByTitleDesc() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Region> query = cb.createQuery(Region.class);
        Root<Region> region = query.from(Region.class);
        query.select(region).orderBy(cb.desc(region.get("title")));
        List<Region> regions = em.createQuery(query).getResultList();
        System.out.println("----------REGIONS SORTED BY TITLE (ASC ORDER)------------------");
        regions.forEach(System.out::println);
    }

    private static void findTeachersByRegion(String regionTitle) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Region> region = teacher.join("region");
        ParameterExpression<String> regionParam = cb.parameter(String.class, "regionTitle");
        query.select(teacher).where(cb.equal(region.get("title"), regionParam));
        List<Teacher> teachers = em.createQuery(query).setParameter("regionTitle", regionTitle).getResultList();
        System.out.printf("----------TEACHERS IN REGION %s ------------------\n", regionTitle);
        teachers.forEach(System.out::println);
    }

    private static void countTeachersInRegion(String regionTitle) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Region> region = teacher.join("region");
        ParameterExpression<String> regionParam = cb.parameter(String.class, "regionTitle");
        query.select(cb.count(teacher)).where(cb.equal(region.get("title"), regionParam));
        Long count = em.createQuery(query).setParameter("regionTitle", regionTitle).getSingleResult();
        System.out.printf("----------TEACHERS' COUNT IN REGION %s ------------------\n", regionTitle);
        System.out.println(count);
    }


    private static void listTeachersAndRegionTitles() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Region> region = teacher.join("region");
        query.multiselect(teacher, region.get("title"));
        List<Object[]> results = em.createQuery(query).getResultList();
        System.out.println("----------TEACHERS - REGIONS ----------------");
        for (Object[] result: results) {
            Teacher teacher1 = (Teacher) result[0];
            String regionTitle = (String) result[1];
            System.out.println(teacher1 + " - " + regionTitle);
        }
    }

    private static void listTeachersAndCoursesTitles() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Course> course = teacher.join("courses");
        query.multiselect(teacher, course.get("title")).orderBy(cb.asc(teacher.get("lastname")), cb.asc(course.get("title")));
        List<Object[]> results = em.createQuery(query).getResultList();
        System.out.println("----------TEACHERS - COURSES ----------------");
        for (Object[] result: results) {
            Teacher teacher1 = (Teacher) result[0];
            String course1 = (String) result[1];
            System.out.println(teacher1 + " - " + course1);
        }
    }

    private static void findTeachersByCourseTitle(String courseTitle) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Course> course = teacher.join("courses");
        ParameterExpression<String> courseTitleParam = cb.parameter(String.class, "courseTitle");
        query.select(teacher).where(cb.equal(course.get("title"), courseTitleParam));
        List<Teacher> teachers = em.createQuery(query).setParameter("courseTitle", "Java").getResultList();
        System.out.printf("----------Teachers By Course %s ------------------\n", courseTitle);
        teachers.forEach(System.out::println);
    }

    private static void countTeachersByLastname() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Course> course = teacher.join("courses", JoinType.LEFT);
        query.multiselect(teacher.get("lastname"), cb.count(course)).groupBy(teacher.get("lastname")).orderBy(cb.desc(cb.count(course)));
        List<Object[]> results = em.createQuery(query).getResultList();
        System.out.println("----------Count of teachers by Lastname ----------------");
        for (Object[] result: results) {
            String lastname = (String) result[0];
            Long count = (Long) result[1];
            System.out.println(lastname + " - " + count);
        }
    }

    private static void listTeachersWithNoCourses() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
        Root<Teacher> teacher = query.from(Teacher.class);
        query.select(teacher).where(cb.isEmpty(teacher.get("courses")));
        System.out.println("----------List of Teachers With No Courses ----------------");
        em.createQuery(query).getResultList().forEach(System.out::println);
    }

    private static void listTeachersWithMoreThanOneCourse() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
        Root<Teacher> teacher = query.from(Teacher.class);
        Join<Teacher, Course> course = teacher.join("courses");
        query.select(teacher).groupBy(teacher.get("id")).having(cb.gt(cb.count(course), 1));
        List<Teacher> teachers = em.createQuery(query).getResultList();
        System.out.println("----------List of Teachers With More than one course ----------------");
        teachers.forEach(System.out::println);
    }

    private static void listCoursesWithTeachersCount() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Course> course = query.from(Course.class);
        Join<Course, Teacher> teacher = course.join("teachers");
        query.multiselect(course.get("title"), cb.count(teacher)).groupBy(course.get("title")).orderBy(cb.desc(cb.count(teacher)));
        System.out.println("----------List of Courses With Number of teachers ----------------");
        List<Object[]> results = em.createQuery(query).getResultList();
        for (Object[] obj: results) {
            String course1 = (String) obj[0];
            Long count = (Long) obj[1];
            System.out.println(course1 + " - " + count);
        }
    }
}
