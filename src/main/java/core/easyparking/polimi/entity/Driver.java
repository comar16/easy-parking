package core.easyparking.polimi.entity;

import core.easyparking.polimi.utils.object.staticvalues.Status;
import lombok.*;
import org.hibernate.annotations.OnDelete;

import javax.persistence.*;
import java.io.Serializable;

import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name="user")
public class Driver implements Serializable {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Basic
    @Column(name = "licenseId")
    private Long licenseId;

    @Basic
    @Column(name = "accountId")
    private Long accountId;

    @Basic
    @Column(nullable = false, length = 64)
    private String name;

    @Basic
    @Column(nullable = false, length = 64)
    private String surname;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    public Driver(Long accountId, String name, String surname, Status status) {
        this.accountId = accountId;
        this.name = name;
        this.surname = surname;
        this.status = status;
    }

    @OnDelete(action = CASCADE)
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "licenseId", referencedColumnName = "licenseId", insertable = false, updatable = false)
    private License license;

    @OnDelete(action = CASCADE)
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "accountId", referencedColumnName = "accountId", insertable = false, updatable = false)
    private Account account;
}
