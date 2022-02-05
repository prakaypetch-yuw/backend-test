package com.example.backendtest.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "\"User\"")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userId;

    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    @JsonIgnore
    private String password;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Address")
    private String address;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "ReferenceCode")
    private String referenceCode;

    @Column(name = "salary")
    private Integer salary;

    @Column(name = "MemberType")
    private String memberType;

    @Column(name = "Active")
    private Boolean active;

    @CreationTimestamp
    @Column(name = "CreateDate")
    private Date createDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "UserRole",
            joinColumns = {
                    @JoinColumn(name = "UserID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "RoleID")})
    private Set<Role> roles;
}