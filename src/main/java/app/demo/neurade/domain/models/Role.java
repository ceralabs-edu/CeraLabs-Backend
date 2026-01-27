package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Short id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "priority", nullable = false)
    private Short priority;

    public boolean hasHigherPriorityThan(Role other) {
        return this.priority < other.priority;
    }

    public boolean isRoleType(RoleType roleType) {
        return this.getId() == roleType.getRoleId();
    }
}
