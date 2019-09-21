/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import entities.Address;
import entities.Customer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Obaydah Mohamad
 */
public class Tester {
    public static void main(String[] args) {
        Customer c = new Customer("Billy", "Johnson");
        Customer c2 = new Customer("Harvey", "Specter");
        
        
        
        //Hobby
//        c.addHobby("Hiking");
//        c.addHobby("Running");
//        c.addHobby("Gaming");
//        
//        c2.addHobby("Swimming");
//        c2.addHobby("Lifting");
//        c2.addHobby("Cooking");
//        
//        //Phone
//        c.addPhone("112", "Politi");
//        c.addPhone("113", "Ikke politet");
        
        //Address
        Address a = new Address("Street1", "City1");
        Address a2 = new Address("Street2", "City2");
        Address a3 = new Address("Street3", "City3");
        Address a4 = new Address("Street4", "City4");
        c.addAddress(a);
        c.addAddress(a2);
        c2.addAddress(a3);
        c2.addAddress(a4);
        
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(c);
            em.persist(c2);
            em.getTransaction().commit();
        }finally{
            em.close();
        }
        //Persistence.generateSchema("pu", null);
    }
}
