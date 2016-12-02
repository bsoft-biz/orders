package biz.bsoft.web.dto;

import biz.bsoft.orders.model.ClientPOS;

public class UserDto {
    private String username;
    private String password;
    private boolean enabled;
    private String email;
    private String firstName;
    private String lastName;
    private ClientPOS clientPOS;

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", clientPOS=" + clientPOS +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public ClientPOS getClientPOS() {
        return clientPOS;
    }

    public void setClientPOS(ClientPOS clientPOS) {
        this.clientPOS = clientPOS;
    }
}
