package com.ezequiel.itacademy.blackjack.mongodb.handlers;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class GameHandler {

    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    //! Crea una nueva partida de Blackjack.
    @Operation(
            summary = "Crea una nueva partida de Blackjack",
            description = "Este endpoint permite crear una nueva partida asignando un jugador.",
            requestBody = @RequestBody(
                    description = "Datos del jugador para crear la partida",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Escribe aquí el id del jugador")
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Partida creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ServerResponse> addGame(ServerRequest request) {
        return request.bodyToMono(String.class)
                .map(playerId -> playerId.trim().replace("\"", ""))
                .flatMap(gameService::createGame)
                .flatMap(savedGame -> ServerResponse.created(URI.create("/game/" + savedGame.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedGame));
    }

    //! Obtener detalles de una partida específica.
    @Operation(
            summary = "Obtener detalles de una partida",
            description = "Este endpoint devuelve la información detallada de una partida específica de Blackjack.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la partida a consultar", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles de la partida obtenidos exitosamente",
                    content = @Content(schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "404", description = "Partida no encontrada")
    })
    public Mono<ServerResponse> getGameById(ServerRequest request) {
        String gameId = request.pathVariable("id").trim();

        return gameService.findGameById(gameId)
                .flatMap(game -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(game))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    //! Permitir que un jugador haga una jugada en una partida.
    @Operation(
            summary = "Realizar una jugada en una partida",
            description = "Este endpoint permite a un jugador realizar una jugada en una partida de Blackjack.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la partida", required = true)
            },
            requestBody = @RequestBody(
                    description = "Tipo de movimiento a realizar (HIT, STAND, DOUBLE, SPLIT)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MoveType.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jugada realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "400", description = "Movimiento no válido"),
            @ApiResponse(responseCode = "404", description = "Partida no encontrada")
    })
    public Mono<ServerResponse> playMove(ServerRequest request) {
        String gameId = request.pathVariable("id").trim();

        return request.bodyToMono(String.class)
                .flatMap(body -> {
                    String move = body.trim().replace("\"", ""); // Eliminar espacios y comillas extras
                    try {
                        MoveType moveType = MoveType.valueOf(move); // Convertir a enum
                        return gameService.addMove(gameId, moveType);
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new RuntimeException("Movimiento no válido"));
                    }
                })
                .flatMap(updatedGame -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedGame))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    //! Elimina una partida de Blackjack.
    @Operation(
            summary = "Eliminar una partida",
            description = "Este endpoint permite eliminar una partida de Blackjack existente.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la partida a eliminar", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Partida eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Partida no encontrada")
    })
    public Mono<ServerResponse> removeGame(ServerRequest request) {
        String gameId = request.pathVariable("id").trim();

        return gameService.findGameById(gameId)
                .flatMap(gameDb -> gameService.deleteGame(gameDb.getId())
                        .then(ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"message\": \"Partida eliminada correctamente\", \"gameId\": \"" + gameId + "\"}")))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
