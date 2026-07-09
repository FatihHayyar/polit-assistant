package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "affairs")
public class AffairEntity {

    @Id
    private Long id;

    @Column(name = "title_de")
    private String titleDe;

    @Column(name = "type_name_de")
    private String typeNameDe;

    @Column(name = "state_name_de")
    private String stateNameDe;

    @Column(name = "begin_date")
    private LocalDateTime beginDate;

    @Column(name = "url_external_de")
    private String urlExternalDe;

    protected AffairEntity() {}

    public Long getId() { return id; }
    public String getTitleDe() { return titleDe; }
    public String getTypeNameDe() { return typeNameDe; }
    public String getStateNameDe() { return stateNameDe; }
    public LocalDateTime getBeginDate() { return beginDate; }
    public String getUrlExternalDe() { return urlExternalDe; }

}