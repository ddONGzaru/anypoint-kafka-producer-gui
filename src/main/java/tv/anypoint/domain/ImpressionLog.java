package tv.anypoint.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class ImpressionLog implements Serializable {

    private static final long serialVersionUID = -2994623061748645868L;

    @Id
    private String id;

    private long campaign;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date impressionTime;

    private int region1;

    private int region2;

    private int region3;

    private int region4;

    private boolean isError;

    private int programProvider;

    private long asset;

    private int cpv;

    private int cueOwner;

    private long device;

    private int playTime;

    private int serviceOperator;

    private float vtr;

    private int zipCode;
}
