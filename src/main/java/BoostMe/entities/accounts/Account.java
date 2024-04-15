package BoostMe.entities.accounts;

import BoostMe.entities.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(30)", nullable = false, unique = true)
    private String username;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
