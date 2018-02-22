package com.game.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import java.io.Serializable;


@Data
public class GameModelId implements Serializable {

    @DynamoDBHashKey
    private String playerId;

    @DynamoDBRangeKey
    private String createdTime;

}
