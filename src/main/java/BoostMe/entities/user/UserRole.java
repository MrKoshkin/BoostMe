package BoostMe.entities.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PreRemove
    public void preRemove() {
        if (user != null) {
            user.removeUserRole(this);
        }

        if (role != null) {
            role.removeUserRole(this);
        }
    }
}