package BoostMe.entities.user;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Role {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    private String name;

}