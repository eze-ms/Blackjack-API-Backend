package com.ezequiel.itacademy.blackjack.mysql.routers;

import com.ezequiel.itacademy.blackjack.mysql.handlers.RankingHandler;
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

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@Tag(name = "Ranking", description = "Gestión de ranking en la API de Blackjack")
public class RankingRouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/ranking/{playerId}",
                    beanClass = RankingHandler.class,
                    beanMethod = "getPlayerRanking",
                    operation = @Operation(
                            summary = "Obtener el ranking de un jugador",
                            description = "Este endpoint devuelve el ranking de un jugador específico basado en su ID.",
                            parameters = {
                                    @Parameter(name = "playerId", in = ParameterIn.PATH, description = "ID del jugador", required = true)
                            }
                    )
            ),
            @RouterOperation(
                    path = "/ranking",
                    beanClass = RankingHandler.class,
                    beanMethod = "getAllRankings",
                    operation = @Operation(
                            summary = "Obtener el ranking de todos los jugadores",
                            description = "Este endpoint devuelve el ranking de todos los jugadores en el sistema."
                    )
            )
    })
    public RouterFunction<ServerResponse> rankingRoutes(RankingHandler handler) {
        return route(GET("/ranking"), handler::getAllRankings)
                .andRoute(GET("/ranking/{playerId}"), handler::getPlayerRanking);
    }
}
