/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
/**
 *
 * @author Obaydah Mohamad
 */
@Entity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.PERSIST)
    private List<Address> addresses = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "PHONE")
    @Column(name="Description")
    private Map<String,String> phones = new HashMap();

    @ElementCollection
    @CollectionTable(
        name = "hobbies",
        joinColumns = @JoinColumn(name = "Customer_id")
    )
    @Column(name = "HOBBY")
    private List<String> hobbies = new ArrayList();

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void addHobby(String s) {
        hobbies.add(s);
    }

    public String getHobbies() {
        return String.join(",", hobbies);
    }
    
    public void addPhone(String phoneNo, String description){
        phones.put(phoneNo, description);
    }
    public String getPhoneDescription(String phoneNo){
        return phones.get(phoneNo);
    }
    
    public void addAddress(Address a){
        addresses.add(a);
    }

    @Override
    public String toString() {
        return "entities.Customer[ id=" + id + " ]";
    }

}
