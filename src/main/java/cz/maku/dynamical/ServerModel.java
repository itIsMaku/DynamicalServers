package cz.maku.dynamical;

import cz.maku.mommons.ef.annotation.AttributeConvert;
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
    @AttributeConvert(converter = BooleanConverter.class) // idk, ef doesn't support boolean :D, will fix it later
    private boolean created;
    private String failure;
    private String address;
    private Integer port;

}

/*

CREATE TABLE `ds_servers` (
	`name` VARCHAR(50) NOT NULL,
	`template` VARCHAR(50) NOT NULL,
	`created` TINYINT NOT NULL DEFAULT 0,
	`failure` VARCHAR(50) NULL DEFAULT NULL,
	`address` VARCHAR(50) NULL DEFAULT '127.0.0.1',
	`port` INT NULL DEFAULT NULL,
	PRIMARY KEY (`name`)
)
COLLATE='utf8mb4_general_ci'
;

 */
