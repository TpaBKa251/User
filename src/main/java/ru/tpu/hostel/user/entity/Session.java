package ru.tpu.hostel.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sessions", schema = "\"user\"")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "\"user\"", referencedColumnName = "id")
    private User userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date")
    private LocalDateTime expirationTime;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "version", nullable = false)
    @Version
    private Long version;
}