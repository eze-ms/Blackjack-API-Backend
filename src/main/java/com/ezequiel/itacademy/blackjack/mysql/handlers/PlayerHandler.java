package com.ezequiel.itacademy.blackjack.mysql.handlers;

import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import com.ezequiel.itacademy.blackjack.mysql.service.PlayerService;
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

import java.util.Map;

@Component
public class PlayerHandler {

    private final PlayerService playerService;

    public PlayerHandler(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(
            summary = "Actualizar el nombre de un jugador",
            description = "Este endpoint permite cambiar el nombre de un jugador en la base de datos.",
            parameters = {
                    @Parameter(
                            name = "playerId",
                            in = ParameterIn.PATH,
                            description = "ID del jugador a actualizar",
                            required = true
                    )
            },
            requestBody = @RequestBody(
                    description = "Nuevo nombre del jugador",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Object.class),
                            examples = @ExampleObject(value = "{ \"name\": \"\" }")
                    )
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nombre actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = Player.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Jugador no encontrado"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos"
                    )
            }
    )
    public Mono<ServerResponse> updatePlayerName(ServerRequest request) {
        Long playerId = Long.parseLong(request.pathVariable("playerId").trim());

        return request.bodyToMono(Map.class) // Recibe JSON correctamente
                .map(body -> body.get("name").toString().trim()) // Extrae solo el valor del nombre
                .flatMap(name -> playerService.updatePlayerName(playerId, name))
                .flatMap(updatedPlayer -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedPlayer))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
