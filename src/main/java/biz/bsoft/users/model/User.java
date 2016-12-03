package biz.bsoft.users.model;

import biz.bsoft.orders.model.ClientPOS;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "oreders_users")
public class User {

    private String username;
    private String password;
    private boolean enabled;
    private Set<UserRole> userRole = new HashSet<UserRole>(0);
    private String email;
    private String firstName;
    private String lastName;
    private ClientPOS clientPOS;


    public User() {
    }

    public User(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public User(String username, String password,
                boolean enabled, Set<UserRole> userRole) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", enabled=" + enabled +
                ", userRole=" + userRole +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", clientPOS=" + clientPOS +
                '}';
    }

    @Id
    @Column(name = "username", unique = true,
            nullable = false, length = 45)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password",
            nullable = false, length = 60)
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "enabled", nullable = false)
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @JsonManagedReference
    public Set<UserRole> getUserRole() {
        return this.userRole;
    }

    public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }

    @Column(unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @OneToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE, CascadeType.PERSIST})
    public ClientPOS getClientPOS() {
        return clientPOS;
    }

    public void setClientPOS(ClientPOS clientPOS) {
        this.clientPOS = clientPOS;
    }
}