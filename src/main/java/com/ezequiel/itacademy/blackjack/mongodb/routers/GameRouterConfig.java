package com.ezequiel.itacademy.blackjack.mongodb.routers;

import com.ezequiel.itacademy.blackjack.mongodb.handlers.GameHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@Tag(name = "Game API", description = "Endpoints para gestionar partidas de Blackjack")
public class GameRouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/game/new", beanClass = GameHandler.class, beanMethod = "addGame",
                    operation = @Operation(summary = "Crea una nueva partida", description = "Crea una nueva partida asignando un jugador.")),

            @RouterOperation(path = "/game/{id}", beanClass = GameHandler.class, beanMethod = "getGameById",
                    operation = @Operation(summary = "Obtiene detalles de una partida", description = "Recupera la información de una partida de Blackjack específica.")),

            @RouterOperation(path = "/game/{id}/play", beanClass = GameHandler.class, beanMethod = "playMove",
                    operation = @Operation(summary = "Realiza una jugada", description = "Permite realizar una jugada en una partida de Blackjack en curso.")),

            @RouterOperation(path = "/game/{id}/delete", beanClass = GameHandler.class, beanMethod = "removeGame",
                    operation = @Operation(summary = "Elimina una partida", description = "Elimina una partida específica de la base de datos."))
    })
    public RouterFunction<ServerResponse> gameRoutes(GameHandler handler) {
        return route(POST("/game/new"), handler::addGame)
                .andRoute(GET("/game/{id}"), handler::getGameById)
                .andRoute(POST("/game/{id}/play"), handler::playMove)
                .andRoute(DELETE("/game/{id}/delete"), handler::removeGame);
    }
}
