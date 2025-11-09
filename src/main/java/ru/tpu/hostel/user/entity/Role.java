package ru.tpu.hostel.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import ru.tpu.hostel.internal.utils.Roles;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuppressWarnings("java:S1700")
@Table(name = "roles", schema = "\"user\"")
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 4828464657708191997L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "\"user\"", referencedColumnName = "id")
    private User user;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

}
