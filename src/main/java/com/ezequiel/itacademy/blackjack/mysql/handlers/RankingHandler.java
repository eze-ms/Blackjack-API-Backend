package com.ezequiel.itacademy.blackjack.mysql.handlers;

import com.ezequiel.itacademy.blackjack.mysql.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RankingHandler {

    private final RankingService rankingService;

    public RankingHandler(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Operation(
            summary = "Obtener el ranking de un jugador",
            description = "Este endpoint devuelve el ranking de un jugador específico basado en su ID.",
            parameters = {
                    @Parameter(
                            name = "playerId",
                            in = ParameterIn.PATH,
                            description = "ID del jugador",
                            required = true)
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ranking obtenido correctamente",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Jugador no encontrado en el ranking"
                    )
            })

    public Mono<ServerResponse> getPlayerRanking(ServerRequest request) {
        Long playerId = Long.parseLong(request.pathVariable("playerId").trim());
        return rankingService.getPlayerRanking(playerId)
                .flatMap(ranking -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(ranking))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Operation(
            summary = "Obtener el ranking de todos los jugadores",
            description = "Este endpoint devuelve el ranking de todos los jugadores en el sistema."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ranking obtenido correctamente",
                            content = @Content(mediaType = "application/json"))
            })

    public Mono<ServerResponse> getAllRankings(ServerRequest request) {
        return rankingService.getAllRankings()
                .collectList()
                .flatMap(rankings -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(rankings));
    }

}
