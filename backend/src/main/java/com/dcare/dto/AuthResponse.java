package com.dcare.dto;

import com.dcare.model.Role;
import java.util.UUID;

public class AuthResponse {
    private String token;
    private UUID userId;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isApproved = true;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, UUID userId, String email, String fullName, Role role, Boolean isApproved, String message) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isApproved = isApproved != null ? isApproved : true;
        this.message = message;
    }

    public static AuthResponseBuilder builder() { return new AuthResponseBuilder(); }

    public static class AuthResponseBuilder {
        private String token;
        private UUID userId;
        private String email;
        private String fullName;
        private Role role;
        private Boolean isApproved = true;
        private String message;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder userId(UUID userId) { this.userId = userId; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AuthResponseBuilder role(Role role) { this.role = role; return this; }
        public AuthResponseBuilder isApproved(Boolean isApproved) { this.isApproved = isApproved; return this; }
        public AuthResponseBuilder message(String message) { this.message = message; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, userId, email, fullName, role, isApproved, message);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
