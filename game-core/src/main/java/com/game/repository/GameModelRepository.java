package com.game.repository;

import com.game.model.GameModel;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameModelRepository extends PagingAndSortingRepository<GameModel, String> {

    @EnableScan // not the best idea - but this is a demo
    Iterable<GameModel> findAll();

    Iterable<GameModel> findByPlayerId(String playerId);

    Iterable<GameModel> findByPlayerIdAndOpponentId(String playerId, String oponentId);

    Iterable<GameModel> findByWinnerId(String winnerId);

}
