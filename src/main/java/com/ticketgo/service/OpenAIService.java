package com.ticketgo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketgo.dto.RouteSearchDto;
import com.ticketgo.entity.Role;
import com.ticketgo.request.ChatRequest;
import com.ticketgo.request.MessageCreateRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {
    @Value("${openai.azure.api-key}")
    private String azureApiKey;

    @Value("${openai.azure.endpoint}")
    private String azureEndpoint;

    @Value("${openai.azure.deployment-name}")
    private String deploymentName;

    private final RouteService routeService;
    private final ChatBotService chatBotService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    public String chatWithToolCalls(ChatRequest request) throws Exception {
        var messageList = chatBotService.getMessages(request.getConversationId());
        List<Map<String, Object>> messages = new ArrayList<>();

        // Add system prompt
        messages.add(Map.of(
                "role", Role.system,
                "content", systemPrompt()
        ));

        // Add message history
        if (messageList != null) {
            for (var message : messageList) {
                messages.add(Map.of(
                        "role", message.getRole().toString(),
                        "content", message.getContent()
                ));
            }
        }

        // Add user message
        messages.add(Map.of(
                "role", Role.user.toString(),
                "content", request.getContent()
        ));

        List<Map<String, Object>> functions = getDefinedFunctions();

        boolean hasToolCalls = true;
        String responseBody = null;
        JsonNode responseJson = null;

        while (hasToolCalls) {
            responseBody = callOpenAI(messages, functions);
            responseJson = mapper.readTree(responseBody);

            JsonNode toolCall = responseJson.at("/choices/0/message/function_call");

            if (toolCall != null && !toolCall.isMissingNode() && toolCall.isObject()) {
                String functionName = toolCall.get("name").asText();
                JsonNode argumentsNode = toolCall.get("arguments");

                String toolResponse = processToolCall(functionName, argumentsNode);

                // Add assistant message with function_call
                Map<String, Object> assistantMsg = new HashMap<>();
                assistantMsg.put("role", "assistant");
                assistantMsg.put("content", null);
                assistantMsg.put("function_call", Map.of(
                        "name", functionName,
                        "arguments", argumentsNode
                ));
                messages.add(assistantMsg);

                // Add tool message with function name and response
                Map<String, Object> toolMsg = new HashMap<>();
                toolMsg.put("role", "function");
                toolMsg.put("name", functionName);
                toolMsg.put("content", toolResponse);
                messages.add(toolMsg);
            } else {
                hasToolCalls = false;
            }
        }

        JsonNode finalMessageNode = responseJson.at("/choices/0/message/content");
        if (!finalMessageNode.isMissingNode()) {
            addMessage(request.getConversationId(), Role.user, request.getContent());
            addMessage(request.getConversationId(), Role.assistant, finalMessageNode.asText());
            return finalMessageNode.asText();
        } else {
            return "[Không có nội dung trả về từ assistant]";
        }
    }

    private String callOpenAI(List<Map<String, Object>> messages, List<Map<String, Object>> functions) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        requestBody.put("functions", functions);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", azureApiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = String.format(
                "%s/openai/deployments/%s/chat/completions?api-version=2024-02-15-preview",
                azureEndpoint, deploymentName
        );

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    private String processToolCall(String functionName, JsonNode argumentsNode) throws Exception {
        if (argumentsNode.isTextual()) {
            argumentsNode = mapper.readTree(argumentsNode.asText());
        }

        switch (functionName) {
            case "SearchBusRoutes":
                RouteSearchDto args = mapper.treeToValue(argumentsNode, RouteSearchDto.class);
                String sortBy = args.getSortBy() != null ? args.getSortBy() : "departureTime";
                String sortDirection = args.getSortDirection() != null ? args.getSortDirection() : "asc";
                int pageNumber = args.getPageNumber() != null ? args.getPageNumber() : 1;
                int pageSize = args.getPageSize() != null ? args.getPageSize() : 10;

                log.info("[Chatbot] Calling searchRoutes with: departureLocation={}, arrivalLocation={}, departureDate={}, sortBy={}, sortDirection={}, pageNumber={}, pageSize={}",
                        args.getDepartureLocation(),
                        args.getArrivalLocation(),
                        args.getDepartureDate(),
                        sortBy,
                        sortDirection,
                        pageNumber,
                        pageSize
                );
                var result = routeService.searchRoutes(
                        args.getDepartureLocation(),
                        args.getArrivalLocation(),
                        args.getDepartureDate() != null ? java.time.LocalDate.parse(args.getDepartureDate()) : null,
                        sortBy,
                        sortDirection,
                        pageNumber,
                        pageSize
                );
                // Convert the response to JSON string
                String jsonResponse = mapper.writeValueAsString(result.getBody());
                log.info("[Chatbot] Response from searchRoutes: {}", jsonResponse);

                return jsonResponse;
            default:
                throw new IllegalArgumentException("Function not supported: " + functionName);
        }
    }

    private void addMessage(String conversationId, Role role, String content) {
        chatBotService.createMessage(new MessageCreateRequestDto(conversationId, role, content));
    }

    private String systemPrompt() {
        var systemTime = LocalDateTime.now();
        return "Bạn là một trợ lý thông minh chuyên hỗ trợ tìm kiếm chuyến xe buýt. Bạn có thể trả lời câu hỏi của người dùng bằng cách sử dụng các công cụ tìm kiếm chuyến xe. Hãy lịch sự, súc tích và chính xác.\n" +
                "\n" +
                "<general_guidelines>\n" +
                "- Luôn trả lời bằng tiếng Việt\n" +
                "- Trả về kết quả dưới định dạng HTML có styling cơ bản (in đậm, danh sách, tiêu đề) phù hợp hiển thị web\n" +
                "- Sử dụng thẻ <ul><li> cho danh sách, <strong> cho text quan trọng, <h3> cho tiêu đề\n" +
                "- Phân tích ý định của người dùng và quyết định có cần gọi API tìm kiếm hay không\n" +
                "- Nếu API có sẵn, map các giá trị người dùng cung cấp vào parameters API\n" +
                "- Nếu API thất bại, thông báo người dùng thử lại sau\n" +
                "- Trả lời một cách rõ ràng và hữu ích\n" +
                "</general_guidelines>\n" +
                "\n" +
                "<route_search_guidelines>\n" +
                "- Khi người dùng hỏi về chuyến xe mà không chỉ định bộ lọc, sử dụng giá trị mặc định cho các tham số không được chỉ định\n" +
                "- Khi hiển thị thông tin chuyến xe, luôn bao gồm các trường: Tuyến đường, Thời gian khởi hành, Thời gian đến, Giá vé, Số ghế trống, Loại xe\n" +
                "- Định dạng thời gian theo dd/MM/yyyy HH:mm\n" +
                "- Định dạng giá tiền theo VND với dấu phẩy phân cách\n" +
                "- Hiển thị kết quả theo định dạng:\n" +
                "  🚌 **[Tuyến đường]**\n" +
                "  - **Khởi hành**: [Thời gian khởi hành]\n" +
                "  - **Đến nơi**: [Thời gian đến]\n" +
                "  - **Giá vé**: [Giá] VND\n" +
                "  - **Ghế trống**: [Số ghế]\n" +
                "  - **Loại xe**: [Loại xe]\n" +
                "</route_search_guidelines>\n" +
                "<url_guidelines>\n" +
                "  - Khi trả về kết quả tìm kiếm, luôn bao gồm URL đến trang FE với selectedScheduleId là scheduleId của mỗi kết quả trong danh sách\n" +
                "  - Định dạng URL: https://ticketgo-black.vercel.app/search?departureLocation=...&arrivalLocation=...&departureDate=...&sortBy=departureTime&sortDirection=asc&pageNumber=1&pageSize=10&selectedScheduleId=...\n" +
                "</url_guidelines>\n" +
                "\nThời gian hiện tại: " + systemTime + "\n";
    }

    private List<Map<String, Object>> getDefinedFunctions() {
        List<Map<String, Object>> functions = new ArrayList<>();

        Map<String, Object> searchBusRoutesTool = Map.of(
                "name", "SearchBusRoutes",
                "description", "Tìm kiếm chuyến xe buýt dựa trên điểm đi, điểm đến, ngày khởi hành và các tiêu chí khác",
                "parameters", Map.of(
                        "type", "object",
                        "properties", getSearchBusRoutesProperties(),
                        "required", List.of()
                )
        );

        functions.add(searchBusRoutesTool);
        return functions;
    }

    private Map<String, Object> getSearchBusRoutesProperties() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("departureLocation", Map.of(
                "type", "string",
                "description", "Điểm khởi hành. Bắt buộc."
        ));
        properties.put("arrivalLocation", Map.of(
                "type", "string",
                "description", "Điểm đến. Bắt buộc."
        ));
        properties.put("departureDate", Map.of(
                "type", "string",
                "format", "date",
                "description", "Ngày khởi hành theo định dạng yyyy-MM-dd. Bắt buộc."
        ));
        properties.put("sortBy", Map.of(
                "type", "string",
                "description", "Sắp xếp theo trường nào (departureTime, price, duration). Tùy chọn."
        ));
        properties.put("sortDirection", Map.of(
                "type", "string",
                "description", "Hướng sắp xếp (asc hoặc desc). Tùy chọn."
        ));
        properties.put("pageNumber", Map.of(
                "type", "integer",
                "description", "Số trang (bắt đầu từ 1). Mặc định là 1.",
                "default", 1
        ));
        properties.put("pageSize", Map.of(
                "type", "integer",
                "description", "Số kết quả mỗi trang. Mặc định là 10.",
                "default", 10
        ));
        return properties;
    }
}
