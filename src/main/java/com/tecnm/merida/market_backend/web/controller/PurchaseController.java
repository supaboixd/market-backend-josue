package com.tecnm.merida.market_backend.web.controller;

import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchase", description = "Manage purchases in a Master-Detail transactional flow")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("")
    @Operation(summary = "Get all purchases", description = "Return all registered purchases with their items")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of purchases")
    public ResponseEntity<List<Purchase>> getAll() {
        return new ResponseEntity<>(purchaseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/client/{idClient}")
    @Operation(summary = "Get purchases by client ID", description = "Return all purchases made by a specific client")
    @ApiResponse(responseCode = "200", description = "Purchases found")
    @ApiResponse(responseCode = "404", description = "No purchases found for this client")
    public ResponseEntity<List<Purchase>> getByClient(
            @Parameter(description = "Client ID", example = "4546221", required = true)
            @PathVariable("idClient") String clientId) {
        return purchaseService.getByClient(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(
            summary = "Register a complete purchase (Master-Detail)",
            description = "Saves a purchase header and its nested product items in a single transactional cascade",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Example Purchase Ticket",
                                    value = """
                                            {
                                              "clientId": "4546221",
                                              "date": "2026-11-20T17:15:00",
                                              "paymentMethod": "E",
                                              "comment": "Compra en caja rapida",
                                              "state": "P",
                                              "items": [
                                                {
                                                  "productId": 1,
                                                  "quantity": 2,
                                                  "total": 39.00,
                                                  "active": true
                                                },
                                                {
                                                  "productId": 2,
                                                  "quantity": 1,
                                                  "total": 25.50,
                                                  "active": true
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponse(responseCode = "201", description = "Purchase and items saved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid purchase payload")
    @ApiResponse(responseCode = "500", description = "Transaction failed and rolled back")
    public ResponseEntity<Purchase> save(@RequestBody Purchase purchase) {
        return new ResponseEntity<>(purchaseService.save(purchase), HttpStatus.CREATED);
    }
}