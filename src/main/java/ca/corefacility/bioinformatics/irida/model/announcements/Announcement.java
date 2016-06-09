package ca.corefacility.bioinformatics.irida.model.announcements;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.user.User;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.TemporalType;

import java.util.Date;

/**
 * An announcement object. Announcements can be created only by admin users, and announcements
 * are displayed on the dashboard page.
 */
@Entity
@Table(name = "announcement")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Announcement implements IridaThing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreatedDate
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private final Date createdDate;

    @Column(name = "message")
    @Lob
    private String message;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "created_by_id")
    @CreatedBy
    private User user;

    /**
     *      Default constructor, needed by Hibernate.
     */
    private Announcement() {
        createdDate = new Date();
        this.message = null;
        this.user = null;
    }

    /**
     *      Create a new {@link Announcement} object for displaying {@param message}. The
     *      admin {@link User} that created the message will be recorded using the
     *      {@link ca.corefacility.bioinformatics.irida.service.SecurityAuditorAware} interface along with the
     *      {@link CreatedBy} annotation on the user field.
     * @param message The content of the announcement to display
     */
    public Announcement(String message) {
        this();
        this.message = message;
    }

    /**
     *      Create a new {@link Announcement} object, for display on the front page.
     * @param message The content of the announcement to display
     * @param user The {@link User} who created the announcement
     */
    public Announcement(String message, User user) {
        this();
        this.message = message;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getMessage() {
        return message;
    }

    public User getCreatedById() {
        return user;
    }

    public String getLabel() {
        return getMessage();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedById(User user) {
        this.user = user;
    }

}
