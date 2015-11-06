package tv.anypoint.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by kwlee on 15. 9. 22.
 */
@Entity
@Data
public class ImpressionLogCampaign implements Serializable {

    private static final long serialVersionUID = -5090718616687586939L;

    @Id
    @GeneratedValue
    private int id;

    private long campaign;

    private int totalImpression;

    private int deviceTotalImpression;

}
