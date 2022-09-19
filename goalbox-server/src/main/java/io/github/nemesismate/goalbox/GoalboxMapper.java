package io.github.nemesismate.goalbox;

import io.github.nemesismate.goalbox.metadata.GoalboxMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper
public interface GoalboxMapper {

    Goalbox mapWithContractData(GoalboxMetadata goalboxMetadata, BigInteger value, Boolean settled);

    @Mapping(target = "contractAddress", source = "contractAddress")
    GoalboxMetadata map(Goalbox goalbox, String contractAddress);

}
