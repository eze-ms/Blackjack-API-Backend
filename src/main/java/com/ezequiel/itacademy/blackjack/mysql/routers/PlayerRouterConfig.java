package com.ezequiel.itacademy.blackjack.mysql.routers;

import com.ezequiel.itacademy.blackjack.mysql.handlers.PlayerHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Players", description = "Gestión de jugadores en la API de Blackjack")
public class PlayerRouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/player/{playerId}",
                    beanClass = PlayerHandler.class,
                    beanMethod = "updatePlayerName",
                    operation = @Operation(
                            summary = "Actualizar el nombre de un jugador",
                            description = "Este endpoint permite cambiar el nombre de un jugador en la base de datos.",
                            parameters = {
                                    @Parameter(name = "playerId", in = ParameterIn.PATH, description = "ID del jugador a actualizar", required = true)
                            }
                    )
            )
    })
    RouterFunction<ServerResponse> playerRoutes(PlayerHandler handler) {
        return route(PUT("/player/{playerId}"), handler::updatePlayerName);
    }
}