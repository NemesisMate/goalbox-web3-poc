package io.github.nemesismate.goalbox.metadata;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.util.UUID;

@Table("goalbox_metadata")
@Data
public class GoalboxMetadata {

    @Id
    UUID id;
    String name;
    String ownerAddress;
    String contractAddress;
    BigInteger goal;

}
