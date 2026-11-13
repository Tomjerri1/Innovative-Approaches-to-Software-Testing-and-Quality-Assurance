package chnu.edu.inno.controller;

import chnu.edu.inno.model.Item;
import chnu.edu.inno.model.dto.response.ApiResponse;
import chnu.edu.inno.model.dto.response.BaseMetaData;
import chnu.edu.inno.model.dto.response.PaginationMetaData;
import chnu.edu.inno.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/items")
public class ItemController {

    private final ItemService itemService;

    // meta.code of the body is used as the real HTTP status
    private static <M extends BaseMetaData, D> ResponseEntity<ApiResponse<M, D>> toResponse(ApiResponse<M, D> response) {
        return ResponseEntity.status(response.getMeta().getCode()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by id", description = "200 - found, 404 - not found")
    public ResponseEntity<ApiResponse<BaseMetaData, Item>> getItemById(@PathVariable String id) {
        return toResponse(itemService.getItemById(id));
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "200 - list of all items")
    public ResponseEntity<ApiResponse<BaseMetaData, Item>> getAllItems() {
        return toResponse(itemService.getAllItems());
    }

    @PostMapping
    @Operation(summary = "Create item", description = "201 - created (id is generated), 400 - bad request")
    public ResponseEntity<ApiResponse<BaseMetaData, Item>> createItem(@RequestBody Item item) {
        return toResponse(itemService.createItem(item));
    }

    @PutMapping
    @Operation(summary = "Update item", description = "200 - updated, 400 - id missing, 404 - not found")
    public ResponseEntity<ApiResponse<BaseMetaData, Item>> updateItem(@RequestBody Item item) {
        return toResponse(itemService.updateItem(item));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item by id", description = "200 - deleted, 404 - not found")
    public ResponseEntity<ApiResponse<BaseMetaData, Item>> deleteItem(@PathVariable String id) {
        return toResponse(itemService.deleteItemById(id));
    }

    //----------------  Pagination ---------------------------

    @GetMapping("/page")
    @Operation(
            method = "GET",
            summary = "Get  ALL items, ASC, PAGEABLE. Default 5 items per page",
            description = "Fetch a PAGE of items  by page number and size "
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success. Return page of items",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "object",
                                            properties = {
                                                    @StringToClassMapItem(key = "meta", value = PaginationMetaData.class),
                                                    @StringToClassMapItem(key = "data", value = Item.class)
                                            }
                                    )
                            )
                    }
            )
    })
    public ResponseEntity<ApiResponse<PaginationMetaData, Item>> getItemsPage(
            @Parameter(description = "Request parameter. Page of paginated result. Cannot be less than zero")
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Request parameter. Size of paginated page result. Cannot be less than zero")
            @RequestParam(name = "size", defaultValue = "5", required = false) Integer size
    ) {
        return toResponse(itemService.getPage(page, size));
    }





}
