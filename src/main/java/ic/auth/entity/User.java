package ic.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "users")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue
    @Column
    private UUID id;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    @CreationTimestamp
    private OffsetDateTime createdAt;
    @Column
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private OffsetDateTime sessionInvalidatesAt;
    @Column
    private OffsetDateTime refreshValidTill;
}