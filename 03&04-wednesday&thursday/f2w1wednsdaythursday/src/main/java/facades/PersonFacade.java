package facades;

import entities.Person;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade{

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Person addPerson(String fName, String lName, String phone) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Person p = new Person(fName, lName, phone);
            em.persist(p);
            em.getTransaction().commit();
            return p;
        }finally{
            em.close();
        }
    }
    
    @Override
    public Person editPerson(Person p) throws PersonNotFoundException{
        EntityManager em = emf.createEntityManager();
        try{
            if(getPerson(p.getId().intValue()) == null) throw new PersonNotFoundException("No person with provided id found");
            em.getTransaction().begin();
            p.setLastEdited();
            em.merge(p);
            em.getTransaction().commit();
            return p;
        }finally{
            em.close();
        }
    }

    @Override
    public Person deletePerson(int id) throws PersonNotFoundException{
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Person p = em.find(Person.class, Long.valueOf(id));
            em.remove(p);
            em.getTransaction().commit();
            return p;
        }catch(Exception e){
            throw new PersonNotFoundException("Could not delete, provided id does not exist");
        }finally{
            em.close();
        }
    }
    

    @Override
    public Person getPerson(int id) throws PersonNotFoundException{
        EntityManager em = emf.createEntityManager();
        try{
            Person p = em.find(Person.class, Long.valueOf(id));
            if(p == null) throw new PersonNotFoundException("No person with provided id found");
            return p;
        }finally{
            em.close();
        }
    }

    @Override
    public List<Person> getAllPersons() {
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<Person> tq = em.createQuery("SELECT p FROM Person p", Person.class);
            return tq.getResultList();
        }finally{
            em.close();
        }
    }

    @Override
    public long countPerson() {
        EntityManager em = emf.createEntityManager();
        try {
            long personCount = (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
            return personCount;
        } finally {
            em.close();
        }
    }

}
