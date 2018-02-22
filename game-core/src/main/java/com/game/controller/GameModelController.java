package com.game.controller;

import com.game.model.GameModel;
import com.game.repository.GameModelRepository;
import com.game.service.TableCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@RestController
@PreAuthorize("hasRole('User')")
@RequestMapping("/game")
public class GameModelController {

    @Autowired TableCreationService tableCreationService;
    @Autowired GameModelRepository gameModelRepository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<GameModel> list(@RequestParam(value = "create", required = false) Boolean create) {
        if (create != null && create) {
            tableCreationService.updateOrCreate(GameModel.class, 1L, 1L, null);
        }
        return gameModelRepository.findAll();  // don't do this in real life
    }

    @RequestMapping(method = RequestMethod.POST)
    public GameModel create(@RequestBody GameModel gameModel) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        gameModel.setCreatedTime(df.format(new Date()));
        return gameModelRepository.save(gameModel);
    }

    @RequestMapping(value = "{playerId}", method = RequestMethod.GET)
    public Iterable<GameModel> getall(@PathVariable("playerId") String playerId,
                                      @RequestParam(value = "opponentId", required = false) String opponentId,
                                      @RequestParam(value = "winner", required = false) Boolean winner){
        if(opponentId != null){
            // LSI find by opponent
            return gameModelRepository.findByPlayerIdAndOpponentId(playerId, opponentId);
        }
        else if(winner != null && winner) {
            // GSI order by created time
            return gameModelRepository.findByWinnerId(playerId);
        }
        else{
            // Return all your games
            return gameModelRepository.findByPlayerId(playerId);
        }
    }

}
