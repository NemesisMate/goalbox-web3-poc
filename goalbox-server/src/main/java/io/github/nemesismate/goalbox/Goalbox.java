package io.github.nemesismate.goalbox;

import java.math.BigInteger;
import java.util.UUID;

public record Goalbox (
    UUID id,
    String name,
    String ownerAddress,
    String contractAddress,
    BigInteger value,
    BigInteger goal,
    boolean settled
) {}
