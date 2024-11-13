package com.milado_api_main.entities;

import com.milado_api_main.enumerated.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "accounts", schema = "dbtogo")
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Column(name = "hash")
    private String password;

    @NotNull(message = "Verification status for phone cannot be null")
    @Column(name = "is_verified_phone")
    private Integer isVerifiedPhone;

    @NotNull(message = "Verification status for Email cannot be null")
    @Column(name = "is_verified_email")
    private Integer isVerifiedEmail;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<AccountRole> accountRoles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus userStatus;


    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.userStatus = UserStatus.PENDING_APPROVAL;
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var auth = new HashSet<GrantedAuthority>(accountRoles.size());
        for (AccountRole accountRole : accountRoles) {
            auth.add((GrantedAuthority) accountRole.getRole());
        }
        return auth;
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}