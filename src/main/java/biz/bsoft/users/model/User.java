package biz.bsoft.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vbabin on 27.03.2016.
 */
@NamedQueries({
        @NamedQuery(
                name = "allUsers",
                query = "from User u"
        )
})
@Entity
    @Table(name = "oreders_users")
    public class User {

        private String username;
        private String password;
        private boolean enabled;
        private Set<UserRole> userRole = new HashSet<UserRole>(0);
        private String email;


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
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", userRole=" + userRole +
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}