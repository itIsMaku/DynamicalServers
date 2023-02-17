package cz.maku.dynamical;

import cz.maku.mommons.ef.annotation.Entity;
import cz.maku.mommons.ef.annotation.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "ds_servers")
public class ServerModel {

    @Id
    private String name;
    private String template;
    private boolean created;
    private String failure;

}
