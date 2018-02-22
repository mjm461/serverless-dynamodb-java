package com.game.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;
import org.springframework.data.annotation.Id;


@Data
@DynamoDBTable(tableName = "GameModelJava")
public class GameModel {

    @DynamoDBIgnore
    @Id
    private GameModelId gameModelId;

    public GameModel(){
        this.gameModelId = new GameModelId();
    }

    @DynamoDBHashKey(attributeName = "playerId")
    public String getPlayerId() {
        return gameModelId.getPlayerId();
    }

    public void setPlayerId(String playerId) {
        this.gameModelId.setPlayerId(playerId);
    }

    @DynamoDBRangeKey(attributeName = "createdTime")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "GameWinnerTimeIndex")
    public String getCreatedTime() {
        return gameModelId.getCreatedTime();
    }

    public void setCreatedTime(String createdTime) {
        this.gameModelId.setCreatedTime(createdTime);
    }

    @DynamoDBAttribute
    @DynamoDBIndexRangeKey(localSecondaryIndexName = "GamePlayerOpponentIndex")
    private String opponentId;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "GameWinnerTimeIndex")
    private String winnerId;

    @DynamoDBAttribute
    private String notes;

}
